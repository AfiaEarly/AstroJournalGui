package astrojournal.ui;

import astrojournal.model.Observation;
import astrojournal.util.DateUtil;

import javax.swing.RowFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

final class ObservationSearchFilter extends RowFilter<ObservationTableModel, Integer> {

    private final List<String> terms;

    ObservationSearchFilter(String query) {
        this.terms = Arrays.stream(query.strip().toLowerCase(Locale.ROOT).split("\\s+"))
                .filter(term -> !term.isEmpty())
                .toList();
    }

    @Override
    public boolean include(Entry<? extends ObservationTableModel, ? extends Integer> entry) {
        String text = searchableText(entry.getModel().observationAt(entry.getIdentifier()));
        return terms.stream().allMatch(text::contains);
    }

    private static String searchableText(Observation observation) {
        String storedTime = observation.time() == null ? "" : observation.time().toString();
        String storedDate = observation.date() == null ? "" : observation.date().toString();
        return String.join("\n",
                storedDate,
                DateUtil.formatDate(observation.date()),
                storedTime,
                DateUtil.formatTime(observation.time()),
                text(observation.objectName()),
                text(observation.celestialType()),
                text(observation.location()),
                text(observation.equipment()),
                text(observation.imageFileName()),
                text(observation.notes()))
                .toLowerCase(Locale.ROOT);
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
