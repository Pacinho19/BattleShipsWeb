package pl.pacinho.battleshipsweb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.enums.GameStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GameDto {

    private String id;
    private GameStatus status;
    private List<String> players;
    private Integer playerIndex;
    private Integer actualPlayer;
    private LocalDateTime startTime;
    private Cell[][] playerBoard;
    private Cell[][] shootingBoard;

    public int getNextPlayer(int offset) {
        int idx = playerIndex + offset;
        if (idx > GameConfig.PLAYERS_COUNT) return idx -  GameConfig.PLAYERS_COUNT;
        return idx;
    }
}