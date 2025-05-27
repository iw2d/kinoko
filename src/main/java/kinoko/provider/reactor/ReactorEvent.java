package kinoko.provider.reactor;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;
import kinoko.util.Rect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ReactorEvent {
    private final ReactorEventType type;
    private final int nextState;

    private Rect rect;
    private int itemId;
    private Set<Integer> skills;

    public ReactorEvent(ReactorEventType type, int nextState) {
        this.type = type;
        this.nextState = nextState;
    }

    public ReactorEventType getType() {
        return type;
    }

    public int getNextState() {
        return nextState;
    }

    public Rect getRect() {
        return rect;
    }

    public Set<Integer> getSkills() {
        return skills;
    }

    public int getItemId() {
        return itemId;
    }

    public static ReactorEvent from(WzProperty eventProp) throws ProviderError {
        final int type = WzProvider.getInteger(eventProp.get("type"));
        final ReactorEventType eventType = ReactorEventType.getByValue(type);
        if (eventType == null) {
            throw new ProviderError("Unhandled reactor event type %d", type);
        }
        final int nextState = WzProvider.getInteger(eventProp.get("state"));

        final ReactorEvent event = new ReactorEvent(eventType, nextState);
        switch (event.getType()) {
            case SKILL -> {
                if (!(eventProp.get("activeSkillID") instanceof WzProperty skillList)) {
                    throw new ProviderError("Failed to resolve reactor event activeSKillID");
                }
                final Set<Integer> skills = new HashSet<>();
                for (var skillEntry : skillList.getItems().entrySet()) {
                    skills.add(WzProvider.getInteger(skillEntry.getValue()));
                }
                event.skills = Collections.unmodifiableSet(skills);
                event.rect = WzProvider.getRect(eventProp);
            }
            case DROP -> {
                event.itemId = WzProvider.getInteger(eventProp.get("0"));
                event.rect = WzProvider.getRect(eventProp);
            }
        }
        return event;
    }
}
