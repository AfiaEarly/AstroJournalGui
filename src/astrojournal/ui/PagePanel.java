package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class PagePanel extends JPanel {

    private static final int LINE_SPACING = 30;
    private static final int TOP_PADDING = 14;
    private static final int BOTTOM_PADDING = 14;
    private static final int SIDE_PADDING = 28;
    private static final int MARGIN_X = 58;

    public PagePanel() {
        this(null);
    }

    public PagePanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(TOP_PADDING + Theme.SPACE_MD, MARGIN_X + Theme.SPACE_MD,
                BOTTOM_PADDING + Theme.SPACE_MD, SIDE_PADDING + Theme.SPACE_MD));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        g2.setColor(Theme.PAPER);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Theme.PAPER_LINE);
        for (int y = TOP_PADDING + LINE_SPACING; y < height - BOTTOM_PADDING; y += LINE_SPACING) {
            g2.drawLine(SIDE_PADDING, y, width - SIDE_PADDING, y);
        }

        g2.setColor(Theme.MARGIN_LINE);
        g2.drawLine(MARGIN_X, TOP_PADDING, MARGIN_X, height - BOTTOM_PADDING);
        g2.dispose();
    }
}
