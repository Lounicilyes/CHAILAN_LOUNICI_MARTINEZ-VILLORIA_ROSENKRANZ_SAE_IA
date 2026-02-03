package MLP;

/**
 * Fonction de transfert Sigmoïde
 * 
 * Fonction : σ(x) = 1 / (1 + e^(-x))
 * Dérivée : σ'(σ) = σ * (1 - σ)
 * 
 * Note : La dérivée reçoit σ(x) en paramètre, pas x
 */
public class Sigmoid implements TransferFunction {

    /**
     * Calcule la fonction sigmoïde
     * 
     * @param x valeur d'entrée
     * @return σ(x) = 1 / (1 + e^(-x))
     */
    @Override
    public double evaluate(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Calcule la dérivée de la sigmoïde
     * 
     * @param sigma résultat de σ(x), PAS x lui-même
     * @return σ'(σ) = σ * (1 - σ)
     */
    @Override
    public double evaluateDer(double sigma) {
        return sigma * (1.0 - sigma);
    }
}
