package py.gov.mitic.htv.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.MetodoRegistroDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.shared.TableDTO;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.model.MetodoRegistro;
import py.gov.mitic.htv.repository.MetodoRegistroRepository;
import py.gov.mitic.htv.specification.GenericSpecification;
import py.gov.mitic.htv.specification.SearchCriteria;
import py.gov.mitic.htv.specification.SearchOperator;

import java.net.UnknownHostException;
import java.util.*;
import java.util.Objects;
/**
 * @autor: Gustavo Quintana
 **/
@Service
public class MetodoRegistroService extends GenericSpecification<MetodoRegistro> {

	@Autowired
	MetodoRegistroRepository metodoRegistroRepository;

	@Autowired
	AuditoriaService auditoriaService;

	ModelMapper modelMapper = new ModelMapper();

	public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, String codigo, String nombre, Boolean estado) {
		Pageable paging = PageRequest.of(page, pageSize, getSortField(sortAsc, sortField));

		List<SearchCriteria> filters = new ArrayList<>();

		if(Objects.nonNull(codigo)){
			filters.add(new SearchCriteria("codigo", SearchOperator.EQUALS, String.valueOf(codigo)));
		}

		if(Objects.nonNull(nombre)){
			filters.add(new SearchCriteria("nombre", SearchOperator.EQUALS, String.valueOf(nombre)));
		}

		if(Objects.nonNull(estado)){
			filters.add(new SearchCriteria("estado", SearchOperator.EQUALS, String.valueOf(estado)));
		}


		Page<MetodoRegistro> pageList = metodoRegistroRepository.findAll(!filters.isEmpty() ? getSpecifications(filters, true) : null, paging);
		TableDTO<MetodoRegistro> tableDTO = new TableDTO<>();
		tableDTO.setLista(pageList.getContent());
		tableDTO.setTotalRecords((int) pageList.getTotalElements());
		return new ResponseDTO(tableDTO, HttpStatus.OK);
	}

	@Transactional
	public ResponseDTO save(MetodoRegistroDTO dto) {
		MetodoRegistro metodoRegistro = modelMapper.map(dto, MetodoRegistro.class);
		metodoRegistroRepository.save(Objects.requireNonNull(metodoRegistro));
		return new ResponseDTO("Metodo Registro creado con éxito", HttpStatus.OK);
	}


	@Transactional
	public ResponseDTO update(Long id, MetodoRegistroDTO dto) {
		MetodoRegistro guardado = metodoRegistroRepository.findById(Objects.requireNonNull(id))
				.orElseThrow(() -> new BadRequestException("Metodo Registro no encontrada"));

		// Mantenemos los datos inmutables
		MetodoRegistro actualizada = modelMapper.map(dto, MetodoRegistro.class);
		actualizada.setIdMetodoRegistro(guardado.getIdMetodoRegistro());
		metodoRegistroRepository.save(Objects.requireNonNull(actualizada));

		return new ResponseDTO("Metodo Registro actualizado con éxito", HttpStatus.OK);
	}

	/**
	 * Borrado lógico de persona
	 * @param id
	 * @return
	 */
	public ResponseDTO delete(Long id) {
		metodoRegistroRepository.findById(Objects.requireNonNull(id)).map(metodoRegistro -> {
			try {
				auditoriaService.auditar("ELIMINAR",
						Objects.nonNull(metodoRegistro) ? metodoRegistro.getIdMetodoRegistro().toString():null,
						Objects.nonNull(metodoRegistro) ? metodoRegistro:null,
						null, "metodo_registro",
						"/metodo-registro/delete/{id}", "/delete", "metodo-registro/delete", null, null);
			} catch (UnknownHostException e) { e.printStackTrace();}
			metodoRegistroRepository.delete(Objects.requireNonNull(metodoRegistro));
			return metodoRegistro;

		}).orElseThrow(() -> new BadRequestException("Metodo Registro no existe"));
		return new ResponseDTO("Metodo Registro eliminado con éxito", HttpStatus.OK);
	}


}