package ru.sberbank.inkass.calc;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.WayInfoDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Getter
@ToString
public class AntWayDto {

    private Long totalTime;
    private Long totalMoney;
    private Long moneyOnThisTrip;
    private List<PointDto> way;
    private Set<PointDto> notVisitedPoint;
    private Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap;


    public AntWayDto(Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap) {
        this.totalTime = 0L;
        this.totalMoney = 0L;
        this.moneyOnThisTrip = 0L;
        this.way = new ArrayList<>();
        this.notVisitedPoint =
                roadMap.keySet().stream()
                        .map(Pair::getKey)
                        .collect(Collectors.toSet());
        this.roadMap =
                roadMap.entrySet().stream()
                        .map(e -> Pair.of(
                                e.getKey(),
                                new WayInfoDto(e.getValue().getTimeInWay(),
                                        e.getValue().getPheromone())))
                        .collect(toMap(Pair::getKey, Pair::getValue));

    }

}
