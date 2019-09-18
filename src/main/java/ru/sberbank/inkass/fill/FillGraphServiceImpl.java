package ru.sberbank.inkass.fill;

import ru.sberbank.inkass.dto.PointDto;

import java.util.*;

public class FillGraphServiceImpl implements FillGraphService {

    private static int maxSumInPoint = 10000;
    private static int maxTimeInPoint = 200;
    private static int maxTimeInWay = 2000;

    public Set<PointDto> fill(int size) {
        Set<PointDto> pointDtos = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            pointDtos.add(PointDto.builder()
                    .isBase(i == 0)
                    .name(i == 0 ? "Bank" : String.format("Point %d", i))
                    .sum(i == 0 ? 0 : Math.random() * maxSumInPoint)
                    .timeInPoint(Math.random() * maxTimeInPoint)
                    .wayOtherPoints(new HashMap<>(size - 1))
                    .build());
        }
        pointDtos.forEach(pointDtoAccept -> pointDtos.stream()
                .filter(pointDto -> !pointDtoAccept.equals(pointDto))
                .forEach(pointDto -> {
                    final double value = Math.random() * maxTimeInWay;
                    pointDtoAccept.getWayOtherPoints().put(pointDto, value);
                    pointDto.getWayOtherPoints().put(pointDtoAccept, value);
                }));
        return pointDtos;
    }
}
