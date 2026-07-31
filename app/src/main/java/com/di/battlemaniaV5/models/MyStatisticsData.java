//For current login user's statics data
package com.di.battlemaniaV5.models;

public class MyStatisticsData {

    String matchName;
    String mId;
    String matchTime;
    String paid;
    String won;

    public MyStatisticsData(String matchName, String mId, String matchTime, String paid, String won) {
        this.matchName = matchName;
        this.mId = mId;
        this.matchTime = matchTime;
        this.paid = paid;
        this.won = won;
    }

    public String getMatchname() {
        return matchName;
    }

    public void setMatchname(String matchName) {
        this.matchName = matchName;
    }

    public String getMid() {
        return mId;
    }

    public void setMid(String mId) {
        this.mId = mId;
    }

    public String getMatchtime() {
        return matchTime;
    }

    public void setMatchtime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getWon() {
        return won;
    }

    public void setWon(String won) {
        this.won = won;
    }
}
