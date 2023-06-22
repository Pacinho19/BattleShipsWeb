package pl.pacinho.battleshipsweb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.pacinho.battleshipsweb.config.UIConfig;
import pl.pacinho.battleshipsweb.model.dto.GameDto;
import pl.pacinho.battleshipsweb.model.dto.ShotDto;
import pl.pacinho.battleshipsweb.model.enums.GameStatus;
import pl.pacinho.battleshipsweb.model.enums.GameType;
import pl.pacinho.battleshipsweb.service.GameService;

@RequiredArgsConstructor
@Controller
public class GameController {

    private final GameService gameService;

    @GetMapping(UIConfig.PREFIX)
    public String gameHome(Model model) {
        return "home";
    }

    @PostMapping(UIConfig.GAMES)
    public String availableGames(Model model) {
        model.addAttribute("games", gameService.getAvailableGames());
        return "fragments/available-games :: availableGamesFrag";
    }

    @PostMapping(UIConfig.NEW_GAME)
    public String newGame(Model model, Authentication authentication, @RequestParam("gameType") GameType gameType) {
        try {
            return "redirect:" + UIConfig.GAMES + "/" + gameService.newGame(authentication.getName(), gameType) + (gameType==GameType.PLAYER ? "/room" : "");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return gameHome(model);
        }
    }

    @GetMapping(UIConfig.GAME_ROOM)
    public String gameRoom(@PathVariable(value = "gameId") String gameId, Model model, Authentication authentication) {
        try {
            GameDto game = gameService.findDtoById(gameId, authentication.getName());
            if (game.getStatus() == GameStatus.IN_PROGRESS) return "redirect:" + UIConfig.GAMES + "/" + gameId;
            if (game.getStatus() == GameStatus.FINISHED)
                throw new IllegalStateException("Game " + gameId + " finished!");

            model.addAttribute("game", game);
            model.addAttribute("joinGame", gameService.canJoin(game, authentication.getName()));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return gameHome(model);
        }
        return "game-room";
    }

    @GetMapping(UIConfig.GAME_PAGE)
    public String gamePage(@PathVariable(value = "gameId") String gameId,
                           Model model,
                           Authentication authentication) {
        model.addAttribute("game", gameService.findDtoById(gameId, authentication.getName()));
        return "game";
    }

    @PostMapping(UIConfig.PLAYERS)
    public String players(@PathVariable(value = "gameId") String gameId,
                          Model model,
                          Authentication authentication) {
        GameDto game = gameService.findDtoById(gameId, authentication.getName());
        model.addAttribute("game", game);
        return "fragments/game-players :: gamePlayersFrag";
    }

    @GetMapping(UIConfig.GAME_BOARD_RELOAD)
    public String reloadBoard(Authentication authentication,
                              Model model,
                              @PathVariable(value = "gameId") String gameId) {
        model.addAttribute("game", gameService.findDtoById(gameId, authentication.getName()));
        return "fragments/board :: boardFrag";
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PostMapping(UIConfig.shot)
    public void shot(Authentication authentication,
                     @RequestBody ShotDto shotDto,
                     @PathVariable(value = "gameId") String gameId) {
        gameService.shot(authentication.getName(), gameId, shotDto);
    }
}