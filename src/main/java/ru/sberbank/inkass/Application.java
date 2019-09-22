package ru.sberbank.inkass;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;
import ru.sberbank.inkass.calc.AntWayDto;
import ru.sberbank.inkass.calc.CalcChanceService;
import ru.sberbank.inkass.calc.CalcChanceServiceImpl;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.FillGraphService;
import ru.sberbank.inkass.fill.FillGraphServiceImpl;
import ru.sberbank.inkass.fill.WayInfoDto;

import java.util.Date;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@EnableIntegration
@SpringBootApplication
public class Application {

    public static final Log LOGGER = LogFactory.getLog(Application.class);

    public static final int WORKING_DAY_COUNT = 1_000;
    public static final int ANT_COUNT = 500;
    public static final double MAX_SUM_IN_POINT = 10_000;
    public static final double MAX_TIME_IN_POINT = 200;
    public static final double MAX_TIME_IN_WAY = 2_000;
    public static final double MAX_MONEY_IN_ANT = MAX_SUM_IN_POINT * 2;
    public static final double WORKING_DAY_LENGTH = 5_000;
    static CalcChanceService calcChanceService = new CalcChanceServiceImpl();


    public static void main(String[] args) {

        FillGraphService fillGraphService = new FillGraphServiceImpl();
        final Map<Pair<PointDto, PointDto>, WayInfoDto> fill = fillGraphService.fill(20);

//        fill.forEach((k, v) -> System.out.println(k + v.toString()));
//        System.out.println(fill.size());

        final long timeBeg = new Date().getTime();

        LOGGER.debug("++++++++++++++++++++++++");
//        System.out.println(new Date() + "++++++++++++++++++++++++");
        IntStream.range(0, WORKING_DAY_COUNT)
                .peek(value -> LOGGER.debug("Day num " + value))
                .forEach(value -> IntStream.range(0, ANT_COUNT)
                        .parallel()
                        .mapToObj(i -> new AntWayDto(fill))
                        .map(q -> calcChanceService.calc(q))
                        .collect(toList()));
/*

        final List<AntWayDto> collect = IntStream.range(0, ANT_COUNT)
                .parallel()
                .mapToObj(i -> new AntWayDto(fill))
                .map(q -> calcChanceService.calc(q))
                .collect(toList());
*/

//        calcChanceService.calc(collect.get(0));
        LOGGER.debug("++++++++++++++++++++++++" /*+ collect.size()*/);
        final long l = new Date().getTime() - timeBeg;
        LOGGER.debug("total time " + (double) l / 1000L + " time per 1000 ants " + l / (double) WORKING_DAY_COUNT / 1000L / ((double) ANT_COUNT / 1000L));

//        LOGGER.debug("=====================");
//        List<AntWayDto> antWayDtos = new ArrayList<>();
//        for (int i = 0; i < ANT_COUNT; i++) {
//            antWayDtos.add(new AntWayDto(fill));
//        }
//        AntWayDto antWayDto = new AntWayDto(fill);
//        LOGGER.debug("=====================" + antWayDtos.size());
//        LOGGER.debug(antWayDto);
    }
}
