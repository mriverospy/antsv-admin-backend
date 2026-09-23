package py.gov.mitic.htv.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from.sender}")
    String mailFromSender;

    public void enviarCorreoHtml(String to, String subject, String htmlBody) {
        try {
            // Crear mensaje de MIME
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(Objects.requireNonNull(to));
            helper.setFrom(Objects.requireNonNull(mailFromSender));
            helper.setSubject(Objects.requireNonNull(subject));

            // Envolver HTML con header y footer
            String header = "<img src=\"cid:encab\" alt=\"Header\" style=\"width:100%;max-width:792px;display:block;margin-bottom:20px;\"/>";
            String footer = "<img src=\"cid:pie\" alt=\"Footer\" style=\"width:100%;max-width:792px;display:block;margin-top:20px;\"/>";
            String wrappedHtml = header + htmlBody + footer;
            helper.setText(wrappedHtml, true);

            // Agregar imagenes embebidas
            if (wrappedHtml.contains("cid:encab")) {
                ClassPathResource headerImg = new ClassPathResource("img/Innova-encab-de-pag.png");
                if (headerImg.exists()) {
                    helper.addInline("encab", headerImg);
                }
            }
            if (wrappedHtml.contains("cid:pie")) {
                ClassPathResource footerImg = new ClassPathResource("img/Innova-pie-de-pag.png");
                if (footerImg.exists()) {
                    helper.addInline("pie", footerImg);
                }
            }

            // Enviar correo
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
