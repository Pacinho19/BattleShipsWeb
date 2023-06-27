package pl.pacinho.battleshipsweb.tools;

import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.model.dto.NewShipDto;
import pl.pacinho.battleshipsweb.model.dto.Position;
import pl.pacinho.battleshipsweb.model.dto.ShotDto;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.GameInfo;
import pl.pacinho.battleshipsweb.model.entity.Ship;
import pl.pacinho.battleshipsweb.model.enums.ShipType;
import pl.pacinho.battleshipsweb.utils.RandomUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BattleShipsTools {

    public static Cell[][] initFreeBoard() {
        Cell[][] cells = new Cell[GameConfig.BOARD_SIZE][GameConfig.BOARD_SIZE];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell(j, i);
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
        //availableCells.removeAll(NeighboursTools.getNeighbours(shipPositions));
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
        List<Position> availableCellsCopy = new ArrayList<>(availableCells);

        while (true) {
            List<Position> shipPositions = new ArrayList<>();

            Position startPosition = availableCellsCopy.get(RandomUtils.getInt(0, availableCellsCopy.size() - 1));

            if (!isCorrectPosition(availableCells, startPosition)) {
                availableCellsCopy.remove(startPosition);
                continue;
            }

            shipPositions.add(startPosition);
            if (mastsCunt == shipPositions.size()) {
                return shipPositions;
            }

            ShipType shipType = ShipType.values()[RandomUtils.getInt(0, ShipType.values().length)];
            List<Position> shipPositions1 = getShipPositions(mastsCunt, availableCells, startPosition, shipType);
            if (shipPositions1.isEmpty())
                shipPositions1 = getShipPositions(mastsCunt, availableCells, startPosition, shipType == ShipType.HORIZONTAL ? ShipType.VERTICAL : ShipType.HORIZONTAL);

            shipPositions.addAll(shipPositions1);

            if (shipPositions.size() == mastsCunt) {
                return shipPositions;
            } else {
                availableCellsCopy.remove(startPosition);
            }
        }
    }

    private static List<Position> getShipPositions(int mastsCunt, List<Position> availableCells, Position startPosition, ShipType shipType) {
        List<Position> out = new ArrayList<>();
        Position actualPosition = startPosition;
        for (int i = 1; i < mastsCunt; i++) {
            Position neighbour = shipType.getNeighbour(actualPosition);
            if (neighbour == null || !isCorrectPosition(availableCells, neighbour))
                break;

            actualPosition = neighbour;
            out.add(neighbour);
        }
        return out;
    }

    private static boolean isCorrectPosition(List<Position> availableCells, Position position) {
        List<Position> neighbours = NeighboursTools.getNeighbours(position);
        return NeighboursTools.checkAllNeighboursIsAvailable(availableCells, neighbours);
    }

    public static void checkShipSunk(Ship ship, Cell[][] opponentShipsBoard) {
        boolean isShipSunk = ship.getMasts()
                .stream()
                .allMatch(p -> {
                    Cell cell = opponentShipsBoard[p.x()][p.y()];
                    return cell.getHit() != null && cell.getHit();
                });
        ship.setSunk(isShipSunk);
    }

    public static void hitNeighboursForDrownedShip(Ship ship, Cell[][] opponentShipsBoard, Cell[][] playershotingBoard) {
        ship.getMasts()
                .stream()
                .map(NeighboursTools::getNeighbours)
                .flatMap(List::stream)
                .forEach(p -> {
                    hitCell(opponentShipsBoard, p);
                    hitCell(playershotingBoard, p);
                });
    }

    private static void hitCell(Cell[][] cells, Position p) {
        if (cells == null)
            return;

        Cell cell = cells[p.x()][p.y()];
        if (cell.getHit() == null && cell.getShip() == null)
            cell.setHit(false);
    }

    public static Map<Integer, Long> getShipCount(Cell[][] playerShipsBoard) {
        return Arrays.stream(playerShipsBoard)
                .flatMap(Arrays::stream)
                .map(Cell::getShip)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.groupingBy(Ship::getMastsCount, Collectors.counting()));

    }

    public static void updateShipsCount(Integer playerIndex, Ship ship, GameInfo gameInfoDto) {
        Map<Integer, Long> shipCount = gameInfoDto.getPlayersState()
                .get(playerIndex)
                .getShipCount();
        shipCount.put(ship.getMastsCount(), shipCount.get(ship.getMastsCount()) - 1);
    }

    public static void incrementShotCount(GameInfo gameInfoDto, Integer playerIndex) {
        gameInfoDto.getPlayersState().get(playerIndex).incrementShotCount();
    }

    public static boolean checkAllShipsIsSunk(Integer playerIndex, GameInfo gameInfoDto) {
        return gameInfoDto.getPlayersState()
                .get(playerIndex)
                .getShipCount()
                .values()
                .stream()
                .reduce(0L, Long::sum) == 0;
    }

    public static int getMaxShipCountByMasts(Integer mastsCount) {
        return GameConfig.MAX_MASTS_COUNT - mastsCount + 1;
    }

    public static Ship placeShip(Cell[][] playerShipsBoard, NewShipDto newShipDto) {
        Ship ship = new Ship();

        List<Position> shipPositions = getNewShipPositions(newShipDto);
        ship.setMasts(shipPositions);
        shipPositions.forEach(p -> playerShipsBoard[p.x()][p.y()].setShip(ship));
        hitNeighboursForDrownedShip(ship, null, playerShipsBoard);
        return ship;
    }

    private static List<Position> getNewShipPositions(NewShipDto newShipDto) {
        final boolean offsetX = newShipDto.shipType() == ShipType.HORIZONTAL;
        return IntStream.range(0, newShipDto.mastsCount())
                .boxed()
                .map(i -> new Position(newShipDto.y() + (!offsetX ? i : 0), newShipDto.x() + (offsetX ? i : 0)))
                .toList();
    }

    public static void clearHits(Cell[][] playerShipsBoard) {
        Arrays.stream(playerShipsBoard)
                .flatMap(Arrays::stream)
                .forEach(c -> c.setHit(null));

    }

    public static void undo(List<Ship> ships, Cell[][] playerShipsBoard) {
        if (ships == null || ships.isEmpty())
            return;

        List<Position> allNeighbors = new ArrayList<>();

        Ship lastShip = ships.get(ships.size() - 1);

        Arrays.stream(playerShipsBoard)
                .flatMap(Arrays::stream)
                .filter(c -> c.getShip() == lastShip)
                .forEach(c -> {
                    allNeighbors.addAll(NeighboursTools.getNeighbours(new Position(c.getX(), c.getY())));
                    c.setShip(null);
                    c.setHit(null);
                });

        allNeighbors
                .forEach(pos -> {
                    if (NeighboursTools.isNeighbourForAnyShip(pos, playerShipsBoard))
                        return;
                    Cell cell = playerShipsBoard[pos.y()][pos.x()];
                    cell.setShip(null);
                    cell.setHit(null);
                });

        ships.remove(lastShip);
    }

    public static void showShips(Cell[][] playerShipsBoard, Cell[][] oponentShootingBoard) {
        Arrays.stream(oponentShootingBoard)
                .flatMap(Arrays::stream)
                .filter(c -> c.getHit() == null)
                .forEach(cell -> {
                    Cell playerCell = playerShipsBoard[cell.getY()][cell.getX()];
                    if (playerCell.getHit() != null || playerCell.getShip() == null)
                        return;

                    cell.setShip(playerCell.getShip());
                });
    }
}