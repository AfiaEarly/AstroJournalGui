package astrojournal.ui;

import astrojournal.model.CelestialType;
import astrojournal.model.Observation;
import astrojournal.service.ImageManager;
import astrojournal.service.ObservationService;
import astrojournal.ui.theme.Theme;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainFrame extends JFrame {

    private static final String ALL_TYPES = "All types";
    private static final String COVER_CARD = "cover";
    private static final String BOOK_CARD = "book";
    private static final int MINIMUM_HEIGHT = 620;
    private static final int PAGE_CHROME_WIDTH = 300;

    private final ObservationService service;
    private final ImageManager imageManager;
    private final Consumer<Path> backgroundSaver;
    private final BackgroundPanel backgroundPanel = new BackgroundPanel();
    private final CardLayout cards = new CardLayout();
    private final JLabel statusLabel = Theme.muted("");
    private final ObservationTablePanel journalTable = new ObservationTablePanel();
    private final JComboBox<String> typeFilter = new JComboBox<>();
    private final JToggleButton sortToggle = Theme.toggleButton("Newest first");
    private final JButton editButton = Theme.secondaryButton("Edit");
    private final JButton deleteButton = Theme.secondaryButton("Delete");
    private boolean updatingFilterOptions;
    private JPanel toolbar;
    private JTabbedPane tabs;

    public MainFrame(ObservationService service, ImageManager imageManager, Path backgroundImage,
            Consumer<Path> backgroundSaver) {
        this.service = service;
        this.imageManager = imageManager;
        this.backgroundSaver = backgroundSaver;

        setTitle("AstroJournal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1120, 720);

        backgroundPanel.setLayout(cards);
        backgroundPanel.setBorder(Theme.padding(Theme.SPACE_LG));
        backgroundPanel.add(buildCoverView(), COVER_CARD);
        backgroundPanel.add(buildBookView(), BOOK_CARD);

        setContentPane(backgroundPanel);
        setMinimumSize(new Dimension(toolbar.getPreferredSize().width + PAGE_CHROME_WIDTH, MINIMUM_HEIGHT));
        setLocationRelativeTo(null);

        if (backgroundImage != null) {
            backgroundPanel.setBackgroundImage(backgroundImage);
        }
        wireJournalControls();
        refreshData();
        showCover();
    }

    private void wireJournalControls() {
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        journalTable.addSelectionListener(selectedCount -> {
            editButton.setEnabled(selectedCount == 1);
            deleteButton.setEnabled(selectedCount >= 1);
        });
        journalTable.addSortListener(() -> {
            syncSortToggle(journalTable.isSortedByDateDescending());
            refreshStatus();
        });
        journalTable.addOpenListener(this::showObservation);
        typeFilter.addActionListener(event -> {
            if (!updatingFilterOptions) {
                journalTable.setTypeFilter(selectedTypeFilter());
                refreshStatus();
            }
        });
        sortToggle.addItemListener(event -> {
            boolean descending = sortToggle.isSelected();
            sortToggle.setText(descending ? "Newest first" : "Oldest first");
            if (journalTable.isSortedByDateDescending() != descending) {
                journalTable.setSortDescendingByDate(descending);
            }
            refreshStatus();
        });
        syncSortToggle(true);
    }

    private void syncSortToggle(boolean descending) {
        sortToggle.setText(descending ? "Newest first" : "Oldest first");
        sortToggle.setSelected(descending);
    }

    private String selectedTypeFilter() {
        Object selected = typeFilter.getSelectedItem();
        return selected == null || ALL_TYPES.equals(selected) ? null : selected.toString();
    }

    private void showCover() {
        cards.show(backgroundPanel, COVER_CARD);
    }

    private void showObservation(Observation observation) {
        ObservationDetailDialog.open(this, observation, imageManager);
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

        addButton.addActionListener(event -> addObservation());
        editButton.addActionListener(event -> editObservation());
        deleteButton.addActionListener(event -> deleteObservation());

        JPanel bar = Theme.transparentPanel(new FlowLayout(FlowLayout.CENTER, Theme.SPACE_SM, 0));
        bar.setBorder(Theme.padding(Theme.SPACE_SM, Theme.SPACE_XL, Theme.SPACE_MD, Theme.SPACE_XL));
        bar.add(addButton);
        bar.add(editButton);
        bar.add(deleteButton);
        bar.add(Box.createHorizontalStrut(Theme.SPACE_LG));
        bar.add(Theme.label("Filter"));
        bar.add(typeFilter);
        bar.add(Box.createHorizontalStrut(Theme.SPACE_MD));
        bar.add(Theme.label("Sort"));
        bar.add(sortToggle);
        toolbar = bar;
        return toolbar;
    }

    private JTabbedPane buildTabs() {
        tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.setFont(Theme.FONT_TAB);
        tabs.setBorder(Theme.padding(Theme.SPACE_MD, Theme.SPACE_MD, 0, Theme.SPACE_MD));
        tabs.addTab("Journal", journalTable);
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
        ImageChooser.choose(this, "Choose a background image").ifPresent(chosen -> {
            backgroundSaver.accept(chosen);
            backgroundPanel.setBackgroundImage(chosen);
        });
    }

    private void addObservation() {
        ObservationFormDialog.open(this, null, imageManager).ifPresent(observation -> {
            try {
                service.add(observation);
            } catch (IOException e) {
                reportStorageError("save", e);
                return;
            }
            refreshData();
            showJournalTab();
        });
    }

    private void editObservation() {
        Observation selected = journalTable.selectedObservations().stream().findFirst().orElse(null);
        if (selected == null) {
            return;
        }
        ObservationFormDialog.open(this, selected, imageManager).ifPresent(observation -> {
            try {
                service.update(observation);
            } catch (IOException e) {
                reportStorageError("save", e);
                return;
            }
            refreshData();
            showJournalTab();
        });
    }

    private void deleteObservation() {
        List<Observation> selected = journalTable.selectedObservations();
        if (selected.isEmpty()) {
            return;
        }
        String question = selected.size() == 1
                ? "Delete this observation?"
                : "Delete these " + selected.size() + " observations?";
        int choice = JOptionPane.showConfirmDialog(this, question + "\nThis cannot be undone.",
                "Delete observations", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            service.delete(selected.stream().map(Observation::id).toList());
        } catch (IOException e) {
            reportStorageError("delete", e);
            return;
        }
        refreshData();
        showJournalTab();
    }

    private void showJournalTab() {
        tabs.setSelectedIndex(0);
    }

    private void reportStorageError(String action, IOException cause) {
        JOptionPane.showMessageDialog(this, "Could not " + action + " the observation:\n" + cause.getMessage(),
                "AstroJournal", JOptionPane.ERROR_MESSAGE);
    }

    private void refreshData() {
        List<Observation> observations = service.all();
        journalTable.setObservations(observations);
        updateFilterOptions(observations);
        refreshStatus();
    }

    private void updateFilterOptions(List<Observation> observations) {
        List<String> options = new ArrayList<>();
        options.add(ALL_TYPES);
        for (CelestialType type : CelestialType.standard()) {
            options.add(type.label());
        }
        for (Observation observation : observations) {
            String type = observation.celestialType();
            if (type != null && !type.isBlank()
                    && options.stream().noneMatch(option -> option.equalsIgnoreCase(type))) {
                options.add(type);
            }
        }

        String selected = (String) typeFilter.getSelectedItem();
        updatingFilterOptions = true;
        typeFilter.removeAllItems();
        for (String option : options) {
            typeFilter.addItem(option);
        }
        typeFilter.setSelectedItem(options.contains(selected) ? selected : ALL_TYPES);
        updatingFilterOptions = false;
    }

    private void refreshStatus() {
        int total = service.all().size();
        int visible = journalTable.visibleRowCount();
        if (visible == total) {
            statusLabel.setText(total + (total == 1 ? " observation recorded" : " observations recorded"));
            return;
        }
        statusLabel.setText("Showing " + visible + " of " + total + " observations");
    }
}
