import MLP.*;
import java.io.IOException;

/**
 * Test de l'algorithme k-NN pour comparaison avec MLP
 */
public class TestKNN {

    public static final String MNIST_DIR = "data/";
    public static final int MAX_TRAIN = 10000; // Réduit car k-NN est lent
    public static final int MAX_TEST = 1000;

    public static void main(String[] args) {
        try {
            System.out.println("=".repeat(60));
            System.out.println("           TEST k-NN SUR MNIST");
            System.out.println("=".repeat(60));

            // Charger les données
            System.out.println("\n>>> Chargement des données MNIST...");
            Imagette[] trainData = chargerDonnees(MNIST_DIR, true, MAX_TRAIN);
            Imagette[] testData = chargerDonnees(MNIST_DIR, false, MAX_TEST);
            System.out.printf("   Train: %d images, Test: %d images%n", trainData.length, testData.length);

            System.out.println("\n>>> Test avec différentes valeurs de k:");
            System.out.println("-".repeat(40));

            int[] valuesK = { 1, 3, 5, 7, 10 };

            for (int k : valuesK) {
                System.out.printf("\nk = %d : ", k);
                long startTime = System.currentTimeMillis();

                kNN knn = new kNN(trainData, k);
                Statistiques stats = new Statistiques(knn, testData);
                double precision = stats.calculerPrecision();

                long timeMs = System.currentTimeMillis() - startTime;
                System.out.printf("Précision = %.2f%% (temps: %dms)%n", precision * 100, timeMs);
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("                 COMPARAISON MLP vs k-NN");
            System.out.println("=".repeat(60));

            System.out.println("\n>>> Test MLP avec même quantité de données...");

            // Test MLP pour comparaison directe
            int[] architecture = { 784, 128, 10 };
            MLPClassification mlp = new MLPClassification(
                    trainData, architecture, 0.5, new Sigmoid(), true, false);

            long mlpStart = System.currentTimeMillis();
            mlp.entrainer(20, testData);
            long mlpTime = System.currentTimeMillis() - mlpStart;

            System.out.printf("\nMLP [784-128-10]: Précision = %.2f%% (temps: %dms)%n",
                    mlp.getFinalTestAccuracy() * 100, mlpTime);

            System.out.println("\n" + "-".repeat(60));
            System.out.println("CONCLUSION:");
            System.out.println("- k-NN: Simple, sans apprentissage, mais lent en prédiction");
            System.out.println("- MLP : Apprentissage nécessaire, mais prédiction très rapide");
            System.out.println("-".repeat(60));

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
}
