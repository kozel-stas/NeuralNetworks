package com.PIVS;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Stanislau Kozel
 */
public class RNN {

    /**
     * Шаг обучения.
     */
    private final double alpha;
    /**
     * Максимальная ошибка.
     */
    private final double maxError;
    /**
     * Максимальное количсетво итераций.
     */
    private final int maxIteration;
    /**
     * Размер окна.
     */
    private final int sizeOfWindow;
    /**
     * Размер 2-го слоя.
     */
    private final int sizeOfSecondLayer;
    /**
     * Флаг, показывающий нужно ли занулять контекстный нейрон между итерациями.
     */
    private final boolean nullifyContextNeuron;

    /**
     * Последовательность.
     */
    private double[] sequence;
    /**
     * Контекстный нейрон.
     */
    private double contextNeuron;
    /**
     * матрица весов между входным и скрытым слоем.
     */
    private double[][] W_1;
    /**
     * матрица весов между скрытым и выходным слоем.
     */
    private double[][] W_2;
    /**
     * 2-ой слой.
     */
    double[] secondLayer;

    /**
     * Конструктор.
     */
    public RNN(
            double alpha,
            double maxError,
            int maxIteration,
            int sizeOfWindow,
            int sizeOfSecondLayer,
            double[] sequence,
            boolean nullifyContextNeuron
    ) {
        this.alpha = alpha;
        this.maxError = maxError;
        this.maxIteration = maxIteration;
        this.sizeOfWindow = sizeOfWindow;
        this.sizeOfSecondLayer = sizeOfSecondLayer;
        this.sequence = sequence;
        this.nullifyContextNeuron = nullifyContextNeuron;
        initialize();
    }

    /**
     * Обучение нейронной сети,
     * W_1, W_2, sequence, contextNeuron, secondLayer.
     */
    void startLearning() {
        int iteration = 0;
        AtomicReference<Double> error = new AtomicReference<>(Double.MAX_VALUE);
        secondLayer = new double[sizeOfSecondLayer];

        while (iteration < maxIteration && error.get() > maxError) {
            error.set(0D);

            train(this::calibrateWeightMatrix);

            train(offset -> {
                double linearError = contextNeuron - sequence[offset + sizeOfWindow];
                error.set(linearError * linearError + error.get());
            });

            iteration++;

            System.out.println("Error " + error + " context neuron " + contextNeuron + " iteration " + iteration);
        }
    }

    /**
     * Предсказание последовательности на последних весах,
     * W_1, W_2, sequence, contextNeuron, secondLayer.
     */
    void forecasting(int number) {

        for (int i = 0; i < number; i++) {

            for (int j = 0; j < sizeOfSecondLayer; j++) {
                secondLayer[j] = 0D;
                secondLayer[j] += contextNeuron * W_1[0][j];
                for (int k = 0; k < sizeOfWindow; k++) {
                    secondLayer[j] += sequence[sequence.length - sizeOfWindow + k] * W_1[k + 1][j];
                }
            }

            contextNeuron = 0;
            for (int k = 0; k < sizeOfSecondLayer; k++) {
                contextNeuron += secondLayer[k] * W_2[k][0];
            }

            sequence = Arrays.copyOf(sequence, sequence.length + 1);
            sequence[sequence.length - 1] = contextNeuron;
        }


        for (int i = 0; i < sequence.length; i++) {
            System.out.print(sequence[i] + ",");
        }

        System.out.println();

        printMatrix(W_1);
        printMatrix(W_2);
    }

    /**
     * Печать матрицы.
     */
    private void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Инитиализация W_1, W_2, заполнение рандомными числами от -1 до 1.
     */
    private void initialize() {
        W_1 = new double[sizeOfWindow + 1][sizeOfSecondLayer];
        W_2 = new double[sizeOfSecondLayer][1];

        for (int i = 0; i < W_1.length; i++) {
            for (int j = 0; j < W_1[i].length; j++) {
                W_1[i][j] = ThreadLocalRandom.current().nextDouble(-1, 1);
            }
        }

        for (int i = 0; i < W_2.length; i++) {
            for (int j = 0; j < W_2[i].length; j++) {
                W_2[i][j] = ThreadLocalRandom.current().nextDouble(-1, 1);
            }
        }

        printMatrix(W_1);
        System.out.println("");
        printMatrix(W_2);
    }

    /**
     * Тренировка нейронной сети, (1 итерация)
     * W_1, W_2, sequence, contextNeuron, secondLayer.
     */
    private void train(Consumer<Integer> postAction) {
        if (nullifyContextNeuron) {
            contextNeuron = 0;
        }

        for (int offset = 0; offset < sequence.length - sizeOfWindow; offset++) {

            for (int j = 0; j < sizeOfSecondLayer; j++) {
                secondLayer[j] = 0D;
                secondLayer[j] += contextNeuron * W_1[0][j];
                for (int k = 0; k < sizeOfWindow; k++) {
                    secondLayer[j] += sequence[offset + k] * W_1[k + 1][j];
                }
            }

            contextNeuron = 0;
            for (int k = 0; k < sizeOfSecondLayer; k++) {
                contextNeuron += secondLayer[k] * W_2[k][0];
            }

            postAction.accept(offset);
        }

    }

    /**
     * Изменение матрицы весов, использует W_1, W_2, sequence, 3-ий слой, alpha.
     */
    private void calibrateWeightMatrix(int offset) {
        double linearError = contextNeuron - sequence[offset + sizeOfWindow];

        for (int i = 0; i < sizeOfWindow; i++) {
            for (int j = 0; j < sizeOfSecondLayer; j++) {
                W_1[i][j] = W_1[i][j] - alpha * sequence[offset + i] * linearError * W_2[j][0];
            }
        }

        for (int i = 0; i < sizeOfSecondLayer; i++) {
            W_2[i][0] = W_2[i][0] - alpha * secondLayer[i] * linearError;
        }
    }

}
