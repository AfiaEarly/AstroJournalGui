package astrojournal.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class DateUtil {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter STORAGE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final List<DateTimeFormatter> TIME_INPUT_FORMATS = List.of(
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mm a")
                    .toFormatter(Locale.ENGLISH),
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mma")
                    .toFormatter(Locale.ENGLISH),
            STORAGE_TIME_FORMAT);

    private DateUtil() {
    }

    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DATE_FORMAT);
    }

    public static LocalTime parseTime(String text) {
        DateTimeParseException failure = null;
        for (DateTimeFormatter format : TIME_INPUT_FORMATS) {
            try {
                return LocalTime.parse(text, format);
            } catch (DateTimeParseException e) {
                failure = e;
            }
        }
        throw failure;
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMAT);
    }

    public static String formatTime(LocalTime time) {
        return time == null ? "" : time.format(DISPLAY_TIME_FORMAT);
    }

    public static long dayGap(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    public static boolean areConsecutive(LocalDate earlier, LocalDate later) {
        return dayGap(earlier, later) == 1;
    }

    public static int currentStreak(Collection<LocalDate> dates, LocalDate today) {
        List<LocalDate> days = distinctSorted(dates);
        if (days.isEmpty()) {
            return 0;
        }
        long gap = dayGap(days.get(days.size() - 1), today);
        if (gap < 0 || gap > 1) {
            return 0;
        }
        return runLengthEndingAt(days, days.size() - 1);
    }

    public static int longestStreak(Collection<LocalDate> dates) {
        List<LocalDate> days = distinctSorted(dates);
        if (days.isEmpty()) {
            return 0;
        }
        int longest = 1;
        int current = 1;
        for (int i = 1; i < days.size(); i++) {
            current = areConsecutive(days.get(i - 1), days.get(i)) ? current + 1 : 1;
            longest = Math.max(longest, current);
        }
        return longest;
    }

    private static int runLengthEndingAt(List<LocalDate> days, int end) {
        int length = 1;
        for (int i = end; i > 0 && areConsecutive(days.get(i - 1), days.get(i)); i--) {
            length++;
        }
        return length;
    }

    private static List<LocalDate> distinctSorted(Collection<LocalDate> dates) {
        return dates.stream().distinct().sorted().toList();
    }
}
