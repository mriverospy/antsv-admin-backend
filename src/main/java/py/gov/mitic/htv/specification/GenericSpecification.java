package py.gov.mitic.htv.specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

public class GenericSpecification<T> {

    public Specification<T> getSpecifications(List<SearchCriteria> filter, boolean isAnd) {
        Specification<T> specification = Specification.where(setSpecifications(filter.remove(0)));
        for (SearchCriteria input : filter) {
            specification = isAnd 
        		? specification.and(setSpecifications(input))
        		: specification.or(setSpecifications(input));
        }
        return specification;
    }

	public  Predicate equal(CriteriaBuilder cb, Expression<?> field, String value) {
        return ObjectUtils.isEmpty(value)
                ? cb.conjunction()
                : cb.equal(field, castType(field.getJavaType(), value.trim()));
    }

    public  Predicate notEqual(CriteriaBuilder cb, Expression<?> field, String value) {
        return ObjectUtils.isEmpty(value)
                ? cb.conjunction()
                : cb.notEqual(field, castType(field.getJavaType(), value.trim()));
    }

    public  Predicate like(CriteriaBuilder cb, Expression<String> field, String value) {
        return ObjectUtils.isEmpty(value)
                ? cb.conjunction()
                : cb.like(cb.lower(field), "%"+value.toLowerCase().trim()+"%");
    }

    @SuppressWarnings("unchecked")
    public Predicate in(CriteriaBuilder cb, Expression<?> field, List<Object> values){
        return ObjectUtils.isEmpty(values)
                ? cb.conjunction()
                : ((CriteriaBuilder.In<Object>) cb.in(field)).value(castType(field.getJavaType(), values));
    }

    public Predicate between(CriteriaBuilder cb, Expression<String> field, String firstValue, String endValue){
         return ObjectUtils.isEmpty(firstValue) || ObjectUtils.isEmpty(endValue)
                 ? cb.conjunction()
                 : cb.between(field, firstValue, endValue);
    }

    @NonNull
    public Sort getSortField(boolean sortAsc, String sortField) {
        if (sortAsc) {
            return Sort.by(sortField).ascending();
        } else {
            return Sort.by(sortField).descending();
        }
    }


    @SuppressWarnings("unchecked") // Casts necesarios por diseño de JPA Criteria API
    public Specification<T> setSpecifications(SearchCriteria filter) {
        return (root, query, cb) -> {
            switch (filter.getOperator()) {
                case EQUALS:
                    return equal(cb, getFieldExpression(root, filter.getField()), filter.getValue().trim());

                case NOT_EQ:
                    return notEqual(cb, getFieldExpression(root, filter.getField()), filter.getValue().trim());

                case LIKE:
                    return like(cb, (Expression<String>) getFieldExpression(root, filter.getField()), filter.getValue().trim());

                case IN:
                    return in(cb, getFieldExpression(root, filter.getField()), Collections.singletonList(filter.getValues()));

                case IS_NULL:
                    return cb.isNull(getFieldExpression(root, filter.getField()));

                case IS_NOT_NULL:
                    return cb.isNotNull(getFieldExpression(root, filter.getField()));

                case GREATER_THAN:
                    return ObjectUtils.isEmpty(filter.getValue())
                            ? cb.conjunction()
                            : cb.gt((Expression<Number>) getFieldExpression(root, filter.getField()),
                            (Number) castType(getFieldExpression(root, filter.getField()).getJavaType(), filter.getValue()));

                case LESS_THAN:
                    return ObjectUtils.isEmpty(filter.getValue())
                            ? cb.conjunction()
                            : cb.lt((Expression<Number>) getFieldExpression(root, filter.getField()),
                            (Number) castType(getFieldExpression(root, filter.getField()).getJavaType(), filter.getValue()));

                case BETWEEN:
                    return between(cb, (Expression<String>) getFieldExpression(root, filter.getField()),
                            filter.getValue().trim(), filter.getEndValue().trim());

                default:
                    throw new RuntimeException("Operación no soportada");
            }
        };
    }

    // Método auxiliar para manejar campos anidados
    private Expression<?> getFieldExpression(Root<?> root, String fieldName) {
        if (fieldName.contains(".")) {
            // Campo anidado: dividir por puntos y navegar
            String[] fieldParts = fieldName.split("\\.");
            Path<?> path = root.get(fieldParts[0]);

            for (int i = 1; i < fieldParts.length; i++) {
                path = path.get(fieldParts[i]);
            }
            return path;
        } else {
            // Campo simple: acceso directo
            return root.get(fieldName);
        }
    }

	private static Object castType(Class<?> fieldType, String value) {
        if(fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);

        } else if(fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);

        } else if(fieldType.isAssignableFrom(Long.class)) {
            return Long.valueOf(value);

        } else if(fieldType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);

        } else if(fieldType.isAssignableFrom(Date.class)) {
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Object castType(Class<?> fieldType, List<?> value) {
        List<Object> lists = new ArrayList<>();
        for (Object obj : value) {
            lists.add(castType(fieldType, (String) obj));
        }
        return lists;
    }

}
