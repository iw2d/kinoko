package kinoko.world.user;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents the ban status of an account.
 * A BanInfo object can represent a permanent ban, a temporary ban, or no ban.
 * All timestamps are stored in UTC using Instant.
 */
public final class BanInfo {

    /** The reason for the ban. Null if the account is not banned. */
    private String reason;

    /** The expiration timestamp for a temporary ban, or null for permanent bans. */
    private Instant tempBanUntil;

    /**
     * Constructs a BanInfo object with the given reason and temporary ban expiration.
     *
     * @param reason the reason for the ban, or null if not banned
     * @param tempBanUntil the expiration time of a temporary ban; null if permanent or not banned
     */
    public BanInfo(String reason, Instant tempBanUntil) {
        this.reason = reason;
        this.tempBanUntil = tempBanUntil;
    }

    /**
     * Returns true if the account is currently banned.
     * Returns false if reason is null (not banned).
     * Returns true if tempBanUntil is null (permanent ban) or in the future (temporary ban still active).
     *
     * @return true if the account is banned, false otherwise
     */
    public boolean isBanned() {
        if (reason == null) return false;
        if (tempBanUntil != null) return Instant.now().isBefore(tempBanUntil);
        return true;
    }

    /**
     * Returns the reason for the ban.
     *
     * @return the ban reason, or null if not banned
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the expiration timestamp of a temporary ban.
     *
     * @return the Instant representing the end of the temporary ban, or null if permanent or not banned
     */
    public Instant getTempBanUntil() {
        return tempBanUntil;
    }

    /**
     * Lifts any ban on the account.
     * After calling this, isBanned() will return false.
     */
    public void liftBan() {
        this.reason = null;
        this.tempBanUntil = null;
    }

    /**
     * Alias for liftBan(). Removes any ban on the account.
     */
    public void unBan() {
        liftBan();
    }

    /**
     * Sets a temporary ban with a duration in minutes from now.
     *
     * @param reason the reason for the temporary ban
     * @param durationMinutes the duration of the ban in minutes
     */
    public void setTempBan(String reason, long durationMinutes) {
        this.reason = reason;
        this.tempBanUntil = Instant.now().plus(Duration.ofMinutes(durationMinutes));
    }

    /**
     * Sets a permanent ban.
     *
     * @param reason the reason for the permanent ban
     */
    public void setPermanentBan(String reason) {
        this.reason = reason;
        this.tempBanUntil = null;
    }
}
