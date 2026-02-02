/**
 * Programme de test du MLP sur les tables logiques ET, OU et XOR
 * 
 * Ce programme permet de tester le bon fonctionnement du reseau de neurones
 * sur des problemes simples de portes logiques.
 * 
 * Les portes logiques sont des fonctions de base en electronique :
 * - ET (AND) : retourne 1 seulement si les deux entrees sont 1
 * - OU (OR) : retourne 1 si au moins une entree est 1
 * - XOR (OU exclusif) : retourne 1 si une seule des entrees est 1
 * 
 * Le XOR est particulierement interessant car il n'est pas lineairement
 * separable,
 * ce qui signifie qu'un simple perceptron (sans couche cachee) ne peut pas
 * l'apprendre.
 * C'est pour cela qu'on a besoin d'un MLP avec au moins une couche cachee !
 * 
 * Usage: java Test [architecture] [fonction] [porte]
 * 
 * Exemples:
 * java Test -> Tests complets par défaut
 * java Test 2,4,1 sigmoid ET -> Architecture 2-4-1, Sigmoid, porte ET
 * java Test 2,8,4,1 tanh XOR -> Architecture 2-8-4-1, Tanh, porte XOR
 * java Test 2,4,2 sigmoid ET 2D -> Sortie 2D
 * 
 * @author Equipe SAE IA
 * @version 1.0
 */
public class Test {

    // ===============================================================
    // DONNEES D'ENTRAINEMENT
    // ===============================================================

    // Les 4 combinaisons possibles pour 2 entrees binaires
    // C'est notre "base de donnees" d'entrainement
    static double[][] entrees = {
            { 0, 0 }, // Premier cas : les deux entrees sont a 0
            { 0, 1 }, // Deuxieme cas : premiere entree a 0, deuxieme a 1
            { 1, 0 }, // Troisieme cas : premiere entree a 1, deuxieme a 0
            { 1, 1 } // Quatrieme cas : les deux entrees sont a 1
    };

    // Sorties attendues pour la porte ET (1 seule dimension)
    // ET = les deux doivent etre vrais pour que la sortie soit vraie
    static double[][] sortiesET = { { 0 }, { 0 }, { 0 }, { 1 } };

    // Sorties attendues pour la porte OU
    // OU = au moins un doit etre vrai pour que la sortie soit vraie
    static double[][] sortiesOU = { { 0 }, { 1 }, { 1 }, { 1 } };

    // Sorties attendues pour la porte XOR (OU exclusif)
    // XOR = un seul doit etre vrai (pas les deux)
    static double[][] sortiesXOR = { { 0 }, { 1 }, { 1 }, { 0 } };

    // Sorties en 2 dimensions (alternative)
    // Format : {probabilite_classe_0, probabilite_classe_1}
    // Utile quand on veut une sortie "one-hot" (classification)
    static double[][] sortiesET_2D = { { 0, 1 }, { 0, 1 }, { 0, 1 }, { 1, 0 } };
    static double[][] sortiesOU_2D = { { 0, 1 }, { 1, 0 }, { 1, 0 }, { 1, 0 } };
    static double[][] sortiesXOR_2D = { { 0, 1 }, { 1, 0 }, { 1, 0 }, { 0, 1 } };

    // ===============================================================
    // PARAMETRES D'APPRENTISSAGE
    // ===============================================================

    // Nombre maximum d'epoques (passages sur toutes les donnees)
    // Si le reseau n'a pas converge apres 10000 epoques, on arrete
    static int nombreEpoquesMax = 10000;

    // Taux d'apprentissage : controle la vitesse d'ajustement des poids
    // 0.5 est une valeur raisonnable pour ces problemes simples
    static double tauxApprentissage = 0.5;

    // Seuil d'erreur : si l'erreur moyenne passe en dessous, on considere
    // que le reseau a converge (il a appris correctement)
    static double seuilErreur = 0.01;

