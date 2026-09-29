package py.gov.mitic.htv.util;

import org.modelmapper.ModelMapper;

import java.util.List;

public class MapperUtil {

    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .toList();
    }

    public static <S, T> T map(S source, Class<T> targetClass, ModelMapper modelMapper) {
        return modelMapper.map(source, targetClass);
    }

}
