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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

public class ObservationTablePanel extends PagePanel {

    private static final int ROW_HEIGHT = 30;
    private static final int[] COLUMN_WIDTHS = { 105, 60, 132, 92, 98, 108, 50, 142 };
    private static final String OPEN_ACTION = "journal.open";

    private final ObservationTableModel model = new ObservationTableModel();
    private final JTable table = new JTable(model);
    private final DefaultRowSorter<ObservationTableModel, Integer> sorter = new TableRowSorter<>(model);

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
    }

    public void setObservations(List<Observation> observations) {
        model.setObservations(observations);
    }

    public void setTypeFilter(String celestialType) {
        if (celestialType == null || celestialType.isBlank()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)^" + Pattern.quote(celestialType) + "$",
                ObservationTableModel.COLUMN_TYPE));
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

    public void addSelectionListener(IntConsumer listener) {
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                listener.accept(table.getSelectedRowCount());
            }
        });
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
