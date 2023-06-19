package pl.pacinho.battleshipsweb.model.dto;

import pl.pacinho.battleshipsweb.config.GameConfig;

public record Position(int x, int y) {

    public int getIdx() {
        return (y * GameConfig.BOARD_SIZE) + x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
