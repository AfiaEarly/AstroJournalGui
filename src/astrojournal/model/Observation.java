package astrojournal.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Observation(
        int id,
        LocalDate date,
        LocalTime time,
        String objectName,
        String celestialType,
        String location,
        String equipment,
        String imageFileName,
        String notes) {

    public Observation withId(int newId) {
        return new Observation(newId, date, time, objectName, celestialType, location, equipment, imageFileName, notes);
    }

    public Observation withDate(LocalDate newDate) {
        return new Observation(id, newDate, time, objectName, celestialType, location, equipment, imageFileName, notes);
    }

    public Observation withTime(LocalTime newTime) {
        return new Observation(id, date, newTime, objectName, celestialType, location, equipment, imageFileName, notes);
    }

    public Observation withObjectName(String newObjectName) {
        return new Observation(id, date, time, newObjectName, celestialType, location, equipment, imageFileName, notes);
    }

    public Observation withCelestialType(String newCelestialType) {
        return new Observation(id, date, time, objectName, newCelestialType, location, equipment, imageFileName, notes);
    }

    public Observation withLocation(String newLocation) {
        return new Observation(id, date, time, objectName, celestialType, newLocation, equipment, imageFileName, notes);
    }

    public Observation withEquipment(String newEquipment) {
        return new Observation(id, date, time, objectName, celestialType, location, newEquipment, imageFileName, notes);
    }

    public Observation withImageFileName(String newImageFileName) {
        return new Observation(id, date, time, objectName, celestialType, location, equipment, newImageFileName, notes);
    }

    public Observation withNotes(String newNotes) {
        return new Observation(id, date, time, objectName, celestialType, location, equipment, imageFileName, newNotes);
    }
}
