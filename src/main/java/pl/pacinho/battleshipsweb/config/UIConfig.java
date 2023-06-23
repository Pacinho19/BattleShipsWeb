package pl.pacinho.battleshipsweb.config;

public class UIConfig {
    public static final String PREFIX = "/battle-ships";
    public static final String GAMES = PREFIX + "/games";
    public static final String NEW_GAME = GAMES + "/new";
    public static final String GAME_PAGE = GAMES + "/{gameId}";
    public static final String GAME_ROOM = GAME_PAGE + "/room";
    public static final String START_GAME = GAME_PAGE + "/start-game";
    public static final String GAME_INIT_SHIPS = GAME_PAGE + "/init-ships";
    public static final String GAME_INIT_SHIPS_PLACE_SHIPS = GAME_INIT_SHIPS + "/place";
    public static final String GAME_INIT_SHIPS_CLEAR_BOARD = GAME_INIT_SHIPS + "/clear-board";
    public static final String GAME_INIT_SHIPS_GENERATE_RANDOM_BOARD = GAME_INIT_SHIPS + "/generate-random-board";
    public static final String GAME_SHOT = GAME_PAGE + "/shot";
    public static final String GAME_PLAYERS = GAME_ROOM + "/players";
    public static final String GAME_BOARD = GAME_PAGE + "/board";
    public static final String GAME_BOARD_RELOAD = GAME_BOARD + "/reload";
}