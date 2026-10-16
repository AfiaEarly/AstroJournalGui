package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class PhotoPanel extends JPanel {

    private static final int PREFERRED_WIDTH = 620;
    private static final int PREFERRED_HEIGHT = 380;
    private static final int INSET = 14;

    private BufferedImage image;
    private String emptyMessage;

    public PhotoPanel(String emptyMessage) {
        this.emptyMessage = emptyMessage;
        setOpaque(true);
        setBackground(Theme.PAPER_SHADE);
        setBorder(BorderFactory.createLineBorder(Theme.PAPER_BORDER));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (image == null) {
            paintEmptyMessage(g2);
        } else {
            paintImage(g2);
        }
        g2.dispose();
    }

    private void paintImage(Graphics2D g2) {
        int availableWidth = Math.max(1, getWidth() - 2 * INSET);
        int availableHeight = Math.max(1, getHeight() - 2 * INSET);
        double scale = Math.min((double) availableWidth / image.getWidth(),
                (double) availableHeight / image.getHeight());
        int width = Math.max(1, (int) Math.round(image.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(image.getHeight() * scale));

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, (getWidth() - width) / 2, (getHeight() - height) / 2, width, height, null);
    }

    private void paintEmptyMessage(Graphics2D g2) {
        g2.setFont(Theme.FONT_BODY);
        g2.setColor(Theme.INK_MUTED);
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(emptyMessage, (getWidth() - metrics.stringWidth(emptyMessage)) / 2,
                (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2);
    }
}
