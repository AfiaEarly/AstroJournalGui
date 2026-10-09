package astrojournal.util;

public final class TextEscaper {

    private TextEscaper() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        var escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> escaped.append("\\\\");
                case '|' -> escaped.append("\\|");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                default -> escaped.append(c);
            }
        }
        return escaped.toString();
    }

    public static String unescape(String value) {
        if (value == null) {
            return "";
        }
        var unescaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);
                switch (next) {
                    case 'n' -> unescaped.append('\n');
                    case 'r' -> unescaped.append('\r');
                    case '\\' -> unescaped.append('\\');
                    case '|' -> unescaped.append('|');
                    default -> unescaped.append(next);
                }
            } else {
                unescaped.append(c);
            }
        }
        return unescaped.toString();
    }
}
