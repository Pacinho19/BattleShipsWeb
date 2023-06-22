package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class PlayerState {

    private int shotCount;

    @Setter
    private Map<Integer, Long> shipCount;

    public PlayerState(Map<Integer, Long> shipCount) {
        this.shotCount = 0;
        this.shipCount = shipCount;
    }

    public void incrementShotCount() {
        shotCount++;
    }
}
