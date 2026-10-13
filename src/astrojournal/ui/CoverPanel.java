package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class CoverPanel extends JPanel {

    private static final int CORNER = 20;
    private static final int SPINE_WIDTH = 26;

    public CoverPanel() {
        setOpaque(false);
        setBorder(new EmptyBorder(Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 6;
        int height = getHeight() - 6;

        g2.setColor(Theme.SHADOW);
        g2.fillRoundRect(12, 14, width - 10, height - 10, CORNER, CORNER);

        g2.setColor(Theme.COVER);
        g2.fillRoundRect(0, 0, width, height, CORNER, CORNER);
        g2.setColor(Theme.COVER_EDGE);
        g2.drawRoundRect(0, 0, width - 1, height - 1, CORNER, CORNER);

        g2.setColor(Theme.SPINE);
        g2.fillRoundRect(0, 0, SPINE_WIDTH, height, CORNER, CORNER);
        g2.fillRect(SPINE_WIDTH - CORNER, 0, CORNER, height);

        g2.setColor(Theme.STITCH);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                new float[] { 6f, 6f }, 0f));
        g2.drawRoundRect(14, 14, width - 28, height - 28, CORNER, CORNER);
        g2.dispose();
    }
}
