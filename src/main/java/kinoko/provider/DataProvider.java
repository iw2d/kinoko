package kinoko.provider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface DataProvider {
    String COMMENT_CHARACTER = "#";
    String DELIMITER = ",";

    static List<List<String>> readData(Path path) throws IOException {
        return Files.lines(path)
                .filter((line) -> !line.strip().startsWith(COMMENT_CHARACTER))
                .filter((line) -> !line.isEmpty())
                .map((line) -> line.substring(0, line.indexOf(COMMENT_CHARACTER)).strip())
                .map((line) -> Arrays.stream(line.split(DELIMITER)).map(String::strip).toList())
                .collect(Collectors.toList());
    }
}
