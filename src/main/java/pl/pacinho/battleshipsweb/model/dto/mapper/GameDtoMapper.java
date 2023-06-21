package pl.pacinho.battleshipsweb.model.dto.mapper;

import pl.pacinho.battleshipsweb.model.dto.GameDto;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.model.entity.Player;
import pl.pacinho.battleshipsweb.tools.PlayerTools;

import java.util.*;

public class GameDtoMapper {

    public static GameDto parse(Game game, String name) {
        return GameDto.builder()
                .id(game.getId())
                .players(game.getPlayers().stream().map(Player::getName).toList())
                .playerIndex(
                        getPlayerIndex(game.getPlayers(), name)
                )
                .status(game.getStatus())
                .actualPlayer(game.getActualPlayer())
                .startTime(game.getStartTime())
                .playerBoard(PlayerTools.getPlayerShipsBoard(game.getPlayers(), name))
                .shootingBoard(PlayerTools.getPlayerShootingBoard(game.getPlayers(), name))
                .gameInfoDto(game.getGameInfoDto())
                .build();
    }

    private static Integer getPlayerIndex(LinkedList<Player> players, String name) {
        Optional<Player> playerOpt = players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();

        if (playerOpt.isEmpty()) return null;
        return playerOpt.get()
                .getIndex();
    }


}