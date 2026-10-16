package astrojournal.ui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.nio.file.Path;
import java.util.Optional;

public final class ImageChooser {

    private static final FileNameExtensionFilter IMAGE_FILTER = new FileNameExtensionFilter(
            "Images (png, jpg, gif, bmp)", "png", "jpg", "jpeg", "gif", "bmp");

    private ImageChooser() {
    }

    public static Optional<Path> choose(Component parent, String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(IMAGE_FILTER);
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return Optional.empty();
        }
        return Optional.of(chooser.getSelectedFile().toPath());
    }
}
