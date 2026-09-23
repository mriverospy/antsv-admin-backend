package py.gov.mitic.htv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.*;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.shared.TableDTO;
import py.gov.mitic.htv.enums.RolEnum;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.model.Permiso;
import py.gov.mitic.htv.model.Rol;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.repository.RolRepository;
import py.gov.mitic.htv.specification.GenericSpecification;
import py.gov.mitic.htv.specification.SearchCriteria;
import py.gov.mitic.htv.specification.SearchOperator;
import py.gov.mitic.htv.util.UsuarioUtil;

/**
 * @autor: Luis Cardozo
 **/
@Service
public class RolService extends GenericSpecification<Rol> {

    @Autowired
    RolRepository rolRepository;

    @Autowired
    private UsuarioUtil usuarioUtil;

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, String nombre, String descripcion) {
        Pageable paging = PageRequest.of(page, pageSize, getSortField(sortAsc, sortField));

        List<SearchCriteria> filters = new ArrayList<>();
        filters.add(new SearchCriteria("idRol", SearchOperator.EQUALS, (!Objects.isNull(id) ? id.toString() : "")));
        filters.add(new SearchCriteria("nombre", SearchOperator.LIKE, nombre));
        filters.add(new SearchCriteria("descripcion", SearchOperator.LIKE, descripcion));

        Page<Rol> pageList = rolRepository.findAll(getSpecifications(filters, true), paging);
        TableDTO<Rol> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }
    public ResponseDTO getRoles() {
        Usuario currentUser = usuarioUtil.getUsuarioActual();

        boolean esAdminGeneral = currentUser.getRoles().stream()
            .anyMatch(rol -> RolEnum.ADMINISTRADOR_GENERAL.getNombre().equals(rol.getNombre()));

        List<Rol> roles;

        if (!esAdminGeneral) {
            List<Long> idsRoles = this.obtenerIdsRoles();
            roles = rolRepository.findRolesAdministradorOrganizacion(idsRoles);
        } else {
            roles = rolRepository.findAllRole();
        }

        return new ResponseDTO(roles, HttpStatus.OK);
    }

    

    public ResponseDTO save(RolDTO dto) {
        Rol rol = new Rol();
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        rol.setEstado(true);
        rolRepository.save(Objects.requireNonNull(rol));
        return new ResponseDTO("Rol creado con éxito", HttpStatus.OK);
    }

    public ResponseDTO update(Long id, RolDTO dto) {
        rolRepository.findById(Objects.requireNonNull(id)).map(rol -> {
            rol.setNombre(dto.getNombre());
            rol.setDescripcion(dto.getDescripcion());
            return rolRepository.save(Objects.requireNonNull(rol));
        }).orElseThrow(() -> new BadRequestException("Rol no existe"));
        return new ResponseDTO("Rol actualizado con éxito", HttpStatus.OK);
    }

    @Transactional
    public ResponseDTO asociarPermisos(RolPermisoDTO dto) {
        try {
            Optional<Rol> rolOp = rolRepository.findById(Objects.requireNonNull(Objects.requireNonNull(dto.getRol()).getIdRol()));
            if(rolOp.isEmpty()) {
                return new ResponseDTO("Rol no existe", HttpStatus.BAD_REQUEST);
            }

            List<Permiso> permisos = new ArrayList<>();
            Rol rol = rolOp.get();
            dto.getPermisos().stream().map(p -> new Permiso(p.getIdPermiso(), p.getNombre(), p.getDescripcion())).forEach(permiso -> {
                rol.removePermiso(permiso);
                if(permiso != null) permisos.add(permiso);
            });

            rol.setPermisos(permisos);
            rolRepository.save(Objects.requireNonNull(rol));
            return new ResponseDTO("Permisos actualizados con éxito", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
    }

    public ResponseDTO updateStatus(Long id) {
        rolRepository.findById(Objects.requireNonNull(id)).map(rol -> {
            rol.setEstado((rol.getEstado() ? false : true));
            return rolRepository.save(Objects.requireNonNull(rol));
        }).orElseThrow(() -> new BadRequestException("Rol no existe"));
        return new ResponseDTO("Estado del Rol actualizado con éxito", HttpStatus.OK);
    }

    public List<Long> obtenerIdsRoles() {
        List<Long> ids = new ArrayList<>();
        RolEnum[] rolesInteres = {
            RolEnum.EVALUADOR_POSTULACION,
            RolEnum.GESTOR_PROGRAMA,
            RolEnum.MENTOR,
            RolEnum.USUARIO_PUBLICO,
        };

        for (RolEnum rolEnum : rolesInteres) {
            rolRepository.findByNombre(rolEnum.getNombre())
                .ifPresent(rol -> ids.add(rol.getIdRol()));
        }

        return ids;
    }



}
