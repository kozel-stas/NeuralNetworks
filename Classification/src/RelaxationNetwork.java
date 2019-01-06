import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stanislau Kozel.
 * Consulted with Maksim Golosov about solution of async mode Hopfield network.
 */
public class RelaxationNetwork {

    /**
     * number of projections in testcase.
     */
    private int numberOfProjection;

    /**
     * height of projection
     */
    private int n;

    /**
     * width of projection
     */
    private int m;

    /**
     * weight matrix
     */
    private double W[][];

    /**
     * input projections
     */
    private double[][][] X;


    public RelaxationNetwork(int n, int m, int numberOfProjection) {
        this.m = m;
        this.n = n;
        this.numberOfProjection = numberOfProjection;
        W = new double[n * m][n * m];
        X = retrieve("projections.txt");
    }

    // Load data from file
    private double[][][] retrieve(String path) {
        double[][][] res = new double[numberOfProjection][n * m][1];
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            AtomicInteger numberOfProjection = new AtomicInteger();
            AtomicInteger numberOfLine = new AtomicInteger();
            reader.lines().forEach((line) -> {
                if ("/".equals(line)) {
                    numberOfLine.set(0);
                    numberOfProjection.getAndIncrement();
                } else {
                    for (int i = 0; i < m; i++) {
                        switch (line.charAt(i)) {
                            case '.':
                                res[numberOfProjection.get()][i + numberOfLine.get() * m][0] = -1;
                                break;
                            case '#':
                                res[numberOfProjection.get()][i + numberOfLine.get() * m][0] = +1;
                                break;
                        }
                    }
                    numberOfLine.incrementAndGet();
                }
            });
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            System.exit(-1);
        }
        return res;
    }

    // recalculate weight matrix for projections.
    public void recalculateWeights() {
        for (int i = 0; i < numberOfProjection; i++) {
            //XiT * Xi - XiT*Wi-1*Xi

            //XiT
            double[][] xT = MatrixOperations.transpose(X[i]);

            double[][] result1 = MatrixOperations.substract(
                    MatrixOperations.multiply(xT, X[i]),
                    MatrixOperations.multiply(
                            MatrixOperations.multiply(xT, W),
                            X[i]
                    )
            );

            System.out.println(result1.length + " " + result1[0].length);

            //Wi-1 * Xi
            double[][] Y = MatrixOperations.multiply(W, X[i]);

            // (Wi-1*Xi - Xi)
            double[][] tempRes = MatrixOperations.substract(Y, X[i]);

            // (Wi-1*Xi - Xi) * (Wi-1*Xi - Xi)T
            double[][] result2 = MatrixOperations.multiply(
                    tempRes,
                    MatrixOperations.transpose(tempRes)
            );

            W = MatrixOperations.plus(
                    W,
                    MatrixOperations.divide(result2, result1[0][0])
            );
        }
        out(W);
    }

    // relaxation, fileName - file of projections for relaxation.
//    public void startRelaxation(String fileName) {
//        double[][][] X = retrieve(fileName);
//        double[][][] out = new double[numberOfProjection][n * m][1];
//        for (int i = 0; i < numberOfProjection; i++) {
//            double[][] res = X[i];
//            double[][] res3;
//            int[] exclude = new int[0];
//            while (true) {
//                double[][] res2 = relaxationForProjection(res, activation(res, MatrixOperations.multiply(W, res)), exclude);
//                res3 = relaxationForProjection(res2, activation(res2, MatrixOperations.multiply(W, res2)), exclude);
//                if (MatrixOperations.equals(res, res3)) {
//                    out[i] = res3;
//                    break;
//                } else {
//                    int index = 0;
//                    int[] tmp = new int[res.length];
//                    for (int k = 0; k < res3.length; k++) {
//                        if (res[k][0] == res3[k][0]) {
//                            tmp[index] = k;
//                            index++;
//                        }
//                    }
//                    exclude = Arrays.copyOfRange(tmp, 0, index);
//                    res = res2;
//                }
//            }
//        }
//
//        save(out, "relax_" + fileName);
//    }

    public void startRelaxation(String fileName) {
        double[][][] X = retrieve(fileName);
        double[][][] out = new double[numberOfProjection][n * m][1];
        for (int i = 0; i < numberOfProjection; i++) {
            double[][] res = X[i];
            double[][] res3;
            while (true) {
                double[][] res2 = relaxationForProjection(res, activation(res, MatrixOperations.multiply(W, res)), new int[0]);
                res3 = relaxationForProjection(res2, activation(res2, MatrixOperations.multiply(W, res2)), new int[0]);
                if (MatrixOperations.equals(res, res3)) {
                    out[i] = res3;
                    break;
                } else {
                    res = res2;
                }
            }
        }

        save(out, "relax_" + fileName);
    }

    //recursive method for asyn relaxing.
    private double[][] relaxationForProjection(double[][] x, double[][] y, int[] excludeIndex) {
        if (excludeIndex.length == x.length) {
            return x;
        }
        int[] newExclude = Arrays.copyOf(excludeIndex, excludeIndex.length + 1);
        newExclude[excludeIndex.length] = nextIntInRangeButExclude(0, y.length, excludeIndex);
        double[][] newX = MatrixOperations.copy(x);
        newX[newExclude[excludeIndex.length]][0] = y[newExclude[excludeIndex.length]][0];
        return relaxationForProjection(newX, activation(newX, MatrixOperations.multiply(W, newX)), newExclude);
    }

    //function of activation
    private double[][] activation(double[][] x, double[][] y) {
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y[i].length; j++) {
                if (y[i][j] > 0) {
                    y[i][j] = 1;
                    continue;
                }
                if (y[i][j] < 0) {
                    y[i][j] = -1;
                    continue;
                }
                if (y[i][j] == 0) {
                    y[i][j] = x[i][j];
                    continue;
                }
            }
        }
        return y;
    }

    //save data to file
    private void save(double[][][] newX, String fileName) {
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(fileName));
            for (int projection = 0; projection < numberOfProjection; projection++) {
                for (int i = 0; i < n; i++) {
                    String s = "";
                    for (int j = 0; j < m; j++) {
                        double x = newX[projection][i * m + j][0];
                        if (x == 1) {
                            s += '#';
                        } else {
                            s += '.';
                        }
                    }
                    bf.write(s);
                    bf.newLine();
                }
                bf.write("/");
                bf.newLine();
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int nextIntInRangeButExclude(int start, int end, int[] excludes) {
//        int rangeLength = end - start - excludes.length;
//        int randomInt = new Random(System.currentTimeMillis()).nextInt(rangeLength) + start;
//
//        for (int i = 0; i < excludes.length; i++) {
//            if (excludes[i] > randomInt) {
//                return randomInt;
//            }
//            randomInt++;
//        }
//        return randomInt;
        if (excludes.length == 0) {
            return start;
        }
        for (int i = start; i < end; i++) {
            int count = 0;
            for (int j : excludes) {
                if (i != j) {
                    count++;
                }
            }
            if (count == excludes.length) {
                return i;
            }
        }
        return excludes[excludes.length - 1] + 1;
    }

    public void out(double[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                System.out.print(a[i][j]);
            }
            System.out.println();
        }
    }

}
