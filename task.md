# AstroJournal — Task List

Legend: `[ ]` todo · `[~]` in progress · `[x]` done

Build: plain JDK (Java 24), `javac -d bin` + `java -cp bin astrojournal.Main`.
Storage: pipe-delimited text file. Images copied into `data/images/`.

---

## Phase 0 — Scaffolding

- [x] **0.1** Create folder tree `src/astrojournal/{model,data,service,ui,ui/theme,util}`, `data/`, `resources/`, `bin/`.
      Done when: all directories exist.
- [x] **0.2** Write `task.md` (this file) into the repo root.
      Done when: `task.md` exists with all phases.
- [x] **0.3** Create `build.bat` (compile every `.java` under `src` into `bin/`) and `run.bat` (`java -cp bin astrojournal.Main`).
      Done when: both scripts run without manual edits.
- [x] **0.4** Create `.gitignore` ignoring `bin/` and `data/images/`.
      Done when: build output is not tracked.

## Phase 1 — Model & Storage

- [x] **1.1** `model/CelestialType.java` — enum Star, Planet, Moon, Constellation, Galaxy, Nebula, Other + custom support.
      Done when: can represent a user-named custom type.
- [x] **1.2** `model/Observation.java` — fields id, date, time, objectName, celestialType, location, equipment, imageFileName, notes + edit-clone helper.
      Done when: an instance can be rebuilt with one field changed.
- [x] **1.3** `util/TextEscaper.java` — escape/unescape `\`, `|`, newline.
      Done when: escape→unescape round-trips any string unchanged.
- [x] **1.4** `data/TextFileStore.java` — one line ↔ `Observation`; skip blank/`#` lines; atomic save via temp file + move.
      Done when: a list saves and reloads identically.
- [x] **1.5** `data/SettingsStore.java` — load/save `key=value`.
      Done when: background-image path survives a reload.

## Phase 2 — Repository & Service

- [x] **2.1** `data/ObservationRepository.java` — load at start, hold list, auto-assign ids, persist on every change.
      Done when: ids are unique and monotonic.
- [x] **2.2** `service/ImageManager.java` — copy chosen image into `data/images/` with a unique name; return filename.
      Done when: duplicate filenames never collide.
- [x] **2.3** `service/ObservationService.java` — add, update, delete(selected ids), filterByType, sortByDate(asc/desc), countsByType, currentStreak, longestStreak.
      Done when: each operation works against the repository.
- [x] **2.4** `util/DateUtil.java` — parse/format `yyyy-MM-dd` + `HH:mm`; streak day math.
      Done when: date round-trips and day-gaps compute correctly across month/year boundaries.

## Phase 3 — UI Shell & Theme

- [x] **3.1** `ui/theme/Theme.java` — space palette (deep navy/black, star-white accents), fonts, spacing constants.
      Done when: every UI class reads colors/fonts from here.
- [x] **3.2** `ui/BackgroundPanel.java` — paint scaled background image + dark overlay.
      Done when: background fills the frame without distortion and text stays readable.
- [x] **3.3** `ui/MainFrame.java` — JFrame, menu bar (File → Upload Background / Exit), toolbar (Add, Edit, Delete, Filter, Sort), tabs Journal / Stats / Streaks.
      Done when: window opens and all controls are present.
- [x] **3.4** Wire background upload through `SettingsStore` so it persists.
      Done when: relaunch keeps the chosen background.

## Phase 4 — Observation List

- [ ] **4.1** `ui/ObservationTablePanel.java` — `JTable` + dark `TableCellRenderer`.
      Done when: rows render in the theme.
- [ ] **4.2** `TableRowSorter` + toolbar sort toggle for date.
      Done when: clicking the date column / toggle sorts asc↔desc.
- [ ] **4.3** Celestial-type filter combo using `RowFilter`.
      Done when: selecting a type shows only matching rows; "All" restores everything.
- [ ] **4.4** Multi-row selection driving Edit/Delete buttons.
      Done when: buttons enable/disable with selection count.

## Phase 5 — Add / Edit / Delete + Images

- [ ] **5.1** `ui/ObservationFormDialog.java` — date, time, object name, type (+custom), location, equipment, notes area, image chooser + preview.
      Done when: dialog opens for both add and edit (prefilled).
- [ ] **5.2** Validation — date, time, object name required; friendly error dialog.
      Done when: invalid input is blocked with a clear message.
- [ ] **5.3** Add flow → `service.add` → refresh table.
      Done when: new record appears immediately.
- [ ] **5.4** Edit flow → prefill → `service.update` → refresh.
      Done when: edits persist after relaunch.
- [ ] **5.5** Delete flow → confirm dialog → `service.delete(ids)` → refresh.
      Done when: multi-row delete removes rows from table and file.
- [ ] **5.6** Image chooser → `ImageManager` copy + preview thumbnail.
      Done when: image lands in `data/images/` and preview shows.

## Phase 6 — Streaks & Stats

- [ ] **6.1** `ui/StatsPanel.java` — observation count per celestial type + total.
      Done when: totals equal actual record counts.
- [ ] **6.2** `ui/StreakPanel.java` — current streak (ending today/yesterday) + longest streak.
      Done when: streak ≥ 2 for today+yesterday; resets after a 2-day gap.
- [ ] **6.3** Refresh stats/streaks whenever data changes.
      Done when: adding/deleting updates both panels.

## Phase 7 — Polish & Verification

- [ ] **7.1** Empty-state messages, consistent spacing/colors, resizable layouts, remove dead code and unnecessary comments.
      Done when: no stray comments and layout resizes cleanly.
- [ ] **7.2** Run the full manual test pass (below).
      Done when: all checks pass.
- [ ] **7.3** Add a short README with build/run steps.
      Done when: a fresh machine can build and launch from the README.

---

## Manual Test Pass

- [ ] `build.bat` compiles with zero errors.
- [ ] `run.bat` launches the themed `MainFrame`.
- [ ] Add a record with an image → row shown, image copied to `data/images/`.
- [ ] Close + relaunch → data persists.
- [ ] Edit a record → persists after relaunch.
- [ ] Multi-row select + Delete → removed from table and file.
- [ ] Filter by type → only matching rows; clearing restores all.
- [ ] Sort by date asc/desc → correct across month/year boundaries.
- [ ] Notes containing `|` and multi-line text round-trip correctly.
- [ ] Stats totals match per-type counts.
- [ ] Streak ≥ 2 for today+yesterday; resets after a 2-day gap.
- [ ] Uploaded background persists after relaunch.
