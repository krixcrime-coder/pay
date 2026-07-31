//For top player data
package com.di.battlemaniaV5.models;

public class TopplayerData {

    String gameName;
    String winnning;
    String userName;
    String memberId;
    String pubgId;

    public TopplayerData(String gameName, String winnning, String userName, String memberId, String pubgId) {
        this.gameName = gameName;
        this.winnning = winnning;
        this.userName = userName;
        this.memberId = memberId;
        this.pubgId = pubgId;
    }

    public String getGamename() {
        return gameName;
    }

    public void setGamename(String gameName) {
        this.gameName = gameName;
    }

    public String getWinnning() {
        return winnning;
    }

    public void setWinnning(String winnning) {
        this.winnning = winnning;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getMemberid() {
        return memberId;
    }

    public void setMemberid(String memberId) {
        this.memberId = memberId;
    }

    public String getPubgid() {
        return pubgId;
    }

    public void setPubgid(String pubgId) {
        this.pubgId = pubgId;
    }
}
