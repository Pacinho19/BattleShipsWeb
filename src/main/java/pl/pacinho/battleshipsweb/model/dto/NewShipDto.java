package pl.pacinho.battleshipsweb.model.dto;

import pl.pacinho.battleshipsweb.model.enums.ShipType;

public record NewShipDto(int x, int y, int mastsCount, ShipType shipType) {
}
