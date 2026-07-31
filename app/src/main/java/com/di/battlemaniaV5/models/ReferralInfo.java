//For current login user's referrals data
package com.di.battlemaniaV5.models;

public class ReferralInfo {

    String rUid;
    String data;
    String playerName;
    String Status;

    public ReferralInfo(String rUid, String data, String playerName, String status) {
        this.rUid = rUid;
        this.data = data;
        this.playerName = playerName;
        Status = status;
    }

    public String getRuid() {
        return rUid;
    }

    public void setRuid(String rUid) {
        this.rUid = rUid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPlayername() {
        return playerName;
    }

    public void setPlayername(String playerName) {
        this.playerName = playerName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
