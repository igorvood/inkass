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

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

@EnableIntegration
@SpringBootApplication
public class Application {

    public static final Log LOGGER = LogFactory.getLog(Application.class);
    public static final int GRAPH_SIZE = 50;

    public static final int WORKING_DAY_COUNT = 1_000;

    public static final int ANT_COUNT = GRAPH_SIZE * 1000;

    public static final double MAX_SUM_IN_POINT = 10_000;
    public static final double MAX_TIME_IN_POINT = 200;
    public static final double MAX_TIME_IN_WAY = 2_000;
    public static final double MAX_MONEY_IN_ANT = MAX_SUM_IN_POINT * 2;
    public static final double WORKING_DAY_LENGTH = 5_000;
    static CalcChanceService calcChanceService = new CalcChanceServiceImpl();


    public static void main(String[] args) {

        FillGraphService fillGraphService = new FillGraphServiceImpl();
        final Map<Pair<PointDto, PointDto>, WayInfoDto> fill = fillGraphService.fill(GRAPH_SIZE);

//        fill.forEach((k, v) -> System.out.println(k + v.toString()));
//        System.out.println(fill.size());

        final long timeBeg = new Date().getTime();

        LOGGER.debug("++++++++++++++++++++++++");
//        System.out.println(new Date() + "++++++++++++++++++++++++");
        IntStream.range(0, WORKING_DAY_COUNT)
                .peek(value -> LOGGER.debug("Day num " + value))
                .forEach(value -> {
                    final List<AntWayDto> antDayResult = IntStream.range(0, ANT_COUNT)
                            .parallel()
                            .mapToObj(i -> new AntWayDto(fill))
                            .map(q -> calcChanceService.runOneAnt(q))

                            .collect(toList());

                    final Map<Pair<PointDto, PointDto>, DoubleSummaryStatistics> collect = antDayResult.parallelStream()
                            .map(q -> Pair.of(q.getWay(), q.getTotalMoney()))
                            .map(listDoublePair -> {
                                final List<PointDto> way = listDoublePair.getLeft();
                                List<Pair<Pair<PointDto, PointDto>, Double>> pairs = new ArrayList<>(way.size() - 1);
                                for (int i = 0; i < way.size() - 1; i++) {
                                    pairs.add(Pair.of(
                                            Pair.of(way.get(i), way.get(i + 1)), listDoublePair.getRight()
                                    ));
                                }
                                return pairs;
                            })
                            .flatMap(Collection::stream)

                            .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, summarizingDouble(value1 -> {
                                return value1;
                            }))));
/*
                    LOGGER.debug(collect.size());
                    final Map<Pair<PointDto, PointDto>, List<Double>> collect2 = antDayResult.parallelStream()
                            .map(q -> Pair.of(q.getWay(), q.getTotalMoney()))
                            .map(listDoublePair -> {
                                final List<PointDto> way = listDoublePair.getLeft();
                                List<Pair<Pair<PointDto, PointDto>, Double>> pairs = new ArrayList<>(way.size() - 1);
                                for (int i = 0; i < way.size() - 1; i++) {
                                    pairs.add(Pair.of(
                                            Pair.of(way.get(i), way.get(i + 1)), listDoublePair.getRight()
                                    ));
                                }
                                return pairs;
                            })
                            .flatMap(Collection::stream)

                            .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, toList())));
                    LOGGER.debug(collect2.size());
*/

                    LOGGER.debug(collect.size());
                });
/*

        final List<AntWayDto> collect = IntStream.range(0, ANT_COUNT)
                .parallel()
                .mapToObj(i -> new AntWayDto(fill))
                .map(q -> calcChanceService.runOneAnt(q))
                .collect(toList());
*/

//        calcChanceService.runOneAnt(collect.get(0));
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
