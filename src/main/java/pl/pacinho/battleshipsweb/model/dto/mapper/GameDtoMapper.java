package pl.pacinho.battleshipsweb.model.dto.mapper;

import pl.pacinho.battleshipsweb.model.dto.GameDto;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.model.entity.Player;

import java.util.*;
import java.util.function.Function;

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
                .playerBoard(getPlayerShipsBoard(game.getPlayers(), name))
                .shootingBoard(getPlayerShootingBoard(game.getPlayers(), name))
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

    private static Cell[][] getPlayerShootingBoard(LinkedList<Player> players, String name) {
        return getPlayerCell(players, name, Player::getShootingBoard);
    }

    private static Cell[][] getPlayerShipsBoard(LinkedList<Player> players, String name) {
        return getPlayerCell(players, name, Player::getPlayerShipsBoard);
    }

    private static Cell[][] getPlayerCell(LinkedList<Player> players, String name, Function<Player, Cell[][]> func) {
        if (name == null) return null;

        Optional<Player> playerOptional = players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();

        return playerOptional.map(func).orElse(null);
    }

}