package astrojournal.data;

import astrojournal.model.Observation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ObservationRepository {

    private final TextFileStore store;
    private final List<Observation> observations = new ArrayList<>();
    private int nextId;

    public ObservationRepository(TextFileStore store) throws IOException {
        this.store = store;
        load();
    }

    public void load() throws IOException {
        observations.clear();
        observations.addAll(store.load());
        nextId = observations.stream().mapToInt(Observation::id).max().orElse(0) + 1;
    }

    public List<Observation> findAll() {
        return List.copyOf(observations);
    }

    public Optional<Observation> findById(int id) {
        return observations.stream().filter(observation -> observation.id() == id).findFirst();
    }

    public Observation add(Observation observation) throws IOException {
        Observation stored = observation.withId(nextId++);
        observations.add(stored);
        store.save(observations);
        return stored;
    }

    public void update(Observation observation) throws IOException {
        for (int i = 0; i < observations.size(); i++) {
            if (observations.get(i).id() == observation.id()) {
                observations.set(i, observation);
                store.save(observations);
                return;
            }
        }
        throw new IllegalArgumentException("No observation with id " + observation.id());
    }

    public boolean delete(Collection<Integer> ids) throws IOException {
        Set<Integer> targets = Set.copyOf(ids);
        if (observations.removeIf(observation -> targets.contains(observation.id()))) {
            store.save(observations);
            return true;
        }
        return false;
    }
}
