package ru.sberbank.inkass;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.calc.AntWayDto;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.FillGraphService;
import ru.sberbank.inkass.fill.FillGraphServiceImpl;
import ru.sberbank.inkass.fill.WayInfoDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Application {


    public static final int WORKING_DAY_COUNT = 100;
    public static final int ANT_COUNT = 50_000;
    public static final int MAX_SUM_IN_POINT = 10_000;
    public static final int MAX_TIME_IN_POINT = 200;
    public static final int MAX_TIME_IN_WAY = 2_000;
    public static final int MAX_MONAY_IN_ANT = MAX_SUM_IN_POINT * 5;
    public static final int WORKIMG_DAY_LENGTH = 20_000;


    public static void main(String[] args) {
        FillGraphService fillGraphService = new FillGraphServiceImpl();
        final Map<Pair<PointDto, PointDto>, WayInfoDto> fill = fillGraphService.fill(20);

        fill.forEach((k, v) -> System.out.println(k + v.toString()));
        System.out.println(fill.size());

        System.out.println(new Date() + "++++++++++++++++++++++++");
        final List<AntWayDto> collect = IntStream.range(0, ANT_COUNT)
                .parallel()
                .mapToObj(i -> new AntWayDto(fill))
                .collect(toList());
        System.out.println(new Date() + "++++++++++++++++++++++++" + collect.size());

        System.out.println(new Date() + "=====================");
        List<AntWayDto> antWayDtos = new ArrayList<>();
        for (int i = 0; i < ANT_COUNT; i++) {
            antWayDtos.add(new AntWayDto(fill));
        }
        AntWayDto antWayDto = new AntWayDto(fill);
        System.out.println(new Date() + "=====================" + antWayDtos.size());
        System.out.println(antWayDto);

    }
}
