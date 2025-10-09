package kinoko.database.postgresql.type;


import kinoko.world.item.InventoryEntry;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
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
        ON CONFLICT (character_id, item_sn)
        DO UPDATE SET slot = EXCLUDED.slot, inventory_type = EXCLUDED.inventory_type
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
}