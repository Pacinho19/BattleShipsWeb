package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class PlayerState {

    private int shootCount;

    @Setter
    private Map<Integer, Long> shipCount;

    public PlayerState(Map<Integer, Long> shipCount) {
        this.shootCount = 0;
        this.shipCount = shipCount;
    }

    public void incrementShotCount() {
        shootCount++;
    }
}
