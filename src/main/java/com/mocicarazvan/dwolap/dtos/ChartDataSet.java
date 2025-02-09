package com.mocicarazvan.dwolap.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChartDataSet {
   private String label;
    private  List<Double> data;
}
