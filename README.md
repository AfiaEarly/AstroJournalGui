# AstroJournal

A desktop journal for recording astronomy observations, styled like a leather-bound notebook.
Built with Java Swing and plain text files — no external dependencies.

## Requirements

- JDK 24 (the source uses records, switch expressions, and other modern Java features)

## Build

```bat
build.bat
```

Compiles every source file under `src/` into `bin/`. The build fails if any source does not compile.

## Run

```bat
run.bat
```

or directly:

```bat
java -cp bin astrojournal.Main
```

## Using the journal

1. Launch the app and click **Add Observation** on the cover.
2. Fill in the date, time, object, and celestial type, and optionally attach a photo.
3. Double-click a row (or press Enter) to open the record, where you can edit or delete it.
4. Use the **Search**, **Filter**, and **Sort** controls above the journal, and open the **Stats** tab for type counts.
   Search matches object, type, location, equipment, notes, date, or time; press Ctrl+F to focus it and Esc to clear it.
5. Upload a background image from the cover or book view; it is remembered for next time.

## Data

AstroJournal keeps its data in the `data/` folder next to the application:

- `data/observations.txt` — one pipe-delimited observation per line
- `data/images/` — photos imported into the journal
- `data/settings.properties` — saved settings such as the background image path

Back up these files to back up the journal. Deleting `data/observations.txt` starts an empty journal.

## Project layout

- `src/astrojournal/model/` — data records and the celestial-type catalog
- `src/astrojournal/data/` — text-file persistence and settings
- `src/astrojournal/service/` — business rules, image storage, streaks and counts
- `src/astrojournal/ui/` — Swing views, dialogs, and the journal theme
- `src/astrojournal/util/` — date helpers and text escaping
