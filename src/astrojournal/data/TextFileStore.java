package astrojournal.data;

import astrojournal.model.Observation;
import astrojournal.util.TextEscaper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TextFileStore {

    private static final String HEADER = "# id|date|time|objectName|type|location|equipment|imageFile|notes";
    private static final char DELIMITER = '|';
    private static final int FIELD_COUNT = 9;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final Path file;

    public TextFileStore(Path file) {
        this.file = file;
    }

    public List<Observation> load() throws IOException {
        if (Files.notExists(file)) {
            return new ArrayList<>();
        }
        var observations = new ArrayList<Observation>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String trimmed = line.strip();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            observations.add(parse(trimmed));
        }
        return observations;
    }

    public void save(List<Observation> observations) throws IOException {
        Path target = file.toAbsolutePath();
        Path directory = target.getParent();
        Files.createDirectories(directory);
        Path temp = Files.createTempFile(directory, "observations", ".tmp");
        try {
            var lines = new ArrayList<String>();
            lines.add(HEADER);
            for (Observation observation : observations) {
                lines.add(format(observation));
            }
            Files.write(temp, lines, StandardCharsets.UTF_8);
            move(temp, target);
        } catch (IOException e) {
            Files.deleteIfExists(temp);
            throw e;
        }
    }

    private static void move(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Observation parse(String line) {
        List<String> fields = splitFields(line);
        if (fields.size() != FIELD_COUNT) {
            throw new IllegalArgumentException("Malformed observation line: " + line);
        }
        return new Observation(
                Integer.parseInt(fields.get(0)),
                LocalDate.parse(fields.get(1), DATE_FORMAT),
                LocalTime.parse(fields.get(2), TIME_FORMAT),
                TextEscaper.unescape(fields.get(3)),
                TextEscaper.unescape(fields.get(4)),
                TextEscaper.unescape(fields.get(5)),
                TextEscaper.unescape(fields.get(6)),
                TextEscaper.unescape(fields.get(7)),
                TextEscaper.unescape(fields.get(8)));
    }

    private static String format(Observation observation) {
        var fields = List.of(
                Integer.toString(observation.id()),
                observation.date().format(DATE_FORMAT),
                observation.time().format(TIME_FORMAT),
                TextEscaper.escape(observation.objectName()),
                TextEscaper.escape(observation.celestialType()),
                TextEscaper.escape(observation.location()),
                TextEscaper.escape(observation.equipment()),
                TextEscaper.escape(observation.imageFileName()),
                TextEscaper.escape(observation.notes()));
        return String.join(String.valueOf(DELIMITER), fields);
    }

    private static List<String> splitFields(String line) {
        var fields = new ArrayList<String>();
        var current = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escaped) {
                current.append(c);
                escaped = false;
            } else if (c == '\\') {
                current.append(c);
                escaped = true;
            } else if (c == DELIMITER) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
