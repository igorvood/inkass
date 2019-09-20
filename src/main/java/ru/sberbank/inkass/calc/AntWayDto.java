package ru.sberbank.inkass.calc;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.WayInfoDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class AntWayDto {

    private Long totalTime;
    private Long totalMoney;
    private Long moneyOnThisTrip;
    private List<PointDto> way;
    private List<PointDto> notVisitedPoint;
    private Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap;


    public AntWayDto(Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap, List<PointDto> notVisitedPoint) {
        this.totalTime = 0L;
        this.totalMoney = 0L;
        this.moneyOnThisTrip = 0L;
        this.way = new ArrayList<>();
        this.notVisitedPoint = notVisitedPoint;
        this.roadMap =
                roadMap.entrySet().stream()
                        .map(e -> Pair.of(
                                e.getKey(),
                                new WayInfoDto(e.getValue().getTimeInWay(),
                                        e.getValue().getPheromone())))
                        .collect(toMap(Pair::getKey, Pair::getValue));

    }

}
