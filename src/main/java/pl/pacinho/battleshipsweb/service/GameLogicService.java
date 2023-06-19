package pl.pacinho.battleshipsweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pacinho.battleshipsweb.exception.GameNotFoundException;
import pl.pacinho.battleshipsweb.model.dto.ShootDto;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.repository.GameRepository;
import pl.pacinho.battleshipsweb.tools.BattleShipsTools;
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

    public String shoot(String name, Game game, ShootDto shootDto) {
        Cell[][] playerShootingBoard = PlayerTools.getPlayerShootingBoard(game.getPlayers(), name);
        Cell shootingCell = playerShootingBoard[shootDto.y()][shootDto.x()];

        Cell[][] opponentShipsBoard = PlayerTools.getOponentShipsBoard(game.getPlayers(), name);
        Cell opponentCell = opponentShipsBoard[shootDto.y()][shootDto.x()];

        boolean isShip = opponentCell.getShip() != null;
        shootingCell.setHit(isShip);
        opponentCell.setHit(isShip);

        if (isShip) {
            BattleShipsTools.checkShipSunk(opponentCell.getShip(), opponentShipsBoard);
            return "Player " + name + " hit " + opponentCell.getShip().getMasts().size() + "-masts ship"
                    + " on X" + shootDto.x()
                    + ", Y" + shootDto.y()
                    + (opponentCell.getShip().isSunk() ? " and drowned him!" : "");
        }


        return "Player " + name + " miss his shot"
                + " on X" + shootDto.x()
                + ", Y" + shootDto.y();
    }
}