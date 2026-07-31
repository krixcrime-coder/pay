//For all game data
package com.di.battlemaniaV5.models;

public class GameData {

    String gameId;
    String gameName;
    String gameImage;
    String gameStatus;
    String totalUpcoming;
    String gameType;
    String totalUpcomingchallenge;
    String packageName;
    String totalUpcomingTournament;

    public GameData(String gameId, String gameName, String gameImage, String gameStatus, String totalUpcoming, String gameType, String totalUpcomingchallenge, String packageName, String totalUpcomingTournament) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameImage = gameImage;
        this.gameStatus = gameStatus;
        this.totalUpcoming = totalUpcoming;
        this.gameType = gameType;
        this.totalUpcomingchallenge = totalUpcomingchallenge;
        this.packageName = packageName;
        this.totalUpcomingTournament=totalUpcomingTournament;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameImage() {
        return gameImage;
    }

    public void setGameImage(String gameImage) {
        this.gameImage = gameImage;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getTotalUpcoming() {
        return totalUpcoming;
    }

    public void setTotalUpcoming(String totalUpcoming) {
        this.totalUpcoming = totalUpcoming;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }


    public String getTotalUpcomingchallenge() {
        return totalUpcomingchallenge;
    }

    public void setTotalUpcomingchallenge(String totalUpcomingchallenge) {
        this.totalUpcomingchallenge = totalUpcomingchallenge;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTotalUpcomingTournament() {
        return totalUpcomingTournament;
    }

    public void setTotalUpcomingTournament(String totalUpcomingTournament) {
        this.totalUpcomingTournament = totalUpcomingTournament;
    }
}
