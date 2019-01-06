import java.util.Arrays;


/**
 * Created by Stanislau Kozel.
 */
public class MatrixOperations {

    public static double[][] substract(double[][] a, double[][] b) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = a[i][j] - b[i][j];
            }
        }
        return res;
    }

    public static double[][] plus(double[][] a, double[][] b) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                res[i][j] = a[i][j] + b[i][j];
            }
        }
        return res;
    }

    public static double[][] divide(double[][] a, double x) {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                res[i][j] = a[i][j] / x;
            }
        }
        return res;
    }


    public static double[][] transpose(double[][] a) {
        double[][] res = new double[a[0].length][a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                res[j][i] = a[i][j];
            }
        }
        return res;
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        double[][] res = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    res[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return res;
    }

    public static boolean equals(double[][] a, double[][] b) {
        if (a == b) {
            return true;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (b[i].length != a[i].length || !Arrays.equals(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    public static double[][] copy(double[][] a) {
        double[][] result = new double[a.length][];
        for (int i = 0; i < a.length; i++) {
            result[i] = Arrays.copyOf(a[i], a[i].length);
        }
        return result;
    }

}
