package pl.pacinho.battleshipsweb.model.enums;

import lombok.RequiredArgsConstructor;
import pl.pacinho.battleshipsweb.model.dto.Pair;
import pl.pacinho.battleshipsweb.model.dto.Position;
import pl.pacinho.battleshipsweb.tools.NeighboursTools;

import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public enum Direction {

    NORTH(NeighboursTools.NORTH_NEIGHBOR),
    SOUTH(NeighboursTools.SOUTH_NEIGHBOR),
    EAST(NeighboursTools.EAST_NEIGHBOR),
    WEST(NeighboursTools.WEST_NEIGHBOR);

    private final Function<Position, Position> neighbourFunc;

    public Position getNeighbour(Position position) {
        return neighbourFunc.apply(position);
    }
}
