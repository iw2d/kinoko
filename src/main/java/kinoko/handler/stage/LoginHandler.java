package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.stage.LoginPacket;
import kinoko.packet.stage.LoginResult;
import kinoko.provider.EtcProvider;
import kinoko.server.Client;
import kinoko.server.Server;
import kinoko.server.ServerConfig;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import kinoko.world.Account;
import kinoko.world.Channel;
import kinoko.world.GameConstants;
import kinoko.world.World;
import kinoko.world.item.Inventory;
import kinoko.world.job.Job;
import kinoko.world.job.LoginJob;
import kinoko.world.user.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class LoginHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.CHECK_PASSWORD)
    public static void handleCheckPassword(Client c, InPacket inPacket) {
        final String username = inPacket.decodeString();
        final String password = inPacket.decodeString();
        final byte[] machineId = inPacket.decodeArray(16);
        final int gameRoomClient = inPacket.decodeInt();
        final int gameStartMode = inPacket.decodeByte();
        final int worldId = inPacket.decodeByte();
        final int channelId = inPacket.decodeByte();
        final byte[] address = inPacket.decodeArray(4);

        final Optional<Account> accountResult = DatabaseManager.accountAccessor().getAccountByUsername(username);
        if (accountResult.isEmpty()) {
            if (ServerConfig.AUTO_CREATE_ACCOUNT) {
                DatabaseManager.accountAccessor().newAccount(username, password);
            }
            c.write(LoginPacket.checkPasswordResultFail(LoginResult.NOT_REGISTERED));
            return;
        }

        final Account account = accountResult.get();
        if (!DatabaseManager.accountAccessor().checkPassword(account, password, false)) {
            c.write(LoginPacket.checkPasswordResultFail(LoginResult.INCORRECT_PASSWORD));
            return;
        }

        c.setAccount(account);
        c.write(LoginPacket.checkPasswordResultSuccess(account));
    }

    @Handler({ InHeader.WORLD_INFORMATION, InHeader.VIEW_WORLD_SELECT })
    public static void handleViewWorldSelect(Client c, InPacket inPacket) {
        for (World world : Server.getInstance().getWorlds()) {
            c.write(LoginPacket.worldInformation(world));
        }
        c.write(LoginPacket.worldInformationEnd());
        c.write(LoginPacket.latestConnectedWorld(ServerConfig.WORLD_ID));
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

            final Optional<World> world = Server.getInstance().getWorlds().stream()
                    .filter(w -> w.getId() == worldId)
                    .findFirst();
            if (world.isEmpty()) {
                c.close();
                return;
            }
            final Optional<Channel> channel = world.get().getChannels().stream()
                    .filter(ch -> ch.getWorldId() == worldId && ch.getChannelId() == channelId)
                    .findFirst();
            if (channel.isEmpty()) {
                c.close();
                return;
            }

            final Account account = c.getAccount();
            if (account == null) {
                c.close();
                return;
            }
            account.setCharacterList(DatabaseManager.characterAccessor().getAvatarDataByAccount(account.getId()));
            c.write(LoginPacket.selectWorldResult(account));
        } else {
            c.close();
        }
    }

    @Handler(InHeader.CHECK_DUPLICATE_ID)
    public static void handleCheckDuplicateId(Client c, InPacket inPacket) {
        final String name = inPacket.decodeString();
        // Validation done on client side, server side validation in NEW_CHAR handler
        if (DatabaseManager.characterAccessor().checkCharacterNameAvailable(name)) {
            c.write(LoginPacket.checkDuplicatedIdResult(name, 0)); // Success
        } else {
            c.write(LoginPacket.checkDuplicatedIdResult(name, 1)); // This name is currently being used.
        }
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

        // Validate character
        if (!GameConstants.isValidCharacterName(name) || EtcProvider.isForbiddenName(name)) {
            c.write(LoginPacket.createNewCharacterResultFail(LoginResult.INVALID_CHARACTER_NAME));
            return;
        }
        Optional<LoginJob> loginJob = LoginJob.getByRace(selectedRace);
        if (loginJob.isEmpty()) {
            c.close();
            return;
        }
        Job job = loginJob.get().getJob();
        if (selectedSubJob != 0 && job != Job.BEGINNER) {
            c.close();
            return;
        }
        for (int i = 0; i < selectedAL.length; i++) {
            if (!EtcProvider.isValidStartingItem(i, selectedAL[i])) {
                c.close();
                return;
            }
        }
        if (gender < 0 || gender > 2) {
            c.close();
            return;
        }

        // Create character
        final Optional<Integer> characterIdResult = DatabaseManager.characterAccessor().nextCharacterId();
        if (characterIdResult.isEmpty()) {
            c.write(LoginPacket.createNewCharacterResultFail(LoginResult.TIMEOUT));
            return;
        }
        final CharacterData characterData = new CharacterData(c.getAccount().getId(), characterIdResult.get());
        characterData.setCharacterName(name);

        final CharacterStat characterStat = new CharacterStat();
        characterStat.setGender(gender);
        characterStat.setSkin((byte) selectedAL[3]);
        characterStat.setFace(selectedAL[0]);
        characterStat.setHair(selectedAL[1] + selectedAL[2]);
        characterStat.setLevel((byte) 1);
        characterStat.setJob(job.getJobId());
        characterStat.setSubJob(selectedSubJob);
        characterStat.setBaseStr((short) 12);
        characterStat.setBaseDex((short) 5);
        characterStat.setBaseInt((short) 4);
        characterStat.setBaseLuk((short) 4);
        characterStat.setHp(50);
        characterStat.setMaxHp(50);
        characterStat.setMp(5);
        characterStat.setMaxMp(5);
        characterStat.setAp((short) 0);
        characterStat.setSp(ExtendSP.getDefault());
        characterStat.setExp(0);
        characterStat.setPop((short) 0);
        characterStat.setPosMap(10000);
        characterStat.setPortal((byte) 0);
        characterData.setCharacterStat(characterStat);

        final CharacterInventory characterInventory = new CharacterInventory();
        characterInventory.setEquipped(new Inventory(Short.MAX_VALUE));
        characterInventory.setEquipInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        characterInventory.setConsumeInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        characterInventory.setInstallInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        characterInventory.setEtcInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        characterInventory.setCashInventory(new Inventory(ServerConfig.INVENTORY_BASE_SLOTS));
        characterInventory.setMoney(0);
        characterData.setCharacterInventory(characterInventory);

        // Save character
        if (DatabaseManager.characterAccessor().newCharacter(characterData)) {
            c.write(LoginPacket.createNewCharacterResultSuccess(characterData));
        } else {
            c.write(LoginPacket.createNewCharacterResultFail(LoginResult.TIMEOUT));
        }
    }

    @Handler(InHeader.SELECT_CHAR)
    public static void handleSelectChar(Client c, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final String macAddress = inPacket.decodeString(); // CLogin::GetLocalMacAddress
        final String macAddressWithHddSerial = inPacket.decodeString(); // CLogin::GetLocalMacAddressWithHDDSerialNo

        if (ServerConfig.REQUIRE_SECONDARY_PASSWORD) {
            c.close();
        }
        final Account account = c.getAccount();
        if (account != null) {
        } else {
            c.close();
        }
    }

    @Handler(InHeader.INITIALIZE_SPW)
    public static void handleInitializeSecondaryPassword(Client c, InPacket inPacket) {
        inPacket.decodeByte(); // 1
        final int characterId = inPacket.decodeInt(); // dwCharacterID
        final String macAddress = inPacket.decodeString(); // CLogin::GetLocalMacAddress
        final String macAddressWithHddSerial = inPacket.decodeString(); // CLogin::GetLocalMacAddressWithHDDSerialNo
        final String secondaryPassword = inPacket.decodeString(); // sSPW

        final Account account = c.getAccount();
        if (account != null && !account.hasSecondaryPassword() &&
                DatabaseManager.accountAccessor().savePassword(account, "", secondaryPassword, true)) {
            account.setHasSecondaryPassword(true);
        } else {
            c.close();
        }
    }

    @Handler(InHeader.CHECK_SPW)
    public static void handleCheckSecondaryPassword(Client c, InPacket inPacket) {
        final String secondaryPassword = inPacket.decodeString(); // sSPW
        final int characterId = inPacket.decodeInt(); // dwCharacterID
        final String macAddress = inPacket.decodeString(); // CLogin::GetLocalMacAddress
        final String macAddressWithHddSerial = inPacket.decodeString(); // CLogin::GetLocalMacAddressWithHDDSerialNo

        final Account account = c.getAccount();
        if (account != null && account.hasSecondaryPassword() &&
                DatabaseManager.accountAccessor().checkPassword(account, secondaryPassword, true)) {

        } else {
            c.close();
        }
    }

    @Handler(InHeader.ALIVE_ACK)
    public static void handleAliveAck(Client c, InPacket inPacket) {
    }

    @Handler(InHeader.EXCEPTION_LOG)
    public static void handleExceptionLog(Client c, InPacket inPacket) {
        final String data = inPacket.decodeString();
        log.error("Exception log | {}", data);
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
