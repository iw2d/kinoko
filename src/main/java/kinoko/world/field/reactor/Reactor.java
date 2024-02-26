package kinoko.world.field.reactor;

import kinoko.packet.field.ReactorPacket;
import kinoko.provider.map.ReactorInfo;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.world.field.FieldObject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Reactor extends FieldObject implements Lockable<Reactor> {
    private final Lock lock = new ReentrantLock();
    private final ReactorTemplate template;
    private final ReactorInfo reactorInfo;
    private int state;

    public Reactor(ReactorTemplate template, ReactorInfo reactorInfo) {
        this.template = template;
        this.reactorInfo = reactorInfo;
        reset(reactorInfo.getX(), reactorInfo.getY(), 0);
    }

    public int getTemplateId() {
        return template.getId();
    }

    public String getAction() {
        return template.getAction();
    }

    public String getName() {
        return reactorInfo.getName();
    }

    public int getReactorTime() {
        return reactorInfo.getReactorTime();
    }

    public boolean isFlip() {
        return reactorInfo.isFlip();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void reset(int x, int y, int state) {
        setX(x);
        setY(y);
        setState(state);
    }

    @Override
    public OutPacket enterFieldPacket() {
        return ReactorPacket.reactorEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return ReactorPacket.reactorLeaveField(this);
    }

    @Override
    public String toString() {
        return String.format("Reactor { %d, oid : %d, state : %d }", getTemplateId(), getId(), getState());
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    public static Reactor from(ReactorTemplate template, ReactorInfo reactorInfo) {
        return new Reactor(template, reactorInfo);
    }
}
