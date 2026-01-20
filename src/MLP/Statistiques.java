package MLP;

/**
 * Classe utilitaire pour calculer les statistiques de classification
 */
public class Statistiques {
    private AlgoClassification algo;
    private Imagette[] donneesTest;

    public Statistiques(AlgoClassification algo, Imagette[] donneesTest) {
        this.algo = algo;
        this.donneesTest = donneesTest;
    }

    /**
     * Calcule la précision de l'algorithme sur les données de test
     * 
     * @return la précision entre 0.0 et 1.0
     */
    public double calculerPrecision() {
        int correct = 0;
        for (Imagette img : donneesTest) {
            if (algo.predire(img) == img.getLabel()) {
                correct++;
            }
        }
        return (double) correct / donneesTest.length;
    }
}
