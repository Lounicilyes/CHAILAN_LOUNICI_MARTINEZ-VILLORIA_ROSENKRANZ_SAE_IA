/**
 * Programme de test du MLP sur les tables logiques ET, OU et XOR
 * 
 * Usage: java Test [architecture] [fonction] [porte]
 * 
 * Exemples:
 * java Test -> Tests complets par défaut
 * java Test 2,4,1 sigmoid ET -> Architecture 2-4-1, Sigmoid, porte ET
 * java Test 2,8,4,1 tanh XOR -> Architecture 2-8-4-1, Tanh, porte XOR
 * java Test 2,4,2 sigmoid ET 2D -> Sortie 2D
 */
public class Test {

    // Données des tables logiques
    static double[][] entrees = {
            { 0, 0 },
            { 0, 1 },
            { 1, 0 },
            { 1, 1 }
    };

    // Sorties attendues (1 dimension)
    static double[][] sortiesET = { { 0 }, { 0 }, { 0 }, { 1 } };
    static double[][] sortiesOU = { { 0 }, { 1 }, { 1 }, { 1 } };
    static double[][] sortiesXOR = { { 0 }, { 1 }, { 1 }, { 0 } };

    // Sorties attendues (2 dimensions)
    static double[][] sortiesET_2D = { { 0, 1 }, { 0, 1 }, { 0, 1 }, { 1, 0 } };
    static double[][] sortiesOU_2D = { { 0, 1 }, { 1, 0 }, { 1, 0 }, { 1, 0 } };
    static double[][] sortiesXOR_2D = { { 0, 1 }, { 1, 0 }, { 1, 0 }, { 0, 1 } };

    // Paramètres d'apprentissage
    static int nombreEpoquesMax = 10000;
    static double tauxApprentissage = 0.5;
    static double seuilErreur = 0.01;

    public static void main(String[] args) {
        if (args.length == 0) {
            // Mode par défaut : tous les tests
            testsComplets();
        } else {
            // Mode paramétré
            testParametre(args);
        }
    }

