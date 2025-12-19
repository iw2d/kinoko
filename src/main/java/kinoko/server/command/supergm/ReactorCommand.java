package kinoko.server.command.supergm;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.ReactorProvider;
import kinoko.provider.map.ReactorInfo;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.field.Field;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * SuperGM commands to spawn and interact with reactors.
 */
public final class ReactorCommand {

    @Command("reactor")
    @Arguments("reactor template ID")
    public static void reactor(User user, String[] args) {
        try {
            final int templateId = Integer.parseInt(args[1]);
            final Optional<ReactorTemplate> reactorTemplateResult = ReactorProvider.getReactorTemplate(templateId);

            if (reactorTemplateResult.isEmpty()) {
                user.systemMessage("Could not resolve reactor template ID: %d", templateId);
                return;
            }

            final Field field = user.getField();
            final ReactorInfo reactorInfo = new ReactorInfo(templateId, "", user.getX(), user.getY(), false, -1);
            field.getReactorPool().addReactor(Reactor.from(reactorTemplateResult.get(), reactorInfo));

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !reactor <reactor template ID>");
        }
    }

    @Command("hitreactor")
    @Arguments("reactor template ID")
    public static void hitReactor(User user, String[] args) {
        try {
            final int templateId = Integer.parseInt(args[1]);
            final Field field = user.getField();
            final Optional<Reactor> reactorResult = field.getReactorPool().getByTemplateId(templateId);

            if (reactorResult.isEmpty()) {
                user.systemMessage("Could not resolve reactor with template ID: %d", templateId);
                return;
            }

            final Reactor reactor = reactorResult.get();
            reactor.setState(reactor.getState() + 1);
            field.getReactorPool().hitReactor(user, reactor, 0);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.systemMessage("Usage: !hitreactor <reactor template ID>");
        }
    }
}
