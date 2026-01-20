import MLP.ChargementMNIST;
import MLP.Imagette;
import MLP.MLP;
import MLP.Sigmoid;
import MLP.Tanh;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestMnist {

    public static void main(String[] args) {
        System.out.println("=== DÉBUT DU TEST MLP vs KNN (MNIST) ===");

        // 1. PARAMÈTRES (A modifier pour vos analyses)
        // ---------------------------------------------
        int tailleCoucheCachee = 50;  // Essayez 20, 50, 100...
        double learningRate = 0.1;    // Essayez 0.01, 0.1, 0.5...
        int nombreEpoques = 20;       // Nombre de passages complets
        int maxImages = 0;            // 0 = tout charger (60000 images), mettez 5000 pour tester vite au début

        // Chemins vers vos fichiers (ADAPTEZ CES CHEMINS SI BESOIN)
        String trainImages = "src/data/train-images.idx3-ubyte";
        String trainLabels = "src/data/train-labels.idx1-ubyte";
        String testImages  = "src/data/t10k-images.idx3-ubyte";
        String testLabels  = "src/data/t10k-labels.idx1-ubyte";

        try {
            // 2. CHARGEMENT DES DONNÉES (Via votre code existant)
            // ---------------------------------------------------
            System.out.println("Chargement des données...");
            ChargementMNIST chargeur = new ChargementMNIST();

            // Chargement base d'apprentissage
            Imagette[] tabTrain = chargeur.charger(trainImages, trainLabels, maxImages);
            // Chargement base de test
            Imagette[] tabTest = chargeur.charger(testImages, testLabels, maxImages == 0 ? 0 : 1000);

            System.out.println("Données chargées : " + tabTrain.length + " entraînement, " + tabTest.length + " test.");

            // 3. CRÉATION DU MLP
            // ------------------
            // Entrée : 784 neurones (car 28x28 pixels)
            // Sortie : 10 neurones (pour les chiffres 0 à 9)
            int tailleEntree = tabTrain[0].getLignes() * tabTrain[0].getColonnes(); // 784
            int[] architecture = { tailleEntree, tailleCoucheCachee, 10 };

            System.out.println("Architecture du réseau : " + architectureToString(architecture));

            // Création avec Sigmoid (classique) ou Tanh
            MLP mlp = new MLP(architecture, learningRate, new Sigmoid());

            // Conversion en liste pour pouvoir mélanger (Shuffle) facilement
            List<Imagette> listeApprentissage = Arrays.asList(tabTrain);

            // 4. BOUCLE D'APPRENTISSAGE
            // -------------------------
            System.out.println("\nLancement de l'apprentissage sur " + nombreEpoques + " époques...");

            for (int e = 1; e <= nombreEpoques; e++) {
                // Mélanger les données à chaque tour pour éviter les cycles
                Collections.shuffle(listeApprentissage);

                double erreurTotale = 0;

                for (Imagette img : listeApprentissage) {
                    // A. Préparer l'entrée (Image -> double[] normalisé)
                    double[] inputs = normaliserImage(img);

                    // B. Préparer la sortie attendue (Label -> One-Hot)
                    // Chiffre 3 devient [0, 0, 0, 1, 0, 0, 0, 0, 0, 0]
                    double[] targets = new double[10];
                    targets[img.getLabel()] = 1.0;

                    // C. Rétropropagation
                    erreurTotale += mlp.backPropagate(inputs, targets);
                }

                double erreurMoyenne = erreurTotale / listeApprentissage.size();

                // 5. TEST PÉRIODIQUE (Validation)
                // -------------------------------
                double precision = testerReseau(mlp, tabTest);

                System.out.printf("Époque %2d/%d | Erreur Moy: %.5f | Précision Test: %.2f%%%n",
                        e, nombreEpoques, erreurMoyenne, precision);
            }

            System.out.println("Fin de l'apprentissage.");

        } catch (IOException e) {
            System.err.println("ERREUR : Impossible de lire les fichiers de données.");
            System.err.println("Vérifiez que le dossier 'data' contient bien les fichiers .idx*-ubyte décompressés.");
            System.err.println("Chemin testé : " + trainImages);
        }
    }

    /**
     * Convertit une Imagette (int 0-255) en double[] (0.0-1.0) pour le MLP
     */
    public static double[] normaliserImage(Imagette img) {
        int lignes = img.getLignes();
        int cols = img.getColonnes();
        double[] data = new double[lignes * cols];

        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < cols; j++) {
                // On met tout à la suite (aplatissement) et on divise par 255
                // img.getValeur(i, j) retourne un int entre 0 et 255
                data[i * cols + j] = img.getValeur(i, j) / 255.0;
            }
        }
        return data;
    }

    /**
     * Teste le réseau sur une liste d'images et retourne le % de réussite
     */
    public static double testerReseau(MLP mlp, Imagette[] images) {
        int correct = 0;

        for (Imagette img : images) {
            double[] inputs = normaliserImage(img);
            double[] outputs = mlp.execute(inputs);

            // Trouver l'index du neurone le plus actif (ArgMax)
            int prediction = 0;
            double maxVal = outputs[0];
            for (int i = 1; i < outputs.length; i++) {
                if (outputs[i] > maxVal) {
                    maxVal = outputs[i];
                    prediction = i;
                }
            }

            // Vérifier si c'est bon
            if (prediction == img.getLabel()) {
                correct++;
            }
        }

        return (double) correct / images.length * 100.0;
    }

    /**
     * Juste pour l'affichage joli de l'architecture
     */
    static String architectureToString(int[] arch) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arch.length; i++) {
            sb.append(arch[i]);
            if (i < arch.length - 1) sb.append("-");
        }
        sb.append("]");
        return sb.toString();
    }
}