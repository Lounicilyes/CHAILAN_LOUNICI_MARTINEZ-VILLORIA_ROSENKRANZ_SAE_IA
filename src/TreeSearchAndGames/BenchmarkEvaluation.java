import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.function.Function;

import ia.framework.common.Action;
import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;
import ia.algo.jeux.AlphaBetaEvalPlayer;
import ia.problemes.MnkGame;
import ia.problemes.MnkEvaluationFunctions;

/**
 * Benchmark pour comparer les fonctions d'évaluation
 *
 * Compare la nouvelle heuristique (potentiel de lignes) vs l'ancienne (compte de pions)
 * en faisant jouer deux IA AlphaBeta l'une contre l'autre.
 */
public class BenchmarkEvaluation {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("B) COMPARAISON DES FONCTIONS D'ÉVALUATION");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("Nouvelle éval: Calcule le potentiel de chaque ligne");
        System.out.println("               (ligne libre avec pions alliés > ligne vide > ligne bloquée)");
        System.out.println("Ancienne éval: Compte simplement la différence de pions");
        System.out.println();

        // Configurations à tester : [rows, cols, streak]
        int[][] configurations = {
            {3, 3, 3},   // Morpion classique
            {4, 4, 3},   // 4x4 avec 3 alignés
            {5, 5, 4},   // 5x5 avec 4 alignés
            {6, 6, 4},   // 6x6 avec 4 alignés
            {7, 6, 4},   // Puissance 4 (7 lignes, 6 colonnes, 4 alignés)
        };

        int profondeur = 4; // Profondeur de recherche
        int nombreParties = 10; // Nombre de parties par configuration

        // Récupérer les fonctions d'évaluation
        Function<GameState, Double> nouvelleEval = MnkEvaluationFunctions.getNouvelleEvaluation();
        Function<GameState, Double> ancienneEval = MnkEvaluationFunctions.getAncienneEvaluation();

        System.out.println("Profondeur de recherche: " + profondeur);
        System.out.println("Nombre de parties par configuration: " + nombreParties);
        System.out.println();

        // Tableau des résultats
        System.out.println("+-----------------------+----------------------+----------------------+--------+");
        System.out.println("| Configuration         | Victoires Nouv. Eval | Victoires Anc. Eval  | Nuls   |");
        System.out.println("+-----------------------+----------------------+----------------------+--------+");

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Configuration,Victoires Nouvelle Eval,Victoires Ancienne Eval,Nuls\n");

        for (int[] config : configurations) {
            int rows = config[0];
            int cols = config[1];
            int streak = config[2];

            String configName = rows + "x" + cols + " (" + streak + " alignés)";

            int victoiresNouvelle = 0;
            int victoiresAncienne = 0;
            int nuls = 0;

            for (int partie = 0; partie < nombreParties; partie++) {
                // Alterner qui commence : nouvelle éval commence les parties paires
                boolean nouvelleCommence = (partie % 2 == 0);

                int resultat = jouerPartie(rows, cols, streak, profondeur,
                                           nouvelleEval, ancienneEval, nouvelleCommence);

                if (resultat == 1) {
                    victoiresNouvelle++;
                } else if (resultat == -1) {
                    victoiresAncienne++;
                } else {
                    nuls++;
                }
            }

            // Afficher les résultats
            System.out.printf("| %-21s | %-20d | %-20d | %-6d |\n",
                              configName, victoiresNouvelle, victoiresAncienne, nuls);

            csvContent.append(configName).append(",")
                      .append(victoiresNouvelle).append(",")
                      .append(victoiresAncienne).append(",")
                      .append(nuls).append("\n");
        }

        System.out.println("+-----------------------+----------------------+----------------------+--------+");
        System.out.println();

        // Sauvegarder en CSV
        try (PrintWriter pw = new PrintWriter(new FileWriter("resultats_evaluation.csv"))) {
            pw.print(csvContent.toString());
            System.out.println("Résultats sauvegardés dans: resultats_evaluation.csv");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Analyse:");
        System.out.println("- Si la nouvelle évaluation gagne plus souvent, elle est meilleure");
        System.out.println("- La nouvelle heuristique devrait être plus performante car elle");
        System.out.println("  évalue le potentiel de victoire plutôt que juste compter les pions");
    }

    /**
     * Joue une partie entre deux IA avec des fonctions d'évaluation différentes
     *
     * @return 1 si nouvelle éval gagne, -1 si ancienne gagne, 0 si nul
     */
    private static int jouerPartie(int rows, int cols, int streak, int profondeur,
                                    Function<GameState, Double> nouvelleEval,
                                    Function<GameState, Double> ancienneEval,
                                    boolean nouvelleCommence) {

        Game game = new MnkGame(rows, cols, streak);
        GameState state = game.init();

        // Créer les joueurs avec leurs fonctions d'évaluation respectives
        AlphaBetaEvalPlayer joueurNouvelle;
        AlphaBetaEvalPlayer joueurAncienne;

        if (nouvelleCommence) {
            // Nouvelle éval = Joueur 1 (X, MAX)
            joueurNouvelle = new AlphaBetaEvalPlayer(game, true, profondeur, nouvelleEval, "Nouvelle");
            joueurAncienne = new AlphaBetaEvalPlayer(game, false, profondeur, ancienneEval, "Ancienne");
        } else {
            // Ancienne éval = Joueur 1 (X, MAX)
            joueurAncienne = new AlphaBetaEvalPlayer(game, true, profondeur, ancienneEval, "Ancienne");
            joueurNouvelle = new AlphaBetaEvalPlayer(game, false, profondeur, nouvelleEval, "Nouvelle");
        }

        // Jouer la partie
        boolean tourNouvelle = nouvelleCommence;
        int maxTours = rows * cols; // Maximum de coups possibles

        for (int tour = 0; tour < maxTours && !state.isFinalState(); tour++) {
            Action action;
            if (tourNouvelle) {
                action = joueurNouvelle.getMove(state);
            } else {
                action = joueurAncienne.getMove(state);
            }

            if (action == null) {
                break;
            }

            state = (GameState) game.doAction(state, action);
            tourNouvelle = !tourNouvelle;
        }

        // Déterminer le résultat
        double gameValue = state.getGameValue();

        if (Double.isNaN(gameValue) || gameValue == 0) {
            return 0; // Nul
        }

        // gameValue > 0 signifie que X (Joueur 1) a gagné
        if (gameValue > 0) {
            return nouvelleCommence ? 1 : -1;
        } else {
            return nouvelleCommence ? -1 : 1;
        }
    }
}
