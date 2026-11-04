package astrojournal.ui;

import astrojournal.ui.theme.Theme;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StatsPanel extends PagePanel {

    private static final int TYPE_WIDTH = 170;
    private static final int BAR_WIDTH = 220;
    private static final int BAR_HEIGHT = 10;
    private static final int COUNT_WIDTH = 70;
    private static final int SHARE_WIDTH = 55;

    private final JPanel table = Theme.transparentPanel(new GridBagLayout());
    private final JPanel card = Theme.paperPanel(new BorderLayout());
    private final JPanel cardRow = Theme.transparentPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JLabel summaryLabel = Theme.muted("");

    public StatsPanel() {
        super(new BorderLayout());

        table.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PAPER_BORDER),
                Theme.padding(Theme.SPACE_XS, Theme.SPACE_MD, Theme.SPACE_XS, Theme.SPACE_MD)));
        card.add(table, BorderLayout.CENTER);

        cardRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardRow.add(card);

        JPanel content = Theme.transparentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(buildHeader());
        content.add(Box.createVerticalStrut(Theme.SPACE_SM));
        content.add(cardRow);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(BAR_HEIGHT * 3);
        add(scrollPane, BorderLayout.CENTER);

        setCounts(Map.of());
    }

    public void setCounts(Map<String, Integer> counts) {
        List<Map.Entry<String, Integer>> entries = counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER))
                .toList();
        int total = entries.stream().mapToInt(Map.Entry::getValue).sum();

        table.removeAll();
        if (!entries.isEmpty()) {
            int row = 0;
            addHeaderRow(row++);
            addRule(row++);
            for (Map.Entry<String, Integer> entry : entries) {
                addDataRow(row++, entry.getKey(), entry.getValue(), total);
            }
        }

        summaryLabel.setText(entries.isEmpty()
                ? "No observations yet — your counts will appear here."
                : summaryText(total, entries.size()) + ", most observed first.");
        cardRow.setVisible(!entries.isEmpty());
        table.revalidate();
        table.repaint();
    }

    private JPanel buildHeader() {
        JPanel header = Theme.transparentPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = Theme.heading("Observation stats");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(heading);
        header.add(Box.createVerticalStrut(Theme.SPACE_XS));
        header.add(summaryLabel);
        return header;
    }

    private void addHeaderRow(int row) {
        addCell(Theme.tableHeader("Celestial type"), row, 0, TYPE_WIDTH, GridBagConstraints.WEST);
        addCell(rightAligned(Theme.tableHeader("Count")), row, 2, COUNT_WIDTH, GridBagConstraints.EAST);
        addCell(rightAligned(Theme.tableHeader("Share")), row, 3, SHARE_WIDTH, GridBagConstraints.EAST);
    }

    private void addDataRow(int row, String type, int count, int total) {
        addCell(Theme.label(type), row, 0, TYPE_WIDTH, GridBagConstraints.WEST);
        addCell(buildBar(count, total), row, 1, BAR_WIDTH, GridBagConstraints.WEST);
        addCell(rightAligned(Theme.label(String.valueOf(count))), row, 2, COUNT_WIDTH, GridBagConstraints.EAST);
        addCell(rightAligned(Theme.muted(shareText(count, total))), row, 3, SHARE_WIDTH, GridBagConstraints.EAST);
    }

    private void addRule(int row) {
        JSeparator rule = new JSeparator();
        rule.setForeground(Theme.PAPER_BORDER);
        rule.setBackground(Theme.PAPER_BORDER);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, Theme.SPACE_XS, 0);
        table.add(rule, constraints);
    }

    private void addCell(Component cell, int row, int column, int width, int anchor) {
        Dimension preferred = cell.getPreferredSize();
        cell.setPreferredSize(new Dimension(width, preferred.height));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.anchor = anchor;
        constraints.insets = new Insets(0, 0, Theme.SPACE_XS, Theme.SPACE_MD);
        table.add(cell, constraints);
    }

    private JPanel buildBar(int count, int total) {
        int filled = Math.max(6, (int) Math.round(count * (double) BAR_WIDTH / total));

        JPanel fill = new JPanel();
        fill.setBackground(Theme.ACCENT);
        fill.setPreferredSize(new Dimension(filled, BAR_HEIGHT));

        JPanel track = new JPanel(new BorderLayout());
        track.setBackground(Theme.PAPER_SHADE);
        track.setPreferredSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));
        track.add(fill, BorderLayout.WEST);
        return track;
    }

    private JLabel rightAligned(JLabel label) {
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private String shareText(int count, int total) {
        return Math.round(count * 100.0 / total) + "%";
    }

    private String summaryText(int total, int types) {
        String observations = total == 1 ? " observation" : " observations";
        String typeWord = types == 1 ? " type" : " types";
        return total + observations + " across " + types + typeWord;
    }
}
