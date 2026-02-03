package ia.algo.jeux;

import java.util.ArrayList;

import ia.framework.common.Action;
import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;
import ia.framework.jeux.Player;

/**
 * Implémentation de l'algorithme Alpha-Beta avec limitation de profondeur
 *
 * Alpha-Beta est une amélioration de MinMax qui permet d'élaguer des branches
 * de l'arbre de recherche sans affecter le résultat final.
 *
 * - Alpha : la meilleure valeur que MAX peut garantir (borne inférieure)
 * - Beta : la meilleure valeur que MIN peut garantir (borne supérieure)
 *
 * Quand alpha >= beta, on peut couper la branche (élagage).
 */
public class AlphaBetaPlayer extends Player {

    /** Profondeur maximale de recherche (-1 = sans limite) */
    private int maxDepth;

    /**
     * Crée un joueur Alpha-Beta
     * @param g Le jeu
     * @param player_one true si joueur 1 (MAX), false si joueur 2 (MIN)
     * @param depth La profondeur maximale (-1 pour sans limite)
     */
    public AlphaBetaPlayer(Game g, boolean player_one, int depth) {
        super(g, player_one);
        this.name = "AlphaBeta";
        this.maxDepth = depth;
        if (depth > 0) {
            this.name = "AlphaBeta(d=" + depth + ")";
        }
    }

    /**
     * Retourne le meilleur coup selon l'algorithme Alpha-Beta
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
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        double bestValue = isMax ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (Action action : actions) {
            // Simuler le coup
            GameState childState = (GameState) game.doAction(state, action);
            incStateCounter();

            // Calculer la valeur de cet état avec élagage
            double value = alphabeta(childState, 1, alpha, beta, !isMax);

            // Mettre à jour le meilleur coup
            if (isMax) {
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
                alpha = Math.max(alpha, bestValue);
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
                beta = Math.min(beta, bestValue);
            }
        }

        return bestAction;
    }

    /**
     * Algorithme Alpha-Beta récursif avec limitation de profondeur
     * @param state L'état du jeu
     * @param currentDepth La profondeur actuelle
     * @param alpha La meilleure valeur pour MAX
     * @param beta La meilleure valeur pour MIN
     * @param isMax true si c'est au tour de MAX, false sinon
     * @return La valeur alpha-beta de l'état
     */
    private double alphabeta(GameState state, int currentDepth,
                             double alpha, double beta, boolean isMax) {
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
                double value = alphabeta(childState, currentDepth + 1, alpha, beta, false);
                maxValue = Math.max(maxValue, value);

                // Mise à jour d'alpha
                alpha = Math.max(alpha, maxValue);

                // Élagage beta : MIN ne choisira jamais cette branche
                if (alpha >= beta) {
                    break; // Coupe beta
                }
            }

            return maxValue;
        } else {
            // Tour de MIN : chercher la valeur minimale
            double minValue = Double.POSITIVE_INFINITY;

            for (Action action : actions) {
                GameState childState = (GameState) game.doAction(state, action);
                double value = alphabeta(childState, currentDepth + 1, alpha, beta, true);
                minValue = Math.min(minValue, value);

                // Mise à jour de beta
                beta = Math.min(beta, minValue);

                // Élagage alpha : MAX ne choisira jamais cette branche
                if (alpha >= beta) {
                    break; // Coupe alpha
                }
            }

            return minValue;
        }
    }
}
