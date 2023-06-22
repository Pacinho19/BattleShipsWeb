package pl.pacinho.battleshipsweb.tools;

import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.model.entity.Player;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

public class PlayerTools {

    public static Cell[][] getPlayerShootingBoard(LinkedList<Player> players, String name) {
        return getPlayerCell(players, name, Player::getShootingBoard);
    }

    public static Cell[][] getPlayerShipsBoard(LinkedList<Player> players, String name) {
        return getPlayerCell(players, name, Player::getPlayerShipsBoard);
    }

    private static Cell[][] getPlayerCell(LinkedList<Player> players, String name, Function<Player, Cell[][]> func) {
        if (name == null) return null;

        Optional<Player> playerOptional = players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();

        return playerOptional.map(func).orElse(null);
    }

    public static Cell[][] getOponentShipsBoard(LinkedList<Player> players, String name) {
        if (name == null) return null;

        Optional<Player> playerOptional = players.stream()
                .filter(p -> !p.getName().equals(name))
                .findFirst();

        return playerOptional.map(Player::getPlayerShipsBoard).orElse(null);
    }

    public static Integer getPlayerIndex(Game game, String name) {
        return game.getPlayers()
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .map(Player::getIndex)
                .orElseThrow(() -> new IllegalArgumentException("Unknown player: " + name));
    }

    public static String getOponentName(String name, Game game) {
        return game.getPlayers()
                .stream()
                .filter(p -> !p.getName().equals(name))
                .findFirst()
                .map(Player::getName)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find oponent name for player " + name + " in game " + game.getId()));
    }

    public static boolean isCPUTurn(Game game) {
        return game.getPlayers()
                .get(game.getActualPlayer() - 1)
                .isCPU();
    }
}
