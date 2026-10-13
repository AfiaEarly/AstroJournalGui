package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class BackgroundPanel extends JPanel {

    private BufferedImage image;
    private boolean overlayEnabled = true;

    public BackgroundPanel() {
        setOpaque(true);
        setBackground(Theme.BACKGROUND);
    }

    public void setBackgroundImage(Path file) {
        image = loadImage(file);
        repaint();
    }

    public void clearBackgroundImage() {
        image = null;
        repaint();
    }

    public void setOverlayEnabled(boolean overlayEnabled) {
        this.overlayEnabled = overlayEnabled;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        drawCover(g2, image);
        if (overlayEnabled) {
            g2.setColor(Theme.OVERLAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }

    private void drawCover(Graphics2D g2, Image source) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imageWidth = source.getWidth(null);
        int imageHeight = source.getHeight(null);
        if (panelWidth <= 0 || panelHeight <= 0 || imageWidth <= 0 || imageHeight <= 0) {
            return;
        }
        double scale = Math.max((double) panelWidth / imageWidth, (double) panelHeight / imageHeight);
        int width = (int) Math.ceil(imageWidth * scale);
        int height = (int) Math.ceil(imageHeight * scale);
        g2.drawImage(source, (panelWidth - width) / 2, (panelHeight - height) / 2, width, height, null);
    }

    private static BufferedImage loadImage(Path file) {
        if (file == null) {
            return null;
        }
        try {
            return ImageIO.read(file.toFile());
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}
