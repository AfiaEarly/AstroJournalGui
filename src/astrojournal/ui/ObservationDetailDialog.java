package astrojournal.ui;

import astrojournal.model.Observation;
import astrojournal.service.ImageManager;
import astrojournal.ui.theme.Theme;
import astrojournal.util.DateUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ObservationDetailDialog extends JDialog {

    private static final int NOTES_HEIGHT = 84;
    private static final String NO_VALUE = "\u2014";

    private ObservationDetailDialog(Window owner, Observation observation, ImageManager imageManager) {
        super(owner, observation.objectName(), ModalityType.APPLICATION_MODAL);
        setContentPane(buildContent(observation, imageManager));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(event -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setLocationRelativeTo(owner);
    }

    public static void open(Component parent, Observation observation, ImageManager imageManager) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        new ObservationDetailDialog(owner, observation, imageManager).setVisible(true);
    }

    private JPanel buildContent(Observation observation, ImageManager imageManager) {
        JPanel content = Theme.paperPanel(new BorderLayout(0, Theme.SPACE_MD));
        content.setBorder(Theme.padding(Theme.SPACE_LG));
        content.add(buildHeader(observation), BorderLayout.NORTH);
        content.add(buildPhoto(observation, imageManager), BorderLayout.CENTER);
        content.add(buildDetails(observation), BorderLayout.SOUTH);
        return content;
    }

    private JPanel buildHeader(Observation observation) {
        JLabel title = Theme.heading(observation.objectName());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = Theme.muted(observation.celestialType() + "  \u00b7  "
                + DateUtil.formatDate(observation.date()) + "  \u00b7  " + DateUtil.formatTime(observation.time()));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel text = Theme.transparentPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(Theme.SPACE_XS));
        text.add(subtitle);

        JButton closeButton = Theme.secondaryButton("Close");
        closeButton.addActionListener(event -> dispose());
        JPanel actions = Theme.transparentPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.add(closeButton);

        JPanel header = Theme.transparentPanel(new BorderLayout());
        header.add(text, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private PhotoPanel buildPhoto(Observation observation, ImageManager imageManager) {
        PhotoPanel photo = new PhotoPanel("No photo attached to this observation");
        String fileName = observation.imageFileName();
        if (fileName == null || fileName.isBlank()) {
            return photo;
        }
        try {
            BufferedImage image = imageManager.load(fileName);
            if (image == null) {
                photo.setEmptyMessage("The photo for this observation is missing");
                return photo;
            }
            photo.setImage(image);
        } catch (IOException e) {
            photo.setEmptyMessage("The photo for this observation could not be opened");
        }
        return photo;
    }

    private JPanel buildDetails(Observation observation) {
        JPanel summary = Theme.transparentPanel(new GridLayout(1, 2, Theme.SPACE_XL, 0));
        summary.add(valueField("Location", observation.location()));
        summary.add(valueField("Equipment", observation.equipment()));

        JPanel details = Theme.transparentPanel(new BorderLayout(0, Theme.SPACE_MD));
        details.setBorder(Theme.padding(Theme.SPACE_MD, 0, 0, 0));
        details.add(summary, BorderLayout.NORTH);
        details.add(notesField("Notes", observation.notes()), BorderLayout.CENTER);
        return details;
    }

    private JPanel valueField(String caption, String value) {
        return captioned(caption, Theme.label(displayValue(value)));
    }

    private JPanel notesField(String caption, String notes) {
        JTextArea area = new JTextArea(displayValue(notes));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(Theme.FONT_BODY);
        area.setBackground(Theme.PAPER_ALT);
        area.setForeground(Theme.INK);
        area.setBorder(Theme.padding(Theme.SPACE_SM));

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.PAPER_BORDER));
        scrollPane.setPreferredSize(new Dimension(0, NOTES_HEIGHT));
        scrollPane.getVerticalScrollBar().setUnitIncrement(Theme.SPACE_MD);
        return captioned(caption, scrollPane);
    }

    private JPanel captioned(String caption, JComponent content) {
        JLabel captionLabel = Theme.muted(caption);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = Theme.transparentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(captionLabel);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        panel.add(content);
        return panel;
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? NO_VALUE : value;
    }
}
