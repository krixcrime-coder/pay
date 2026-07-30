package com.app.rush47.models;

/** One row of the Top Players / Leaderboard list, ranked by prize money won. */
public class LeaderboardEntry {

    private final String rank;
    private final String userName;
    private final String totalEarnings;
    private final String totalKills;

    public LeaderboardEntry(String rank, String userName, String totalEarnings, String totalKills) {
        this.rank = rank;
        this.userName = userName;
        this.totalEarnings = totalEarnings;
        this.totalKills = totalKills;
    }

    public String getRank() { return rank; }
    public String getUserName() { return userName; }
    public String getTotalEarnings() { return totalEarnings; }
    public String getTotalKills() { return totalKills; }
}
