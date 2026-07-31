//For current login user's transaction data
package com.di.battlemaniaV5.models;

public class TransactionDetails {

    String transactionId;
    String note;
    String matchId;
    String noteId;
    String date;
    String joinMoney;
    String winMoney;
    String deposit;
    String withdraw;

    public TransactionDetails(String transactionId, String note, String matchId, String noteId, String date, String joinMoney, String winMoney, String deposit, String withdraw) {
        this.transactionId = transactionId;
        this.note = note;
        this.matchId = matchId;
        this.noteId = noteId;
        this.date = date;
        this.joinMoney = joinMoney;
        this.winMoney = winMoney;
        this.deposit = deposit;
        this.withdraw = withdraw;
    }

    public String getTransactionid() {
        return transactionId;
    }

    public void setTransactionid(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMatchid() {
        return matchId;
    }

    public void setMatchid(String matchId) {
        this.matchId = matchId;
    }

    public String getNoteid() {
        return noteId;
    }

    public void setNoteid(String noteId) {
        this.noteId = noteId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJoinmoney() {
        return joinMoney;
    }

    public void setJoinmoney(String joinMoney) {
        this.joinMoney = joinMoney;
    }

    public String getWinmoney() {
        return winMoney;
    }

    public void setWinmoney(String winMoney) {
        this.winMoney = winMoney;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(String withdraw) {
        this.withdraw = withdraw;
    }
}