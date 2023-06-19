package pl.pacinho.battleshipsweb.model.enums;

import lombok.RequiredArgsConstructor;
import pl.pacinho.battleshipsweb.model.dto.Position;
import pl.pacinho.battleshipsweb.tools.NeighboursTools;

import java.util.function.Function;

@RequiredArgsConstructor
public enum ShipType {

    HORIZONTAL(NeighboursTools.EAST_NEIGHBOR),
    VERTICAL(NeighboursTools.SOUTH_NEIGHBOR);

    private final Function<Position, Position> neighbourFunc;

    public Position getNeighbour(Position position) {
        return neighbourFunc.apply(position);
    }
}
