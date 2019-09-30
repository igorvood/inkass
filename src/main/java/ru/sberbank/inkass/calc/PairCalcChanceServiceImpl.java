package ru.sberbank.inkass.calc;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.WayInfoDto;

import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static com.sun.org.apache.xalan.internal.lib.ExsltMath.random;
import static java.util.stream.Collectors.toMap;
import static ru.sberbank.inkass.tune.Const.MAX_MONEY_IN_ANT;
import static ru.sberbank.inkass.tune.Const.WORKING_DAY_LENGTH;

public class PairCalcChanceServiceImpl implements CalcChanceService {


    @Override
    public AntWayDto runOneAnt(AntWayDto antWayDto) {
        Pair<PointDto, PointDto> nextPoint = null;
        do {
            nextPoint = getNextPoint(antWayDto);
        } while (registerPoint(antWayDto, nextPoint));
        return antWayDto;

    }

    /**
     * @param antWayDto
     * @return собирает все точки которые успеем посетить
     */
    private Stream<PointDto> getProbablyPoint(AntWayDto antWayDto) {
        if (!(antWayDto.getMoneyOnThisTrip() < MAX_MONEY_IN_ANT)) {
            return Stream.of(antWayDto.getBankPoint());
        }
        return antWayDto.getNotVisitedPoint()
                .stream()
                //Все точки которые не банк
                .filter(point -> !point.isBase())
                //все точки деньги из которых поместятся сейчас
                .filter(point -> point.getSum() + antWayDto.getMoneyOnThisTrip() <= MAX_MONEY_IN_ANT)
                //все точки до которых успеем доехать, побыть там и если что вернуться в банк до окончания раб дня
                .filter(point ->
                        antWayDto.getTotalTime()
                                + point.getTimeInPoint()
                                + antWayDto.getRoadMap().get(Pair.of(antWayDto.getCurrentPoint(), point)).getTimeInWay()
                                + antWayDto.getRoadMap().get(Pair.of(point, antWayDto.getBankPoint())).getTimeInWay()
                                + antWayDto.getBankPoint().getTimeInPoint()
                                < WORKING_DAY_LENGTH);
    }

    private Pair<PointDto, PointDto> getNextPoint(AntWayDto antWayDto) {

        double sumForCalcChance = 0d;
        final Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap = antWayDto.getRoadMap();
        final PointDto currentPoint = antWayDto.getCurrentPoint();

        Map<Pair<PointDto, PointDto>, Double> possibleWays = getProbablyPoint(antWayDto)
                .map(nextPoint -> Pair.of(currentPoint, nextPoint))
                .map(pointDtoPointDtoPair -> Pair.of(pointDtoPointDtoPair, roadMap.get(pointDtoPointDtoPair).getComplexWeight()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (possibleWays.isEmpty()) {
            final Pair<PointDto, PointDto> of = Pair.of(antWayDto.getCurrentPoint(), antWayDto.getBankPoint());

            try {
                final double complexWeight = roadMap.get(of).getComplexWeight();
                possibleWays.put(of, complexWeight);
            } catch (Exception e) {
                return Pair.of(antWayDto.getCurrentPoint(), antWayDto.getBankPoint());
            }
        }
//            Assert.notEmpty(possibleWays, "possibleWays is empty");

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

    private boolean registerPoint(AntWayDto antWayDto, Pair<PointDto, PointDto> nextPoint) {
        //        delete
        final double sum = antWayDto.getWayPair().stream().map(q -> q.getRight().getTimeInPoint()).mapToDouble(value -> value).sum();

        //        delete

        final PointDto right = nextPoint.getRight();
        Assert.notNull(right, "registerPoint Point is empty");
        Assert.notNull(antWayDto, "registerPoint antWayDto is empty");
        if (nextPoint.getLeft().equals(right))
            return false;
        final WayInfoDto wayInfoDto = antWayDto.getRoadMap().get(nextPoint);
        Assert.notNull(antWayDto, "registerPoint wayInfoDto is empty");
        final double moneyOnThisTrip1 = antWayDto.getMoneyOnThisTrip();
        final double moneyOnThisTrip = right.equals(antWayDto.getBankPoint()) ? 0 : (moneyOnThisTrip1 + right.getSum());
        antWayDto.setMoneyOnThisTrip(moneyOnThisTrip);
        antWayDto.setTotalMoney(antWayDto.getTotalMoney() + right.getSum());
        antWayDto.setTotalTime(antWayDto.getTotalTime() + wayInfoDto.getTimeInWay() + right.getTimeInPoint());
        antWayDto.getWayPair().add(nextPoint);
        Assert.isTrue(right.equals(antWayDto.getBankPoint()) || antWayDto.getNotVisitedPoint().remove(right), () -> String.format("Point all ready visited %s", right));
        if (antWayDto.getMoneyOnThisTrip() > MAX_MONEY_IN_ANT)
            Assert.isTrue(antWayDto.getMoneyOnThisTrip() < MAX_MONEY_IN_ANT, () -> "Max money in ant " + MAX_MONEY_IN_ANT + " but current " + antWayDto.getMoneyOnThisTrip());
        if (antWayDto.getTotalTime() > WORKING_DAY_LENGTH)
            Assert.isTrue((antWayDto.getTotalTime() < WORKING_DAY_LENGTH) || antWayDto.getBankPoint().equals(right), () -> "Max working day for ant " + WORKING_DAY_LENGTH + " but current " + antWayDto.getTotalTime());
        antWayDto.setCurrentPoint(right);
        return true;

    }

}
