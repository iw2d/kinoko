package kinoko.database;

import kinoko.server.family.FamilyTree;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildRanking;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FamilyAccessor {
    default Collection<FamilyTree> getAllFamilies(){
        throw new UnsupportedOperationException("This database must implement getting families");
    };

    default void saveFamily(FamilyTree family){
        throw new UnsupportedOperationException("This database must implement saving a singular family");
    }

    default void saveFamilies(Collection <FamilyTree> families){
        throw new UnsupportedOperationException("This database must implement saving families");
    }

    /**
     * Saves a collection of FamilyTree instances by delegating to {@link #saveFamilies(Collection)}.
     *
     * This method performs a null and empty check before calling {@code saveFamilies},
     * ensuring that no unnecessary operations are performed if the collection is empty.
     *
     * Note: This method is **not intended to be overridden** by implementing classes.
     * However, it is a default method in the interface, so technically it **can** be overridden if necessary.
     *
     * @param families the collection of FamilyTree objects to save
     */
    default void saveAll(Collection<FamilyTree> families){
        if (families == null || families.isEmpty()) return;

        saveFamilies(families);
    }
}