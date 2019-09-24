package ru.sberbank.inkass.fill;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.sberbank.inkass.tune.Const.*;

public class FillGraphServiceImpl implements FillGraphService {

    public Map<Pair<PointDto, PointDto>, WayInfoDto> fill(int size) {

        List<PointDto> pointDtos = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            pointDtos.add(PointDto.builder()
                    .isBase(i == 0)
                    .name(i == 0 ? "Bank" : String.format("Point %d", i))
                    .sum(i == 0 ? 0 : Math.random() * MAX_SUM_IN_POINT)
                    .timeInPoint(Math.random() * MAX_TIME_IN_POINT)
                    .build());
        }

        Map<Pair<PointDto, PointDto>, WayInfoDto> wayInfoDtoMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                final WayInfoDto wayInfoDto = new WayInfoDto(Math.random() * MAX_TIME_IN_WAY, 1L);
                wayInfoDtoMap.put(Pair.of(pointDtos.get(i), pointDtos.get(j)), wayInfoDto);
                wayInfoDtoMap.put(Pair.of(pointDtos.get(j), pointDtos.get(i)), wayInfoDto);
            }
        }

        return wayInfoDtoMap;
    }
}
