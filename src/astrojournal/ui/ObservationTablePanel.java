package astrojournal.ui;

import astrojournal.model.Observation;
import astrojournal.ui.theme.Theme;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ObservationTablePanel extends PagePanel {

    private static final int ROW_HEIGHT = 30;
    private static final int[] COLUMN_WIDTHS = { 105, 80, 118, 92, 98, 108, 50, 136 };
    private static final String OPEN_ACTION = "journal.open";

    private final ObservationTableModel model = new ObservationTableModel();
    private final JTable table = new JTable(model) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getRowCount() == 0) {
                paintEmptyState(g);
            }
        }
    };
    private final DefaultRowSorter<ObservationTableModel, Integer> sorter = new TableRowSorter<>(model);
    private String typeFilter;
    private String searchText = "";

    public ObservationTablePanel() {
        super(new BorderLayout());

        table.setRowSorter(sorter);
        table.setDefaultRenderer(Object.class, new ObservationCellRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setRowHeight(ROW_HEIGHT);
        table.setFont(Theme.FONT_BODY);
        table.setBackground(Theme.PAPER);
        table.setForeground(Theme.INK);
        table.setGridColor(Theme.PAPER_LINE);
        table.setSelectionBackground(Theme.SELECTION);
        table.setSelectionForeground(Theme.INK);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(Theme.FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(Theme.PAPER_SHADE);
        table.getTableHeader().setForeground(Theme.INK);

        TableColumnModel columns = table.getColumnModel();
        for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
            columns.getColumn(i).setPreferredWidth(COLUMN_WIDTHS[i]);
        }

        sorter.setSortKeys(List.of(new RowSorter.SortKey(ObservationTableModel.COLUMN_DATE, SortOrder.DESCENDING)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.PAPER_BORDER));
        scrollPane.getVerticalScrollBar().setUnitIncrement(ROW_HEIGHT);
        add(scrollPane, BorderLayout.CENTER);

        table.setToolTipText("Double-click an observation to view it and its photo");
        sorter.addRowSorterListener(event -> table.repaint());
    }

    private void paintEmptyState(Graphics g) {
        String message = model.getRowCount() == 0
                ? "No observations yet \u2014 click Add to start your journal."
                : "No observations match your search or filter.";
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(Theme.FONT_BODY);
        g2.setColor(Theme.INK_MUTED);
        FontMetrics metrics = g2.getFontMetrics();
        int x = Math.max(Theme.SPACE_MD, (getWidth() - metrics.stringWidth(message)) / 2);
        int y = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;
        g2.drawString(message, x, y);
        g2.dispose();
    }

    public void setObservations(List<Observation> observations) {
        model.setObservations(observations);
    }

    public void setTypeFilter(String celestialType) {
        typeFilter = celestialType == null || celestialType.isBlank() ? null : celestialType;
        applyFilter();
    }

    public void setSearchText(String query) {
        searchText = query == null ? "" : query;
        applyFilter();
    }

    private void applyFilter() {
        List<RowFilter<ObservationTableModel, Integer>> filters = new ArrayList<>();
        if (typeFilter != null) {
            filters.add(RowFilter.regexFilter("(?i)^" + Pattern.quote(typeFilter) + "$",
                    ObservationTableModel.COLUMN_TYPE));
        }
        if (!searchText.isBlank()) {
            filters.add(new ObservationSearchFilter(searchText));
        }
        sorter.setRowFilter(switch (filters.size()) {
            case 0 -> null;
            case 1 -> filters.getFirst();
            default -> RowFilter.andFilter(filters);
        });
    }

    public void setSortDescendingByDate(boolean descending) {
        sorter.setSortKeys(List.of(new RowSorter.SortKey(ObservationTableModel.COLUMN_DATE,
                descending ? SortOrder.DESCENDING : SortOrder.ASCENDING)));
    }

    public boolean isSortedByDateDescending() {
        List<? extends RowSorter.SortKey> keys = sorter.getSortKeys();
        if (keys.isEmpty()) {
            return false;
        }
        RowSorter.SortKey first = keys.getFirst();
        return first.getColumn() == ObservationTableModel.COLUMN_DATE && first.getSortOrder() == SortOrder.DESCENDING;
    }

    public int visibleRowCount() {
        return table.getRowCount();
    }

    public void addSortListener(Runnable listener) {
        sorter.addRowSorterListener(event -> listener.run());
    }

    public void addOpenListener(Consumer<Observation> listener) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() != 2) {
                    return;
                }
                int viewRow = table.rowAtPoint(event.getPoint());
                if (viewRow >= 0) {
                    listener.accept(model.observationAt(table.convertRowIndexToModel(viewRow)));
                }
            }
        });
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), OPEN_ACTION);
        table.getActionMap().put(OPEN_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    listener.accept(model.observationAt(table.convertRowIndexToModel(viewRow)));
                }
            }
        });
    }
}
