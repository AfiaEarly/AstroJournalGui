package astrojournal.ui;

import astrojournal.model.CelestialType;
import astrojournal.service.ObservationService;
import astrojournal.ui.theme.Theme;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainFrame extends JFrame {

    private static final String ALL_TYPES = "All types";
    private static final String COVER_CARD = "cover";
    private static final String BOOK_CARD = "book";

    private final ObservationService service;
    private final Consumer<Path> backgroundSaver;
    private final BackgroundPanel backgroundPanel = new BackgroundPanel();
    private final CardLayout cards = new CardLayout();
    private final JLabel statusLabel = Theme.muted("");

    public MainFrame(ObservationService service, Path backgroundImage, Consumer<Path> backgroundSaver) {
        this.service = service;
        this.backgroundSaver = backgroundSaver;

        setTitle("AstroJournal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(1120, 720);
        setLocationRelativeTo(null);

        backgroundPanel.setLayout(cards);
        backgroundPanel.setBorder(Theme.padding(Theme.SPACE_LG));
        backgroundPanel.add(buildCoverView(), COVER_CARD);
        backgroundPanel.add(buildBookView(), BOOK_CARD);

        setContentPane(backgroundPanel);

        if (backgroundImage != null) {
            backgroundPanel.setBackgroundImage(backgroundImage);
        }
        refreshStatus();
        showCover();
    }

    private void showCover() {
        cards.show(backgroundPanel, COVER_CARD);
    }

    private void showBook() {
        cards.show(backgroundPanel, BOOK_CARD);
    }

    private JPanel buildCoverView() {
        JPanel view = Theme.transparentPanel(new BorderLayout());

        CoverPanel cover = new CoverPanel();
        cover.setPreferredSize(new Dimension(430, 560));
        cover.setLayout(new BoxLayout(cover, BoxLayout.Y_AXIS));

        JLabel titleLabel = Theme.coverTitle("AstroJournal");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitleLabel = Theme.coverSubtitle("A journal of the night sky");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addButton = Theme.primaryButton("Add Observation");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(event -> showBook());

        cover.add(Box.createVerticalGlue());
        cover.add(titleLabel);
        cover.add(Box.createVerticalStrut(Theme.SPACE_SM));
        cover.add(subtitleLabel);
        cover.add(Box.createVerticalStrut(Theme.SPACE_XL));
        cover.add(addButton);
        cover.add(Box.createVerticalGlue());

        JPanel center = Theme.transparentPanel(new GridBagLayout());
        center.add(cover);
        view.add(center, BorderLayout.CENTER);

        JButton uploadButton = Theme.smallSecondaryButton("Upload Background");
        uploadButton.addActionListener(event -> chooseBackground());
        JButton exitButton = Theme.smallSecondaryButton("Exit");
        exitButton.addActionListener(event -> System.exit(0));

        JPanel actions = Theme.transparentPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACE_SM, 0));
        actions.add(uploadButton);
        actions.add(exitButton);
        view.add(actions, BorderLayout.SOUTH);
        return view;
    }

    private JPanel buildBookView() {
        JPanel view = Theme.transparentPanel(new BorderLayout());
        view.setBorder(Theme.padding(Theme.SPACE_MD));

        BookPanel book = new BookPanel();
        book.setLayout(new BorderLayout());
        book.add(buildTopArea(), BorderLayout.NORTH);
        book.add(buildTabs(), BorderLayout.CENTER);
        book.add(buildStatusBar(), BorderLayout.SOUTH);
        view.add(book, BorderLayout.CENTER);
        return view;
    }

    private JPanel buildTopArea() {
        JPanel top = Theme.transparentPanel(new BorderLayout());
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildToolbar(), BorderLayout.SOUTH);
        return top;
    }

    private JPanel buildHeader() {
        JPanel header = Theme.transparentPanel(new BorderLayout());
        header.setBorder(Theme.padding(Theme.SPACE_LG, Theme.SPACE_XL, Theme.SPACE_SM, Theme.SPACE_XL));

        JLabel titleLabel = Theme.title("AstroJournal");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitleLabel = Theme.muted("Chart your nights under the stars");
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel text = Theme.transparentPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(Theme.SPACE_XS));
        text.add(subtitleLabel);
        header.add(text, BorderLayout.WEST);

        JButton closeButton = Theme.secondaryButton("Close Book");
        closeButton.addActionListener(event -> showCover());
        JButton exitButton = Theme.secondaryButton("Exit");
        exitButton.addActionListener(event -> System.exit(0));

        JPanel actions = Theme.transparentPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACE_SM, 0));
        actions.add(closeButton);
        actions.add(exitButton);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildToolbar() {
        JButton addButton = Theme.primaryButton("Add");
        JButton editButton = Theme.secondaryButton("Edit");
        JButton deleteButton = Theme.secondaryButton("Delete");
        JComboBox<String> typeFilter = new JComboBox<>(filterOptions());
        JToggleButton sortToggle = Theme.toggleButton("Newest first");

        addButton.addActionListener(event -> comingSoon("Adding observations"));
        editButton.addActionListener(event -> comingSoon("Editing observations"));
        deleteButton.addActionListener(event -> comingSoon("Deleting observations"));

        JPanel toolbar = Theme.transparentPanel(new FlowLayout(FlowLayout.CENTER, Theme.SPACE_SM, 0));
        toolbar.setBorder(Theme.padding(Theme.SPACE_SM, Theme.SPACE_XL, Theme.SPACE_MD, Theme.SPACE_XL));
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(Box.createHorizontalStrut(Theme.SPACE_LG));
        toolbar.add(Theme.label("Filter"));
        toolbar.add(typeFilter);
        toolbar.add(Box.createHorizontalStrut(Theme.SPACE_MD));
        toolbar.add(Theme.label("Sort"));
        toolbar.add(sortToggle);
        return toolbar;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.setFont(Theme.FONT_TAB);
        tabs.setBorder(Theme.padding(Theme.SPACE_MD, Theme.SPACE_MD, 0, Theme.SPACE_MD));
        tabs.addTab("Journal", placeholder("No observations yet", "Your recorded nights will appear here."));
        tabs.addTab("Stats", placeholder("No statistics yet", "Counts per celestial type will appear here."));
        tabs.addTab("Streaks", placeholder("No streaks yet", "Your observation streak will appear here."));
        updateTabColors(tabs);
        tabs.addChangeListener(event -> updateTabColors(tabs));
        return tabs;
    }

    private void updateTabColors(JTabbedPane tabs) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            boolean selected = i == tabs.getSelectedIndex();
            tabs.setForegroundAt(i, selected ? Theme.INK : Theme.INK_MUTED);
        }
    }

    private JPanel placeholder(String title, String message) {
        PagePanel page = new PagePanel(new GridBagLayout());
        JPanel content = Theme.transparentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel heading = Theme.heading(title);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel detail = Theme.muted(message);
        detail.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(heading);
        content.add(Box.createVerticalStrut(Theme.SPACE_SM));
        content.add(detail);
        page.add(content);
        return page;
    }

    private JPanel buildStatusBar() {
        JPanel bar = Theme.transparentPanel(new BorderLayout());
        bar.setBorder(Theme.padding(Theme.SPACE_SM, Theme.SPACE_XL, Theme.SPACE_MD, Theme.SPACE_XL));
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void chooseBackground() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a background image");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(
                new FileNameExtensionFilter("Images (png, jpg, gif, bmp)", "png", "jpg", "jpeg", "gif", "bmp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path chosen = chooser.getSelectedFile().toPath();
        backgroundSaver.accept(chosen);
        backgroundPanel.setBackgroundImage(chosen);
    }

    private void refreshStatus() {
        int count = service.all().size();
        statusLabel.setText(count + (count == 1 ? " observation recorded" : " observations recorded"));
    }

    private void comingSoon(String feature) {
        JOptionPane.showMessageDialog(this, feature + " is not available yet.",
                "AstroJournal", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String[] filterOptions() {
        List<String> options = new ArrayList<>();
        options.add(ALL_TYPES);
        for (CelestialType type : CelestialType.standard()) {
            options.add(type.label());
        }
        return options.toArray(String[]::new);
    }
}
