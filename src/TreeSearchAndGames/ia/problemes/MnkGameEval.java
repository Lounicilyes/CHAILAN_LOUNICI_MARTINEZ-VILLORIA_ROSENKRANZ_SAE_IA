package ia.problemes;

import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;

/**
 * Représente un jeu MnK avec une fonction d'évaluation améliorée
 *
 * Cette version utilise MnkGameStateEval qui implémente une fonction
 * d'évaluation basée sur le potentiel de victoire (lignes gagnables).
 */
public class MnkGameEval extends AbstractMnkGame {

    public MnkGameEval(int r, int c, int s) {
        super(r, c, s);
    }

    /**
     * Crée l'état initial avec la fonction d'évaluation améliorée
     */
    @Override
    public GameState init() {
        MnkGameStateEval s = new MnkGameStateEval(this.rows, this.cols, this.streak);
        s.updateGameValue();
        return s;
    }
}
