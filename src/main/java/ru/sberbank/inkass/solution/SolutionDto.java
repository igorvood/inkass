package ru.sberbank.inkass.solution;

import lombok.Getter;
import ru.sberbank.inkass.dto.PointDto;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SolutionDto {
    SortedMap<Integer, PointDto> way = new TreeMap<>();

    @Getter
    double summ = 0;

    double time = 0;
    int numPoints = way.size();

    public SolutionDto(PointDto startPoint) {
        way.put(numPoints, startPoint);
    }


    private void addSum(double toAdd){
        summ += toAdd;
    }

    private void addTime(double toAdd){
        summ += toAdd;
    }

    public void addPoint(PointDto p, double wayToPoint){
        way.put(numPoints, p);
        //добавляем деньги
        addSum(p.getSum());
        //время на точке
        addTime(p.getTimeInPoint());
        //время до точки
        addTime(wayToPoint);
    }
}
