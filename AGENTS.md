# AstroJournal — Agent Guidelines

Desktop journal for recording astronomy observations. Java Swing UI, plain text file as
the database, and a journal-book look — leather cover, stitched spine, cream ruled pages,
serif/handwritten type.

Authoritative docs: `plan.md` (requirements + design) and `task.md` (task checklist).

## Code Style

- Java 24. Use records, `switch` expressions, `var`, and text blocks where they improve clarity.
- Plain JDK only — **no external dependencies**, no Maven/Gradle. Everything under `src/`.
- Clean, well-structured code. **Avoid unnecessary comment lines**; let names explain intent.
- One primary class per file, and the filename must match the class name.
- Keep classes small and single-purpose. No dead code, no unused imports.

## Architecture

Layered, with strict separation — never let a layer reach across into another:

- `model/` — data only (`Observation`, `CelestialType`). No I/O, no UI.
- `data/` — persistence (`ObservationRepository`, `TextFileStore`, `SettingsStore`).
- `service/` — business rules (`ObservationService`, `ImageManager`). UI must call this, not `data/`.
- `ui/` — Swing components only. No file parsing or persistence logic.
- `util/` — stateless helpers (`DateUtil`, `TextEscaper`).

UI → service → repository → text file. Dependencies point one direction only.

## Build and Run

```
build.bat        # javac all sources under src/ into bin/
run.bat          # java -cp bin astrojournal.Main
```

There is no test framework. Verify changes by compiling cleanly and exercising the app
against the Manual Test Pass in `task.md`.

## Conventions

**Storage format** — one observation per line, pipe-delimited, fixed field order:

```
id|date|time|objectName|type|location|equipment|imageFile|notes
```

- `date` is `yyyy-MM-dd`, `time` is `HH:mm`.
- Escape `\` → `\\`, `|` → `\|`, newline → `\n` via `TextEscaper`. Round-trips must be lossless.
- Skip blank lines and lines starting with `#`. Save atomically (temp file + move).

**Data files** live under `data/`: `observations.txt`, `images/`, `settings.properties`.
Never hand-edit them in code paths that bypass `TextFileStore`.

**Images** are always copied into `data/images/` with a unique name; records store only
the filename, never an absolute path.

**Celestial types** are a fixed enum (Star, Planet, Moon, Constellation, Galaxy, Nebula,
Other) plus a custom user-named option.

**Streak rule** — consecutive calendar days ending today or yesterday. A gap of two or
more days resets the current streak.

**Theme** — all colors, fonts, and spacing come from `ui/theme/Theme.java`. Never hard-code
colors in individual components. The palette is journal-book oriented: `COVER`/`SPINE`/`STITCH`
for the leather binding, `PAPER`/`PAPER_LINE`/`MARGIN_LINE` for the page, `INK`/`INK_MUTED` for
text, `ACCENT` for the ribbon-red primary action. Body text uses a serif (`FONT_BODY`); the title
uses a script family (`FONT_TITLE`).

**UI controls** — build controls through `Theme` factories, never style inline:

- Buttons: `Theme.primaryButton` / `secondaryButton` / `smallSecondaryButton` / `toggleButton`.
  They are flat, rounded and hand-painted (rounded rect drawn in `paintComponent` before the super
  call so text renders on top); use `FONT_BUTTON` (or the compact `smallSecondaryButton` with
  `FONT_SMALL` for secondary desk actions). Add hover/selection states inside `Theme`, not per screen.
- Labels: `Theme.title` / `heading` / `label` / `muted`; panels: `Theme.transparentPanel(...)`;
  spacing/borders: `Theme.padding(...)`.
- Tab labels use `Theme.FONT_TAB`; keep the selected tab bright (`INK`) and the others muted
  (`INK_MUTED`).
- The book frame is `ui/BookPanel.java` (leather cover + spine; its `EmptyBorder` insets children
  onto the page). Ruled paper is `ui/PagePanel.java`; use it as the surface for tab content.
- `ui/MainFrame.java` has two `CardLayout` views over the desk background: the closed **cover**
  (`ui/CoverPanel.java`, shown first — title plus "Add Observation" on the leather; "Upload
  Background" / "Exit" sit on the desk at the bottom-right, not on the cover) and the opened
  **book** (header, centered toolbar, tabs, status bar inside the `BookPanel`). "Add Observation"
  opens the book; "Close Book" returns to the cover. There is no menu bar.

**Background image** — the uploaded image paints behind the book like a desk (`ui/BackgroundPanel`,
plus a light dark scrim) and its path is persisted via `SettingsStore`. Keep the scrim light so the
image stays visible around the book.

**Placeholders** — where a control's backing feature is not implemented yet, it shows a
`comingSoon(...)` notice in `MainFrame` instead of silently doing nothing. Remove these as the
matching feature lands (Phases 4–6).

## Workflow

- Update the matching checkboxes in `task.md` as tasks complete.
- Keep `plan.md` in sync if requirements change.
- Record new or changed UI/theme conventions in this file.
