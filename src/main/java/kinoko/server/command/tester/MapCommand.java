package kinoko.server.command.tester;

import kinoko.packet.world.MessagePacket;
import kinoko.provider.map.PortalInfo;
import kinoko.server.command.Command;
import kinoko.server.command.Arguments;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.user.User;

import java.util.Optional;

/**
 * Tester commands to warp users to specific maps or get their current location.
 */
public final class MapCommand {

    @Command("whereami")
    public static void whereAmI(User user, String[] args) {
        user.write(MessagePacket.system("You are in map: %d", user.getField().getFieldId()));
    }

    @Command({ "map", "warp" })
    @Arguments("field ID to warp to")
    public static void map(User user, String[] args) {
        try {
            // Parse the field ID
            final int fieldId = Integer.parseInt(args[1]);

            // Use supplied portal name or fallback to server default
            final String portalName = args.length > 2 ? args[2] : GameConstants.DEFAULT_PORTAL_NAME;

            // Get the field
            final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(fieldId);
            if (fieldResult.isEmpty()) {
                user.write(MessagePacket.system("Could not resolve field ID: %d", fieldId));
                return;
            }
            final Field targetField = fieldResult.get();

            // Get the portal by name
            final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
            if (portalResult.isEmpty()) {
                user.write(MessagePacket.system("Could not resolve portal '%s' for field ID: %d", portalName, fieldId));
                return;
            }

            // Warp the user
            user.warp(targetField, portalResult.get(), false, false);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            user.write(MessagePacket.system("Usage: !map <field ID> [portal name]"));
        }
    }

}
