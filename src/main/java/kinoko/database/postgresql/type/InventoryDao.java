package kinoko.database.postgresql.type;


import kinoko.world.item.*;
import kinoko.world.user.CharacterData;
import org.postgresql.util.PGobject;

import java.util.Map;
import java.util.EnumMap;
import java.sql.*;
import java.util.Collection;
import java.util.stream.Stream;


public class InventoryDao {

    /**
     * Saves a character's inventory to the database.
     *
     * This method collects all inventory entries from the character's InventoryManager,
     * saves all the items to the items table (creating new item_sn values if needed),
     * removes any items that the character no longer has, and updates the player inventory
     * table with the current inventory entries using a batch insert/update.
     *
     * @param conn the active database connection
     * @param characterData the character whose inventory is being saved
     * @throws SQLException if any SQL error occurs during the operation
     */
    public static void saveCharacter(Connection conn, CharacterData characterData) throws SQLException {
        String sqlInventory = """
        INSERT INTO player.inventory (character_id, inventory_type, slot, item_sn)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (item_sn)
        DO UPDATE SET slot = EXCLUDED.slot, inventory_type = EXCLUDED.inventory_type, character_id = EXCLUDED.character_id
    """;

            int characterId = characterData.getCharacterId();

            InventoryManager inv = characterData.getInventoryManager();
            Collection<InventoryEntry> allEntries = Stream.of(
                            inv.getEquipped(),
                            inv.getEquipInventory(),
                            inv.getConsumeInventory(),
                            inv.getInstallInventory(),
                            inv.getEtcInventory(),
                            inv.getCashInventory()
                    )
                    .flatMap(inventory -> inventory.getType()
                            .map(type -> inventory.asInventoryEntries(type).stream())
                            .orElseGet(Stream::empty)) // skip inventories with no type
                    .toList();

            // Extract just the Item objects
            Collection<Item> allItems = allEntries.stream()
                    .map(InventoryEntry::item)
                    .toList();

            ItemDao.saveItemsBatch(conn, allItems);  // Insert or Save the Items themselves.
            deleteUnusedItems(conn, characterId, allItems); // Remove any inventory items that the player no longer has.

        Map<InventoryType, PGobject> typeObjects = new EnumMap<>(InventoryType.class);
        for (InventoryType type : InventoryType.values()) {
            PGobject obj = new PGobject();
            obj.setType("inventory_type_enum");
            obj.setValue(type.name());
            typeObjects.put(type, obj);
        }

        // Add in items to the player inventory.
        try (PreparedStatement stmtInventory = conn.prepareStatement(sqlInventory);
        ) {
            // --- Insert/update items ---
            for (InventoryEntry entry : allEntries) {
                Item item = entry.item();
                InventoryType type = entry.type(); // from InventoryEntry
                PGobject enumValue = typeObjects.get(type);

                stmtInventory.setInt(1, characterId);
                stmtInventory.setObject(2, enumValue);
                stmtInventory.setInt(3, entry.slot());
                // Item SN should be updated since we handled all items earlier.
                stmtInventory.setLong(4, item.getItemSn());
                stmtInventory.addBatch();
            }

            stmtInventory.executeBatch();
        }
    }