    /**
     * Parse une architecture depuis une chaîne "2,4,1" -> int[]{2,4,1}
     */
    static int[] parseArchitecture(String arch) {
        String[] parts = arch.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    /**
     * Mode paramétré par ligne de commande
     */
    static void testParametre(String[] args) {
        // args[0] = architecture (ex: "2,4,1")
        // args[1] = fonction ("sigmoid" ou "tanh")
        // args[2] = porte ("ET", "OU", "XOR")
        // args[3] = optionnel: "2d" ou "melange"

        int[] architecture = parseArchitecture(args[0]);

        TransferFunction fonction = args[1].equalsIgnoreCase("tanh") ? new Tanh() : new Sigmoid();
        String nomFonction = args[1].equalsIgnoreCase("tanh") ? "Tanh" : "Sigmoid";

        String porte = args[2].toUpperCase();

        boolean mode2D = args.length > 3 && args[3].equalsIgnoreCase("2d");
        boolean melange = args.length > 3 && args[3].equalsIgnoreCase("melange");

        // Obtenir les sorties appropriées
        double[][] sorties = getSorties(porte, mode2D);

        // Afficher configuration
        System.out.println("=== TEST PARAMETRE ===");
        System.out.println("Architecture: " + architectureToString(architecture));
        System.out.println("Fonction: " + nomFonction);
        System.out.println("Porte: " + porte);
        System.out.println("Mode 2D: " + mode2D);
        System.out.println("Melange: " + melange);
        System.out.println();

        // Exécuter le test
        if (melange) {
            testerPorteMelange(porte, entrees, sorties, architecture, fonction);
        } else {
            testerPorte(porte, entrees, sorties, architecture, fonction);
        }
    }

    /**
     * Retourne les sorties pour une porte donnée
     */
    static double[][] getSorties(String porte, boolean mode2D) {
        if (mode2D) {
            switch (porte) {
                case "ET":
                    return sortiesET_2D;
                case "OU":
                    return sortiesOU_2D;
                default:
                    return sortiesXOR_2D;
            }
        } else {
            switch (porte) {
                case "ET":
                    return sortiesET;
                case "OU":
                    return sortiesOU;
                default:
                    return sortiesXOR;
            }
        }
    }

    /**
     * Convertit une architecture en chaîne lisible
     */
    static String architectureToString(int[] arch) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arch.length; i++) {
            sb.append(arch[i]);
            if (i < arch.length - 1)
                sb.append("-");
        }
        return sb.toString();
    }

    /**
     * Tests complets (mode par défaut)
     */
    static void testsComplets() {
        System.out.println("=== TESTS MLP SUR TABLES LOGIQUES ===\n");

        System.out.println(">>> FONCTION DE TRANSFERT : SIGMOID <<<\n");
        testerAvecFonction(new Sigmoid(), "Sigmoid");

        System.out.println("\n>>> FONCTION DE TRANSFERT : TANH <<<\n");
        testerAvecFonction(new Tanh(), "Tanh");
    }

    /**
     * Effectue tous les tests avec une fonction de transfert donnée
     */
    static void testerAvecFonction(TransferFunction fonction, String nomFonction) {
        // Tests 1D
        System.out.println("--- Sortie 1 dimension ---");
        testerPorte("ET", entrees, sortiesET, new int[] { 2, 4, 1 }, fonction);
        testerPorte("OU", entrees, sortiesOU, new int[] { 2, 4, 1 }, fonction);
        testerPorte("XOR", entrees, sortiesXOR, new int[] { 2, 4, 1 }, fonction);

        // Tests 2D
        System.out.println("\n--- Sortie 2 dimensions ---");
        testerPorte("ET", entrees, sortiesET_2D, new int[] { 2, 4, 2 }, fonction);
        testerPorte("OU", entrees, sortiesOU_2D, new int[] { 2, 4, 2 }, fonction);
        testerPorte("XOR", entrees, sortiesXOR_2D, new int[] { 2, 4, 2 }, fonction);

        // Test avec données mélangées
        System.out.println("\n--- Donnees melangees ---");
        testerPorteMelange("ET", entrees, sortiesET, new int[] { 2, 4, 1 }, fonction);
        testerPorteMelange("OU", entrees, sortiesOU, new int[] { 2, 4, 1 }, fonction);
        testerPorteMelange("XOR", entrees, sortiesXOR, new int[] { 2, 4, 1 }, fonction);
    }

    /**
     * Test d'une porte logique
     */
    static void testerPorte(String nomPorte, double[][] entrees, double[][] sorties,
            int[] architecture, TransferFunction fonction) {
        MLP mlp = new MLP(architecture, tauxApprentissage, fonction);

        int epoque = 0;
        double erreur = 1.0;
        boolean convergence = false;

        while (epoque < nombreEpoquesMax && !convergence) {
            erreur = 0;
            for (int i = 0; i < entrees.length; i++) {
                erreur += mlp.backPropagate(entrees[i], sorties[i]);
            }
            erreur /= entrees.length;

            if (erreur < seuilErreur) {
                convergence = true;
            }
            epoque++;
        }

        int reussis = 0;
        for (int i = 0; i < entrees.length; i++) {
            double[] resultat = mlp.execute(entrees[i]);
            if (estCorrect(resultat, sorties[i])) {
                reussis++;
            }
        }

        String statut = (reussis == entrees.length) ? "OK" : "ECHEC";
        System.out.printf("%s: %d/%d reussis, %d epoques, erreur=%.4f [%s]%n",
                nomPorte, reussis, entrees.length, epoque, erreur, statut);
    }

    /**
     * Test avec données mélangées
     */
    static void testerPorteMelange(String nomPorte, double[][] entrees, double[][] sorties,
            int[] architecture, TransferFunction fonction) {
        MLP mlp = new MLP(architecture, tauxApprentissage, fonction);

        int[] indices = { 0, 1, 2, 3 };
        int epoque = 0;
        double erreur = 1.0;
        boolean convergence = false;

        while (epoque < nombreEpoquesMax && !convergence) {
            melangerTableau(indices);

            erreur = 0;
            for (int idx : indices) {
                erreur += mlp.backPropagate(entrees[idx], sorties[idx]);
            }
            erreur /= entrees.length;

            if (erreur < seuilErreur) {
                convergence = true;
            }
            epoque++;
        }

        int reussis = 0;
        for (int i = 0; i < entrees.length; i++) {
            double[] resultat = mlp.execute(entrees[i]);
            if (estCorrect(resultat, sorties[i])) {
                reussis++;
            }
        }

        String statut = (reussis == entrees.length) ? "OK" : "ECHEC";
        System.out.printf("%s (melange): %d/%d reussis, %d epoques, erreur=%.4f [%s]%n",
                nomPorte, reussis, entrees.length, epoque, erreur, statut);
    }

    static boolean estCorrect(double[] resultat, double[] attendu) {
        for (int i = 0; i < resultat.length; i++) {
            int pred = (resultat[i] > 0.5) ? 1 : 0;
            int exp = (int) attendu[i];
            if (pred != exp)
                return false;
        }
        return true;
    }

    static void melangerTableau(int[] tableau) {
        for (int i = tableau.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            int temp = tableau[i];
            tableau[i] = tableau[j];
            tableau[j] = temp;
        }
    }
}
