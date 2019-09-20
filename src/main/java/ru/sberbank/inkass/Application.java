package ru.sberbank.inkass;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.FillGraphService;
import ru.sberbank.inkass.fill.FillGraphServiceImpl;
import ru.sberbank.inkass.fill.WayInfoDto;

import java.util.Map;

public class Application {

    public static int MAX_SUM_IN_POINT = 10000;
    public static int MAX_TIME_IN_POINT = 200;
    public static int MAX_TIME_IN_WAY = 2000;


    public static void main(String[] args) {
        FillGraphService fillGraphService = new FillGraphServiceImpl();
        final Map<Pair<PointDto, PointDto>, WayInfoDto> fill = fillGraphService.fill(20);

        fill.forEach((k, v) -> System.out.println(k + v.toString()));
        System.out.println(fill.size());

    }
}
