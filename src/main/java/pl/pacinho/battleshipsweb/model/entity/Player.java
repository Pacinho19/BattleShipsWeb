package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.battleshipsweb.tools.BattleShipsTools;

@Getter
public class Player {

    private final String name;
    private int index;

    @Setter
    private Cell[][] playerShipsBoard;
    private Cell[][] shootingBoard;
    private boolean CPU;

    @Setter
    private boolean ready;

    @Setter
    private Ship lastShipPlacedManually;

    public Player(String name, int index, boolean CPU) {
        this.CPU = CPU;
        this.ready = CPU;
        this.name = name;
        this.index = index;
        this.shootingBoard = BattleShipsTools.initFreeBoard();
        this.playerShipsBoard = BattleShipsTools.initShipsBoard();
    }

}
