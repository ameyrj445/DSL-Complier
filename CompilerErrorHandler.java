import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CompilerErrorHandler {
    private static final List<String> errors = new ArrayList<>();

    private CompilerErrorHandler() {
    }

    public static String format(int line, String message) {
        return "Error at line " + line + ": " + message;
    }

    public static void report(int line, String message) {
        String formatted = format(line, message);
        errors.add(formatted);
        System.out.println(formatted);
    }

    public static void report(Exception e) {
        if (e != null && e.getMessage() != null) {
            errors.add(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public static RuntimeException asException(int line, String message) {
        return new RuntimeException(format(line, message));
    }

    public static List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public static int getErrorCount() {
        return errors.size();
    }

    public static void clear() {
        errors.clear();
    }
}
