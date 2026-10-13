package astrojournal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageManager {

    private final Path imagesDirectory;

    public ImageManager(Path imagesDirectory) {
        this.imagesDirectory = imagesDirectory;
    }

    public String importImage(Path source) throws IOException {
        Files.createDirectories(imagesDirectory);
        String fileName = uniqueFileName(source.getFileName().toString());
        Files.copy(source, resolve(fileName));
        return fileName;
    }

    public Path resolve(String fileName) {
        return imagesDirectory.resolve(fileName);
    }

    private String uniqueFileName(String original) {
        if (Files.notExists(resolve(original))) {
            return original;
        }
        String base = baseName(original);
        String extension = extension(original);
        int counter = 1;
        String candidate;
        do {
            candidate = base + "-" + counter++ + extension;
        } while (Files.exists(resolve(candidate)));
        return candidate;
    }

    private static String baseName(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot <= 0 ? fileName : fileName.substring(0, dot);
    }

    private static String extension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot <= 0 ? "" : fileName.substring(dot);
    }
}
