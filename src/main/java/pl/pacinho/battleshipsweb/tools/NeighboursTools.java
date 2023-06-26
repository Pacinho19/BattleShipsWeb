package pl.pacinho.battleshipsweb.tools;

import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.model.dto.Position;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.enums.Direction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class NeighboursTools {

    public static final Function<Position, Position> NORTH_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x(), inputPos.y() - 1);
        return validPosition(pos);
    };

    public static final Function<Position, Position> NORTH_EAST_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x()+1, inputPos.y() - 1);
        return validPosition(pos);
    };

    public static final Function<Position, Position> NORTH_WEST_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x()-1, inputPos.y() - 1);
        return validPosition(pos);
    };
    public static final Function<Position, Position> SOUTH_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x(), inputPos.y() + 1);
        return validPosition(pos);
    };

    public static final Function<Position, Position> SOUTH_EAST_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x()+1, inputPos.y() + 1);
        return validPosition(pos);
    };

    public static final Function<Position, Position> SOUTH_WEST_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x()-1, inputPos.y() + 1);
        return validPosition(pos);
    };

    public static final Function<Position, Position> EAST_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x() + 1, inputPos.y());
        return validPosition(pos);
    };

    public static final Function<Position, Position> WEST_NEIGHBOR = (inputPos) -> {
        Position pos = new Position(inputPos.x() - 1, inputPos.y());
        return validPosition(pos);
    };

    private static Position validPosition(Position pos) {
        if (pos.x() < 0 || pos.x() >= GameConfig.BOARD_SIZE)
            return null;

        if (pos.y() < 0 || pos.y() >= GameConfig.BOARD_SIZE)
            return null;

        return pos;
    }

    public static List<Position> getNeighbours(List<Position> shipPositions) {
        return shipPositions.stream()
                .map(NeighboursTools::getNeighbours)
                .flatMap(List::stream)
                .toList();
    }

    public static List<Position> getNeighbours(Position position) {
        return Arrays.stream(Direction.values())
                .map(direction -> direction.getNeighbour(position))
                .filter(Objects::nonNull)
                .toList();
    }

    public static boolean checkAllNeighboursIsAvailable(List<Position> available, List<Position> neighbours) {
        return new HashSet<>(available).containsAll(neighbours);
    }

    public static boolean isNeighbourForAnyShip(Position pos, Cell[][] playerShipsBoard) {
        return Arrays.stream(playerShipsBoard)
                .flatMap(Arrays::stream)
                .filter(c -> c.getShip() != null)
                .anyMatch(c -> NeighboursTools.getNeighbours(new Position(c.getX(), c.getY()))
                        .stream()
                        .anyMatch(pos1 -> pos1.x() == pos.x() && pos1.y() == pos.y()));
    }
}
