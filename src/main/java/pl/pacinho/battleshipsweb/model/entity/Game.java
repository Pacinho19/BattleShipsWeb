package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.battleshipsweb.model.enums.GameStatus;
import pl.pacinho.battleshipsweb.model.enums.GameType;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;

@Getter
public class Game {

    private String id;
    @Setter
    private GameStatus status;
    private LinkedList<Player> players;
    @Setter
    private int actualPlayer;
    private LocalDateTime startTime;
    private GameInfo gameInfoDto;
    private GameType gameType;
    @Setter
    private boolean shipsManuallyInit;

    public Game(String player1, GameType gameType) {
        players = new LinkedList<>();
        Player player = new Player(player1, 1, false);
        players.add(player);
        this.gameType = gameType;
        this.id = UUID.randomUUID().toString();
        this.status = GameStatus.NEW;
        this.startTime = LocalDateTime.now();
        this.actualPlayer = 1;
        this.gameInfoDto = new GameInfo();
        gameInfoDto.addPlayer(player);

        this.shipsManuallyInit = false;
    }

}
