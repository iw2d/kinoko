package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.stage.LoginPacket;
import kinoko.provider.EtcProvider;
import kinoko.server.*;
import kinoko.util.Util;
import kinoko.world.Account;
import kinoko.world.World;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterStat;
import kinoko.world.job.Job;
import kinoko.world.job.LoginJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class LoginHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.CHECK_PASSWORD)
    public static void handleCheckPassword(Client c, InPacket inPacket) {
        final String password = inPacket.decodeString();
        final String username = inPacket.decodeString();
        final byte[] machineId = inPacket.decodeArray(16);
        final int gameRoomClient = inPacket.decodeInt();
        final int gameStartMode = inPacket.decodeByte();
        final int worldId = inPacket.decodeByte();
        final int channelId = inPacket.decodeByte();
        final byte[] address = inPacket.decodeArray(4);

        if (ServerConfig.AUTO_CREATE_ACCOUNT) {
            DatabaseManager.accountAccessor().newAccount(username, password);
        }
        final Optional<Account> result = DatabaseManager.accountAccessor().getAccountByPassword(username, password);
        if (result.isEmpty()) {
            c.write(LoginPacket.checkPasswordResultFail(4)); // Incorrect Password
            return;
        }
        c.write(LoginPacket.checkPasswordResultSuccess(result.get()));
    }

    @Handler({ InHeader.WORLD_INFORMATION, InHeader.VIEW_WORLD_SELECT })
    public static void handleViewWorldSelect(Client c, InPacket inPacket) {
        for (World world : Server.getInstance().getWorlds()) {
            c.write(LoginPacket.worldInformation(world));
        }
        c.write(LoginPacket.worldInformationEnd());
    }

    @Handler(InHeader.CHECK_USER_LIMIT)
    public static void handlerCheckUserLimit(Client c, InPacket inPacket) {
        final int worldId = inPacket.decodeShort();
        c.write(LoginPacket.checkUserLimitResult());
    }

    @Handler(InHeader.SELECT_WORLD)
    public static void handleSelectWorld(Client c, InPacket inPacket) {
        final byte gameStartMode = inPacket.decodeByte();
        if (gameStartMode == 2) {
            final int worldId = inPacket.decodeByte();
            final int channelId = inPacket.decodeByte();
            inPacket.decodeInt(); // unk
            c.write(LoginPacket.selectWorldResult());
        }
    }

    @Handler(InHeader.CHECK_DUPLICATE_ID)
    public static void handleCheckDuplicateId(Client c, InPacket inPacket) {
        final String name = inPacket.decodeString();
        c.write(LoginPacket.checkDuplicatedIdResult(name));
    }

    @Handler(InHeader.NEW_CHAR)
    public static void handleNewChar(Client c, InPacket inPacket) {
        final String name = inPacket.decodeString();
        final int selectedRace = inPacket.decodeInt();
        final short selectedSubJob = inPacket.decodeShort();
        final int[] selectedAL = new int[]{
                inPacket.decodeInt(), // face
                inPacket.decodeInt(), // hair
                inPacket.decodeInt(), // hair color
                inPacket.decodeInt(), // skin
                inPacket.decodeInt(), // coat
                inPacket.decodeInt(), // pants
                inPacket.decodeInt(), // shoes
                inPacket.decodeInt(), // weapon
        };
        final byte gender = inPacket.decodeByte();

        // validate character
        Optional<LoginJob> loginJob = LoginJob.getByRace(selectedRace);
        if (loginJob.isEmpty()) {
            // invalid
            return;
        }
        Job job = loginJob.get().getJob();
        if (selectedSubJob != 0 && job != Job.BEGINNER) {
            // invalid
            return;
        }
        for (int i = 0; i < selectedAL.length; i++) {
            if (!EtcProvider.isValidStartingItem(i, selectedAL[i])) {
                // invalid
                return;
            }
        }
        if (gender < 0 || gender > 2) {
            // invalid
            return;
        }

        // create character
        final CharacterData cd = new CharacterData(1);
        final CharacterStat cs = CharacterStat.getDefault(cd, name, selectedAL, gender);
        cs.setJob(job.getJobId());
        cs.setSubJob(selectedSubJob);
        cs.setPosMap(10000);
        cd.setCharacterStat(cs);

        // save character
        DatabaseManager.characterAccessor().newCharacter(cd);
        c.write(LoginPacket.createNewCharacterResult(cd));
    }

    @Handler(InHeader.ALIVE_ACK)
    public static void handleAliveAck(Client c, InPacket inPacket) {
    }

    @Handler(InHeader.CLIENT_ERROR)
    public static void handleClientError(Client c, InPacket inPacket) {
        final short callType = inPacket.decodeShort();
        final int errorType = inPacket.decodeInt();
        final int bufferSize = inPacket.decodeShort();
        inPacket.decodeInt(); // unk

        final short op = inPacket.decodeShort();
        log.error("[Error {}] {}({}) | {}", errorType, OutHeader.getByValue(op), Util.opToString(op), inPacket);
        c.close();
    }

    @Handler(InHeader.UPDATE_CLIENT_ENVIRONMENT)
    public static void handleUpdateClientEnvironment(Client c, InPacket inPacket) {
    }
}
