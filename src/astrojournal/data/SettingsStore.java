package astrojournal.data;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class SettingsStore {

    private final Path file;
    private final Properties properties = new Properties();

    public SettingsStore(Path file) {
        this.file = file;
    }

    public void load() throws IOException {
        properties.clear();
        if (Files.notExists(file)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
    }

    public void save() throws IOException {
        Path target = file.toAbsolutePath();
        Path directory = target.getParent();
        Files.createDirectories(directory);
        Path temp = Files.createTempFile(directory, "settings", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                properties.store(writer, null);
            }
            move(temp, target);
        } catch (IOException e) {
            Files.deleteIfExists(temp);
            throw e;
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void set(String key, String value) {
        if (value == null) {
            properties.remove(key);
        } else {
            properties.setProperty(key, value);
        }
    }

    private static void move(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
