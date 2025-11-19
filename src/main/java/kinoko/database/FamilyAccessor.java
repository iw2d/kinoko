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

    default void saveAll(Collection<FamilyTree> families){

    }
}