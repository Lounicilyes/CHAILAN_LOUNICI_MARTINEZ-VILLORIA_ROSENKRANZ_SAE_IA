import MLP.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Tests MLP sur MNIST - Étude de l'influence des paramètres
 * 
 * Paramètres étudiés:
 * 1. Nombre de neurones dans la couche cachée
 * 2. Nombre de couches cachées
 * 3. Taux d'apprentissage
 * 4. Évolution du taux d'apprentissage (decay)
 * 5. Données mélangées ou non
 */
public class TestMnistComplet {

    public static final String MNIST_DIR = "data/";
    public static final String FASHION_DIR = "fashion/";
    public static final int MAX_TRAIN = 60000;
    public static final int MAX_TEST = 10000;
    public static final int EPOCHS = 30;

    public static void main(String[] args) {
        java.util.Locale.setDefault(java.util.Locale.US);
        try {
            System.out.println("=".repeat(70));
            System.out.println("         ÉTUDE MLP SUR MNIST - INFLUENCE DES PARAMÈTRES");
            System.out.println("=".repeat(70));

            // Charger les données
            System.out.println("\n>>> Chargement des données MNIST...");
            Imagette[] trainData = chargerDonnees(MNIST_DIR, true, MAX_TRAIN);
            Imagette[] testData = chargerDonnees(MNIST_DIR, false, MAX_TEST);
            System.out.printf("   Train: %d images, Test: %d images%n", trainData.length, testData.length);

            // Fichier CSV pour les résultats
            PrintWriter csv = new PrintWriter(new FileWriter("resultats_mlp.csv"));
            csv.println("Test,Architecture,LearningRate,Shuffle,Decay,Epochs,TrainAcc,TestAcc,TimeMs");

            // ============================================================
            // TEST 1: Perceptron sans couche cachée (baseline)
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST 1: PERCEPTRON SANS COUCHE CACHÉE (baseline)");
            System.out.println("=".repeat(70));
            testerConfig("T1_baseline", new int[] { 784, 10 }, 0.5, true, false, trainData, testData, csv);

            // ============================================================
            // TEST 2: Influence du NOMBRE DE NEURONES
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST 2: INFLUENCE DU NOMBRE DE NEURONES");
            System.out.println("=".repeat(70));
            int[] neurones = { 32, 64, 128, 256 };
            for (int n : neurones) {
                System.out.printf("\n>>> %d neurones dans la couche cachée%n", n);
                testerConfig("T2_neurons_" + n, new int[] { 784, n, 10 }, 0.5, true, false, trainData, testData, csv);
            }

            // ============================================================
            // TEST 3: Influence du NOMBRE DE COUCHES
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST 3: INFLUENCE DU NOMBRE DE COUCHES");
            System.out.println("=".repeat(70));

            System.out.println("\n>>> 0 couche cachée [784, 10]");
            testerConfig("T3_layers_0", new int[] { 784, 10 }, 0.5, true, false, trainData, testData, csv);

            System.out.println("\n>>> 1 couche cachée [784, 128, 10]");
            testerConfig("T3_layers_1", new int[] { 784, 128, 10 }, 0.5, true, false, trainData, testData, csv);

            System.out.println("\n>>> 2 couches cachées [784, 128, 64, 10]");
            testerConfig("T3_layers_2", new int[] { 784, 128, 64, 10 }, 0.5, true, false, trainData, testData, csv);

            // ============================================================
            // TEST 4: Influence du TAUX D'APPRENTISSAGE
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST 4: INFLUENCE DU TAUX D'APPRENTISSAGE");
            System.out.println("=".repeat(70));
            double[] learningRates = { 0.1, 0.3, 0.5, 0.8, 1.0 };
            for (double lr : learningRates) {
                System.out.printf("\n>>> Learning rate = %.1f%n", lr);
                testerConfig("T4_lr_" + lr, new int[] { 784, 128, 10 }, lr, true, false, trainData, testData, csv);
            }

            // ============================================================
            // TEST 5: Influence de la DÉCROISSANCE du learning rate
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST 5: INFLUENCE DE LA DÉCROISSANCE DU LEARNING RATE");
            System.out.println("=".repeat(70));

            System.out.println("\n>>> SANS décroissance (lr=0.5)");
            testerConfig("T5_decay_off", new int[] { 784, 128, 10 }, 0.5, true, false, trainData, testData, csv);

            System.out.println("\n>>> AVEC décroissance (lr=0.5)");
            testerConfig("T5_decay_on", new int[] { 784, 128, 10 }, 0.5, true, true, trainData, testData, csv);

            // ============================================================
            // TEST 6: Influence du MÉLANGE des données
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST 6: INFLUENCE DU MÉLANGE DES DONNÉES");
            System.out.println("=".repeat(70));

            System.out.println("\n>>> Données NON mélangées");
            testerConfig("T6_shuffle_off", new int[] { 784, 128, 10 }, 0.5, false, false, trainData, testData, csv);

            System.out.println("\n>>> Données mélangées");
            testerConfig("T6_shuffle_on", new int[] { 784, 128, 10 }, 0.5, true, false, trainData, testData, csv);

            // ============================================================
            // TEST 7: Configuration OPTIMALE
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TEST 7: CONFIGURATION OPTIMALE");
            System.out.println("=".repeat(70));
            System.out.println("\n>>> [784, 256, 128, 10], lr=0.8, shuffle=true, decay=true");
            testerConfig("T7_optimal", new int[] { 784, 256, 128, 10 }, 0.8, true, true, trainData, testData, csv);

            // ============================================================
            // TESTS FASHION-MNIST (si disponible)
            // ============================================================
            try {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("                    TESTS SUR FASHION-MNIST");
                System.out.println("=".repeat(70));

                Imagette[] fashionTrain = chargerDonnees(FASHION_DIR, true, MAX_TRAIN);
                Imagette[] fashionTest = chargerDonnees(FASHION_DIR, false, MAX_TEST);
                System.out.printf("   Train: %d images, Test: %d images%n", fashionTrain.length, fashionTest.length);

                // Perceptron sans couche cachée
                System.out.println("\n>>> Perceptron sans couche cachée");
                testerConfig("F1_baseline", new int[] { 784, 10 }, 0.5, true, false, fashionTrain, fashionTest, csv);

                // Une couche cachée
                System.out.println("\n>>> Une couche cachée [784, 128, 10]");
                testerConfig("F2_1layer", new int[] { 784, 128, 10 }, 0.5, true, false, fashionTrain, fashionTest, csv);

                // Deux couches cachées
                System.out.println("\n>>> Deux couches cachées [784, 256, 128, 10]");
                testerConfig("F3_2layers", new int[] { 784, 256, 128, 10 }, 0.5, true, false, fashionTrain, fashionTest,
                        csv);

                // Configuration optimale
                System.out.println("\n>>> Configuration optimale avec décroissance");
                testerConfig("F4_optimal", new int[] { 784, 256, 128, 10 }, 0.8, true, true, fashionTrain, fashionTest,
                        csv);

            } catch (IOException e) {
                System.out.println("\n[INFO] Fashion-MNIST non disponible, tests ignorés.");
                System.out.println("       Pour activer, placez les fichiers dans: " + FASHION_DIR);
            }

            csv.close();

            // ============================================================
            // RÉSUMÉ
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("                      RÉSUMÉ");
            System.out.println("=".repeat(70));
            System.out.println("\nFichiers générés:");
            System.out.println("  - resultats_mlp.csv : tableau récapitulatif");
            System.out.println("  - courbes_*.csv : données pour tracer les courbes d'apprentissage");
            System.out.println("\nPour tracer les courbes, utilisez les fichiers CSV avec Excel/Python/etc.");

        } catch (IOException e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Imagette[] chargerDonnees(String dir, boolean train, int max) throws IOException {
        String imageFile, labelFile;
        if (train) {
            imageFile = dir + "train-images.idx3-ubyte";
            labelFile = dir + "train-labels.idx1-ubyte";
        } else {
            imageFile = dir + "t10k-images.idx3-ubyte";
            labelFile = dir + "t10k-labels.idx1-ubyte";
        }
        return new ChargementMNIST().charger(imageFile, labelFile, max);
    }

    private static void testerConfig(String testName, int[] layers, double lr,
            boolean shuffle, boolean decay,
            Imagette[] trainData, Imagette[] testData,
            PrintWriter csv) {

        MLPClassification mlp = new MLPClassification(
                trainData, layers, lr, new Sigmoid(), shuffle, decay);

        mlp.entrainer(EPOCHS, testData);

        double trainAcc = mlp.getFinalTrainAccuracy() * 100;
        double testAcc = mlp.getFinalTestAccuracy() * 100;
        long timeMs = mlp.getTrainingTimeMs();

        System.out.printf("\n   RÉSULTAT: Train=%.2f%%, Test=%.2f%%, Temps=%dms%n", trainAcc, testAcc, timeMs);

        // Sauvegarder dans CSV
        String archStr = arrayToString(layers);
        csv.printf("%s,%s,%.2f,%b,%b,%d,%.4f,%.4f,%d%n",
                testName, archStr, lr, shuffle, decay, EPOCHS,
                mlp.getFinalTrainAccuracy(), mlp.getFinalTestAccuracy(), timeMs);
        csv.flush();

        // Sauvegarder courbes d'apprentissage
        sauvegarderCourbes(testName, mlp);
    }

    private static void sauvegarderCourbes(String testName, MLPClassification mlp) {
        String filename = "courbes_" + testName + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Epoch,TrainError,TrainAcc,TestAcc");
            for (int i = 0; i < mlp.getTrainErrors().size(); i++) {
                pw.printf("%d,%.6f,%.4f,%.4f%n",
                        i + 1,
                        mlp.getTrainErrors().get(i),
                        mlp.getTrainAccuracies().get(i),
                        i < mlp.getTestAccuracies().size() ? mlp.getTestAccuracies().get(i) : 0.0);
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde: " + e.getMessage());
        }
    }

    private static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0)
                sb.append("-");
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
