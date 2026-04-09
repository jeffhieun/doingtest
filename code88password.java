import java.security.SecureRandom;
import java.util.Base64;

public class Base64Password {

    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[111111];

        random.nextBytes(bytes);

        String password = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        System.out.println(password);
    }
}
