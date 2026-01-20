package MLP;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Classification utilisant un Perceptron Multi-Couches (MLP)
 * Avec tracking des statistiques d'entraînement
 */
public class MLPClassification extends AlgoClassification {

    private MLP mlp;
    private int inputSize;
    private int outputSize;
    private boolean shuffle;
    private double initialLearningRate;
    private boolean decayLearningRate;

    // Statistiques d'entraînement
    private List<Double> trainErrors;
    private List<Double> trainAccuracies;
    private List<Double> testAccuracies;
    private long trainingTimeMs;

    public MLPClassification(Imagette[] donneesEntrainement, int[] layers,
            double learningRate, TransferFunction transferFunction,
            boolean shuffle, boolean decayLearningRate) {
        super(donneesEntrainement);
        this.inputSize = layers[0];
        this.outputSize = layers[layers.length - 1];
        this.shuffle = shuffle;
        this.initialLearningRate = learningRate;
        this.decayLearningRate = decayLearningRate;
        this.mlp = new MLP(layers, learningRate, transferFunction);
        this.trainErrors = new ArrayList<>();
        this.trainAccuracies = new ArrayList<>();
        this.testAccuracies = new ArrayList<>();
    }

    private double[] imagetteToInput(Imagette img) {
        double[] input = new double[inputSize];
        int idx = 0;
        for (int i = 0; i < img.getLignes(); i++) {
            for (int j = 0; j < img.getColonnes(); j++) {
                input[idx++] = img.getValeur(i, j) / 255.0;
            }
        }
        return input;
    }

    private double[] labelToOutput(int label) {
        double[] output = new double[outputSize];
        output[label] = 1.0;
        return output;
    }

    private int outputToLabel(double[] output) {
        int maxIdx = 0;
        double maxVal = output[0];
        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxVal) {
                maxVal = output[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    public void entrainer(int epochs, Imagette[] testSet) {
        long startTime = System.currentTimeMillis();

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < donneesEntrainement.length; i++) {
            indices.add(i);
        }

        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalError = 0.0;

            if (shuffle) {
                Collections.shuffle(indices);
            }

            if (decayLearningRate) {
                double newRate = initialLearningRate * (1.0 - (double) epoch / epochs);
                mlp.setLearningRate(Math.max(0.01, newRate));
            }

            for (int idx : indices) {
                Imagette img = donneesEntrainement[idx];
                double[] input = imagetteToInput(img);
                double[] expectedOutput = labelToOutput(img.getLabel());
                totalError += mlp.backPropagate(input, expectedOutput);
            }

            double avgError = totalError / donneesEntrainement.length;
            trainErrors.add(avgError);

            double trainAcc = calculerPrecision(donneesEntrainement);
            trainAccuracies.add(trainAcc);

            if (testSet != null) {
                double testAcc = calculerPrecision(testSet);
                testAccuracies.add(testAcc);
            }

            if ((epoch + 1) % 5 == 0 || epoch == 0) {
                System.out.printf("Epoch %3d/%d - Erreur: %.4f - Train: %.2f%%",
                        epoch + 1, epochs, avgError, trainAcc * 100);
                if (testSet != null) {
                    System.out.printf(" - Test: %.2f%%", testAccuracies.get(testAccuracies.size() - 1) * 100);
                }
                System.out.println();
            }
        }

        trainingTimeMs = System.currentTimeMillis() - startTime;
    }

    public double calculerPrecision(Imagette[] data) {
        int correct = 0;
        for (Imagette img : data) {
            if (predire(img) == img.getLabel()) {
                correct++;
            }
        }
        return (double) correct / data.length;
    }

    @Override
    public int predire(Imagette img) {
        double[] input = imagetteToInput(img);
        double[] output = mlp.execute(input);
        return outputToLabel(output);
    }

    // Getters
    public List<Double> getTrainErrors() {
        return trainErrors;
    }

    public List<Double> getTrainAccuracies() {
        return trainAccuracies;
    }

    public List<Double> getTestAccuracies() {
        return testAccuracies;
    }

    public long getTrainingTimeMs() {
        return trainingTimeMs;
    }

    public double getFinalTrainAccuracy() {
        return trainAccuracies.isEmpty() ? 0 : trainAccuracies.get(trainAccuracies.size() - 1);
    }

    public double getFinalTestAccuracy() {
        return testAccuracies.isEmpty() ? 0 : testAccuracies.get(testAccuracies.size() - 1);
    }
}
