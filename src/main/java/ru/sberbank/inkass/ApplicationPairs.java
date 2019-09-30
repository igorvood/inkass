package ru.sberbank.inkass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;
import ru.sberbank.infasrtucture.FileService;
import ru.sberbank.inkass.calc.AntWayDto;
import ru.sberbank.inkass.calc.CalcChanceService;
import ru.sberbank.inkass.calc.PairCalcChanceServiceImpl;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.FillGraphService;
import ru.sberbank.inkass.fill.FillGraphServiceImpl;
import ru.sberbank.inkass.fill.GraphDto;

import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static ru.sberbank.inkass.tune.Const.*;

@EnableIntegration
@SpringBootApplication
public class ApplicationPairs {

    public static final Log LOGGER = LogFactory.getLog(ApplicationPairs.class);
    static CalcChanceService calcChanceService = new PairCalcChanceServiceImpl();


    public static void main(String[] args) {

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GraphDto fill;
//--------------------------------------
        FillGraphService fillGraphService = new FillGraphServiceImpl();
        fill = fillGraphService.fill(GRAPH_SIZE);


        final String s = gson.toJson(fill);
        FileService.write("outGraph.gson", s);
//        ------------------------------------

/*
        final String read = FileService.read("outGraph.gson");
        fill = gson.fromJson(read, GraphDto.class);
*/

        final long timeBeg = new Date().getTime();

        LOGGER.debug("++++++++++++++++++++++++");
        IntStream.range(0, WORKING_DAY_COUNT)
                .peek(value -> LOGGER.debug("Day num " + value))
                .forEach(value -> {

                    final Map<MutablePair<PointDto, PointDto>, DoubleSummaryStatistics> collect = IntStream.range(0, ANT_COUNT)
                            .parallel()
                            .mapToObj(i -> new AntWayDto(fill.getInfoDtoTreeMap()))
                            .map(q -> calcChanceService.runOneAnt(q))

                            .flatMap((Function<AntWayDto, Stream<MutablePair<MutablePair<PointDto, PointDto>, Double>>>) antWayDto ->
                                    antWayDto.getWayPair().stream()
                                            .map(q -> new MutablePair(q, antWayDto.getTotalMoney())))
                            .collect(groupingBy(MutablePair::getLeft, mapping(MutablePair::getRight, summarizingDouble(value1 -> value1))));




/*                    final List<AntWayDto> antDayResult = IntStream.range(0, ANT_COUNT)
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
                    */
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
