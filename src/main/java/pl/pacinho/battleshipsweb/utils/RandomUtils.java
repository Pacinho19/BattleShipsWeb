package pl.pacinho.battleshipsweb.utils;

import java.util.Random;

public class RandomUtils {
    public static int getInt(int min, int max) {
        Random random = new Random();
        return random.ints(min, max)
                .findFirst()
                .getAsInt();
    }
}
