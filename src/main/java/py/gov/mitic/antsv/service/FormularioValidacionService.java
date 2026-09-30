package py.gov.mitic.htv.service;

import java.util.*;
import org.springframework.stereotype.Service;
import py.gov.mitic.htv.dto.FormularioDTO;
import py.gov.mitic.htv.dto.FormularioDTO.*;
import py.gov.mitic.htv.exceptions.TramiteException;

@Service
public class FormularioValidacionService {

    public static final Set<String> TIPOS = Set.of(
        "TEXT",
        "TEXTAREA",
        "EMAIL",
        "NUMBER",
        "DATE",
        "BOOLEAN",
        "SELECT",
        "RADIO",
        "MULTISELECT",
        "MAP"
    );

    public void validar(FormularioDTO d) {
        exigir(
            d != null && texto(d.nombre(), 250),
            "El nombre del formulario es obligatorio (máximo 250 caracteres)"
        );
        exigir(
            d.secciones() != null && !d.secciones().isEmpty() && d.secciones().size() <= 30,
            "Defina entre 1 y 30 secciones"
        );
        exigir(
            d.campos() != null && !d.campos().isEmpty() && d.campos().size() <= 200,
            "Defina entre 1 y 200 campos"
        );
        exigir(
            d.grupos() != null &&
                d.grupos().size() <= 30 &&
                d.requisitos() != null &&
                d.requisitos().size() <= 50 &&
                d.reglas() != null &&
                d.reglas().size() <= 100,
            "Colecciones de configuración inválidas"
        );
        var secciones = new HashSet<String>();
        for (var s : d.secciones()) {
            exigir(s != null, "Sección inválida");
            codigo(secciones, s.codigo());
            exigir(texto(s.titulo(), 250), "Título de sección inválido");
        }
        var grupos = new HashMap<String, Grupo>();
        for (var g : d.grupos()) {
            exigir(
                g != null && validoCodigo(g.codigo()) && !grupos.containsKey(g.codigo()),
                "Código de grupo duplicado o inválido"
            );
            exigir(
                secciones.contains(g.seccion()) && texto(g.nombre(), 250),
                "Sección o nombre de grupo inválido"
            );
            exigir(
                g.minimo() >= 0 && g.maximo() >= Math.max(1, g.minimo()) && g.maximo() <= 100,
                "Cantidad de filas inválida (máximo 100)"
            );
            grupos.put(g.codigo(), g);
        }
        var campos = new HashMap<String, Campo>();
        for (var c : d.campos()) {
            exigir(
                c != null &&
                    validoCodigo(c.codigo()) &&
                    !campos.containsKey(c.codigo()) &&
                    !c.codigo().startsWith("grupo_"),
                "Código de campo duplicado o inválido"
            );
            exigir(
                secciones.contains(c.seccion()) &&
                    texto(c.etiqueta(), 250) &&
                    c.tipo() != null &&
                    TIPOS.contains(c.tipo()),
                "Sección, etiqueta o tipo de campo inválido"
            );
            exigir(
                c.grupo() == null ||
                    (grupos.containsKey(c.grupo()) && grupos.get(c.grupo()).seccion().equals(c.seccion())),
                "Grupo ajeno a la sección"
            );
            exigir(
                c.longitudMaxima() == null || (c.longitudMaxima() > 0 && c.longitudMaxima() <= 10000),
                "Longitud máxima inválida"
            );
            exigir(
                c.minimo() == null || c.maximo() == null || c.minimo().compareTo(c.maximo()) <= 0,
                "Rango numérico inválido"
            );
            exigir(c.ayuda() == null || c.ayuda().length() <= 2000, "Ayuda demasiado extensa");
            exigir(c.opciones() != null && c.opciones().size() <= 500, "Opciones inválidas");
            if (Set.of("SELECT", "RADIO", "MULTISELECT").contains(c.tipo())) exigir(
                !c.opciones().isEmpty(),
                "El campo de selección necesita opciones"
            );
            else exigir(c.opciones().isEmpty(), "Este tipo de campo no admite opciones");
            var opciones = new HashSet<String>();
            for (var o : c.opciones()) {
                exigir(o != null, "Opción inválida");
                exigir(
                    o.codigo() != null &&
                        o.codigo().matches("[A-Za-z0-9][A-Za-z0-9_]{0,79}") &&
                        opciones.add(o.codigo()),
                    "Código de opción inválido o duplicado"
                );
                exigir(texto(o.etiqueta(), 250), "Etiqueta de opción inválida");
            }
            campos.put(c.codigo(), c);
        }
        var requisitos = new HashSet<String>();
        for (var r : d.requisitos()) {
            exigir(r != null, "Requisito inválido");
            codigo(requisitos, r.codigo());
            exigir(
                !campos.containsKey(r.codigo()) && texto(r.nombre(), 250),
                "Nombre o código de requisito inválido"
            );
            exigir(
                r.minimo() >= 0 && r.maximo() >= Math.max(1, r.minimo()) && r.maximo() <= 10,
                "Cantidad de archivos inválida (máximo 10)"
            );
            exigir(r.tamanioMaximoMb() > 0 && r.tamanioMaximoMb() <= 20, "Tamaño de archivo entre 1 y 20 MB");
            exigir(
                r.extensiones() != null &&
                    r.extensiones().matches("(pdf|jpg|jpeg|png)(,(pdf|jpg|jpeg|png))*"),
                "Extensiones permitidas: pdf,jpg,jpeg,png"
            );
            exigir(
                r.descripcion() == null || r.descripcion().length() <= 4000,
                "Descripción de requisito demasiado extensa"
            );
        }
        validarFunciones(d);
        // Las condiciones dependen únicamente de campos raíz sin condición propia: no hay ciclos ni orden implícito.
        var destinos = new HashSet<String>();
        for (var r : d.reglas()) {
            exigir(r != null && campos.containsKey(r.origen()), "Campo origen desconocido");
            var origen = campos.get(r.origen());
            exigir(
                origen.grupo() == null &&
                    Set.of("TEXT", "SELECT", "RADIO", "BOOLEAN").contains(origen.tipo()),
                "El origen debe ser un campo raíz de texto, selección o booleano"
            );
            exigir(
                campos.containsKey(r.destino()) || requisitos.contains(r.destino()),
                "Destino desconocido"
            );
            exigir(
                destinos.add(r.destino()) && !r.origen().equals(r.destino()),
                "Solo una condición por destino"
            );
            exigir(
                r.operador() != null &&
                    r.accion() != null &&
                    Set.of("IGUAL", "DISTINTO").contains(r.operador()) &&
                    Set.of("MOSTRAR_SI", "REQUERIR_SI").contains(r.accion()),
                "Regla no soportada"
            );
            exigir(r.valor() != null && r.valor().length() <= 1000, "Valor de comparación inválido");
        }
        for (var r : d.reglas())
            exigir(!destinos.contains(r.origen()), "No se permiten dependencias encadenadas o cíclicas");
    }

