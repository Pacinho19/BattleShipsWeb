package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Cell {

    private boolean hit;
    private boolean miss;
    private Ship ship;

    public Cell() {
        this.hit = false;
        this.miss = false;
    }

    public Cell(Ship ship) {
        this.ship = ship;
    }
}
