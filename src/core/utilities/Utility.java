package core.utilities;

public class Utility {


    public static boolean doubleEquals(double a, double b, double allowedGap) {
        return Math.abs(b - a) <= allowedGap;

    }

}
