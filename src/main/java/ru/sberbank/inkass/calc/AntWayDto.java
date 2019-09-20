package ru.sberbank.inkass.calc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.sberbank.inkass.dto.PointDto;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
public class AntWayDto {

    private Long totalTime;
    private Long totalMoney;
    private Long moneyOnThisTrip;
    private List<PointDto> way;

    public AntWayDto() {
        this(0L, 0L, 0L, new ArrayList<>());
    }

}
