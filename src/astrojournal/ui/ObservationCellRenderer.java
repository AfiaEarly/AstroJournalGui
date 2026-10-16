package astrojournal.ui;

import astrojournal.ui.theme.Theme;
import astrojournal.util.DateUtil;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalTime;

public class ObservationCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
            int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        int modelColumn = table.convertColumnIndexToModel(column);
        label.setFont(Theme.FONT_BODY);
        label.setForeground(selected ? Theme.INK : foregroundFor(modelColumn));
        label.setHorizontalAlignment(alignmentFor(modelColumn));
        label.setText(textFor(modelColumn, value));
        return label;
    }

    private static String textFor(int modelColumn, Object value) {
        if (value == null) {
            return "";
        }
        return switch (modelColumn) {
            case ObservationTableModel.COLUMN_DATE -> DateUtil.formatDate((LocalDate) value);
            case ObservationTableModel.COLUMN_TIME -> DateUtil.formatTime((LocalTime) value);
            case ObservationTableModel.COLUMN_IMAGE -> value.toString().isBlank() ? "" : "yes";
            default -> value.toString();
        };
    }

    private static Color foregroundFor(int modelColumn) {
        return switch (modelColumn) {
            case ObservationTableModel.COLUMN_DATE, ObservationTableModel.COLUMN_TIME,
                    ObservationTableModel.COLUMN_OBJECT ->
                Theme.INK;
            default -> Theme.INK_MUTED;
        };
    }

    private static int alignmentFor(int modelColumn) {
        return switch (modelColumn) {
            case ObservationTableModel.COLUMN_DATE, ObservationTableModel.COLUMN_TIME,
                    ObservationTableModel.COLUMN_TYPE, ObservationTableModel.COLUMN_IMAGE ->
                SwingConstants.CENTER;
            default -> SwingConstants.LEFT;
        };
    }
}
