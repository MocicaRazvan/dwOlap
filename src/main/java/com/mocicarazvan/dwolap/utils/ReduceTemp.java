package com.mocicarazvan.dwolap.utils;

import com.mocicarazvan.dwolap.dtos.ChartDataSet;
import com.mocicarazvan.dwolap.dtos.ChartDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReduceTemp<T> implements Transformable<ReduceTemp<T>> {
    private List<T> labels = new ArrayList<>();
    private Map<String, List<Double>> data = new HashMap<>();

    public void appendToMap(String key, Number value) {
        if (value == null) {
            return;
        }
        data.computeIfAbsent(key, _ -> new ArrayList<>()).add(value.doubleValue());
    }

    public List<ChartDataSet> getDataAsChartDto() {
        return data
                .entrySet().stream().reduce(new ArrayList<>(), (acc, el) -> {
                    acc.add(new ChartDataSet(el.getKey(), el.getValue()));
                    return acc;
                }, (acc1, acc2) ->
                {
                    acc1.addAll(acc2);
                    return acc1;
                });
    }

    public ChartDto<T> getChartDto() {
        return new ChartDto<>(labels, getDataAsChartDto());
    }


    public ReduceTemp<T> merge(ReduceTemp<T> other) {
        other.data.forEach((key, value) -> {
            if (data.containsKey(key)) {
                data.get(key).addAll(value);
            } else {
                data.put(key, value);
            }
        });
        this.labels.addAll(other.labels);
        return this;
    }

}
