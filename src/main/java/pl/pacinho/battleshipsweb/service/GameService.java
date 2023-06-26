package pl.pacinho.battleshipsweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.pacinho.battleshipsweb.config.GameConfig;
import pl.pacinho.battleshipsweb.model.dto.*;
import pl.pacinho.battleshipsweb.model.dto.mapper.GameDtoMapper;
import pl.pacinho.battleshipsweb.model.entity.Game;
import pl.pacinho.battleshipsweb.model.entity.Player;
import pl.pacinho.battleshipsweb.model.entity.Ship;
import pl.pacinho.battleshipsweb.model.enums.GameStatus;
import pl.pacinho.battleshipsweb.model.enums.GameType;
import pl.pacinho.battleshipsweb.repository.GameRepository;
import pl.pacinho.battleshipsweb.tools.BattleShipsTools;
import pl.pacinho.battleshipsweb.tools.CpuGun;
import pl.pacinho.battleshipsweb.tools.PlayerTools;
import pl.pacinho.battleshipsweb.utils.SleepUtils;

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
        if (game.getPlayers().size() == GameConfig.PLAYERS_COUNT) game.setStatus(GameStatus.INIT_SHIPS);
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

        if (game.getStatus() != GameStatus.IN_PROGRESS && game.getStatus() != GameStatus.INIT_SHIPS)
            throw new IllegalStateException("Game " + game.getId() + " not started!");

        if (!checkPlayGame(name, game))
            throw new IllegalStateException("Game " + game.getId() + " in progress! You can't open game page!");
    }

    public void shot(String name, String gameId, ShotDto shotDto) {
        Game game = gameLogicService.findById(gameId);
        String result = gameLogicService.shot(name, game, shotDto);
        finishRound(game, new ReloadBoardDto(result, getShotAnimationInfo(shotDto, PlayerTools.getOponentName(name, game))));

        if (PlayerTools.isCPUTurn(game)) {
            SleepUtils.sleep(1_000);
            shot("CPU", gameId, CpuGun.shot(gameId, PlayerTools.getPlayerShootingBoard(game.getPlayers(), "CPU")));
        }
    }

    private ShotAnimationDto getShotAnimationInfo(ShotDto shotDto, String oponentName) {
        return new ShotAnimationDto(shotDto, oponentName);
    }

    private void finishRound(Game game, ReloadBoardDto info) {
        simpMessagingTemplate.convertAndSend("/reload-board/" + game.getId(), info);
    }

    public GameDto generateRandomBoard(String playerName, String gameId) {
        Game game = gameLogicService.findById(gameId);
        game.setShipsManuallyInit(false);
        Player player = PlayerTools.getPlayerByName(playerName, game);
        player.setPlayerShipsBoard(BattleShipsTools.initShipsBoard());
        return GameDtoMapper.parse(game, playerName);
    }

    public void startGame(String gameId, String name) {
        //TODO Players ships board validator

        Game game = gameLogicService.findById(gameId);
        Player player = PlayerTools.getPlayerByName(name, game);
        player.setReady(true);
        BattleShipsTools.clearHits(player.getPlayerShipsBoard());
        if (PlayerTools.allPlayersAreReady(game.getPlayers())) {
            game.setStatus(GameStatus.IN_PROGRESS);
            simpMessagingTemplate.convertAndSend("/start-game/" + game.getId(), "");
        }
    }

    public GameDto clearBoard(String name, String gameId) {
        Game game = gameLogicService.findById(gameId);
        game.setShipsManuallyInit(true);

        Player player = PlayerTools.getPlayerByName(name, game);
        player.setPlayerShipsBoard(BattleShipsTools.initFreeBoard());
        return GameDtoMapper.parse(game, name);
    }

    public GameDto placeShip(String name, String gameId, NewShipDto newShipDto) {
        Game game = gameLogicService.findById(gameId);
        game.setShipsManuallyInit(true);

        Player player = PlayerTools.getPlayerByName(name, game);
        Ship ship = BattleShipsTools.placeShip(player.getPlayerShipsBoard(), newShipDto);
        player.getShipPlacedManually().add(ship);

        return findDtoById(gameId, name);
    }

    public GameDto undo(String name, String gameId) {
        Game game = gameLogicService.findById(gameId);
        game.setShipsManuallyInit(true);
        Player player = PlayerTools.getPlayerByName(name, game);
        BattleShipsTools.undo(player.getShipPlacedManually(), player.getPlayerShipsBoard());
        return findDtoById(gameId, name);
    }

    public void checkInitShipsPage(GameDto game, String name) {
        if (!checkPlayGame(name, game))
            throw new IllegalStateException("Game " + game.getId() + " in progress! You can't open game page!");
    }
}