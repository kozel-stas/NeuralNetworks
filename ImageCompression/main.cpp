//created by Stanislau Kozel.
#include <iostream>
#include <fstream>
#include <time.h>
#include <cmath>
#include <chrono>
#include "image_util.h"

using namespace std;

//Image
MatrixOfImage *matrixOfImage;
//RGB data of image
RGB **rgbInfo;

// width of blocks
int block_high;
// high of blocks
int block_width;
// msx error
int maxError;

// number of blocks in width
int number_of_blocks_width;
// number of blocks in high
int number_of_blocks_height;

// 2 layer of neurons
int P;
// matrix of blocks with reference vector
double **X;
// matrix of blocks after composing-decomposing
double **X_2;
// matrix with linear error
double **deltaX;

// number of blocks in picture
int L;
// block_width * block_high * 3;
int N;

// matrix of weight 1
double **W_1;
// matrix of weight 2
double **W_2;
// second layer
double *Y;
// data for W_1
double *tmpArray;

// no operation with global variables
void delMatrix(double **array, int size) {
    for (int i = 0; i < size; i++) {
        delete array[i];
    }
    delete[] array;
}

// no operation with global variables
double **createMatrix(int i, int j) {
    double **array = new double *[i];
    for (int count = 0; count < i; count++)
        array[count] = new double[j];
    return array;
}

// no operation with global variables
double *createArray(int i) {
    return new double[i];
}

// operation with X, convert image to reference vector that stored in X(global variable).
void convertToArrayOfRef() {
    int index = 0;
    int sub_index = 0;
    for (int block_number_i_index = 0; block_number_i_index < number_of_blocks_height; block_number_i_index++) {
        for (int block_number_j_index = 0; block_number_j_index < number_of_blocks_width; block_number_j_index++) {
            for (int i = block_number_i_index * block_high; i < block_number_i_index * block_high + block_high; i++) {
                for (int j = block_number_j_index * block_width;
                     j < block_number_j_index * block_width + block_width; j++) {
                    X[index][sub_index] = (2 * (double) rgbInfo[i][j].red / 255) - 1;
                    sub_index++;
                    X[index][sub_index] = (2 * (double) rgbInfo[i][j].green / 255) - 1;
                    sub_index++;
                    X[index][sub_index] = (2 * (double) rgbInfo[i][j].blue / 255) - 1;
                    sub_index++;
                }
            }
            sub_index = 0;
            index++;
        }
        for (int i = block_number_i_index * block_high; i < block_number_i_index * block_high + block_high; i++) {
            for (int j = matrixOfImage->width - block_width;
                 j < matrixOfImage->width; j++) {
                X[index][sub_index] = (2 * (double) rgbInfo[i][j].red / 255) - 1;
                sub_index++;
                X[index][sub_index] = (2 * (double) rgbInfo[i][j].green / 255) - 1;
                sub_index++;
                X[index][sub_index] = (2 * (double) rgbInfo[i][j].blue / 255) - 1;
                sub_index++;
            }
        }
        sub_index = 0;
        index++;
    }
    for (int block_number_j_index = 0; block_number_j_index < number_of_blocks_width; block_number_j_index++) {
        for (int i = matrixOfImage->height - block_high; i < matrixOfImage->height; i++) {
            for (int j = block_number_j_index * block_width;
                 j < block_number_j_index * block_width + block_width; j++) {
                X[index][sub_index] = (2 * (double) rgbInfo[i][j].red / 255) - 1;
                sub_index++;
                X[index][sub_index] = (2 * (double) rgbInfo[i][j].green / 255) - 1;
                sub_index++;
                X[index][sub_index] = (2 * (double) rgbInfo[i][j].blue / 255) - 1;
                sub_index++;
            }
        }
        sub_index = 0;
        index++;
    }
    for (int i = matrixOfImage->height - block_high; i < matrixOfImage->height; i++) {
        for (int j = matrixOfImage->width - block_width;
             j < matrixOfImage->width; j++) {
            X[index][sub_index] = (2 * (double) rgbInfo[i][j].red / 255) - 1;
            sub_index++;
            X[index][sub_index] = (2 * (double) rgbInfo[i][j].green / 255) - 1;
            sub_index++;
            X[index][sub_index] = (2 * (double) rgbInfo[i][j].blue / 255) - 1;
            sub_index++;
        }
    }
}