    private void validarFunciones(FormularioDTO d) {
        exigir(d.funciones().size() <= 20, "Máximo 20 funciones por formulario");
        var codigos = new HashSet<String>();
        var destinos = new HashSet<String>();
        var entradas = new HashSet<String>();
        var campos = new java.util.HashMap<String, FormularioDTO.Campo>();
        d.campos().forEach(c -> campos.put(c.codigo(), c));
        for (var f : d.funciones()) {
            exigir(f != null && validoCodigo(f.codigo()) && codigos.add(f.codigo()), "Código de función inválido o duplicado");
            exigir("SII_CONSULTAR_PERSONA".equals(f.funcion()) && f.versionFuncion() == 1, "Función o versión no soportada");
            exigir(Set.of("AL_SALIR_DEL_CAMPO", "AL_CAMBIAR_SELECCION", "MANUAL").contains(java.util.Objects.toString(f.evento(), "")), "Evento no soportado");
            var origen = campos.get(f.origen());
            exigir(origen != null && origen.grupo() == null && Set.of("TEXT", "SELECT", "RADIO").contains(origen.tipo()), "El origen debe ser un campo raíz de texto o selección");
            exigir(!"AL_CAMBIAR_SELECCION".equals(f.evento()) || Set.of("SELECT", "RADIO").contains(origen.tipo()), "Seleccione un campo de selección para este evento");
            exigir(f.entradas() != null && f.entradas().size() == 1, "La consulta requiere el número de documento");
            var entrada = f.entradas().getFirst();
            exigir(entrada != null && "numeroDocumento".equals(entrada.parametro()) && f.origen().equals(entrada.campo()), "Vincule numeroDocumento al campo origen");
            entradas.add(entrada.campo());
            exigir(f.salidas() != null && f.salidas().size() == 3, "Mapee nombres, apellidos y fechaNacimiento");
            var atributos = new HashSet<String>();
            for (var salida : f.salidas()) {
                exigir(salida != null && Set.of("nombres", "apellidos", "fechaNacimiento").contains(java.util.Objects.toString(salida.atributo(), "")) && atributos.add(salida.atributo()), "Salida inválida o duplicada");
                String contexto = "Función " + f.codigo() + ", salida " + salida.atributo() + ": ";
                exigir(salida.campo() != null && !salida.campo().isBlank(), contexto + "seleccione un campo destino");
                var destino = campos.get(salida.campo());
                exigir(destino != null, contexto + "el campo destino " + salida.campo() + " no existe");
                exigir(destino.grupo() == null, contexto + "el destino debe estar fuera de un grupo repetible");
                exigir(destinos.add(salida.campo()), contexto + "el campo " + salida.campo() + " ya recibe otra salida; seleccione un campo diferente");
                exigir(destino.tipo().equals(salida.atributo().equals("fechaNacimiento") ? "DATE" : "TEXT"), "Tipo incompatible para " + salida.atributo());
                exigir(Set.of("ENTRADA", "TEXTO").contains(java.util.Objects.toString(salida.presentacion(), "")), "Presentación no soportada");
            }
        }
        exigir(java.util.Collections.disjoint(destinos, entradas), "No se permiten ciclos ni cadenas entre funciones");
        for (var r : d.reglas()) {
            exigir(r == null || !destinos.contains(r.destino()), "Los campos autocompletados no pueden ocultarse ni tener otra condición");
            exigir(r == null || !entradas.contains(r.destino()), "El origen de una función no puede depender de otra condición");
        }
    }

    private void codigo(Set<String> usados, String c) {
        exigir(validoCodigo(c) && usados.add(c), "Código duplicado o inválido: " + c);
    }

    private boolean validoCodigo(String c) {
        return c != null && c.matches("[A-Za-z][A-Za-z0-9_]{0,79}") && !Set.of("constructor", "prototype", "hasOwnProperty").contains(c);
    }

    private boolean texto(String t, int max) {
        return t != null && !t.isBlank() && t.length() <= max;
    }

    public static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new TramiteException(400, mensaje);
    }
}
