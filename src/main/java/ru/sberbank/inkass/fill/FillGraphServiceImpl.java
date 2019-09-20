package ru.sberbank.inkass.fill;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static ru.sberbank.inkass.Application.*;

public class FillGraphServiceImpl implements FillGraphService {

    public Set<PointDto> fill(int size) {
        Set<PointDto> pointDtos = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            pointDtos.add(PointDto.builder()
                    .isBase(i == 0)
                    .name(i == 0 ? "Bank" : String.format("Point %d", i))
                    .sum(i == 0 ? 0 : Math.random() * MAX_SUM_IN_POINT)
                    .timeInPoint(Math.random() * MAX_TIME_IN_POINT)
                    .wayOtherPoints(new HashMap<>(size - 1))
                    .build());
        }
        pointDtos.forEach(pointDtoAccept -> pointDtos.stream()
                .filter(pointDto -> !pointDtoAccept.equals(pointDto))
                .forEach(pointDto -> {
                    final double value = Math.random() * MAX_TIME_IN_WAY;
                    pointDtoAccept.getWayOtherPoints().put(pointDto, Pair.of(value, 1d));
                    pointDto.getWayOtherPoints().put(pointDtoAccept, Pair.of(value, 1d));
                }));
        return pointDtos;
    }
}
