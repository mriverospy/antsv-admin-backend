package py.gov.mitic.htv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.model.VerificacionCodigoValidacion;
import py.gov.mitic.htv.repository.UsuarioRepository;
import py.gov.mitic.htv.repository.VerificacionCodigoValidacionRepository;
import py.gov.mitic.htv.util.GeneradorCodigo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    VerificacionCodigoValidacionRepository verificacionCodigoValidacionRepository;

    @Value("${htv.admin.validacion.codigo.expirado.minutos}")
    private Integer expiryMinute;

    @Value("${htv.admin.linK.acceso.plataforma}")
    private String linkAccesoPlataformaAdmin;

    @Autowired
    MailService mailService;

    public void setPassword(String codigo, String newPassword) {

        verificacionCodigoValidacionRepository.findByCodigo(codigo)
                .map(validationCode -> {

                    Usuario usuario = null;
                    if (validationCode.getTipo().equals(VerificacionCodigoValidacion.TIPO_RESET_PASSWORD)) {
                        usuario = usuarioRepository.findByCorreo(validationCode.getCorreo());
                    } else {
                        usuario = usuarioRepository.findByUsername(validationCode.getCorreo());
                    }
                    if (Objects.isNull(usuario)) {
                        throw new BadRequestException("No existe usuario con el correo " + validationCode.getCorreo());
                    }
                    // 1. Verificar que el código sea correcto
                    if (!validationCode.getCodigo().equals(codigo)) {
                        throw new RuntimeException("Código de validación no valido");
                    }

                    // 1. Verificar que el código no esta usado
                    if (validationCode.getUsado().booleanValue()) {
                        throw new RuntimeException("Código de validación ya fue utilizado");
                    }

                    Instant instant = validationCode.getFechaRegistro().toInstant();
                    LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime expiryDate = localDateTime.plusMinutes(expiryMinute);
                    LocalDateTime currentDate = LocalDateTime.now();
                    // 2. Verificar que el código no haya expirado
                    if (expiryDate.isBefore(currentDate)) {
                        throw new RuntimeException(
                                "El código ha expirado. Por favor, contacte a soporte para un nuevo enlace.");
                    }
                    validationCode.setUsado(true);
                    verificacionCodigoValidacionRepository.save(Objects.requireNonNull(validationCode));

                    String encodedPassword = passwordEncoder.encode(newPassword);

                    usuario.setPassword(encodedPassword);
                    usuarioRepository.save(Objects.requireNonNull(usuario));

                    return true;
                })
                .orElse(false);
    }

    public void resetPassword(String email) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("El correo electrónico es obligatorio");
        }
        email = email.trim();
        Usuario usuario = usuarioRepository.findByCorreo(email);

        if (usuario == null) {
            throw new RuntimeException("No se encontró un usuario con el correo " + email);
        }

        if (usuario.getEstadoRegistro() == null) {
            throw new RuntimeException("El usuario no tiene un estado asignado");
        }

        if (!Usuario.APROBADO.equals(usuario.getEstadoRegistro())) {
            throw new RuntimeException("El usuario no se encuentra aprobado");
        }

        VerificacionCodigoValidacion verificacion = guardarCodigoValidacion(email);

        if (verificacion == null || verificacion.getCodigo() == null || verificacion.getCodigo().isBlank()) {
            throw new RuntimeException("No se pudo generar el código de validación");
        }

        enviarCorreo(usuario, verificacion.getCodigo());
    }

    private VerificacionCodigoValidacion guardarCodigoValidacion(String email) {
        VerificacionCodigoValidacion verificacionCodigoValidacion = new VerificacionCodigoValidacion();
        verificacionCodigoValidacion.setCorreo(email);
        verificacionCodigoValidacion.setCodigo(GeneradorCodigo.generarCodigoAlfanumerico());
        verificacionCodigoValidacion.setUsado(false);
        verificacionCodigoValidacion.setFechaRegistro(new Date());
        verificacionCodigoValidacion.setTipo(VerificacionCodigoValidacion.TIPO_PASSWORD);
        verificacionCodigoValidacionRepository.save(Objects.requireNonNull(verificacionCodigoValidacion));
        return verificacionCodigoValidacion;
    }

    private void enviarCorreo(Usuario usuario, String codigo) {

        if (usuario == null) {
            return;
        }

        if (codigo == null || codigo.isBlank()) {
            return;
        }

        if (!Usuario.APROBADO.equals(usuario.getEstadoRegistro())) {
            return;
        }

        new Thread(() -> {
            try {
                String destinatario = usuario.getCorreo();
                String asunto = "Reestablecimiento de Contraseña – Distrito Innova";
                String correo = obtenerCorreo(usuario, codigo);

                mailService.enviarCorreoHtml(
                        destinatario,
                        asunto,
                        correo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String obtenerCorreo(Usuario usuario, String codigo) {

        StringBuilder sb = new StringBuilder();
        String resetLink = linkAccesoPlataformaAdmin + "/#/set-password?token=" + codigo;

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("    <meta charset=\"UTF-8\">\n");
        sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("    <title>Restablecimiento de Contraseña - Distrito Innova</title>\n");
        sb.append("</head>\n");
        sb.append(
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333333; margin: 0; padding: 20px;\">\n");
        sb.append("\n");
        sb.append(
                "    <div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #dddddd; padding: 20px; border-radius: 8px;\">\n");
        sb.append("        \n");
        sb.append(
                "        <h2 style=\"color: #007bff; border-bottom: 2px solid #007bff; padding-bottom: 10px;\">Solicitud de Restablecimiento de Contraseña</h2>\n");
        sb.append("        \n");
        sb.append("        <p>Estimado/a <strong>").append(usuario.getNombre()).append(" ")
                .append(usuario.getApellido()).append("</strong>,</p>\n");
        sb.append("        \n");
        sb.append(
                "        <p>Hemos recibido una solicitud para restablecer la contraseña de su cuenta en <strong>Distrito Innova</strong>.</p>\n");
        sb.append("        \n");
        sb.append(
                "        <p style=\"margin-bottom: 30px;\">Para establecer una nueva contraseña, haga clic en el siguiente bot&oacute;n. Este enlace expirar&aacute; en <strong>")
                .append(expiryMinute).append(" minutos </strong> por motivos de seguridad:</p>\n");
        sb.append("        \n");
        sb.append("        <p style=\"text-align: center;\">\n");
        sb.append("            <a href=\"").append(resetLink).append(
                "\" style=\"background-color: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;\">Establecer Nueva Contraseña</a>\n");
        sb.append("        </p>\n");
        sb.append("        \n");
        sb.append("        <p style=\"margin-top: 30px; font-size: 14px;\">\n");
        sb.append(
                "            Si usted no solicit&oacute; este restablecimiento, por favor, ignore este correo. Su contrase&ntilde;a actual seguir&aacute; siendo v&aacute;lida.\n");
        sb.append("        </p>\n");
        sb.append("        <p style=\"font-size: 14px; color: #777777;\">\n");
        sb.append(
                "            Si el bot&oacute;n no funciona, puede copiar y pegar el siguiente enlace en su navegador: <br>\n");
        sb.append("            <a href=\"").append(resetLink)
                .append("\" style=\"word-break: break-all; color: #007bff;\">").append(resetLink).append("</a>\n");
        sb.append("        </p>\n");
        sb.append("        \n");
        sb.append("        <hr style=\"border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;\">\n");
        sb.append(
                "        <p style=\"font-size: 14px; text-align: center;\">Atentamente,<br>El equipo de Distrito Innova</p>\n");
        sb.append("\n");
        sb.append("    </div>\n");
        sb.append("\n");
        sb.append("</body>\n");
        sb.append("</html>");

        return sb.toString();

    }

}
