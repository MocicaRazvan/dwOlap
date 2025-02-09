package com.mocicarazvan.dwolap.utils;

import java.util.function.Function;

public interface Transformable<T extends Transformable<T>> {

    @SuppressWarnings("unchecked")
    default <R> R map(Function<T, R> mapper) {
        return mapper.apply((T) this);
    }


}