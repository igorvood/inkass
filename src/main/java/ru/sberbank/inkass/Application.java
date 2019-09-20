package ru.sberbank.inkass;

import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.FillGraphService;
import ru.sberbank.inkass.fill.FillGraphServiceImpl;
import ru.sberbank.inkass.solution.SolutionDto;
import ru.sberbank.inkass.solution.SolutionService;

import java.util.List;
import java.util.Set;

public class Application {

    public static int MAX_SUM_IN_POINT = 10000;
    public static int MAX_TIME_IN_POINT = 200;
    public static int MAX_TIME_IN_WAY = 2000;


    public static void main(String[] args) {
        FillGraphService fillGraphService = new FillGraphServiceImpl();
        final Set<PointDto> fill = fillGraphService.fill(20);

        //fill.forEach(q -> System.out.println(q.hashCode()));

        System.out.println(fill);
        SolutionService solutionService = new SolutionService(fill);

        double lim = 5000;
        //сет путей, который привозят максимально близкую к лимиту сумму обратно в банк.
        // т е каждый элемент сета - один цикл проезда от базы до базы
        List<SolutionDto> solutionSet = solutionService.getSolution(lim);

        //выбор маршрута на следующий круг
        SolutionDto optimalSolution = solutionService.getOptimalSolution();
    }
}