    /**
     * Remove inventory items that are no longer in use for a specific character.
     *
     * If the provided collection of items is non-empty, this method deletes all rows in
     * `player.inventory` for the given character whose `item_sn` is not present in the collection.
     * CAUTION: If the collection is empty, it deletes all inventory items for the character.
     * This is because it is expected for the player's entire inventory to be passed in.
     *
     * @param conn   the database connection to use
     * @param charId the ID of the character whose inventory should be cleaned
     * @param items  the collection of items to retain; all others will be deleted
     * @throws SQLException if a database access error occurs
     */
    private static void deleteUnusedItems(Connection conn, int charId, Collection<Item> items) throws SQLException {
        if (!items.isEmpty()) {
            Long[] itemSnArray = items.stream()
                    .filter(item -> !item.hasNoSN())   // skip items with no SN, aka has not been created in item.Items yet.
                    .map(Item::getItemSn)
                    .toArray(Long[]::new);

            if (itemSnArray.length == 0) {
                return; // nothing to delete
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM player.inventory WHERE character_id = ? AND item_sn <> ALL (?)")) {
                deleteStmt.setInt(1, charId);
                Array sqlArray = conn.createArrayOf("bigint", itemSnArray);
                deleteStmt.setArray(2, sqlArray);
                deleteStmt.executeUpdate();
            }
        } else {
            // delete all items if the player's collection is empty
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM player.inventory WHERE character_id = ?")) {
                deleteStmt.setInt(1, charId);
                deleteStmt.executeUpdate();
            }
        }
    }

    /**
     * Loads a character's complete inventory data from the database and constructs an {@link InventoryManager}.
     *
     * This method queries all inventory slots belonging to the given character, joins them with the corresponding
     * item data from {@code item.full_item}, and populates the proper inventory categories such as EQUIPPED,
     * EQUIP, CONSUME, INSTALL, ETC, and CASH.
     *
     * Each item is deserialized using {@link ItemDao#from(ResultSet)} and inserted into its respective inventory
     * container in the {@link InventoryManager}.
     *
     * @param conn         the active database connection
     * @param characterId  the character ID whose inventory will be loaded
     * @return a fully populated {@link InventoryManager} containing all of the character's items
     * @throws SQLException if a database access error occurs or the query fails
     * @throws IllegalArgumentException if an unknown inventory type is encountered
     */
    public static InventoryManager loadInventoryManager(Connection conn, int characterId) throws SQLException {
        InventoryManager im = new InventoryManager();

        String sql = """
        SELECT inv.inventory_type, inv.slot, fi.*
        FROM player.inventory inv
        JOIN item.full_item fi ON inv.item_sn = fi.item_sn
        WHERE inv.character_id = ?
        ORDER BY inv.inventory_type, inv.slot
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int slot = rs.getInt("slot");
                Item item = ItemDao.from(rs);

                String invType = rs.getString("inventory_type");
                switch (invType.toUpperCase()) {
                    case "EQUIPPED" -> im.getEquipped().addItem(slot, item);
                    case "EQUIP" -> im.getEquipInventory().addItem(slot, item);
                    case "CONSUME" -> im.getConsumeInventory().addItem(slot, item);
                    case "INSTALL" -> im.getInstallInventory().addItem(slot, item);
                    case "ETC" -> im.getEtcInventory().addItem(slot, item);
                    case "CASH" -> im.getCashInventory().addItem(slot, item);
                    default -> throw new IllegalArgumentException("Unknown inventory type: " + invType);
                }
            }
        }

        return im;
    }

    /**
     * Loads a character's equipped inventory.
     *
     * This includes all equipped items, along with their EquipData, PetData, and RingData if present.
     *
     * @param conn        the database connection
     * @param characterId the ID of the character
     * @return an Inventory object populated with all equipped items
     * @throws SQLException if a database access error occurs
     */
    public static Inventory loadEquippedInventory(Connection conn, int characterId) throws SQLException {
        Inventory equipped = new Inventory(24, InventoryType.EQUIPPED); // default equipped size

        String sql = """
            SELECT f.*, i.slot
            FROM player.inventory i
            JOIN item.full_item f ON i.item_sn = f.item_sn
            WHERE i.character_id = ? AND i.inventory_type = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);

            PGobject enumValue = new PGobject();
            enumValue.setType("inventory_type_enum");
            enumValue.setValue(InventoryType.EQUIPPED.name());
            stmt.setObject(2, enumValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long itemSn = rs.getLong("item_sn");
                    int slot = rs.getInt("slot");
                    int itemId = rs.getInt("item_id");
                    short quantity = rs.getShort("quantity");
                    short attribute = rs.getShort("attribute");
                    String title = rs.getString("title");
                    Timestamp dateExpireTs = rs.getTimestamp("date_expire");

                    EquipData equipData = null;
                    if (rs.getObject("inc_str") != null) {
                        equipData = new EquipData(
                                rs.getShort("inc_str"),
                                rs.getShort("inc_dex"),
                                rs.getShort("inc_int"),
                                rs.getShort("inc_luk"),
                                rs.getShort("inc_max_hp"),
                                rs.getShort("inc_max_mp"),
                                rs.getShort("inc_pad"),
                                rs.getShort("inc_mad"),
                                rs.getShort("inc_pdd"),
                                rs.getShort("inc_mdd"),
                                rs.getShort("inc_acc"),
                                rs.getShort("inc_eva"),
                                rs.getShort("inc_craft"),
                                rs.getShort("inc_speed"),
                                rs.getShort("inc_jump"),
                                rs.getByte("ruc"),
                                rs.getByte("cuc"),
                                rs.getInt("iuc"),
                                rs.getByte("chuc"),
                                rs.getByte("grade"),
                                rs.getShort("option_1"),
                                rs.getShort("option_2"),
                                rs.getShort("option_3"),
                                rs.getShort("socket_1"),
                                rs.getShort("socket_2"),
                                rs.getByte("level_up_type"),
                                rs.getByte("level"),
                                rs.getInt("exp"),
                                rs.getInt("durability")
                        );
                    }

                    PetData petData = null;
                    if (rs.getObject("pet_name") != null) {
                        petData = new PetData(
                                rs.getString("pet_name"),
                                rs.getByte("pet_level"),
                                rs.getByte("fullness"),
                                rs.getShort("tameness"),
                                rs.getShort("pet_skill"),
                                rs.getShort("pet_attribute"),
                                rs.getInt("remain_life")
                        );
                    }

                    RingData ringData = null;
                    if (rs.getObject("pair_character_id") != null) {
                        ringData = new RingData(
                                rs.getInt("pair_character_id"),
                                rs.getString("pair_character_name"),
                                rs.getLong("pair_item_sn")
                        );
                    }

                    Item item = new Item(
                            itemId,
                            quantity,
                            itemSn,
                            false, // cash flag, will get set on save.
                            attribute,
                            title,
                            dateExpireTs != null ? dateExpireTs.toInstant() : null,
                            equipData,
                            petData,
                            ringData
                    );

                    equipped.putItem(slot, item);
                }
            }
        }

        return equipped;
    }

}