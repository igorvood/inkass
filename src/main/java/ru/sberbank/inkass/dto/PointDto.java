package ru.sberbank.inkass.dto;


import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class PointDto {
    private final String name;
    //    время инкассации
    private final double timeInPoint;
    //    сумма инкассации
    private final double sum;
    //    расстояния до других точек инкассации, в паре первыое число - расстояние, 2-уровень ферромона
    private Map<PointDto, Pair<Double, Double>> wayOtherPoints;
    // признак того что точка является банком куда нужно все отвезти
    private final boolean isBase;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointDto pointDto = (PointDto) o;
        return Double.compare(pointDto.timeInPoint, timeInPoint) == 0 &&
                Double.compare(pointDto.sum, sum) == 0 &&
                isBase == pointDto.isBase &&
                name.equals(pointDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timeInPoint, sum, isBase);
    }

    @Override
    public String toString() {
        return "PointDto{" +
                "name='" + name + '\'' +
                ", timeInPoint=" + timeInPoint +
                ", sum=" + sum +
                ", isBase=" + isBase +
                '}';
    }
}
