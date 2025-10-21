package com.grabit.Utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelMapperUtility {
    private static ModelMapper modelMapper;

    private static ModelMapper getModelMapper() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        }
        return modelMapper;
    }

    public static <S, D> D map(final S entity, Class<D> destination) {
        return getModelMapper().map(entity, destination);
    }
}
