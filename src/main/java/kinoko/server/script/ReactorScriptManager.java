package kinoko.server.script;

import kinoko.packet.field.FieldEffectPacket;
import kinoko.provider.ItemProvider;
import kinoko.provider.MobProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.Foothold;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.reward.Reward;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.item.Item;
import kinoko.world.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReactorScriptManager extends ScriptManager {
    private final Reactor reactor;

    public ReactorScriptManager(User user, Reactor reactor) {
        super(user);
        this.reactor = reactor;
    }

    @Override
    public void disposeManager() {
        ScriptDispatcher.removeScriptManager(ScriptType.REACTOR, user);
    }

    public Reactor getReactor() {
        return reactor;
    }

    public Field getField() {
        return reactor.getField();
    }

    public void dropRewards(List<List<Object>> rewardList) {
        // Resolve rewards
        final List<Reward> possibleRewards = new ArrayList<>();
        for (List<Object> rewardTuple : rewardList) {
            if (rewardTuple.size() < 4) {
                log.error("Invalid tuple length for ReactorScriptManager.dropRewards {}", rewardTuple);
                return;
            }
            final int itemId = ((Number) rewardTuple.get(0)).intValue(); // 0 if money
            final int min = ((Number) rewardTuple.get(1)).intValue();
            final int max = ((Number) rewardTuple.get(2)).intValue();
            final double prob = ((Number) rewardTuple.get(3)).doubleValue();
            final int questId = rewardTuple.size() > 4 ? ((Number) rewardTuple.get(4)).intValue() : 0;
            possibleRewards.add(new Reward(itemId, min, max, prob, questId));
        }
        // Create drops from possible rewards
        final List<Drop> drops = new ArrayList<>();
        for (Reward reward : possibleRewards) {
            // Drop probability
            if (!Util.succeedDouble(reward.getProb())) {
                continue;
            }
            // Create drop
            if (reward.isMoney()) {
                final int money = Util.getRandom(reward.getMin(), reward.getMax());
                if (money <= 0) {
                    continue;
                }
                drops.add(Drop.money(DropOwnType.USEROWN, reactor, money, user.getCharacterId()));
            } else {
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(reward.getItemId());
                if (itemInfoResult.isEmpty()) {
                    continue;
                }
                final int quantity = Util.getRandom(reward.getMin(), reward.getMax());
                final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), quantity);
                drops.add(Drop.item(DropOwnType.USEROWN, reactor, item, user.getCharacterId(), reward.getQuestId()));
            }
        }
        // Add drops to field
        getField().getDropPool().addDrops(drops, DropEnterType.CREATE, reactor.getX(), reactor.getY() - GameConstants.DROP_HEIGHT, 0);
    }

    public void changeBgm(String uol) {
        getField().broadcastPacket(FieldEffectPacket.changeBgm(uol));
    }

    public void spawnMob(int templateId, int appearType, int x, int y) {
        final MobAppearType mobAppearType = MobAppearType.getByValue(appearType);
        if (mobAppearType == null) {
            log.error("Unknown mob appear type received for spawnMob : {}", appearType);
        }
        spawnMob(templateId, mobAppearType != null ? mobAppearType : MobAppearType.REGEN, x, y);
    }

    private void spawnMob(int templateId, MobAppearType appearType, int x, int y) {
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(templateId);
        if (mobTemplateResult.isEmpty()) {
            log.error("Could not resolve mob template ID : {}", templateId);
            return;
        }
        final Optional<Foothold> footholdResult = getField().getFootholdBelow(x, y - GameConstants.REACTOR_SPAWN_HEIGHT);
        final Mob mob = new Mob(
                mobTemplateResult.get(),
                null,
                x,
                y,
                footholdResult.map(Foothold::getFootholdId).orElse(0)
        );
        mob.setAppearType(appearType);
        getField().getMobPool().addMob(mob);
    }
}
