//For all live match data
package com.di.battlemaniaV5.models;

public class AllOngoingMatchData {

    String mId;
    String matchBanner;
    String matchName;
    String matchTime;
    String winPrize;
    String perKill;
    String entryFee;
    String type;
    String version;
    String map;
    String matchUrl;
    String memberId;
    String matchType;
    String roomDescription;
    String joinStatus;

    public AllOngoingMatchData(String mId, String matchBanner, String matchName, String matchTime, String winPrize, String perKill, String entryFee, String type, String version, String map, String matchUrl, String memberId, String matchType, String roomDescription, String joinStatus) {
        this.mId = mId;
        this.matchBanner = matchBanner;
        this.matchName = matchName;
        this.matchTime = matchTime;
        this.winPrize = winPrize;
        this.perKill = perKill;
        this.entryFee = entryFee;
        this.type = type;
        this.version = version;
        this.map = map;
        this.matchUrl = matchUrl;
        this.memberId = memberId;
        this.matchType = matchType;
        this.roomDescription = roomDescription;
        this.joinStatus = joinStatus;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getMatchbanner() {
        return matchBanner;
    }

    public void setMatchbanner(String matchBanner) {
        this.matchBanner = matchBanner;
    }

    public String getMatchname() {
        return matchName;
    }

    public void setMatchname(String matchName) {
        this.matchName = matchName;
    }

    public String getMatchtime() {
        return matchTime;
    }

    public void setMatchtime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getWinprize() {
        return winPrize;
    }

    public void setWinprize(String winPrize) {
        this.winPrize = winPrize;
    }

    public String getPerkill() {
        return perKill;
    }

    public void setPerkill(String perKill) {
        this.perKill = perKill;
    }

    public String getEntryfee() {
        return entryFee;
    }

    public void setEntryfee(String entryFee) {
        this.entryFee = entryFee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMatchurl() {
        return matchUrl;
    }

    public void setMatchurl(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public String getMemberid() {
        return memberId;
    }

    public void setMemberid(String memberId) {
        this.memberId = memberId;
    }

    public String getMatchtype() {
        return matchType;
    }

    public void setMatchtype(String matchType) {
        this.matchType = matchType;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getJoinstatus() {
        return joinStatus;
    }

    public void setJoinstatus(String joinStatus) {
        this.joinStatus = joinStatus;
    }
}
