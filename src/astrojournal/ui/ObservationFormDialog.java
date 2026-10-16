package astrojournal.ui;

import astrojournal.model.CelestialType;
import astrojournal.model.Observation;
import astrojournal.service.ImageManager;
import astrojournal.ui.theme.Theme;
import astrojournal.util.DateUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ObservationFormDialog extends JDialog {

    private static final String CUSTOM_OPTION = "Custom\u2026";
    private static final String NO_PHOTO_MESSAGE = "No photo attached";
    private static final int LABEL_WIDTH = 100;
    private static final int PHOTO_WIDTH = 340;
    private static final int PHOTO_HEIGHT = 170;

    private final ImageManager imageManager;
    private final int id;

    private final JTextField dateField = Theme.textField(10);
    private final JTextField timeField = Theme.textField(6);
    private final JTextField objectField = Theme.textField(18);
    private final JComboBox<String> typeCombo = new JComboBox<>();
    private final JTextField customTypeField = Theme.textField(12);
    private final JTextField locationField = Theme.textField(18);
    private final JTextField equipmentField = Theme.textField(18);
    private final JTextArea notesArea = Theme.textArea(4, 28);
    private final PhotoPanel photoPanel = new PhotoPanel(NO_PHOTO_MESSAGE);
    private final JButton removeImageButton = Theme.smallSecondaryButton("Remove");

    private String imageFileName = "";
    private Path pendingImagePath;
    private Observation result;

    private ObservationFormDialog(Window owner, Observation existing, ImageManager imageManager) {
        super(owner, existing == null ? "New Observation" : "Edit Observation", ModalityType.APPLICATION_MODAL);
        this.imageManager = imageManager;
        this.id = existing == null ? 0 : existing.id();

        setContentPane(buildContent(existing != null));
        prefill(existing);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(event -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(owner);
    }

    public static Optional<Observation> open(Component parent, Observation existing, ImageManager imageManager) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        ObservationFormDialog dialog = new ObservationFormDialog(owner, existing, imageManager);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.result);
    }

    private void prefill(Observation existing) {
        if (existing == null) {
            dateField.setText(DateUtil.formatDate(LocalDate.now()));
            timeField.setText(DateUtil.formatTime(LocalTime.now().withSecond(0).withNano(0)));
        } else {
            dateField.setText(DateUtil.formatDate(existing.date()));
            timeField.setText(DateUtil.formatTime(existing.time()));
            objectField.setText(existing.objectName());
            locationField.setText(existing.location());
            equipmentField.setText(existing.equipment());
            notesArea.setText(existing.notes());
            imageFileName = existing.imageFileName() == null ? "" : existing.imageFileName();
        }
        populateTypes();
        selectType(existing == null ? null : existing.celestialType());
        refreshPreview();
    }

    private void populateTypes() {
        typeCombo.removeAllItems();
        for (CelestialType type : CelestialType.standard()) {
            typeCombo.addItem(type.label());
        }
        typeCombo.addItem(CUSTOM_OPTION);
    }

    private void selectType(String type) {
        if (type == null || type.isBlank()) {
            typeCombo.setSelectedItem(CelestialType.STAR.label());
            return;
        }
        Optional<CelestialType> standard = CelestialType.fromLabel(type);
        if (standard.isPresent()) {
            typeCombo.setSelectedItem(standard.get().label());
            return;
        }
        typeCombo.setSelectedItem(CUSTOM_OPTION);
        customTypeField.setText(type);
    }

    private JPanel buildContent(boolean editing) {
        JPanel content = Theme.paperPanel(new BorderLayout(0, Theme.SPACE_MD));
        content.setBorder(Theme.padding(Theme.SPACE_LG));
        content.add(Theme.heading(editing ? "Edit Observation" : "New Observation"), BorderLayout.NORTH);
        content.add(buildForm(), BorderLayout.CENTER);
        content.add(buildActions(), BorderLayout.SOUTH);
        return content;
    }

    private JPanel buildForm() {
        JPanel form = Theme.transparentPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        addRow(form, "Date / Time", buildDateTimeRow());
        addRow(form, "Object", objectField);
        addRow(form, "Type", buildTypeRow());
        addRow(form, "Location", locationField);
        addRow(form, "Equipment", equipmentField);
        addRow(form, "Notes", buildNotesRow());
        addRow(form, "Photo", buildPhotoRow());
        return form;
    }

    private void addRow(JPanel form, String caption, JComponent field) {
        if (form.getComponentCount() > 0) {
            form.add(Box.createVerticalStrut(Theme.SPACE_SM));
        }
        JLabel label = Theme.muted(caption);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, label.getPreferredSize().height));

        JPanel row = Theme.transparentPanel(new BorderLayout(Theme.SPACE_SM, 0));
        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(row);
    }

    private JPanel buildDateTimeRow() {
        JPanel row = Theme.transparentPanel(new BorderLayout(Theme.SPACE_SM, 0));
        row.add(dateField, BorderLayout.CENTER);
        row.add(timeField, BorderLayout.EAST);
        return row;
    }

    private JPanel buildTypeRow() {
        typeCombo.addActionListener(
                event -> customTypeField.setEnabled(CUSTOM_OPTION.equals(typeCombo.getSelectedItem())));
        JPanel row = Theme.transparentPanel(new BorderLayout(Theme.SPACE_SM, 0));
        row.add(typeCombo, BorderLayout.WEST);
        row.add(customTypeField, BorderLayout.CENTER);
        return row;
    }

    private JScrollPane buildNotesRow() {
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.PAPER_BORDER));
        scrollPane.getVerticalScrollBar().setUnitIncrement(Theme.SPACE_MD);
        return scrollPane;
    }

    private JPanel buildPhotoRow() {
        photoPanel.setPreferredSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));

        JButton chooseImageButton = Theme.smallSecondaryButton("Choose Image\u2026");
        chooseImageButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        chooseImageButton.addActionListener(event -> chooseImage());
        removeImageButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        removeImageButton.addActionListener(event -> removeImage());

        JPanel buttons = Theme.transparentPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.add(chooseImageButton);
        buttons.add(Box.createVerticalStrut(Theme.SPACE_SM));
        buttons.add(removeImageButton);

        JPanel buttonsHolder = Theme.transparentPanel(new BorderLayout());
        buttonsHolder.add(buttons, BorderLayout.NORTH);

        JPanel row = Theme.transparentPanel(new BorderLayout(Theme.SPACE_MD, 0));
        row.add(photoPanel, BorderLayout.CENTER);
        row.add(buttonsHolder, BorderLayout.EAST);
        return row;
    }

    private JPanel buildActions() {
        JButton cancelButton = Theme.secondaryButton("Cancel");
        cancelButton.addActionListener(event -> dispose());
        JButton saveButton = Theme.primaryButton("Save");
        saveButton.addActionListener(event -> save());
        getRootPane().setDefaultButton(saveButton);

        JPanel actions = Theme.transparentPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACE_SM, 0));
        actions.add(cancelButton);
        actions.add(saveButton);
        return actions;
    }

    private void chooseImage() {
        ImageChooser.choose(this, "Choose a photo").ifPresent(path -> {
            pendingImagePath = path;
            refreshPreview();
        });
    }

    private void removeImage() {
        pendingImagePath = null;
        imageFileName = "";
        refreshPreview();
    }

    private void refreshPreview() {
        Path source = currentImageSource();
        if (source == null) {
            photoPanel.setImage(null);
            photoPanel.setEmptyMessage(NO_PHOTO_MESSAGE);
        } else {
            applyPreview(source);
        }
        removeImageButton.setEnabled(source != null);
    }

    private void applyPreview(Path source) {
        try {
            BufferedImage image = imageManager.read(source);
            if (image == null) {
                photoPanel.setImage(null);
                photoPanel.setEmptyMessage("The photo could not be found");
            } else {
                photoPanel.setImage(image);
            }
        } catch (IOException e) {
            photoPanel.setImage(null);
            photoPanel.setEmptyMessage("The photo could not be opened");
        }
    }

    private Path currentImageSource() {
        if (pendingImagePath != null) {
            return pendingImagePath;
        }
        return imageFileName.isBlank() ? null : imageManager.resolve(imageFileName);
    }

    private void save() {
        List<String> errors = new ArrayList<>();
        LocalDate date = parseDate(errors);
        LocalTime time = parseTime(errors);
        String objectName = objectField.getText().strip();
        if (objectName.isEmpty()) {
            errors.add("Object name is required.");
        }
        String type = resolveType();
        if (type.isEmpty()) {
            errors.add("Celestial type is required.");
        }
        if (!errors.isEmpty()) {
            showErrors(errors);
            return;
        }

        String fileName;
        try {
            fileName = storeImage();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "The photo could not be saved:\n" + e.getMessage(),
                    "AstroJournal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        result = new Observation(id, date, time, objectName, type,
                locationField.getText().strip(), equipmentField.getText().strip(), fileName,
                notesArea.getText().strip());
        dispose();
    }

    private LocalDate parseDate(List<String> errors) {
        String text = dateField.getText().strip();
        if (text.isEmpty()) {
            errors.add("Date is required (yyyy-MM-dd).");
            return null;
        }
        try {
            return DateUtil.parseDate(text);
        } catch (DateTimeParseException e) {
            errors.add("\"" + text + "\" is not a valid date. Use yyyy-MM-dd.");
            return null;
        }
    }

    private LocalTime parseTime(List<String> errors) {
        String text = timeField.getText().strip();
        if (text.isEmpty()) {
            errors.add("Time is required (HH:mm).");
            return null;
        }
        try {
            return DateUtil.parseTime(text);
        } catch (DateTimeParseException e) {
            errors.add("\"" + text + "\" is not a valid time. Use HH:mm.");
            return null;
        }
    }

    private String resolveType() {
        Object selected = typeCombo.getSelectedItem();
        if (CUSTOM_OPTION.equals(selected)) {
            return customTypeField.getText().strip();
        }
        return selected == null ? "" : selected.toString();
    }

    private String storeImage() throws IOException {
        return pendingImagePath == null ? imageFileName : imageManager.importImage(pendingImagePath);
    }

    private void showErrors(List<String> errors) {
        String message = "Please check the following:\n\n"
                + String.join("\n", errors.stream().map(error -> "\u2022 " + error).toList());
        JOptionPane.showMessageDialog(this, message, "AstroJournal", JOptionPane.WARNING_MESSAGE);
    }
}
