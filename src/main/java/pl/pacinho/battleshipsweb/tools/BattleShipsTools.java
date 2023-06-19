package pl.pacinho.battleshipsweb.tools;

import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.model.dto.Position;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.Ship;
import pl.pacinho.battleshipsweb.model.enums.ShipType;
import pl.pacinho.battleshipsweb.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BattleShipsTools {

    public static Cell[][] initFreeBoard() {
        Cell[][] cells = new Cell[GameConfig.BOARD_SIZE][GameConfig.BOARD_SIZE];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell();
            }
        }
        return cells;
    }

    public static Cell[][] initShipsBoard() {
        Cell[][] cells = initFreeBoard();

        List<Position> availableCells = initAvailableCells();
        for (int i = GameConfig.MAX_MASTS_COUNT; i > 0; i--) {
            for (int j = 0; j < GameConfig.MAX_MASTS_COUNT - i + 1; j++) {
                Ship ship = new Ship();

                List<Position> shipPositions = generateShips(i, availableCells);
                removeFromAvailableCells(shipPositions, availableCells);
                ship.setMasts(shipPositions);
                shipPositions.forEach(p -> cells[p.x()][p.y()].setShip(ship));
            }
        }

//        for (int i = 0; i < cells.length; i++) {
//            for (int j = 0; j < cells[i].length; j++) {
//                Cell cell = cells[i][j];
//                if (cell.getShip() == null)
//                    System.out.print("*");
//                else
//                System.out.print(cell.getShip().getMasts().size());
//            }
//            System.out.print("\n");
//        }

        return cells;
    }

    private static void removeFromAvailableCells(List<Position> shipPositions, List<Position> availableCells) {
        availableCells.removeAll(shipPositions);
        availableCells.removeAll(NeighboursTools.getNeighbours(shipPositions));
    }


    private static List<Position> initAvailableCells() {
        return IntStream.range(0, GameConfig.BOARD_SIZE)
                .boxed()
                .map(i -> IntStream.range(0, GameConfig.BOARD_SIZE)
                        .boxed()
                        .map(j -> new Position(i, j))
                        .toList())
                .flatMap(List::stream)
                .sorted(Comparator.comparing(Position::getIdx))
                .collect(Collectors.toList());
    }

    private static List<Position> generateShips(int mastsCunt, List<Position> availableCells) {
        List<Position> shipPositions = new ArrayList<>();
        while (true) {
            shipPositions.clear();
            Position startPosition = availableCells.get(RandomUtils.getInt(0, availableCells.size() - 1));

            if (!isCorrectPosition(availableCells, startPosition)) {
                continue;
            }

            shipPositions.add(startPosition);
            ShipType shipType = ShipType.values()[RandomUtils.getInt(0, ShipType.values().length)];

            Position actualPosition = startPosition;
            for (int i = 1; i < mastsCunt; i++) {
                Position neighbour = shipType.getNeighbour(actualPosition);
                if (neighbour == null || !isCorrectPosition(availableCells, neighbour))
                    break;

                actualPosition = neighbour;
                shipPositions.add(neighbour);
            }

            if (shipPositions.size() == mastsCunt)
                return shipPositions;

        }
    }

    private static boolean isCorrectPosition(List<Position> availableCells, Position position) {
        List<Position> neighbours = NeighboursTools.getNeighbours(position);
        if (!NeighboursTools.checkAllNeighboursIsAvailable(availableCells, neighbours))
            return false;

        return true;
    }
}