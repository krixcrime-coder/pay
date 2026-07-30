package com.app.rush47.models;

import org.json.JSONObject;

/**
 * One match. Populated from matches.php / match_detail.php /
 * my_matches.php - field names match the backend's `matches` table
 * directly (m_id, match_name, win_prize, etc.) since the backend now
 * mirrors the original app's schema.
 *
 * match_status: "1"=upcoming, "2"=completed, "3"=ongoing, "4"=cancelled
 * (matches the original admin panel's values) - use isUpcoming() /
 * isOngoing() / isCompleted() rather than comparing the raw string.
 */
public class Tournament {

    private final String matchId;
    private final String gameId;
    private final String type;
    private final String title;
    private final String entryFee;
    private final String prize;
    private final String perKill;
    private final String mapName;
    private final String version;
    private final String imageUrl;
    private final boolean pinned;
    private final String matchTime;
    private final int noOfPlayer;
    private final int slotTotal;
    private final int slotsFilled;
    private final String slotNumber;
    private final String matchStatus;
    private final boolean joined;
    private final String matchDesc;
    private final String matchPrivateDesc;
    private final String roomDescription;
    private final String matchSponsor;
    private final String killed;
    private final String totalWin;

    public Tournament(String matchId, String gameId, String type, String title,
                       String entryFee, String prize, String perKill, String mapName,
                       String version, String imageUrl, boolean pinned,
                       String matchTime, int noOfPlayer, int slotTotal, int slotsFilled, String slotNumber,
                       String matchStatus, boolean joined, String matchDesc, String matchPrivateDesc,
                       String roomDescription, String matchSponsor, String killed, String totalWin) {
        this.matchId = matchId;
        this.gameId = gameId;
        this.type = type;
        this.title = title;
        this.entryFee = entryFee;
        this.prize = prize;
        this.perKill = perKill;
        this.mapName = mapName;
        this.version = version;
        this.imageUrl = imageUrl;
        this.pinned = pinned;
        this.matchTime = matchTime;
        this.noOfPlayer = noOfPlayer;
        this.slotTotal = slotTotal;
        this.slotsFilled = slotsFilled;
        this.slotNumber = slotNumber;
        this.matchStatus = matchStatus;
        this.joined = joined;
        this.matchDesc = matchDesc;
        this.matchPrivateDesc = matchPrivateDesc;
        this.roomDescription = roomDescription;
        this.matchSponsor = matchSponsor;
        this.killed = killed;
        this.totalWin = totalWin;
    }

    public String getTournamentId() {
        return matchId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public String getPrize() {
        return prize;
    }

    public String getPerKill() {
        return perKill;
    }

    public String getMapName() {
        return mapName;
    }

    public String getVersion() {
        return version;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isPinned() {
        return pinned;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public int getNoOfPlayer() {
        return noOfPlayer;
    }

    public int getSlotTotal() {
        return slotTotal;
    }

    public int getSlotsFilled() {
        return slotsFilled;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    /** Raw original-schema status: "1"=upcoming,"2"=completed,"3"=ongoing,"4"=cancelled. */
    public String getStatus() {
        return matchStatus;
    }

    public boolean isUpcoming() {
        return "1".equals(matchStatus);
    }

    public boolean isCompleted() {
        return "2".equals(matchStatus);
    }

    public boolean isOngoing() {
        return "3".equals(matchStatus);
    }

    public boolean isCancelled() {
        return "4".equals(matchStatus);
    }

    public boolean isJoined() {
        return joined;
    }

    public String getMatchDesc() {
        return matchDesc;
    }

    public String getMatchPrivateDesc() {
        return matchPrivateDesc;
    }

    /** Free-text room ID/password block, only non-empty once you've joined and admin has filled it in. */
    public String getRoomDescription() {
        return roomDescription;
    }

    public String getMatchSponsor() {
        return matchSponsor;
    }

    public String getKilled() {
        return killed;
    }

    public String getTotalWin() {
        return totalWin;
    }

    public boolean isFull() {
        return slotTotal > 0 && slotsFilled >= slotTotal;
    }

    /**
     * Builds a Tournament from any of matches.php / match_detail.php /
     * my_matches.php's per-match JSON object. Missing fields fall back
     * to safe defaults instead of throwing, since each endpoint only
     * sends the fields relevant to it.
     */
    public static Tournament fromJson(JSONObject o) {
        return new Tournament(
                o.optString("m_id", ""),
                o.optString("game_id", ""),
                o.optString("type", ""),
                o.optString("match_name", ""),
                o.optString("entry_fee", "0"),
                o.optString("win_prize", "0"),
                o.optString("per_kill", "0"),
                o.optString("MAP", ""),
                o.optString("version", ""),
                o.optString("match_banner", ""),
                "1".equals(o.optString("pin_match", "0")),
                o.optString("match_time", ""),
                o.optInt("no_of_player", 1),
                o.optInt("number_of_position", 0),
                o.optInt("slots_filled", 0),
                o.optString("position", ""),
                o.optString("match_status", "1"),
                o.optBoolean("is_joined", false),
                o.optString("match_desc", ""),
                o.optString("match_private_desc", ""),
                o.optString("room_description", ""),
                o.optString("match_sponsor", ""),
                o.optString("killed", "0"),
                o.optString("total_win", "0"));
    }
}
