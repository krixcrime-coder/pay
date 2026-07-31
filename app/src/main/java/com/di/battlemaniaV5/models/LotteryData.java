//For all lottery data
package com.di.battlemaniaV5.models;

public class LotteryData {

    String lId;
    String lTitla;
    String lImage;
    String lTime;
    String lRule;
    String lfee;
    String lPrize;
    String lSize;
    String lTotalJoined;
    String ljoinStatus;
    String lWonBy;
    String lJoinedMember;

    public LotteryData(String lId, String lTitla, String lImage, String lTime, String lRule, String lfee, String lPrize, String lSize, String lTotalJoined, String ljoinStatus, String lWonBy, String lJoinedMember) {
        this.lId = lId;
        this.lTitla = lTitla;
        this.lImage = lImage;
        this.lTime = lTime;
        this.lRule = lRule;
        this.lfee = lfee;
        this.lPrize = lPrize;
        this.lSize = lSize;
        this.lTotalJoined = lTotalJoined;
        this.ljoinStatus = ljoinStatus;
        this.lWonBy = lWonBy;
        this.lJoinedMember = lJoinedMember;
    }


    public String getlId() {
        return lId;
    }

    public void setlId(String lId) {
        this.lId = lId;
    }

    public String getlTitla() {
        return lTitla;
    }

    public void setlTitla(String lTitla) {
        this.lTitla = lTitla;
    }

    public String getlImage() {
        return lImage;
    }

    public void setlImage(String lImage) {
        this.lImage = lImage;
    }

    public String getlTime() {
        return lTime;
    }

    public void setlTime(String lTime) {
        this.lTime = lTime;
    }

    public String getlRule() {
        return lRule;
    }

    public void setlRule(String lRule) {
        this.lRule = lRule;
    }

    public String getLfee() {
        return lfee;
    }

    public void setLfee(String lfee) {
        this.lfee = lfee;
    }

    public String getlPrize() {
        return lPrize;
    }

    public void setlPrize(String lPrize) {
        this.lPrize = lPrize;
    }

    public String getlSize() {
        return lSize;
    }

    public void setlSize(String lSize) {
        this.lSize = lSize;
    }

    public String getlTotalJoined() {
        return lTotalJoined;
    }

    public void setlTotalJoined(String lTotalJoined) {
        this.lTotalJoined = lTotalJoined;
    }

    public String getLjoinStatus() {
        return ljoinStatus;
    }

    public void setLjoinStatus(String ljoinStatus) {
        this.ljoinStatus = ljoinStatus;
    }

    public String getlWonBy() {
        return lWonBy;
    }

    public void setlWonBy(String lWonBy) {
        this.lWonBy = lWonBy;
    }

    public String getlJoinedMember() {
        return lJoinedMember;
    }

    public void setlJoinedMember(String lJoinedMember) {
        this.lJoinedMember = lJoinedMember;
    }
}