// operation with matrixOfImage, convert reference vector to image that stored in matrixOfImage(global variable).
void restoreImage(double **X, const char *name) {
    int index = 0;
    int sub_index = 0;
    RGB **rgbInfo = new RGB *[matrixOfImage->height];
    for (int i = 0; i < matrixOfImage->height; i++) {
        rgbInfo[i] = new RGB[matrixOfImage->width];
    }
    for (int block_number_i_index = 0; block_number_i_index < number_of_blocks_height; block_number_i_index++) {
        for (int block_number_j_index = 0; block_number_j_index < number_of_blocks_width; block_number_j_index++) {
            for (int i = block_number_i_index * block_high; i < block_number_i_index * block_high + block_high; i++) {
                for (int j = block_number_j_index * block_width;
                     j < block_number_j_index * block_width + block_width; j++) {
                    int tmp = 255 * (X[index][sub_index] + 1) / 2;
                    if (tmp > 255) tmp = 255;
                    if (tmp < 0) tmp = 0;
                    rgbInfo[i][j].red = tmp;
                    sub_index++;
                    tmp = 255 * (X[index][sub_index] + 1) / 2;
                    if (tmp > 255) tmp = 255;
                    if (tmp < 0) tmp = 0;
                    rgbInfo[i][j].green = tmp;
                    sub_index++;
                    tmp = 255 * (X[index][sub_index] + 1) / 2;
                    if (tmp > 255) tmp = 255;
                    if (tmp < 0) tmp = 0;
                    rgbInfo[i][j].blue = tmp;
                    sub_index++;
                }
            }
            sub_index = 0;
            index++;
        }
        // comrnts
        // memory
        for (int i = block_number_i_index * block_high; i < block_number_i_index * block_high + block_high; i++) {
            for (int j = matrixOfImage->width - block_width;
                 j < matrixOfImage->width; j++) {
                int tmp = 255 * (X[index][sub_index] + 1) / 2;
                if (tmp > 255) tmp = 255;
                if (tmp < 0) tmp = 0;
                rgbInfo[i][j].red = tmp;
                sub_index++;
                tmp = 255 * (X[index][sub_index] + 1) / 2;
                if (tmp > 255) tmp = 255;
                if (tmp < 0) tmp = 0;
                rgbInfo[i][j].green = tmp;
                sub_index++;
                tmp = 255 * (X[index][sub_index] + 1) / 2;
                if (tmp > 255) tmp = 255;
                if (tmp < 0) tmp = 0;
                rgbInfo[i][j].blue = tmp;
                sub_index++;
            }
        }
        sub_index = 0;
        index++;
    }
    for (int block_number_j_index = 0; block_number_j_index < number_of_blocks_width; block_number_j_index++) {
        for (int i = matrixOfImage->height - block_high; i < matrixOfImage->height; i++) {
            for (int j = block_number_j_index * block_width;
                 j < block_number_j_index * block_width + block_width; j++) {
                int tmp = 255 * (X[index][sub_index] + 1) / 2;
                if (tmp > 255) tmp = 255;
                if (tmp < 0) tmp = 0;
                rgbInfo[i][j].red = tmp;
                sub_index++;
                tmp = 255 * (X[index][sub_index] + 1) / 2;
                if (tmp > 255) tmp = 255;
                if (tmp < 0) tmp = 0;
                rgbInfo[i][j].green = tmp;
                sub_index++;
                tmp = 255 * (X[index][sub_index] + 1) / 2;
                if (tmp > 255) tmp = 255;
                if (tmp < 0) tmp = 0;
                rgbInfo[i][j].blue = tmp;
                sub_index++;
            }
        }
        sub_index = 0;
        index++;
    }
    for (int i = matrixOfImage->height - block_high; i < matrixOfImage->height; i++) {
        for (int j = matrixOfImage->width - block_width;
             j < matrixOfImage->width; j++) {
            int tmp = 255 * (X[index][sub_index] + 1) / 2;
            if (tmp > 255) tmp = 255;
            if (tmp < 0) tmp = 0;
            rgbInfo[i][j].red = tmp;
            sub_index++;
            tmp = 255 * (X[index][sub_index] + 1) / 2;
            if (tmp > 255) tmp = 255;
            if (tmp < 0) tmp = 0;
            rgbInfo[i][j].green = tmp;
            sub_index++;
            tmp = 255 * (X[index][sub_index] + 1) / 2;
            if (tmp > 255) tmp = 255;
            if (tmp < 0) tmp = 0;
            rgbInfo[i][j].blue = tmp;
            sub_index++;
        }
    }
    matrixOfImage->matrixOfPixels = rgbInfo;
    formImage(matrixOfImage, name);
    for (int i = 0; i < matrixOfImage->height; i++) {
        delete rgbInfo[i];
    }
    delete[] rgbInfo;
}

// generate random values and stored it to matrix W_1 and W_2.
void generateRandomWeight() {
    double MAX_VALUE = 2;
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < P; j++) {
            double randWeight = ((double) (rand()) / RAND_MAX) * MAX_VALUE - 1;
            W_1[i][j] = randWeight;
            W_2[j][i] = randWeight;
        }
    }
}

// calculate alpha for matrix W_1, used reference vector(X - global variable)
double calculateAlphaW_1(int index) {
    double value = N * N / 16;
    for (int i = 0; i < N; i++) {
        value += X[index][i] * X[index][i];
    }
    return 1 / value;
}

