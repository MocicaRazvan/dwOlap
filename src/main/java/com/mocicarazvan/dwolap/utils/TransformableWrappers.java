package com.mocicarazvan.dwolap.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

public interface TransformableWrappers {

    static <T> MappableWrapper<T> of(T value) {
        return new MappableWrapper<>(value);
    }

    static DefaultMappableWrapper of() {
        return new DefaultMappableWrapper();
    }

    @Getter
    @RequiredArgsConstructor
    class MappableWrapper<T> {
        private final T value;

        public <R> MappableWrapper<R> map(Function<T, R> mapper) {
            return TransformableWrappers.of(mapper.apply(value));
        }

        public <R> R mapToValue(Function<T, R> mapper) {
            return mapper.apply(value);
        }
    }

    class DefaultMappableWrapper extends MappableWrapper<String> {
        public DefaultMappableWrapper() {
            super("");
        }
    }
}
