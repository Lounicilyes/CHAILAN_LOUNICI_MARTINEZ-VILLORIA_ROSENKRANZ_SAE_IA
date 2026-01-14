/**
 * Test des fonctions de transfert avec les valeurs de référence
 */
public class TestFonctions {

    public static void main(String[] args) {
        double[] entrees = { -1.0, -0.5, 0.0, 0.5, 1.0 };

        Sigmoid sigmoid = new Sigmoid();
        Tanh tanh = new Tanh();

        System.out.println("=== TEST SIGMOID ===");
        System.out.println("entree\t\tsigma\t\tsigma'");
        for (double x : entrees) {
            double sigma = sigmoid.evaluate(x);
            double sigmaPrime = sigmoid.evaluateDer(sigma);
            System.out.printf("%.1f\t\t%.5f\t\t%.5f%n", x, sigma, sigmaPrime);
        }

        System.out.println("\n=== TEST TANH ===");
        System.out.println("entree\t\ttanh\t\ttanh'");
        for (double x : entrees) {
            double t = tanh.evaluate(x);
            double tPrime = tanh.evaluateDer(t);
            System.out.printf("%.1f\t\t%.5f\t\t%.5f%n", x, t, tPrime);
        }
    }
}
