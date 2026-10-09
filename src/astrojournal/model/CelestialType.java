package astrojournal.model;

import java.util.List;
import java.util.Optional;

public enum CelestialType {
    STAR("Star"),
    PLANET("Planet"),
    MOON("Moon"),
    CONSTELLATION("Constellation"),
    GALAXY("Galaxy"),
    NEBULA("Nebula"),
    OTHER("Other"),
    CUSTOM("Custom");

    private static final List<CelestialType> STANDARD = List.of(STAR, PLANET, MOON, CONSTELLATION, GALAXY, NEBULA,
            OTHER);

    private final String label;

    CelestialType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public boolean isCustom() {
        return this == CUSTOM;
    }

    public static List<CelestialType> standard() {
        return STANDARD;
    }

    public static Optional<CelestialType> fromLabel(String label) {
        return STANDARD.stream()
                .filter(type -> type.label.equalsIgnoreCase(label))
                .findFirst();
    }

    public static boolean isStandard(String label) {
        return fromLabel(label).isPresent();
    }
}
