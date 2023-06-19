package pl.pacinho.battleshipsweb.config;

public class UIConfig {
    public static final String PREFIX = "/battle-ships";
    public static final String GAMES = PREFIX + "/games";
    public static final String GAME = PREFIX + "/game";
    public static final String NEW_GAME = GAMES + "/new";
    public static final String GAME_PAGE = GAMES + "/{gameId}";
    public static final String GAME_ROOM = GAME_PAGE + "/room";
    public static final String SHOOT = GAME_PAGE + "/shoot";
    public static final String PLAYERS = GAME_ROOM + "/players";
    public static final String GAME_BOARD = GAME_PAGE + "/board";
    public static final String GAME_BOARD_RELOAD = GAME_BOARD + "/reload";
}