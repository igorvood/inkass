package ru.sberbank.inkass.fill;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;

import java.util.Map;

public interface FillGraphService {

    Map<Pair<PointDto, PointDto>, WayInfoDto> fill(int size);

}
