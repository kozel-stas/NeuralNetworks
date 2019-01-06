
public class Main {

    public static void main(String[] args) {
//        RelaxationNetwork relaxationNetwork = new RelaxationNetwork(15, 30, 4);
//        relaxationNetwork.recalculateWeights();
//        relaxationNetwork.startRelaxation("projections.txt");
//        relaxationNetwork.startRelaxation("noise_projections.txt");

        int[] a = new int[]{-1, 1, 1, -1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, 1};
        int[][] res = new int[15][15];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                res[i][j] = a[i] * a[j];
                if (i == j) {
                    res[i][j] = 0;
                }
            }
        }
        sout(res);

        int[] b = new int[]{1, 1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1, 1, 1};
        int[][] res2 = new int[15][15];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                res2[i][j] = b[i] * b[j];
                if (i == j) {
                    res2[i][j] = 0;
                }
            }
        }

        System.out.println("2");

        sout(res2);


        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] += res2[i][j];
            }
        }

        System.out.println("3-W");
        sout(res);


        int[] a_relax = new int[15];

        for (int i = 0; i < a_relax.length; i++) {
            for (int k = 0; k < a.length; k++) {
                a_relax[i] += a[i] * res[k][i];
            }
        }

        System.out.println("1 - check");
        sout(a_relax);

        a_relax = activ(a_relax);

        System.out.println("1 - activation");
        sout(a_relax);

        int[] a_relax_w = new int[15];

        for (int i = 0; i < a_relax_w.length; i++) {
            for (int k = 0; k < a.length; k++) {
                a_relax_w[i] += a_relax[i] * res[k][i];
            }
        }

        System.out.println("1 - activation - state2");
        a_relax_w = activ(a_relax_w);
        sout(a_relax_w);

        int[] b_relax = new int[15];

        for (int i = 0; i < b_relax.length; i++) {
            for (int k = 0; k < a.length; k++) {
                b_relax[i] += a[i] * res[k][i];
            }
        }

        System.out.println("2 - check");
        sout(b_relax);

        b_relax = activ(b_relax);

        System.out.println("2 - activation");
        sout(b_relax);

        int[] b_relax_w = new int[15];

        for (int i = 0; i < b_relax_w.length; i++) {
            for (int k = 0; k < a.length; k++) {
                b_relax_w[i] += b_relax[i] * res[k][i];
            }
        }

        System.out.println("2 - activation - state2");
        b_relax_w = activ(b_relax_w);
        sout(b_relax_w);


        int[] b_relax_w_w = new int[15];

        for (int i = 0; i < b_relax_w_w.length; i++) {
            for (int k = 0; k < a.length; k++) {
                b_relax_w_w[i] += b_relax_w[i] * res[k][i];
            }
        }

        System.out.println("2 - activation - state3");
        b_relax_w_w = activ(b_relax_w_w);
        sout(b_relax_w_w);


        int[] final_relax = new int[15];
        int[] in = new int[]{-1, -1, -1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1};

        for (int i = 0; i < final_relax.length; i++) {
            for (int k = 0; k < a.length; k++) {
                final_relax[i] += in[i] * res[k][i];
            }
        }

        System.out.println("final - relax");
        final_relax = activ(final_relax);
        sout(final_relax);

        int[] final_relax_w = new int[15];

        for (int i = 0; i < final_relax_w.length; i++) {
            for (int k = 0; k < a.length; k++) {
                final_relax_w[i] += final_relax[i] * res[k][i];
            }
        }

        System.out.println("final - relax state 2");
        final_relax_w = activ(final_relax_w);
        sout(final_relax_w);
    }

    public static void sout(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] < 0) {
                    System.out.print(a[i][j] + " ");
                } else {
                    System.out.print(" " + a[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public static void sout(int[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] < 0) {
                System.out.print(a[i] + " ");
            } else {
                System.out.print(" " + a[i] + " ");
            }
        }
        System.out.println();
    }

    public static int[] activ(int[] a) {
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            if (a[i] >= 0) {
                res[i] = 1;
            } else {
                res[i] = -1;
            }
        }
        return res;
    }

}