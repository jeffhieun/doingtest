import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrongPasswordGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+<>?";

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4");
        }

        List<Character> password = new ArrayList<>();

        // Ensure at least one of each type
        password.add(UPPER.charAt(random.nextInt(UPPER.length())));
        password.add(LOWER.charAt(random.nextInt(LOWER.length())));
        password.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        String allChars = UPPER + LOWER + DIGITS + SPECIAL;

        // Fill remaining
        for (int i = 4; i < length; i++) {
            password.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle to avoid predictable pattern
        Collections.shuffle(password);

        StringBuilder result = new StringBuilder();
        for (char c : password) {
            result.append(c);
        }

        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(generatePassword(12));
    }
}