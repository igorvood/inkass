package ru.sberbank.inkass;

import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.fill.FillGraphService;
import ru.sberbank.inkass.fill.FillGraphServiceImpl;

import java.util.Set;

public class Application {

    public static void main(String[] args) {
        FillGraphService fillGraphService = new FillGraphServiceImpl();
        final Set<PointDto> fill = fillGraphService.fill(2000);
        fill.forEach(q -> System.out.println(q.hashCode()));

        System.out.println(fill);
    }
}