    /**
     * Point d'entree du programme.
     * Si pas d'arguments : lance tous les tests.
     * Sinon : lance le test avec les parametres specifies.
     * 
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // Mode par defaut : on teste tout
            testsComplets();
        } else {
            // Mode parametre : on teste selon les arguments
            testParametre(args);
        }
    }

    /**
     * Convertit une chaine "2,4,1" en tableau d'entiers {2, 4, 1}
     * Permet de specifier l'architecture du reseau en ligne de commande.
     * 
     * @param arch chaine representant l'architecture (ex: "2,4,1")
     * @return tableau d'entiers correspondant
     */
    static int[] parseArchitecture(String arch) {
        // On decoupe la chaine par les virgules
        String[] parts = arch.split(",");
        int[] result = new int[parts.length];

        // On convertit chaque partie en entier
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    /**
     * Execute un test avec les parametres de la ligne de commande.
     * 
     * @param args tableau des arguments :
     *             args[0] = architecture (ex: "2,4,1")
     *             args[1] = fonction ("sigmoid" ou "tanh")
     *             args[2] = porte ("ET", "OU", "XOR")
     *             args[3] = optionnel: "2d" ou "melange"
     */
    static void testParametre(String[] args) {
        // Recuperer l'architecture depuis le premier argument
        int[] architecture = parseArchitecture(args[0]);

        // Creer la fonction de transfert selon le deuxieme argument
        // Si "tanh" -> Tanh, sinon -> Sigmoid (par defaut)
        TransferFunction fonction = args[1].equalsIgnoreCase("tanh") ? new Tanh() : new Sigmoid();
        String nomFonction = args[1].equalsIgnoreCase("tanh") ? "Tanh" : "Sigmoid";

        // Recuperer le nom de la porte logique
        String porte = args[2].toUpperCase();

        // Verifier les options supplementaires (mode 2D ou melange)
        boolean mode2D = args.length > 3 && args[3].equalsIgnoreCase("2d");
        boolean melange = args.length > 3 && args[3].equalsIgnoreCase("melange");

        // Obtenir le bon tableau de sorties selon la porte et le mode
        double[][] sorties = getSorties(porte, mode2D);

        // Afficher la configuration choisie
        System.out.println("=== TEST PARAMETRE ===");
        System.out.println("Architecture: " + architectureToString(architecture));
        System.out.println("Fonction: " + nomFonction);
        System.out.println("Porte: " + porte);
        System.out.println("Mode 2D: " + mode2D);
        System.out.println("Melange: " + melange);
        System.out.println();

        // Lancer le test avec ou sans melange des donnees
        if (melange) {
            testerPorteMelange(porte, entrees, sorties, architecture, fonction);
        } else {
            testerPorte(porte, entrees, sorties, architecture, fonction);
        }
    }

    /**
     * Retourne le tableau de sorties pour une porte donnee.
     * 
     * @param porte  nom de la porte ("ET", "OU", "XOR")
     * @param mode2D true pour les sorties en 2 dimensions
     * @return tableau des sorties attendues
     */
    static double[][] getSorties(String porte, boolean mode2D) {
        if (mode2D) {
            // Mode 2 dimensions : sorties "one-hot"
            switch (porte) {
                case "ET":
                    return sortiesET_2D;
                case "OU":
                    return sortiesOU_2D;
                default:
                    return sortiesXOR_2D;
            }
        } else {
            // Mode 1 dimension : sortie simple 0 ou 1
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
     * Convertit un tableau d'architecture en chaine lisible.
     * Ex: {2, 4, 1} devient "2-4-1"
     * 
     * @param arch tableau de l'architecture
     * @return chaine formatee
     */
    static String architectureToString(int[] arch) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arch.length; i++) {
            sb.append(arch[i]);
            // On ajoute un tiret entre chaque nombre (sauf apres le dernier)
            if (i < arch.length - 1)
                sb.append("-");
        }
        return sb.toString();
    }

    /**
     * Lance tous les tests (mode par defaut).
     * Teste les 3 portes logiques avec les 2 fonctions de transfert,
     * en mode 1D, 2D et avec donnees melangees.
     */
    static void testsComplets() {
        System.out.println("=== TESTS MLP SUR TABLES LOGIQUES ===\n");

        // Tester avec la fonction Sigmoid
        System.out.println(">>> FONCTION DE TRANSFERT : SIGMOID <<<\n");
        testerAvecFonction(new Sigmoid(), "Sigmoid");

        // Tester avec la fonction Tanh
        System.out.println("\n>>> FONCTION DE TRANSFERT : TANH <<<\n");
        testerAvecFonction(new Tanh(), "Tanh");
    }

    /**
     * Effectue tous les tests avec une fonction de transfert donnee.
     * 
     * @param fonction    la fonction de transfert a utiliser
     * @param nomFonction le nom de la fonction (pour l'affichage)
     */
    static void testerAvecFonction(TransferFunction fonction, String nomFonction) {
        // Tests avec sortie en 1 dimension
        System.out.println("--- Sortie 1 dimension ---");
        testerPorte("ET", entrees, sortiesET, new int[] { 2, 4, 1 }, fonction);
        testerPorte("OU", entrees, sortiesOU, new int[] { 2, 4, 1 }, fonction);
        testerPorte("XOR", entrees, sortiesXOR, new int[] { 2, 4, 1 }, fonction);

        // Tests avec sortie en 2 dimensions (classification)
        System.out.println("\n--- Sortie 2 dimensions ---");
        testerPorte("ET", entrees, sortiesET_2D, new int[] { 2, 4, 2 }, fonction);
        testerPorte("OU", entrees, sortiesOU_2D, new int[] { 2, 4, 2 }, fonction);
        testerPorte("XOR", entrees, sortiesXOR_2D, new int[] { 2, 4, 2 }, fonction);

        // Tests avec donnees melangees a chaque epoque
        // Ca peut ameliorer l'apprentissage en evitant les patterns repetitifs
        System.out.println("\n--- Donnees melangees ---");
        testerPorteMelange("ET", entrees, sortiesET, new int[] { 2, 4, 1 }, fonction);
        testerPorteMelange("OU", entrees, sortiesOU, new int[] { 2, 4, 1 }, fonction);
        testerPorteMelange("XOR", entrees, sortiesXOR, new int[] { 2, 4, 1 }, fonction);
    }

    /**
     * Teste l'apprentissage d'une porte logique par le MLP.
     * 
     * @param nomPorte     nom de la porte (pour l'affichage)
     * @param entrees      tableau des entrees
     * @param sorties      tableau des sorties attendues
     * @param architecture architecture du reseau {entree, cache..., sortie}
     * @param fonction     fonction de transfert a utiliser
     */
    static void testerPorte(String nomPorte, double[][] entrees, double[][] sorties,
            int[] architecture, TransferFunction fonction) {

        // Creer un nouveau MLP avec l'architecture specifiee
        MLP mlp = new MLP(architecture, tauxApprentissage, fonction);

        int epoque = 0;
        double erreur = 1.0;
        boolean convergence = false;

        // BOUCLE D'APPRENTISSAGE
        // On repete jusqu'a convergence ou nombre max d'epoques atteint
        while (epoque < nombreEpoquesMax && !convergence) {
            erreur = 0;

            // On presente chaque exemple au reseau
            for (int i = 0; i < entrees.length; i++) {
                // backPropagate fait le calcul et ajuste les poids
                // Il retourne l'erreur pour cet exemple
                erreur += mlp.backPropagate(entrees[i], sorties[i]);
            }

            // On calcule l'erreur moyenne sur tous les exemples
            erreur /= entrees.length;

            // Si l'erreur est assez faible, on a converge !
            if (erreur < seuilErreur) {
                convergence = true;
            }
            epoque++;
        }

        // PHASE DE TEST : on verifie si le reseau a bien appris
        int reussis = 0;
        for (int i = 0; i < entrees.length; i++) {
            // On execute le reseau sur chaque entree
            double[] resultat = mlp.execute(entrees[i]);

            // On verifie si la prediction est correcte
            if (estCorrect(resultat, sorties[i])) {
                reussis++;
            }
        }

        // Affichage du resultat
        String statut = (reussis == entrees.length) ? "OK" : "ECHEC";
        System.out.printf("%s: %d/%d reussis, %d epoques, erreur=%.4f [%s]%n",
                nomPorte, reussis, entrees.length, epoque, erreur, statut);
    }

    /**
     * Teste l'apprentissage avec melange des donnees a chaque epoque.
     * Le melange peut ameliorer la convergence en evitant que le reseau
     * "apprenne l'ordre" des exemples plutot que le pattern reel.
     * 
     * @param nomPorte     nom de la porte
     * @param entrees      tableau des entrees
     * @param sorties      tableau des sorties attendues
     * @param architecture architecture du reseau
     * @param fonction     fonction de transfert
     */
    static void testerPorteMelange(String nomPorte, double[][] entrees, double[][] sorties,
            int[] architecture, TransferFunction fonction) {

        MLP mlp = new MLP(architecture, tauxApprentissage, fonction);

        // Tableau d'indices qu'on va melanger
        int[] indices = { 0, 1, 2, 3 };
        int epoque = 0;
        double erreur = 1.0;
        boolean convergence = false;

        while (epoque < nombreEpoquesMax && !convergence) {
            // On melange les indices avant chaque epoque
            // Ca change l'ordre de presentation des exemples
            melangerTableau(indices);

            erreur = 0;
            // On parcourt les exemples dans l'ordre melange
            for (int idx : indices) {
                erreur += mlp.backPropagate(entrees[idx], sorties[idx]);
            }
            erreur /= entrees.length;

            if (erreur < seuilErreur) {
                convergence = true;
            }
            epoque++;
        }

        // Phase de test (identique a la version sans melange)
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

    /**
     * Verifie si une prediction est correcte.
     * On arrondit chaque valeur (seuil 0.5) et on compare avec la sortie attendue.
     * 
     * @param resultat sortie du reseau
     * @param attendu  sortie souhaitee
     * @return true si la prediction est correcte, false sinon
     */
    static boolean estCorrect(double[] resultat, double[] attendu) {
        for (int i = 0; i < resultat.length; i++) {
            // On arrondit : si > 0.5 alors 1, sinon 0
            int pred = (resultat[i] > 0.5) ? 1 : 0;
            int exp = (int) attendu[i];

            // Si au moins une valeur est differente, c'est faux
            if (pred != exp)
                return false;
        }
        return true;
    }

    /**
     * Melange un tableau d'entiers de maniere aleatoire (Fisher-Yates shuffle).
     * Cet algorithme garantit une distribution uniforme des permutations.
     * 
     * @param tableau le tableau a melanger (modifie en place)
     */
    static void melangerTableau(int[] tableau) {
        // On parcourt le tableau de la fin vers le debut
        for (int i = tableau.length - 1; i > 0; i--) {
            // On choisit un indice aleatoire entre 0 et i (inclus)
            int j = (int) (Math.random() * (i + 1));

            // On echange les elements aux positions i et j
            int temp = tableau[i];
            tableau[i] = tableau[j];
            tableau[j] = temp;
        }
    }
}
