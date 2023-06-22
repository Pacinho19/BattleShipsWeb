package pl.pacinho.battleshipsweb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import pl.pacinho.battleshipsweb.model.dto.GameActionDto;
import pl.pacinho.battleshipsweb.model.dto.JoinGameDto;
import pl.pacinho.battleshipsweb.service.GameService;

@RequiredArgsConstructor
@Controller
public class BattleShipsWsController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameService gameService;

    @MessageMapping("/join")
    public void join(@Payload GameActionDto gameActionDto, Authentication authentication) {
        String exception = null;
        try {
            gameService.joinGame(authentication.getName(), gameActionDto.getGameId(), false);
        } catch (Exception ex) {
            exception = ex.getMessage();
        }
        simpMessagingTemplate.convertAndSend("/join/" + gameActionDto.getGameId(),
                new JoinGameDto(authentication.getName(), gameService.checkStartGame(gameActionDto.getGameId()), exception));
    }

}