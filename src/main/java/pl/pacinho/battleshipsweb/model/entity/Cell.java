package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Cell {

    private Boolean hit;
    private Ship ship;

    public Cell(Ship ship) {
        this.ship = ship;
    }
}
