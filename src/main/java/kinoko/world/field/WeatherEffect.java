package kinoko.world.field;

import java.time.Instant;

public final class WeatherEffect {
    private final int itemId;
    private final String message;
    private final Instant expireTime;

    public WeatherEffect(int itemId, String message, Instant expireTime) {
        this.itemId = itemId;
        this.message = message;
        this.expireTime = expireTime;
    }

    public int getItemId() {
        return itemId;
    }

    public String getMessage() {
        return message;
    }

    public Instant getExpireTime() {
        return expireTime;
    }
}
