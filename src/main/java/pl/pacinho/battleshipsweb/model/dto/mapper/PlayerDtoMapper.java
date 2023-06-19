package pl.pacinho.battleshipsweb.model.dto.mapper;


import pl.pacinho.battleshipsweb.model.dto.PlayerDto;
import pl.pacinho.battleshipsweb.model.entity.Player;

public class PlayerDtoMapper {
    public static PlayerDto parse(Player player) {
        return new PlayerDto(
                player.getName()
        );
    }
}