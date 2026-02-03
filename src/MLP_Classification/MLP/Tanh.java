package MLP;

/**
 * Fonction de transfert Tangente Hyperbolique
 * 
 * Fonction : σ(x) = tanh(x)
 * Dérivée : σ'(σ) = 1 - σ²
 * 
 * Note : La dérivée reçoit σ(x) en paramètre, pas x
 */
public class Tanh implements TransferFunction {

    /**
     * Calcule la tangente hyperbolique
     * 
     * @param x valeur d'entrée
     * @return σ(x) = tanh(x)
     */
    @Override
    public double evaluate(double x) {
        return Math.tanh(x);
    }

    /**
     * Calcule la dérivée de tanh
     * 
     * @param sigma résultat de σ(x), PAS x lui-même
     * @return σ'(σ) = 1 - σ²
     */
    @Override
    public double evaluateDer(double sigma) {
        return 1.0 - (sigma * sigma);
    }
}
