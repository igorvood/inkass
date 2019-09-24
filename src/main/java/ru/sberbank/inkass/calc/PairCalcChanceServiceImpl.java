package ru.sberbank.inkass.calc;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.WayInfoDto;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.DoubleStream;

import static com.sun.org.apache.xalan.internal.lib.ExsltMath.random;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static ru.sberbank.inkass.tune.Const.MAX_MONEY_IN_ANT;
import static ru.sberbank.inkass.tune.Const.WORKING_DAY_LENGTH;

public class PairCalcChanceServiceImpl implements CalcChanceService {

    @Override
    public AntWayDto runOneAnt(AntWayDto antWayDto) {
        Pair<PointDto, PointDto> nextPoint = null;
        do {
            final Set<PointDto> probablyPoint = getProbablyPoint(antWayDto);
            nextPoint = getNextPoint(probablyPoint, antWayDto);

        } while (registerPoint(antWayDto, nextPoint));
        return antWayDto;

    }

    private boolean registerPoint(AntWayDto antWayDto, Pair<PointDto, PointDto> nextPoint) {
        final PointDto right = nextPoint.getRight();
        Assert.notNull(right, "registerPoint Point is empty");
        Assert.notNull(antWayDto, "registerPoint antWayDto is empty");
        if (nextPoint.getLeft().equals(right))
            return false;
        final WayInfoDto wayInfoDto = antWayDto.getRoadMap().get(nextPoint);
        Assert.notNull(antWayDto, "registerPoint wayInfoDto is empty");
        final double moneyOnThisTrip = nextPoint.equals(antWayDto.getBankPoint()) ? 0 : (antWayDto.getMoneyOnThisTrip() + right.getSum());
        antWayDto.setMoneyOnThisTrip(moneyOnThisTrip);
        antWayDto.setTotalMoney(antWayDto.getTotalMoney() + right.getSum());
        antWayDto.setTotalTime(antWayDto.getTotalTime() + wayInfoDto.getTimeInWay() + right.getTimeInPoint());
        antWayDto.getWayPair().add(nextPoint);
        Assert.isTrue(right.equals(antWayDto.getBankPoint()) || antWayDto.getNotVisitedPoint().remove(right), () -> String.format("Point all ready visited %s", right));
        Assert.isTrue(antWayDto.getMoneyOnThisTrip() < MAX_MONEY_IN_ANT, () -> "Max money in ant " + MAX_MONEY_IN_ANT + " but current " + antWayDto.getMoneyOnThisTrip());
        if (antWayDto.getTotalTime() < WORKING_DAY_LENGTH)
            Assert.isTrue(antWayDto.getTotalTime() < WORKING_DAY_LENGTH, () -> "Max working day for ant " + WORKING_DAY_LENGTH + " but current " + antWayDto.getTotalTime());
        antWayDto.setCurrentPoint(right);
        return true;

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

    private Pair<PointDto, PointDto> getNextPoint(Set<PointDto> probablyPoint, AntWayDto antWayDto) {

        double sumForCalcChance = 0d;
        final Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap = antWayDto.getRoadMap();
        final PointDto currentPoint = antWayDto.getCurrentPoint();

        final Map<Pair<PointDto, PointDto>, Double> collect = probablyPoint.stream()
                .map(nextPoint -> {
                    final Pair<PointDto, PointDto> of = Pair.of(currentPoint, nextPoint);
                    return Pair.of(of, roadMap.get(of).getComplexWeight());
                })
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));


        final Map<Pair<PointDto, PointDto>, Double> possibleWays =
                antWayDto.getRoadMap().entrySet().stream()
                        // поиск путей
                        .filter(pairWayInfoDto -> pairWayInfoDto.getKey().getLeft().equals(antWayDto.getCurrentPoint()) &&
                                probablyPoint.contains(pairWayInfoDto.getKey().getRight()))
                        // расчет веса пути
                        .map(entryWay -> Pair.of(entryWay.getKey(), entryWay.getValue().getPheromone() * entryWay.getValue().getWeightWay()))
                        // пупутно суммирую все веса
//                        .peek(q -> sumForCalcChance[0] = sumForCalcChance[0] + q.getValue())
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));


        sumForCalcChance = possibleWays.values().stream().flatMapToDouble(DoubleStream::of).sum();

        final double random = random();
        double current = 0;
        Pair<PointDto, PointDto> lastPoint = null;
        // цикл вычислений следущей точки
        for (Map.Entry<Pair<PointDto, PointDto>, Double> p : possibleWays.entrySet()) {
            current = current + p.getValue() / sumForCalcChance;
            if (current > random) {
                return p.getKey();
            }
            lastPoint = p.getKey();
        }

        // если сле точка вычислена(была последней), то вернем ее, иначе едем в банк
        return lastPoint != null ? lastPoint : Pair.of(antWayDto.getCurrentPoint(), antWayDto.getBankPoint());
    }

}
