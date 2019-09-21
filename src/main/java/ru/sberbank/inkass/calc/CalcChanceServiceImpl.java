package ru.sberbank.inkass.calc;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sun.org.apache.xalan.internal.lib.ExsltMath.random;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static ru.sberbank.inkass.Application.MAX_MONEY_IN_ANT;
import static ru.sberbank.inkass.Application.WORKING_DAY_LENGTH;

public class CalcChanceServiceImpl implements CalcChanceService {

    @Override
    public void calc(AntWayDto antWayDto) {
        final Set<PointDto> probablyPoint = getProbablyPoint(antWayDto);
        final PointDto nextPoint = getNextPoint(probablyPoint, antWayDto);
        registerPoint(antWayDto, nextPoint);
        System.out.println(nextPoint);
    }

    private void registerPoint(AntWayDto antWayDto, PointDto nextPoint) {
        antWayDto.getWay().add(nextPoint);

    }

    /**
     * @param antWayDto
     * @return собирает все точки которые успеем посетить
     */
    private Set<PointDto> getProbablyPoint(AntWayDto antWayDto) {
        if (antWayDto.getMoneyOnThisTrip() < MAX_MONEY_IN_ANT) {
            final Set<PointDto> collect = antWayDto.getNotVisitedPoint()
                    .stream()
                    //Все точки которые не банк
                    .filter(point -> !point.isBase())
                    //все точки деньги из которых поместятся сейчас
                    .filter(point -> point.getSum() <= MAX_MONEY_IN_ANT - antWayDto.getMoneyOnThisTrip())
                    //все точки до которых успеем доехать, побыть там и если что вернуться в банк до окончания раб дня
                    .filter(point ->
                            antWayDto.getTotalTime()
                                    + point.getTimeInPoint()
                                    + antWayDto.getRoadMap().get(Pair.of(antWayDto.getCurrentPoint(), point)).getTimeInWay()
                                    + antWayDto.getRoadMap().get(Pair.of(point, antWayDto.getBankPoint())).getTimeInWay()
                                    < WORKING_DAY_LENGTH)
                    .collect(toSet());
            return collect;
        }
        final HashSet<PointDto> pointDtos = new HashSet<>();
        pointDtos.add(antWayDto.getBankPoint());
        return pointDtos;
    }

    private PointDto getNextPoint(Set<PointDto> pointDtos, AntWayDto antWayDto) {

        final double[] sumForCalcChance = {0d};
        final Map<Pair<PointDto, PointDto>, Double> possibleWays =
                antWayDto.getRoadMap().entrySet().stream()
                        // поиск путей
                        .filter(pairWayInfoDto -> pairWayInfoDto.getKey().getLeft().equals(antWayDto.getCurrentPoint()) &&
                                pointDtos.contains(pairWayInfoDto.getKey().getRight()))
                        // расчет веса пути
                        .map(entryWay -> Pair.of(entryWay.getKey(), entryWay.getValue().getPheromone() * entryWay.getValue().getWeightWay()))
                        // пупутно суммирую все веса
                        .peek(q -> sumForCalcChance[0] = sumForCalcChance[0] + q.getValue())
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        final double random = random();
        double current = 0;
        PointDto lastPoint = null;
        // цикл вычислений следущей точки
        for (Map.Entry<Pair<PointDto, PointDto>, Double> p : possibleWays.entrySet()) {
            current = current + p.getValue() / sumForCalcChance[0];
            if (current > random) {
                return p.getKey().getRight();
            }
            lastPoint = p.getKey().getRight();
        }

        // если сле точка вычислена(была последней), то вернем ее, иначе едем в банк
        return lastPoint != null ? lastPoint : antWayDto.getBankPoint();
    }
}
