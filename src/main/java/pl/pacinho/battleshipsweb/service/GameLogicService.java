package pl.pacinho.battleshipsweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.exception.GameNotFoundException;
import pl.pacinho.battleshipsweb.model.dto.ShootDto;
import pl.pacinho.battleshipsweb.model.entity.Cell;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.repository.GameRepository;
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

    public void shoot(String name, Game game, ShootDto shootDto) {
        Cell[][] playerShootingBoard = PlayerTools.getPlayerShootingBoard(game.getPlayers(), name);
        Cell shootingCell = playerShootingBoard[shootDto.y()][shootDto.x()];

        Cell[][] oponentShipsBoard = PlayerTools.getOponentShipsBoard(game.getPlayers(), name);
        Cell oponentCell = oponentShipsBoard[shootDto.y()][shootDto.x()];

        boolean isShip = oponentCell.getShip() != null;
        shootingCell.setHit(isShip);
        oponentCell.setHit(isShip);
    }
}