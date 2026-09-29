package py.gov.mitic.htv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.PermisoDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.shared.TableDTO;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.model.Permiso;
import py.gov.mitic.htv.repository.PermisoRepository;
import py.gov.mitic.htv.specification.GenericSpecification;
import py.gov.mitic.htv.specification.SearchCriteria;
import py.gov.mitic.htv.specification.SearchOperator;

/**
 * @autor: Luis Cardozo
 **/
@Service
public class PermisoService extends GenericSpecification<Permiso> {

    @Autowired
    PermisoRepository permisoRepository;

    @Transactional(readOnly = true)
    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id, String nombre, String descripcion) {
        Pageable paging = PageRequest.of(page, pageSize, getSortField(sortAsc, sortField));

        List<SearchCriteria> filters = new ArrayList<>();
        filters.add(new SearchCriteria("idPermiso", SearchOperator.EQUALS, (!Objects.isNull(id) ? id.toString() : "")));
        filters.add(new SearchCriteria("nombre", SearchOperator.LIKE, nombre));
        filters.add(new SearchCriteria("descripcion", SearchOperator.LIKE, descripcion));

        Page<Permiso> pageList = permisoRepository.findAll(getSpecifications(filters, true), paging);

        TableDTO<Permiso> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
        return new ResponseDTO(tableDTO, HttpStatus.OK);
    }

    public ResponseDTO getPermisos() {
        List<Permiso> permisos = permisoRepository.findAllPermiso();
        return new ResponseDTO(permisos, HttpStatus.OK);
    }

    public ResponseDTO save(PermisoDTO dto) {
        Permiso permiso = new Permiso();
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        permisoRepository.save(Objects.requireNonNull(permiso));
        return new ResponseDTO("Permiso creado con éxito", HttpStatus.OK);
    }

    public ResponseDTO update(Long id, PermisoDTO dto) {
        permisoRepository.findById(Objects.requireNonNull(id)).map(permiso -> {
            permiso.setNombre(dto.getNombre());
            permiso.setDescripcion(dto.getDescripcion());
            return permisoRepository.save(Objects.requireNonNull(permiso));
        }).orElseThrow(() -> new BadRequestException("Permiso no existe"));
        return new ResponseDTO("Permiso actualizado con éxito", HttpStatus.OK);
    }

}
