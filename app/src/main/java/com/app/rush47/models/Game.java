package com.app.rush47.models;

/**
 * A single game entry shown in the "Esports Games" list on the Play tab
 * (e.g. Free Fire, BGMI). Tapping a card is meant to open the tournament
 * list for that game - that screen isn't built yet.
 */
public class Game {

    private final String gameId;
    private final String name;
    private final String bannerUrl;
    private final int matchesAvailable;

    public Game(String gameId, String name, String bannerUrl, int matchesAvailable) {
        this.gameId = gameId;
        this.name = name;
        this.bannerUrl = bannerUrl;
        this.matchesAvailable = matchesAvailable;
    }

    public String getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public int getMatchesAvailable() {
        return matchesAvailable;
    }
}
