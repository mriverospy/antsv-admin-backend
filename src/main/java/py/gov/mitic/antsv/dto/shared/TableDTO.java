package py.gov.mitic.htv.dto.shared;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TableDTO<T> {

    private List<T> lista;

    private int totalRecords;

}