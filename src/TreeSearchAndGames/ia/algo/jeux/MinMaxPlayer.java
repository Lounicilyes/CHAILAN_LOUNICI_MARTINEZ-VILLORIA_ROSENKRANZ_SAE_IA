package ia.algo.jeux;

import java.util.ArrayList;

import ia.framework.common.Action;
import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;
import ia.framework.jeux.Player;

/**
 * Implémentation de l'algorithme MinMax avec limitation de profondeur
 *
 * MinMax est un algorithme de décision pour les jeux à deux joueurs.
 * - Le joueur MAX (joueur 1) cherche à maximiser le score
 * - Le joueur MIN (joueur 2) cherche à minimiser le score
 *
 * La limitation de profondeur permet d'arrêter la recherche à une certaine
 * profondeur et d'utiliser une fonction d'évaluation pour estimer la valeur.
 */
public class MinMaxPlayer extends Player {

    /** Profondeur maximale de recherche (-1 = sans limite) */
    private int maxDepth;

    /**
     * Crée un joueur MinMax
     * @param g Le jeu
     * @param player_one true si joueur 1 (MAX), false si joueur 2 (MIN)
     * @param depth La profondeur maximale (-1 pour sans limite)
     */
    public MinMaxPlayer(Game g, boolean player_one, int depth) {
        super(g, player_one);
        this.name = "MinMax";
        this.maxDepth = depth;
        if (depth > 0) {
            this.name = "MinMax(d=" + depth + ")";
        }
    }

    /**
     * Retourne le meilleur coup selon l'algorithme MinMax
     * @param state L'état actuel du jeu
     * @return L'action choisie
     */
    @Override
    public Action getMove(GameState state) {
        resetStateCounter();

        ArrayList<Action> actions = game.getActions(state);
        Action bestAction = null;

        // Le joueur 1 (X) est MAX, le joueur 2 (O) est MIN
        boolean isMax = (this.player == PLAYER1);
        double bestValue = isMax ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (Action action : actions) {
            // Simuler le coup
            GameState childState = (GameState) game.doAction(state, action);
            incStateCounter();

            // Calculer la valeur de cet état
            double value = minmax(childState, 1, !isMax);

            // Mettre à jour le meilleur coup
            if (isMax) {
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
            }
        }

        return bestAction;
    }

    /**
     * Algorithme MinMax récursif avec limitation de profondeur
     * @param state L'état du jeu
     * @param currentDepth La profondeur actuelle
     * @param isMax true si c'est au tour de MAX, false sinon
     * @return La valeur minimax de l'état
     */
    private double minmax(GameState state, int currentDepth, boolean isMax) {
        incStateCounter();

        // Condition d'arrêt 1 : État final (fin de partie)
        if (state.isFinalState()) {
            return state.getGameValue();
        }

        // Condition d'arrêt 2 : Profondeur maximale atteinte
        if (maxDepth > 0 && currentDepth >= maxDepth) {
            // Utiliser la fonction d'évaluation
            return state.getGameValue();
        }

        ArrayList<Action> actions = game.getActions(state);

        if (isMax) {
            // Tour de MAX : chercher la valeur maximale
            double maxValue = Double.NEGATIVE_INFINITY;

            for (Action action : actions) {
                GameState childState = (GameState) game.doAction(state, action);
                double value = minmax(childState, currentDepth + 1, false);
                maxValue = Math.max(maxValue, value);
            }

            return maxValue;
        } else {
            // Tour de MIN : chercher la valeur minimale
            double minValue = Double.POSITIVE_INFINITY;

            for (Action action : actions) {
                GameState childState = (GameState) game.doAction(state, action);
                double value = minmax(childState, currentDepth + 1, true);
                minValue = Math.min(minValue, value);
            }

            return minValue;
        }
    }
}
