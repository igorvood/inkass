package ru.sberbank.inkass.fill;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.PointDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toMap;


@Data
public class GraphDto {

    private List<EdgeDto> edgeDtos;

    transient private TreeMap<Pair<PointDto, PointDto>, WayInfoDto> infoDtoTreeMap;

    public GraphDto() {
        this.edgeDtos = new ArrayList<>();
    }

    public TreeMap<Pair<PointDto, PointDto>, WayInfoDto> getInfoDtoTreeMap() {
        if (infoDtoTreeMap == null) {
            final Map<Pair<PointDto, PointDto>, WayInfoDto> collect = edgeDtos.stream()
                    .map(q -> Pair.of(Pair.of(q.getFrom(), q.getTo()), q.getWayInfo()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
            infoDtoTreeMap = new TreeMap<>(collect);
        }

        return infoDtoTreeMap;
    }
}
