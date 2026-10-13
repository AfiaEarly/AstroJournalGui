package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class BookPanel extends JPanel {

    private static final int SPINE_WIDTH = 44;
    private static final int PAGE_INSET = 22;
    private static final int CORNER = 18;
    private static final int PAGE_CORNER = 6;

    public BookPanel() {
        setOpaque(false);
        setBorder(new EmptyBorder(PAGE_INSET, SPINE_WIDTH + 18, PAGE_INSET, PAGE_INSET));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int coverWidth = getWidth() - 6;
        int coverHeight = getHeight() - 6;

        g2.setColor(Theme.SHADOW);
        g2.fillRoundRect(12, 14, coverWidth - 10, coverHeight - 10, CORNER, CORNER);

        g2.setColor(Theme.COVER);
        g2.fillRoundRect(0, 0, coverWidth, coverHeight, CORNER, CORNER);
        g2.setColor(Theme.COVER_EDGE);
        g2.drawRoundRect(0, 0, coverWidth - 1, coverHeight - 1, CORNER, CORNER);

        drawSpine(g2, coverHeight);
        drawPages(g2, getWidth(), getHeight());
        g2.dispose();
    }

    private void drawSpine(Graphics2D g2, int coverHeight) {
        g2.setColor(Theme.SPINE);
        g2.fillRoundRect(0, 0, SPINE_WIDTH, coverHeight, CORNER, CORNER);
        g2.fillRect(SPINE_WIDTH - CORNER, 0, CORNER, coverHeight);

        g2.setColor(Theme.STITCH);
        for (int y = 30; y < coverHeight - 26; y += 26) {
            g2.fillOval(SPINE_WIDTH - 17, y, 5, 5);
        }
    }

    private void drawPages(Graphics2D g2, int width, int height) {
        int x = SPINE_WIDTH + 18;
        int y = PAGE_INSET;
        int w = width - x - PAGE_INSET;
        int h = height - 2 * PAGE_INSET;

        g2.setColor(Theme.PAPER_SHADE);
        g2.fillRoundRect(x + 4, y + 4, w - 4, h - 4, PAGE_CORNER, PAGE_CORNER);
        g2.setColor(Theme.PAPER);
        g2.fillRoundRect(x, y, w, h, PAGE_CORNER, PAGE_CORNER);
    }
}
