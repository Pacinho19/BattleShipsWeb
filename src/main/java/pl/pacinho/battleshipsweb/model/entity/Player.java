package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import pl.pacinho.battleshipsweb.tools.BattleShipsTools;

@Getter
public class Player {

    private final String name;
    private int index;
    private Cell[][] playerShipsBoard;
    private Cell[][] shootingBoard;
    private boolean CPU;

    public Player(String name, int index, boolean CPU) {
        this.CPU=CPU;
        this.name = name;
        this.index = index;
        this.shootingBoard = BattleShipsTools.initFreeBoard();
        this.playerShipsBoard = BattleShipsTools.initShipsBoard();
    }

}
