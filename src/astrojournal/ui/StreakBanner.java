package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

public class StreakBanner extends JPanel {

    private final JLabel currentValue = Theme.heading("0 days");
    private final JLabel companionLabel = Theme.muted("");

    public StreakBanner() {
        super(new BorderLayout(Theme.SPACE_MD, 0));
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, Theme.ACCENT),
                Theme.padding(Theme.SPACE_XS, Theme.SPACE_SM, Theme.SPACE_XS, 0)));

        JLabel caption = Theme.muted("Current streak");
        caption.setAlignmentY(Component.CENTER_ALIGNMENT);
        currentValue.setAlignmentY(Component.CENTER_ALIGNMENT);
        companionLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel current = Theme.transparentPanel();
        current.setLayout(new BoxLayout(current, BoxLayout.X_AXIS));
        current.add(caption);
        current.add(Box.createHorizontalStrut(Theme.SPACE_SM));
        current.add(currentValue);

        add(current, BorderLayout.WEST);
        add(companionLabel, BorderLayout.EAST);
        setStreaks(0, 0);
    }

    public void setStreaks(int current, int longest) {
        currentValue.setText(dayText(current));
        companionLabel.setText(longest == 0
                ? "Log an observation to start a streak"
                : "Best: " + dayText(longest));
    }

    private String dayText(int days) {
        return days == 1 ? "1 day" : days + " days";
    }
}
