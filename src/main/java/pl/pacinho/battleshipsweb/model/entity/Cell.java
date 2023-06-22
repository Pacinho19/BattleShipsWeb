package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class Cell {

    private Boolean hit;
    private Ship ship;

    private final int x;
    private final int y;
}
