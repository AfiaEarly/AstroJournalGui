package astrojournal.service;

import astrojournal.data.ObservationRepository;
import astrojournal.model.CelestialType;
import astrojournal.model.Observation;
import astrojournal.util.DateUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ObservationService {

    private final ObservationRepository repository;

    public ObservationService(ObservationRepository repository) {
        this.repository = repository;
    }

    public List<Observation> all() {
        return repository.findAll();
    }

    public Observation add(Observation observation) throws IOException {
        return repository.add(observation);
    }

    public void update(Observation observation) throws IOException {
        repository.update(observation);
    }

    public void delete(Collection<Integer> ids) throws IOException {
        repository.delete(ids);
    }

    public List<Observation> filterByType(String celestialType) {
        if (celestialType == null || celestialType.isBlank()) {
            return all();
        }
        String target = celestialType.trim();
        return all().stream()
                .filter(observation -> observation.celestialType().equalsIgnoreCase(target))
                .toList();
    }

    public List<Observation> sortByDate(boolean ascending) {
        Comparator<Observation> byDate = Comparator.comparing(Observation::date)
                .thenComparing(Observation::time)
                .thenComparingInt(Observation::id);
        if (!ascending) {
            byDate = byDate.reversed();
        }
        return all().stream().sorted(byDate).toList();
    }

    public Map<String, Integer> countsByType() {
        var counts = new LinkedHashMap<String, Integer>();
        for (CelestialType type : CelestialType.standard()) {
            counts.put(type.label(), 0);
        }
        for (Observation observation : all()) {
            counts.merge(observation.celestialType(), 1, Integer::sum);
        }
        return counts;
    }

    public int currentStreak() {
        return DateUtil.currentStreak(observationDates(), LocalDate.now());
    }

    public int longestStreak() {
        return DateUtil.longestStreak(observationDates());
    }

    private List<LocalDate> observationDates() {
        return all().stream().map(Observation::date).toList();
    }
}
