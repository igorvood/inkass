package ru.sberbank.inkass.fill;

import ru.sberbank.inkass.dto.PointDto;

import java.util.ArrayList;
import java.util.List;

import static ru.sberbank.inkass.tune.Const.*;

public class FillGraphServiceImpl implements FillGraphService {

    public GraphDto fill(int size) {

        List<PointDto> pointDtos = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            double sum = i == 0 ? 0 : Math.random() * MAX_SUM_IN_POINT;
            sum = i == 1 ? 100_000_000 : sum;
            pointDtos.add(PointDto.builder()
                    .isBase(i == 0)
                    .name(i == 0 ? "Bank" : String.format("Point %d", i))
                    .sum(sum)
                    .timeInPoint(Math.random() * MAX_TIME_IN_POINT)
                    .build());
        }


        GraphDto wayInfoDtoMap = new GraphDto();
        final List<EdgeDto> edgeDtos = wayInfoDtoMap.getEdgeDtos();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                final WayInfoDto wayInfoDto = (i == 0 && j == 1) ? new WayInfoDto(1, 1L) : new WayInfoDto(Math.random() * MAX_TIME_IN_WAY, 1L);
                edgeDtos.add(new EdgeDto(pointDtos.get(i), pointDtos.get(j), wayInfoDto));
                edgeDtos.add(new EdgeDto(pointDtos.get(j), pointDtos.get(i), wayInfoDto));
            }
        }

        return wayInfoDtoMap;
    }
}
