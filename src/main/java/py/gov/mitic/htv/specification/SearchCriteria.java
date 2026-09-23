package py.gov.mitic.htv.specification;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCriteria {

    private String field;
    private SearchOperator operator;
    private String value;
    private String endValue;
    private List<String> values; // Used in case of IN operator

    public SearchCriteria() {
    }

    public SearchCriteria(String field, SearchOperator operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public SearchCriteria(String field, SearchOperator operator, String value, String endValue) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.endValue = endValue;
    }

    public SearchCriteria(String field, SearchOperator operator, List<String> values) {
        this.field = field;
        this.operator = operator;
        this.values = values;
    }

}
