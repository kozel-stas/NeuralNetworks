package com.PIVS;

import java.awt.*;

/**
 * Created by Stanislau Kozel.
 */
public class Main {

    public static void main(String[] args) {
        RNN rnn = new RNN(0.000000000005, 0.0000005, 10000000, 6, 2, new double[]{1, 2, 6, 24, 120, 720, 5040, 40320}, true);

        rnn.startLearning();
        rnn.forecasting(5);

        Toolkit.getDefaultToolkit().beep();
    }
}
