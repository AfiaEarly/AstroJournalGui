package astrojournal.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

public final class DateUtil {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private DateUtil() {
    }

    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DATE_FORMAT);
    }

    public static LocalTime parseTime(String text) {
        return LocalTime.parse(text, TIME_FORMAT);
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMAT);
    }

    public static String formatTime(LocalTime time) {
        return time == null ? "" : time.format(TIME_FORMAT);
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
