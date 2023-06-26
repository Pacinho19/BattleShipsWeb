package pl.pacinho.battleshipsweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pacinho.battleshipsweb.exception.GameNotFoundException;
import pl.pacinho.battleshipsweb.model.dto.ShotDto;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.model.enums.GameStatus;
import pl.pacinho.battleshipsweb.repository.GameRepository;
import pl.pacinho.battleshipsweb.tools.BattleShipsTools;
import pl.pacinho.battleshipsweb.tools.CpuGun;
import pl.pacinho.battleshipsweb.tools.PlayerTools;


@RequiredArgsConstructor
@Service
public class GameLogicService {

    private final GameRepository gameRepository;

    public Game findById(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId))
                ;
    }

    public void nextPlayer(Game game) {
        if (game.getActualPlayer() == 1)
            game.setActualPlayer(2);
        else
            game.setActualPlayer(1);
    }

    public String shot(String name, Game game, ShotDto shotDto) {
        Cell[][] playerShootingBoard = PlayerTools.getPlayerShootingBoard(game.getPlayers(), name);
        Cell shotingCell = playerShootingBoard[shotDto.y()][shotDto.x()];

        Cell[][] opponentShipsBoard = PlayerTools.getOponentShipsBoard(game.getPlayers(), name);
        Cell opponentCell = opponentShipsBoard[shotDto.y()][shotDto.x()];

        boolean isShip = opponentCell.getShip() != null;
        shotingCell.setHit(isShip);
        opponentCell.setHit(isShip);

        if (PlayerTools.isCPUTurn(game) && isShip) {
            CpuGun.addCpuLastHitShot(game.getId(), shotDto, playerShootingBoard);
        }

        BattleShipsTools.incrementShotCount(game.getGameInfoDto(), PlayerTools.getPlayerIndex(game, name));

        if (isShip) {
            BattleShipsTools.checkShipSunk(opponentCell.getShip(), opponentShipsBoard);

            if (opponentCell.getShip().isSunk()) {
                CpuGun.clearCpuShot(game.getId());
                BattleShipsTools.hitNeighboursForDrownedShip(opponentCell.getShip(), opponentShipsBoard, playerShootingBoard);
                BattleShipsTools.updateShipsCount(PlayerTools.getPlayerIndex(game, name), opponentCell.getShip(), game.getGameInfoDto());
            }

            if (BattleShipsTools.checkAllShipsIsSunk(PlayerTools.getPlayerIndex(game, name), game.getGameInfoDto())) {
                game.setStatus(GameStatus.FINISHED);
                return "Player " + name + " win the game !";
            }

            return "Player " + name + " hit " + opponentCell.getShip().getMasts().size() + "-masts ship"
                    + " on X" + shotDto.x()
                    + ", Y" + shotDto.y()
                    + (opponentCell.getShip().isSunk() ? " and drowned him!" : "");
        }

        nextPlayer(game);
        return "Player " + name + " miss his shot"
                + " on X" + shotDto.x()
                + ", Y" + shotDto.y();
    }
}