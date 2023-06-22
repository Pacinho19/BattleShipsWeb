package pl.pacinho.battleshipsweb.utils;

public class SleepUtils {
    public static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
