package astrojournal.ui;

import astrojournal.model.Observation;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ObservationTableModel extends AbstractTableModel {

    public static final int COLUMN_DATE = 0;
    public static final int COLUMN_TIME = 1;
    public static final int COLUMN_OBJECT = 2;
    public static final int COLUMN_TYPE = 3;
    public static final int COLUMN_LOCATION = 4;
    public static final int COLUMN_EQUIPMENT = 5;
    public static final int COLUMN_IMAGE = 6;
    public static final int COLUMN_NOTES = 7;

    private static final String[] COLUMNS = {
            "Date", "Time", "Object", "Type", "Location", "Equipment", "Image", "Notes"
    };

    private List<Observation> observations = List.of();

    public void setObservations(List<Observation> observations) {
        this.observations = List.copyOf(observations);
        fireTableDataChanged();
    }

    public Observation observationAt(int row) {
        return observations.get(row);
    }

    @Override
    public int getRowCount() {
        return observations.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case COLUMN_DATE -> LocalDate.class;
            case COLUMN_TIME -> LocalTime.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Observation observation = observations.get(rowIndex);
        return switch (columnIndex) {
            case COLUMN_DATE -> observation.date();
            case COLUMN_TIME -> observation.time();
            case COLUMN_OBJECT -> text(observation.objectName());
            case COLUMN_TYPE -> text(observation.celestialType());
            case COLUMN_LOCATION -> text(observation.location());
            case COLUMN_EQUIPMENT -> text(observation.equipment());
            case COLUMN_IMAGE -> text(observation.imageFileName());
            case COLUMN_NOTES -> text(observation.notes());
            default -> "";
        };
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
