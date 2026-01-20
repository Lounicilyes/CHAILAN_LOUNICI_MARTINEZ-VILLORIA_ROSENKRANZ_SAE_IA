import MLP.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Programme principal pour tester le MLP sur MNIST et Fashion-MNIST
 * Teste différentes architectures, learning rates, et paramètres
 */
public class TestMnistComplet {

    // Configuration des datasets
    public static final String MNIST_DIR = "data/";
    public static final String FASHION_DIR = "fashion/";

    // Paramètres par défaut
    public static final int MAX_TRAIN = 60000;
    public static final int MAX_TEST = 10000;
    public static final int DEFAULT_EPOCHS = 30;

    public static void main(String[] args) {
        try {
            System.out.println("=".repeat(70));
            System.out.println("           TESTS MLP - MNIST ET FASHION-MNIST");
            System.out.println("=".repeat(70));

            // Charger les données MNIST
            System.out.println("\n>>> Chargement des données MNIST...");
            Imagette[] mnistTrain = chargerDonnees(MNIST_DIR, true, MAX_TRAIN);
            Imagette[] mnistTest = chargerDonnees(MNIST_DIR, false, MAX_TEST);
            System.out.printf("   Train: %d images, Test: %d images%n", mnistTrain.length, mnistTest.length);

            // Fichier CSV pour les résultats
            PrintWriter csv = new PrintWriter(new FileWriter("resultats_mlp.csv"));
            csv.println("Dataset,Architecture,LearningRate,Shuffle,Decay,Epochs,TrainAcc,TestAcc,TimeMs");

            // ============================================================
            // PARTIE 1: Tests sur MNIST
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("                    TESTS SUR MNIST");
            System.out.println("=".repeat(70));

            // Test 1: Perceptron sans couche cachée
            System.out.println("\n--- Test 1: Perceptron sans couche cachée [784, 10] ---");
            testerConfiguration("MNIST", new int[] { 784, 10 }, 0.5, true, false,
                    DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);

            // Test 2: Une couche cachée - différentes tailles
            System.out.println("\n--- Test 2: Une couche cachée - Variation du nombre de neurones ---");
            int[] taillesCoucheCachee = { 32, 64, 128, 256 };
            for (int taille : taillesCoucheCachee) {
                System.out.printf("\n>> Architecture [784, %d, 10]%n", taille);
                testerConfiguration("MNIST", new int[] { 784, taille, 10 }, 0.5, true, false,
                        DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);
            }

            // Test 3: Deux couches cachées
            System.out.println("\n--- Test 3: Deux couches cachées ---");
            System.out.println("\n>> Architecture [784, 128, 64, 10]");
            testerConfiguration("MNIST", new int[] { 784, 128, 64, 10 }, 0.5, true, false,
                    DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);

            System.out.println("\n>> Architecture [784, 256, 128, 10]");
            testerConfiguration("MNIST", new int[] { 784, 256, 128, 10 }, 0.5, true, false,
                    DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);

            // Test 4: Variation du learning rate
            System.out.println("\n--- Test 4: Variation du taux d'apprentissage ---");
            double[] learningRates = { 0.1, 0.3, 0.5, 0.8, 1.0 };
            for (double lr : learningRates) {
                System.out.printf("\n>> Learning rate = %.1f%n", lr);
                testerConfiguration("MNIST", new int[] { 784, 128, 10 }, lr, true, false,
                        DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);
            }

            // Test 5: Décroissance du learning rate
            System.out.println("\n--- Test 5: Avec et sans décroissance du learning rate ---");
            System.out.println("\n>> Sans décroissance:");
            testerConfiguration("MNIST", new int[] { 784, 128, 10 }, 0.8, true, false,
                    DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);
            System.out.println("\n>> Avec décroissance:");
            testerConfiguration("MNIST", new int[] { 784, 128, 10 }, 0.8, true, true,
                    DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);

            // Test 6: Données mélangées vs non-mélangées
            System.out.println("\n--- Test 6: Données mélangées vs non-mélangées ---");
            System.out.println("\n>> Données NON mélangées:");
            testerConfiguration("MNIST", new int[] { 784, 128, 10 }, 0.5, false, false,
                    DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);
            System.out.println("\n>> Données mélangées:");
            testerConfiguration("MNIST", new int[] { 784, 128, 10 }, 0.5, true, false,
                    DEFAULT_EPOCHS, mnistTrain, mnistTest, csv);

            // ============================================================
            // PARTIE 2: Tests sur Fashion-MNIST (si disponible)
            // ============================================================
            try {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("                 TESTS SUR FASHION-MNIST");
                System.out.println("=".repeat(70));

                Imagette[] fashionTrain = chargerDonnees(FASHION_DIR, true, MAX_TRAIN);
                Imagette[] fashionTest = chargerDonnees(FASHION_DIR, false, MAX_TEST);
                System.out.printf("   Train: %d images, Test: %d images%n", fashionTrain.length, fashionTest.length);

                System.out.println("\n--- Perceptron sans couche cachée ---");
                testerConfiguration("FASHION", new int[] { 784, 10 }, 0.5, true, false,
                        DEFAULT_EPOCHS, fashionTrain, fashionTest, csv);

                System.out.println("\n--- Une couche cachée [784, 128, 10] ---");
                testerConfiguration("FASHION", new int[] { 784, 128, 10 }, 0.5, true, false,
                        DEFAULT_EPOCHS, fashionTrain, fashionTest, csv);

                System.out.println("\n--- Deux couches cachées [784, 256, 128, 10] ---");
                testerConfiguration("FASHION", new int[] { 784, 256, 128, 10 }, 0.5, true, false,
                        DEFAULT_EPOCHS, fashionTrain, fashionTest, csv);

                System.out.println("\n--- Meilleure config avec décroissance ---");
                testerConfiguration("FASHION", new int[] { 784, 256, 128, 10 }, 0.8, true, true,
                        DEFAULT_EPOCHS, fashionTrain, fashionTest, csv);

            } catch (IOException e) {
                System.out.println("\n[INFO] Fashion-MNIST non disponible, tests ignorés.");
                System.out.println("       Pour activer, placez les fichiers dans: " + FASHION_DIR);
            }

            csv.close();

            // ============================================================
            // RÉSUMÉ FINAL
            // ============================================================
            System.out.println("\n" + "=".repeat(70));
            System.out.println("                      RÉSUMÉ FINAL");
            System.out.println("=".repeat(70));
            System.out.println("\nRésultats sauvegardés dans: resultats_mlp.csv");

        } catch (IOException e) {
            System.err.println("Erreur de chargement: " + e.getMessage());
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

    private static void testerConfiguration(String dataset, int[] layers, double lr,
            boolean shuffle, boolean decay, int epochs,
            Imagette[] trainData, Imagette[] testData,
            PrintWriter csv) {

        MLPClassification mlp = new MLPClassification(
                trainData, layers, lr, new Sigmoid(), shuffle, decay);

        mlp.entrainer(epochs, testData);

        System.out.printf("\n   RÉSULTAT: Train=%.2f%%, Test=%.2f%%, Temps=%dms%n",
                mlp.getFinalTrainAccuracy() * 100,
                mlp.getFinalTestAccuracy() * 100,
                mlp.getTrainingTimeMs());

        String archStr = arrayToString(layers);
        csv.printf("%s,%s,%.2f,%b,%b,%d,%.4f,%.4f,%d%n",
                dataset, archStr, lr, shuffle, decay, epochs,
                mlp.getFinalTrainAccuracy(), mlp.getFinalTestAccuracy(),
                mlp.getTrainingTimeMs());
        csv.flush();

        sauvegarderCourbes(dataset, archStr, lr, shuffle, decay, mlp);
    }

    private static void sauvegarderCourbes(String dataset, String arch, double lr,
            boolean shuffle, boolean decay,
            MLPClassification mlp) {
        String filename = String.format("courbes_%s_%s_lr%.1f_sh%b_dc%b.csv",
                dataset, arch.replace(",", "-"), lr, shuffle, decay);

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
            System.err.println("Erreur sauvegarde courbes: " + e.getMessage());
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