// calculate alpha for matrix W_2, used vector of data(Y - global variable)
double calculateAlphaW_2() {
    double value = N * N / 16;
    for (int i = 0; i < P; i++) {
        value += Y[i] * Y[i];
    }
    return 1 / value;
}

// recalculate W_1(global variable), used deltaX, W_2, X
void calculateW_1(int index) {
    double alpha = calculateAlphaW_1(index);

    for (int j = 0; j < P; j++) {
        tmpArray[j] = 0;
        for (int i = 0; i < N; i++) {
            tmpArray[j] += deltaX[index][i] * W_2[j][i];
        }
    }

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < P; j++) {
            W_1[i][j] = W_1[i][j] - alpha * X[index][i] * tmpArray[j];
        }
    }
}

// recalculate W_2(global variable), used W_2, Y, deltaX
void calculateW_2(int index) {
    double alpha = calculateAlphaW_2();

    for (int i = 0; i < P; i++) {
        for (int j = 0; j < N; j++) {
            W_2[i][j] = W_2[i][j] - alpha * Y[i] * deltaX[index][j];
        }
    }
}

// training neural network, calculate Y, X_2, deltaX.
double composeDecomposePicture() {

    for (int i = 0; i < L; i++) {

        for (int j = 0; j < P; j++) {
            Y[j] = 0;
            for (int k = 0; k < N; k++) {
                Y[j] += W_1[k][j] * X[i][k];
            }
        }

        for (int k = 0; k < N; k++) {
            X_2[i][k] = 0;
            for (int j = 0; j < P; j++) {
                X_2[i][k] += Y[j] * W_2[j][k];
            }

            // delta X
            deltaX[i][k] = X_2[i][k] - X[i][k];
        }

        calculateW_1(i);
        calculateW_2(i);

    }

    double error = 0;
    for (int i = 0; i < L; i++) {

        for (int j = 0; j < P; j++) {
            Y[j] = 0;
            for (int k = 0; k < N; k++) {
                Y[j] += W_1[k][j] * X[i][k];
            }
        }

        for (int k = 0; k < N; k++) {
            X_2[i][k] = 0;
            for (int j = 0; j < P; j++) {
                X_2[i][k] += Y[j] * W_2[j][k];
            }
            // error
            error += (X_2[i][k] - X[i][k]) * (X_2[i][k] - X[i][k]);
        }

    }
    return error;
}

// free all resources.
void free() {
    delMatrix(W_1, N);
    delMatrix(W_2, P);
    delMatrix(deltaX, L);
    delMatrix(X_2, L);
    delMatrix(X, L);
    delete[] Y;
    delete[] tmpArray;
    delete matrixOfImage;
}

int main(int argc, char *argv[]) {
    srand(time(0));
    double Z = 0;

    matrixOfImage = getMatrixOfImage("test.bmp");
    rgbInfo = matrixOfImage->matrixOfPixels;

    block_width = 0;
    block_high = 0;
    P = 0;

    cout << "block width: ";
    cin >> block_width;
    cout << std::endl << "block high: ";
    cin >> block_high;
    cout << std::endl << "neuron amount: ";
    cin >> P;

    // size of line
    N = block_width * block_high * 3;

    double width = (double) matrixOfImage->width / (double) block_width;
    double height = (double) matrixOfImage->height / (double) block_high;

    if (width == (double) ((int) width)) {
        number_of_blocks_width = ((int) width) - 1;
    } else {
        number_of_blocks_width = (int) floor(width);
    }
    if (height == (double) ((int) height)) {
        number_of_blocks_height = ((int) height) - 1;
    } else {
        number_of_blocks_height = (int) floor(height);
    }

    // number of blocks.
    L = (number_of_blocks_width + 1) * (number_of_blocks_height + 1);
    X = createMatrix(L, N);
    tmpArray = createArray(P);
    X_2 = createMatrix(L, N);
    deltaX = createMatrix(L, N);
    Y = createArray(P);

    convertToArrayOfRef();

    cout << endl << "input max error: ";
    cin >> maxError;

    W_1 = createMatrix(N, P);
    W_2 = createMatrix(P, N);
    generateRandomWeight();

    Z = (L * N) / ((L + N) * P * 2.0);

    int numOfIteration = 0;
    double error = 0;
    cout << "Z: " << Z << endl;
    do {
        numOfIteration++;
        auto begin = std::chrono::steady_clock::now();
        error = composeDecomposePicture();
        auto end = std::chrono::steady_clock::now();
        auto elapsed_ms = std::chrono::duration_cast<std::chrono::milliseconds>(end - begin);
        cout << "Iteration " << numOfIteration << " error " << error
             << " time: " << elapsed_ms.count() << endl;
        if (numOfIteration % 100 == 0 || error < maxError) {
            char str[30];
            sprintf(str, "result%d.bmp", numOfIteration);
            restoreImage(X_2, str);
        }
    } while (error > maxError);

    free();
    return 0;
}
