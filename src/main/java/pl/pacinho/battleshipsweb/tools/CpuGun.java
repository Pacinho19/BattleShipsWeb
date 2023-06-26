package pl.pacinho.battleshipsweb.tools;

import pl.pacinho.battleshipsweb.model.dto.Position;
import pl.pacinho.battleshipsweb.model.dto.ShotDto;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.enums.ShipType;
import pl.pacinho.battleshipsweb.utils.RandomUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CpuGun {

    private static final Map<String, List<Position>> CPU_NEXT_SHOTS = new HashMap<>();
    private static final Map<String, List<ShotDto>> CPU_LAST_HIT_SHOTS = new HashMap<>();

    public static ShotDto shot(String gameId, Cell[][] shipsBoard) {
        List<Position> cpuNextShots = CPU_NEXT_SHOTS.get(gameId);
        List<ShotDto> cpuLastShot = CPU_LAST_HIT_SHOTS.get(gameId);

        ShotDto shotDto;
        if (cpuNextShots == null)
            shotDto = randomShot(shipsBoard);
        else
            shotDto = smartShot(cpuNextShots, cpuLastShot, shipsBoard, gameId);

        return shotDto;
    }

    private static ShotDto smartShot(List<Position> cpuNextShots, List<ShotDto> cpuLastShots, Cell[][] shipsBoard, String gameId) {
        List<Position> filteredPositions = filterPositions(cpuNextShots, shipsBoard);
        if (cpuLastShots.size() == 1) {
            return shotAtSelected(filteredPositions, gameId);
        }

        ShipType shipType = checkShipType(cpuLastShots);
        List<Position> positions = removeIncorrectPositions(cpuLastShots.get(0), filteredPositions, shipType, shipsBoard);
        return shotAtSelected(positions, gameId);
    }

    private static List<Position> filterPositions(List<Position> cpuNextShots, Cell[][] shipsBoard) {
        return cpuNextShots.stream()
                .filter(p -> isFree(shipsBoard, p))
                .collect(Collectors.toList());
    }

    private static boolean isFree(Cell[][] shipsBoard, Position p) {
        return shipsBoard[p.y()][p.x()].getHit() == null;
    }

    private static ShotDto shotAtSelected(List<Position> positions, String gameId) {
        int shotIdx = positions.size() == 1 ? 0 : RandomUtils.getInt(0, positions.size() - 1);
        Position position = positions.get(shotIdx);
        positions.remove(position);

        CPU_NEXT_SHOTS.put(gameId, positions);
        return new ShotDto(position.x(), position.y());
    }

    private static List<Position> removeIncorrectPositions(ShotDto shotDto, List<Position> cpuNextShots, ShipType shipType, Cell[][] shipsBoard) {
        return cpuNextShots.stream()
                .filter(p -> (shipType == ShipType.VERTICAL ? p.x() == shotDto.x() : p.y() == shotDto.y()) && isFree(shipsBoard, p))
                .collect(Collectors.toList());
    }

    private static ShotDto randomShot(Cell[][] shipsBoard) {
        List<Cell> availablePos = Arrays.stream(shipsBoard)
                .flatMap(Arrays::stream)
                .filter(c -> c.getHit() == null)
                .toList();

        int shotIdx = RandomUtils.getInt(0, availablePos.size() - 1);
        Cell cell = availablePos.get(shotIdx);
        return new ShotDto(cell.getX(), cell.getY());
    }

    public static void clearCpuShot(String gameId) {
        CPU_NEXT_SHOTS.remove(gameId);
        CPU_LAST_HIT_SHOTS.remove(gameId);
    }

    public static void addCpuLastHitShot(String gameId, ShotDto shotDto, Cell[][] playerShootingBoard) {
        CPU_LAST_HIT_SHOTS.computeIfAbsent(gameId, key -> new ArrayList<>())
                .add(shotDto);

        List<Position> cells = CPU_NEXT_SHOTS.computeIfAbsent(gameId, key -> new ArrayList<>());
        cells.addAll(filterNeighbors(shotDto, playerShootingBoard));
    }

    private static List<Position> filterNeighbors(ShotDto shotDto, Cell[][] playerShootingBoard) {
        List<Position> neighbours = NeighboursTools.getNeighbours(new Position(shotDto.x(), shotDto.y()));
        List<Position> out = new ArrayList<>();
        out.addAll(removeIncorrectPositions(shotDto, neighbours, ShipType.HORIZONTAL, playerShootingBoard));
        out.addAll(removeIncorrectPositions(shotDto, neighbours, ShipType.VERTICAL, playerShootingBoard));
        return out;
    }

    private static ShipType checkShipType(List<ShotDto> cpuLastShots) {
        return cpuLastShots.stream()
                .map(ShotDto::x)
                .distinct()
                .count() == 1 ? ShipType.VERTICAL : ShipType.HORIZONTAL;
    }
}
