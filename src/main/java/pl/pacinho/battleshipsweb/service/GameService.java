package pl.pacinho.battleshipsweb.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.model.dto.GameDto;
import pl.pacinho.battleshipsweb.model.dto.ReloadBoardDto;
import pl.pacinho.battleshipsweb.model.dto.ShotAnimationDto;
import pl.pacinho.battleshipsweb.model.dto.ShotDto;
import pl.pacinho.battleshipsweb.model.dto.mapper.GameDtoMapper;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.model.enums.GameStatus;
import pl.pacinho.battleshipsweb.model.enums.GameType;
import pl.pacinho.battleshipsweb.repository.GameRepository;
import pl.pacinho.battleshipsweb.tools.BattleShipsTools;
import pl.pacinho.battleshipsweb.tools.PlayerTools;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameLogicService gameLogicService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<GameDto> getAvailableGames() {
        return gameRepository.getAvailableGames();
    }

    public String newGame(String name, GameType gameType) {
        List<GameDto> activeGames = getAvailableGames();
        if (activeGames.size() >= 10)
            throw new IllegalStateException("Cannot create new Game! Active game count : " + activeGames.size());

        String gameId = gameRepository.newGame(name, gameType);
        if (gameType == GameType.CPU)
            joinGame("CPU", gameId, true);

        simpMessagingTemplate.convertAndSend("/game-created", "");
        return gameId;
    }

    public GameDto findDtoById(String gameId, String name) {
        return GameDtoMapper.parse(gameLogicService.findById(gameId), name);
    }


    public void joinGame(String name, String gameId, boolean isCPU) throws IllegalStateException {
        Game game = gameRepository.joinGame(name, gameId, isCPU);
        if (game.getPlayers().size() == GameConfig.PLAYERS_COUNT) game.setStatus(GameStatus.IN_PROGRESS);
    }

    public boolean canJoin(GameDto game, String name) {
        return game.getPlayers().size() < GameConfig.PLAYERS_COUNT && game.getPlayers().stream().noneMatch(p -> p.equals(name));
    }

    public boolean checkPlayGame(String name, GameDto game) {
        return game.getPlayers()
                .stream()
                .anyMatch(p -> p.equals(name));
    }

    public boolean checkStartGame(String gameId) {
        Game game = gameLogicService.findById(gameId);
        return game.getPlayers().size() == GameConfig.PLAYERS_COUNT;
    }

    public void checkGamePage(GameDto game, String name) {
        if (game.getStatus() == GameStatus.FINISHED)
            throw new IllegalStateException("Game " + game.getId() + " finished!");

        if (game.getStatus() != GameStatus.IN_PROGRESS)
            throw new IllegalStateException("Game " + game.getId() + " not started!");

        if (!checkPlayGame(name, game))
            throw new IllegalStateException("Game " + game.getId() + " in progress! You can't open game page!");
    }

    @SneakyThrows
    public void shot(String name, String gameId, ShotDto shotDto) {
        Game game = gameLogicService.findById(gameId);
        String result = gameLogicService.shot(name, game, shotDto);
        finishRound(game, new ReloadBoardDto(result, getShotAnimationInfo(shotDto, PlayerTools.getOponentName(name, game))));

        if (PlayerTools.isCPUTurn(game)){
            Thread.sleep(1_000);
            shot("CPU", gameId, BattleShipsTools.randomShot(PlayerTools.getPlayerShipsBoard(game.getPlayers(), "CPU")));
        }
    }

    private ShotAnimationDto getShotAnimationInfo(ShotDto shotDto, String oponentName) {
        return new ShotAnimationDto(shotDto, oponentName);
    }

    private void finishRound(Game game, ReloadBoardDto info) {
        simpMessagingTemplate.convertAndSend("/reload-board/" + game.getId(), info);
    }
}