//For joining single match data
package com.di.battlemaniaV5.models;

public class JoinSingleMatchData {

    String userName;
    String pubgId;
    String team;
    String position;
    String teamNumber;

    public JoinSingleMatchData(String userName, String pubgId, String team, String position, String teamNumber) {
        this.userName = userName;
        this.pubgId = pubgId;
        this.team = team;
        this.position = position;
        this.teamNumber = teamNumber;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getPubgid() {
        return pubgId;
    }

    public void setPubgid(String pubgId) {
        this.pubgId = pubgId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTeamnumber() {
        return teamNumber;
    }

    public void setTeamnumber(String teamNumber) {
        this.teamNumber = teamNumber;
    }
}
