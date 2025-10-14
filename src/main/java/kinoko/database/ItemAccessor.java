package kinoko.database;

import kinoko.world.item.Item;


public interface ItemAccessor {
    default boolean saveItem(Item item){
        if (DatabaseManager.isRelational()){
            throw new UnsupportedOperationException("saveItem() needs to be implemented for this database.");
        }
        return true;
    }
}
