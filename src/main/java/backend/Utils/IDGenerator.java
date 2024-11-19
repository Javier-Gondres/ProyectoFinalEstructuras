package backend.Utils;

import java.util.UUID;

public class IDGenerator {
    public static String generateId(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, Math.min(length, uuid.length()));
    }
}
