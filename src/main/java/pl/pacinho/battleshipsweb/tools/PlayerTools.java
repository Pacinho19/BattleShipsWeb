package pl.pacinho.battleshipsweb.tools;

import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.model.entity.Player;
import pl.pacinho.battleshipsweb.model.entity.Ship;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static Cell[][] getOponentShipsBoard(Game game, String name) {
        return getPlayerCell(game.getPlayers(), getOponentName(name, game), Player::getPlayerShipsBoard);
    }

    public static Cell[][] getOponentShootingBoard(Game game, String name) {
        return getPlayerCell(game.getPlayers(), getOponentName(name, game), Player::getShootingBoard);
    }

    public static Integer getPlayerIndex(Game game, String name) {
        return getPlayerByName(name, game)
                .getIndex();
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

    public static Player getPlayerByName(String playerName, Game game) {
        return game.getPlayers()
                .stream()
                .filter(p -> p.getName().equals(playerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find oponent name for player " + playerName + " in game " + game.getId()));
    }

    public static boolean allPlayersAreReady(LinkedList<Player> players) {
        return players.stream()
                .allMatch(Player::isReady);
    }

    public static boolean isPlayerReady(Game game, String name) {
        try {
            return getPlayerByName(name, game)
                    .isReady();
        } catch (Exception ex) {
            //Empty
        }
        return false;
    }

    public static int getNextShipManuallyInit(Game game, String name) {
        try {
            Map<Integer, List<Ship>> shipsMap = Arrays.stream(PlayerTools.getPlayerShipsBoard(game.getPlayers(), name))
                    .flatMap(Arrays::stream)
                    .map(Cell::getShip)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.groupingBy(Ship::getMastsCount));

            Map.Entry<Integer, List<Ship>> smallestShip = shipsMap.entrySet().stream()
                    .min(Map.Entry.comparingByKey())
                    .get();

            return smallestShip.getValue().size()== BattleShipsTools.getMaxShipCountByMasts(smallestShip.getKey()) ?  smallestShip.getKey()-1 : smallestShip.getKey();

        } catch (Exception ex) {
            //Empty
        }
        return GameConfig.MAX_MASTS_COUNT;
    }
}
