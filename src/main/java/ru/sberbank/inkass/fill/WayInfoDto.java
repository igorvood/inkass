package ru.sberbank.inkass.fill;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WayInfoDto {

    private final double timeInWay;

    private double pheromone;

}
