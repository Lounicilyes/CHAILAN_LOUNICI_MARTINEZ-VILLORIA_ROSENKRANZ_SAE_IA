/**
 * Test des fonctions de transfert avec les valeurs de reference.
 * 
 * Ce programme permet de verifier que les fonctions Sigmoid et Tanh
 * calculent correctement les valeurs et leurs derivees.
 * 
 * On teste avec des valeurs typiques : -1, -0.5, 0, 0.5, 1
 * Ces valeurs couvrent les cas extremes et les cas moyens.
 * 
 * Rappel sur les fonctions de transfert :
 * - Elles "ecrasent" les valeurs dans un intervalle borne
 * - Sigmoid : sortie entre 0 et 1 (utile pour les probabilites)
 * - Tanh : sortie entre -1 et 1 (centre autour de zero)
 * 
 * La derivee est importante car elle est utilisee dans la retropropagation
 * pour calculer de combien ajuster les poids.
 * 
 * @author Equipe SAE IA
 * @version 1.0
 */
public class TestFonctions {

    /**
     * Point d'entree du programme.
     * Affiche les valeurs de Sigmoid et Tanh pour plusieurs entrees.
     * 
     * @param args arguments de la ligne de commande (non utilises)
     */
    public static void main(String[] args) {
        // Valeurs de test : on prend des valeurs representatives
        // - Valeurs negatives (-1, -0.5) pour voir le comportement a gauche
        // - Zero (point central)
        // - Valeurs positives (0.5, 1) pour voir le comportement a droite
        double[] entrees = { -1.0, -0.5, 0.0, 0.5, 1.0 };

        // Creer les instances des fonctions de transfert
        Sigmoid sigmoid = new Sigmoid();
        Tanh tanh = new Tanh();

        // =============================================
        // TEST DE LA FONCTION SIGMOID
        // =============================================
        System.out.println("=== TEST SIGMOID ===");
        System.out.println("entree\t\tsigma\t\tsigma'");

        // Pour chaque valeur d'entree
        for (double x : entrees) {
            // Calculer sigma(x) = 1 / (1 + e^(-x))
            double sigma = sigmoid.evaluate(x);

            // Calculer sigma'(sigma) = sigma * (1 - sigma)
            // ATTENTION : on passe sigma, pas x, a evaluateDer !
            // C'est une optimisation car on a deja calcule sigma
            double sigmaPrime = sigmoid.evaluateDer(sigma);

            // Afficher les resultats
            System.out.printf("%.1f\t\t%.5f\t\t%.5f%n", x, sigma, sigmaPrime);
        }

        // =============================================
        // TEST DE LA FONCTION TANH
        // =============================================
        System.out.println("\n=== TEST TANH ===");
        System.out.println("entree\t\ttanh\t\ttanh'");

        for (double x : entrees) {
            // Calculer tanh(x)
            double t = tanh.evaluate(x);

            // Calculer tanh'(t) = 1 - t^2
            // Meme principe : on passe le resultat de tanh, pas x
            double tPrime = tanh.evaluateDer(t);

            // Afficher les resultats
            System.out.printf("%.1f\t\t%.5f\t\t%.5f%n", x, t, tPrime);
        }
    }
}
