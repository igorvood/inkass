package ru.sberbank.inkass.fill;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sberbank.inkass.dto.PointDto;

@Data
@AllArgsConstructor
public class EdgeDto {

    private PointDto from;
    private PointDto to;
    private WayInfoDto wayInfo;
}
