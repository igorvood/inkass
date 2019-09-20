package ru.sberbank.inkass.fill;

import ru.sberbank.inkass.dto.PointDto;

import java.util.Set;

public interface FillGraphService {

    Set<PointDto> fill(int size);

}
