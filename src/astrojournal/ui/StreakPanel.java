package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class StreakPanel extends PagePanel {

    private static final int CARD_WIDTH = 260;
    private static final int CARD_HEIGHT = 150;

    private final JLabel currentValue = Theme.display("0");
    private final JLabel currentUnit = Theme.muted("days");
    private final JLabel longestValue = Theme.display("0");
    private final JLabel longestUnit = Theme.muted("days");
    private final JLabel emptyHint = Theme.muted("Log an observation to start your first streak.");

    public StreakPanel() {
        super(new BorderLayout());

        JPanel cards = Theme.transparentPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_LG, 0));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.add(buildCard("Current streak", currentValue, currentUnit, "Ends today or yesterday."));
        cards.add(buildCard("Longest streak", longestValue, longestUnit, "Your best run so far."));

        emptyHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel content = Theme.transparentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(buildHeader());
        content.add(Box.createVerticalStrut(Theme.SPACE_LG));
        content.add(cards);
        content.add(Box.createVerticalStrut(Theme.SPACE_MD));
        content.add(emptyHint);

        add(content, BorderLayout.NORTH);
        setStreaks(0, 0);
    }

    public void setStreaks(int current, int longest) {
        currentValue.setText(String.valueOf(current));
        longestValue.setText(String.valueOf(longest));
        currentUnit.setText(current == 1 ? "day" : "days");
        longestUnit.setText(longest == 1 ? "day" : "days");
        emptyHint.setVisible(current == 0 && longest == 0);
    }

    private JPanel buildHeader() {
        JPanel header = Theme.transparentPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = Theme.heading("Observation streaks");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hint = Theme.muted("Consecutive calendar days with at least one observation.");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(heading);
        header.add(Box.createVerticalStrut(Theme.SPACE_XS));
        header.add(hint);
        return header;
    }

    private JPanel buildCard(String title, JLabel value, JLabel unit, String note) {
        JPanel card = Theme.paperPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        card.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PAPER_BORDER),
                Theme.padding(Theme.SPACE_MD, Theme.SPACE_LG, Theme.SPACE_MD, Theme.SPACE_LG)));

        JLabel caption = Theme.heading(title);
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel number = Theme.transparentPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        number.setAlignmentX(Component.LEFT_ALIGNMENT);
        number.add(value);
        number.add(unit);

        JLabel noteLabel = Theme.muted(note);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(caption);
        card.add(Box.createVerticalStrut(Theme.SPACE_SM));
        card.add(number);
        card.add(Box.createVerticalStrut(Theme.SPACE_SM));
        card.add(noteLabel);
        return card;
    }
}
