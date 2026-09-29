package py.gov.mitic.htv.repository.projections;

/**
 * Proyección DTO basada en interfaz
 * Enfocado a las consultas personalizadas
 * @autor: Luis Cardozo
 **/
public interface UsuarioPermisoDTO {
	
    Long getIdUsuario();
    
    Long getIdRol();
    
    Long getIdPermiso();
    
    String getPermiso();
    
}
