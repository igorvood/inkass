package ru.sberbank.inkass.solution;

import ru.sberbank.inkass.dto.PointDto;

import java.util.*;

public class SolutionService {

    private List<PointDto> points;
    private PointDto bank;

    public SolutionService(List<PointDto> points) {
        this.points = points;
    }

    public List<SolutionDto> getSolution(double lim){
        List<SolutionDto> res = new ArrayList<>();

        PointDto bank = points.get(0);

        Map<PointDto, Double> wayOtherPoints = bank.getWayOtherPoints();

        addNewLvl(wayOtherPoints, res);

        return res;
    }

    private void addNewLvl(Map<PointDto, Double> wayOtherPoints, List<SolutionDto> res) {

        //тут надо сделать, чтобы при превышении лимита последней точкой становился банк и этот путь завершался
        //этот метод еще не разрабатывался

      //  for (PointDto pointDto : wayOtherPoints.keySet()) {

        //первая точка пути всегда банк, без времени ожидания и суммы, выезжаем пустые и без задержек
      //      SolutionDto solutionDto = new SolutionDto(bank);
      //      solutionDto.addPoint(pointDto, wayOtherPoints.get(pointDto));
      //      res.add(solutionDto);
      //  }


    }


    public SolutionDto getOptimalSolution(){
        return null;
    }
}
