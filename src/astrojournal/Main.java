package astrojournal;

import astrojournal.data.ObservationRepository;
import astrojournal.data.SettingsStore;
import astrojournal.data.TextFileStore;
import astrojournal.service.ObservationService;
import astrojournal.ui.MainFrame;
import astrojournal.ui.theme.Theme;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {

    private static final Path DATA_DIRECTORY = Path.of("data");
    private static final String BACKGROUND_KEY = "background.image";

    private Main() {
    }

    public static void main(String[] args) {
        Theme.installDefaults();
        SwingUtilities.invokeLater(Main::launch);
    }

    private static void launch() {
        try {
            SettingsStore settings = new SettingsStore(DATA_DIRECTORY.resolve("settings.properties"));
            settings.load();

            ObservationService service = new ObservationService(
                    new ObservationRepository(new TextFileStore(DATA_DIRECTORY.resolve("observations.txt"))));

            MainFrame frame = new MainFrame(service, storedBackground(settings),
                    chosen -> saveBackground(settings, chosen));
            frame.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "AstroJournal could not start:\n" + e.getMessage(),
                    "AstroJournal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Path storedBackground(SettingsStore settings) {
        String stored = settings.get(BACKGROUND_KEY);
        if (stored == null) {
            return null;
        }
        Path path = Path.of(stored);
        return Files.exists(path) ? path : null;
    }

    private static void saveBackground(SettingsStore settings, Path image) {
        settings.set(BACKGROUND_KEY, image.toAbsolutePath().toString());
        try {
            settings.save();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not save the background setting:\n" + e.getMessage(),
                    "AstroJournal", JOptionPane.ERROR_MESSAGE);
        }
    }
}
