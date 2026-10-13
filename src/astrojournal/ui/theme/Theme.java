package astrojournal.ui.theme;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public final class Theme {

    public static final Color BACKGROUND = new Color(0x1A130E);
    public static final Color OVERLAY = new Color(10, 7, 5, 95);
    public static final Color SHADOW = new Color(0, 0, 0, 110);

    public static final Color COVER = new Color(0x4A3226);
    public static final Color COVER_EDGE = new Color(0x6E4E38);
    public static final Color COVER_TEXT = new Color(0xEDE0BE);
    public static final Color SPINE = new Color(0x2E1E15);
    public static final Color STITCH = new Color(0xC9A86A);

    public static final Color PAPER = new Color(0xF4ECD6);
    public static final Color PAPER_ALT = new Color(0xEADFC0);
    public static final Color PAPER_HOVER = new Color(0xE0D3AF);
    public static final Color PAPER_SHADE = new Color(0xE4D8B8);
    public static final Color PAPER_LINE = new Color(0xD8CBAB);
    public static final Color PAPER_BORDER = new Color(0xC7B993);
    public static final Color MARGIN_LINE = new Color(0xC98E86);

    public static final Color INK = new Color(0x2B2620);
    public static final Color INK_MUTED = new Color(0x6E6555);
    public static final Color ACCENT = new Color(0x8C3B2E);
    public static final Color ACCENT_HOVER = new Color(0xA5533F);
    public static final Color ACCENT_TEXT = new Color(0xF7EFDD);
    public static final Color SELECTION = new Color(0xE3D2A8);

    private static final String BODY_FAMILY = resolveFamily("Georgia", "Book Antiqua", "Palatino Linotype", "Serif");
    private static final String TITLE_FAMILY = resolveFamily("Segoe Script", "Ink Free", "Bradley Hand ITC",
            BODY_FAMILY);

    public static final Font FONT_TITLE = new Font(TITLE_FAMILY, Font.BOLD, 30);
    public static final Font FONT_HEADING = new Font(BODY_FAMILY, Font.BOLD, 18);
    public static final Font FONT_BODY = new Font(BODY_FAMILY, Font.PLAIN, 15);
    public static final Font FONT_SMALL = new Font(BODY_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(BODY_FAMILY, Font.PLAIN, 15);
    public static final Font FONT_TAB = new Font(BODY_FAMILY, Font.BOLD, 16);

    private static final int BUTTON_RADIUS = 10;

    public static final int SPACE_XS = 4;
    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 16;
    public static final int SPACE_LG = 24;
    public static final int SPACE_XL = 32;

    private static final Border BUTTON_PADDING = new EmptyBorder(SPACE_MD - 2, SPACE_LG, SPACE_MD - 2, SPACE_LG);
    private static final Border SMALL_BUTTON_PADDING = new EmptyBorder(SPACE_SM - 2, SPACE_MD, SPACE_SM - 2, SPACE_MD);

    private Theme() {
    }

    public static void installDefaults() {
        UIManager.put("Panel.background", PAPER);
        UIManager.put("Label.foreground", INK);
        UIManager.put("Label.background", PAPER);
        UIManager.put("Label.font", FONT_BODY);

        UIManager.put("Button.font", FONT_BUTTON);
        UIManager.put("Button.foreground", INK);
        UIManager.put("Button.background", PAPER_ALT);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.disabledText", INK_MUTED);
        UIManager.put("ToggleButton.font", FONT_BUTTON);
        UIManager.put("ToggleButton.foreground", INK);
        UIManager.put("ToggleButton.background", PAPER_ALT);
        UIManager.put("ToggleButton.disabledText", INK_MUTED);

        UIManager.put("MenuBar.background", PAPER);
        UIManager.put("MenuBar.foreground", INK);
        UIManager.put("MenuBar.font", FONT_BODY);
        UIManager.put("Menu.background", PAPER);
        UIManager.put("Menu.foreground", INK);
        UIManager.put("Menu.font", FONT_BODY);
        UIManager.put("Menu.selectionBackground", SELECTION);
        UIManager.put("Menu.selectionForeground", INK);
        UIManager.put("MenuItem.background", PAPER);
        UIManager.put("MenuItem.foreground", INK);
        UIManager.put("MenuItem.font", FONT_BODY);
        UIManager.put("MenuItem.selectionBackground", SELECTION);
        UIManager.put("MenuItem.selectionForeground", INK);
        UIManager.put("PopupMenu.background", PAPER);
        UIManager.put("PopupMenu.foreground", INK);
        UIManager.put("Separator.foreground", PAPER_BORDER);
        UIManager.put("Separator.background", PAPER);

        UIManager.put("ToolBar.background", PAPER);
        UIManager.put("ToolBar.foreground", INK);
        UIManager.put("ToolBar.font", FONT_BODY);

        UIManager.put("TabbedPane.font", FONT_TAB);
        UIManager.put("TabbedPane.background", PAPER_SHADE);
        UIManager.put("TabbedPane.foreground", INK_MUTED);
        UIManager.put("TabbedPane.selected", PAPER);
        UIManager.put("TabbedPane.selectedForeground", INK);
        UIManager.put("TabbedPane.contentAreaColor", PAPER);
        UIManager.put("TabbedPane.highlight", PAPER_BORDER);
        UIManager.put("TabbedPane.light", PAPER);
        UIManager.put("TabbedPane.darkShadow", PAPER_BORDER);
        UIManager.put("TabbedPane.borderHightlightColor", PAPER_BORDER);
        UIManager.put("TabbedPane.focus", SELECTION);

        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("Table.background", PAPER);
        UIManager.put("Table.foreground", INK);
        UIManager.put("Table.selectionBackground", SELECTION);
        UIManager.put("Table.selectionForeground", INK);
        UIManager.put("Table.gridColor", PAPER_LINE);
        UIManager.put("Table.showGrid", Boolean.TRUE);
        UIManager.put("TableHeader.background", PAPER_SHADE);
        UIManager.put("TableHeader.foreground", INK);
        UIManager.put("TableHeader.font", FONT_HEADING);

        UIManager.put("ScrollPane.background", PAPER);
        UIManager.put("Viewport.background", PAPER);
        UIManager.put("ScrollBar.thumb", PAPER_BORDER);
        UIManager.put("ScrollBar.track", PAPER_SHADE);

        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("ComboBox.foreground", INK);
        UIManager.put("ComboBox.background", PAPER_ALT);
        UIManager.put("ComboBox.selectionBackground", SELECTION);
        UIManager.put("ComboBox.selectionForeground", INK);

        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("TextField.background", PAPER_ALT);
        UIManager.put("TextField.foreground", INK);
        UIManager.put("TextField.caretForeground", INK);
        UIManager.put("TextArea.font", FONT_BODY);
        UIManager.put("TextArea.background", PAPER_ALT);
        UIManager.put("TextArea.foreground", INK);
        UIManager.put("TextArea.caretForeground", INK);

        UIManager.put("CheckBox.font", FONT_BODY);
        UIManager.put("CheckBox.foreground", INK);
        UIManager.put("CheckBox.background", PAPER);
        UIManager.put("RadioButton.font", FONT_BODY);
        UIManager.put("RadioButton.foreground", INK);
        UIManager.put("RadioButton.background", PAPER);

        UIManager.put("List.font", FONT_BODY);
        UIManager.put("List.background", PAPER);
        UIManager.put("List.foreground", INK);
        UIManager.put("List.selectionBackground", SELECTION);
        UIManager.put("List.selectionForeground", INK);

        UIManager.put("OptionPane.background", PAPER);
        UIManager.put("OptionPane.messageForeground", INK);
        UIManager.put("OptionPane.messageFont", FONT_BODY);
        UIManager.put("OptionPane.buttonFont", FONT_BUTTON);

        UIManager.put("SplitPane.background", PAPER);
        UIManager.put("SplitPane.dividerSize", 6);
    }

    public static JPanel transparentPanel() {
        return transparentPanel(null);
    }

    public static JPanel transparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    public static JLabel title(String text) {
        return styledLabel(text, FONT_TITLE, INK);
    }

    public static JLabel heading(String text) {
        return styledLabel(text, FONT_HEADING, INK);
    }

    public static JLabel label(String text) {
        return styledLabel(text, FONT_BODY, INK);
    }

    public static JLabel muted(String text) {
        return styledLabel(text, FONT_BODY, INK_MUTED);
    }

    public static JLabel coverTitle(String text) {
        return styledLabel(text, FONT_TITLE, COVER_TEXT);
    }

    public static JLabel coverSubtitle(String text) {
        return styledLabel(text, FONT_BODY, COVER_TEXT);
    }

    public static JButton primaryButton(String text) {
        return flatButton(text, ACCENT, ACCENT_HOVER, ACCENT_TEXT, null, FONT_BUTTON, BUTTON_PADDING);
    }

    public static JButton secondaryButton(String text) {
        return flatButton(text, PAPER_ALT, PAPER_HOVER, INK, PAPER_BORDER, FONT_BUTTON, BUTTON_PADDING);
    }

    public static JButton smallSecondaryButton(String text) {
        return flatButton(text, PAPER_ALT, PAPER_HOVER, INK, PAPER_BORDER, FONT_SMALL, SMALL_BUTTON_PADDING);
    }

    public static JToggleButton toggleButton(String text) {
        JToggleButton button = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                paintFlat(g, this, getBackground(), isSelected() ? ACCENT : PAPER_BORDER);
                super.paintComponent(g);
            }
        };
        styleButton(button, FONT_BUTTON, BUTTON_PADDING);
        button.setBackground(PAPER_ALT);
        button.setForeground(INK);
        button.addItemListener(event -> {
            boolean selected = button.isSelected();
            button.setBackground(selected ? ACCENT : PAPER_ALT);
            button.setForeground(selected ? ACCENT_TEXT : INK);
        });
        return button;
    }

    public static Border padding(int all) {
        return new EmptyBorder(all, all, all, all);
    }

    public static Border padding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    private static String resolveFamily(String... candidates) {
        List<String> available = List.of(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String candidate : candidates) {
            if (available.contains(candidate)) {
                return candidate;
            }
        }
        return candidates[candidates.length - 1];
    }

    private static JLabel styledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setOpaque(false);
        return label;
    }

    private static JButton flatButton(String text, Color base, Color hover, Color foreground, Color outline,
            Font font, Border padding) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                paintFlat(g, this, getBackground(), outline);
                super.paintComponent(g);
            }
        };
        styleButton(button, font, padding);
        button.setBackground(base);
        button.setForeground(foreground);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (button.isEnabled()) {
                    button.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(base);
            }
        });
        return button;
    }

    private static void styleButton(javax.swing.AbstractButton button, Font font, Border padding) {
        button.setFont(font);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(padding);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static void paintFlat(Graphics g, Component component, Color background, Color outline) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = component.getWidth();
        int height = component.getHeight();
        g2.setColor(background);
        g2.fillRoundRect(0, 0, width, height, BUTTON_RADIUS, BUTTON_RADIUS);
        if (outline != null) {
            g2.setColor(outline);
            g2.drawRoundRect(0, 0, width - 1, height - 1, BUTTON_RADIUS, BUTTON_RADIUS);
        }
        g2.dispose();
    }
}
