package pl.pacinho.battleshipsweb.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.battleshipsweb.model.enums.GameStatus;

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

    public Game(String player1) {
        players = new LinkedList<>();
        Player player = new Player(player1, 1);
        players.add(player);
        this.id = UUID.randomUUID().toString();
        this.status = GameStatus.NEW;
        this.startTime = LocalDateTime.now();
        this.actualPlayer = 1;
        this.gameInfoDto = new GameInfo();
        gameInfoDto.addPlayer(player);
    }

}
