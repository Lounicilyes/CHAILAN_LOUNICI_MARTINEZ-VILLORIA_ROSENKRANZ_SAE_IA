package MLP;

/**
 * Classe abstraite pour les algorithmes de classification
 */
public abstract class AlgoClassification {
    protected Imagette[] donneesEntrainement;

    public AlgoClassification(Imagette[] donneesEntrainement) {
        this.donneesEntrainement = donneesEntrainement;
    }

    /**
     * Prédit l'étiquette d'une imagette
     * 
     * @param img l'imagette à classifier
     * @return l'étiquette prédite (0-9)
     */
    public abstract int predire(Imagette img);
}
