package com.app.rush47.models;

/** One row from leaderboard.php, shown in TopPlayerActivity. */
public class TopPlayer {
    private final String rank;
    private final String userName;
    private final String totalEarnings;

    public TopPlayer(String rank, String userName, String totalEarnings) {
        this.rank = rank;
        this.userName = userName;
        this.totalEarnings = totalEarnings;
    }

    public String getRank() { return rank; }
    public String getUserName() { return userName; }
    public String getTotalEarnings() { return totalEarnings; }
}
