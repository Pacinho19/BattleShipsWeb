package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.battleshipsweb.model.dto.Position;

import java.util.List;

@Getter
@Setter
public class Ship {

    private List<Position> masts;
    private boolean sunk;

    public int getMastsCount() {
        return masts.size();
    }
}