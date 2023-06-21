package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import pl.pacinho.battleshipsweb.tools.BattleShipsTools;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GameInfo {

    private Map<Integer, PlayerState> playersState;

    public GameInfo() {
        playersState = new HashMap<>();
    }

    public void addPlayer(Player player) {
        this.playersState.put(player.getIndex(), new PlayerState(BattleShipsTools.getShipCount(player.getPlayerShipsBoard())));
    }
}
