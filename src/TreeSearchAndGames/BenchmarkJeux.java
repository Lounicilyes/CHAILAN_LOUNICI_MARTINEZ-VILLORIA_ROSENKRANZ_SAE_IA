import ia.problemes.*;
import ia.framework.jeux.*;
import ia.algo.jeux.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Benchmark pour comparer MinMax vs AlphaBeta
 * ET comparer les fonctions d'évaluation (ancienne vs nouvelle)
 *
 * Génère des fichiers CSV pour créer des graphiques
 */
public class BenchmarkJeux {

    public static void main(String[] args) {
        System.out.println("=== BENCHMARK DES ALGORITHMES DE JEUX ===\n");

        // PARTIE 1 : Comparaison MinMax vs AlphaBeta
        benchmarkMinMaxVsAlphaBeta();

        System.out.println("\n\n");

        // PARTIE 2 : Comparaison des fonctions d'évaluation
        benchmarkFonctionsEvaluation();
    }

    /**
     * Benchmark MinMax vs AlphaBeta (nombre d'états explorés)
     */
    private static void benchmarkMinMaxVsAlphaBeta() {
        System.out.println("=== PARTIE 1 : COMPARAISON MINMAX vs ALPHABETA ===");
        System.out.println("=== Graphique à barres : Nombre d'états explorés ===\n");

        String csvFile = "resultats_minmax_vs_alphabeta.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("Configuration;Profondeur;MinMax_Etats;AlphaBeta_Etats;Reduction_Pourcent");

            System.out.println("Résultats enregistrés dans : " + csvFile);
            System.out.println("\n" + "=".repeat(80));

            comparerMinMaxAlphaBeta(writer);

            System.out.println("\n" + "=".repeat(80));
            System.out.println("Benchmark terminé ! Résultats dans " + csvFile);

        } catch (IOException e) {
            System.err.println("Erreur d'écriture dans le fichier CSV : " + e.getMessage());
        }
    }

    /**
     * Benchmark des fonctions d'évaluation (ancienne vs nouvelle)
     */
    private static void benchmarkFonctionsEvaluation() {
        System.out.println("=== PARTIE 2 : COMPARAISON DES FONCTIONS D'ÉVALUATION ===");
        System.out.println("=== Tableau des victoires : Nouvelle Eval vs Ancienne Eval ===\n");

        String csvFile = "resultats_fonctions_evaluation.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("Configuration;Profondeur;NbParties;Victoires_NouvelleEval;Victoires_AncienneEval;Nuls");

            System.out.println("Résultats enregistrés dans : " + csvFile);
            System.out.println("\n" + "=".repeat(80));

            comparerFonctionsEvaluation(writer);

            System.out.println("\n" + "=".repeat(80));
            System.out.println("Benchmark terminé ! Résultats dans " + csvFile);

        } catch (IOException e) {
            System.err.println("Erreur d'écriture dans le fichier CSV : " + e.getMessage());
        }
    }

    /**
     * Compare MinMax et AlphaBeta sur le même problème
     * Génère des données pour un graphique à barres
     */
    private static void comparerMinMaxAlphaBeta(PrintWriter writer) {
        // Configurations de jeux à tester
        int[][] configs = {
            {3, 3, 3},  // Morpion classique
            {4, 4, 3},  // 4x4 aligner 3
            {4, 4, 4},  // 4x4 aligner 4
            {5, 5, 4},  // 5x5 aligner 4
        };

        // Profondeurs à tester
        int[] profondeurs = {2, 3, 4, 5};

        System.out.println("\n  Configuration       | Prof | MinMax États | AlphaBeta États | Réduction");
        System.out.println("  " + "-".repeat(75));

        for (int[] config : configs) {
            int rows = config[0];
            int cols = config[1];
            int streak = config[2];
            String configName = rows + "x" + cols + "_align" + streak;

            for (int depth : profondeurs) {
                // Test MinMax
                MnkGame game1 = new MnkGame(rows, cols, streak);
                Player minmax = new MinMaxPlayer(game1, true, depth);
                Player random1 = new RandomPlayer(game1, false);

                jouerPartie(game1, minmax, random1);
                int etatsMinMax = minmax.getStateCounter();

                // Test AlphaBeta (même configuration)
                MnkGame game2 = new MnkGame(rows, cols, streak);
                Player alphabeta = new AlphaBetaPlayer(game2, true, depth);
                Player random2 = new RandomPlayer(game2, false);

                jouerPartie(game2, alphabeta, random2);
                int etatsAlphaBeta = alphabeta.getStateCounter();

                // Calculer la réduction
                double reduction = 0;
                if (etatsMinMax > 0) {
                    reduction = (1.0 - (double) etatsAlphaBeta / etatsMinMax) * 100;
                }

                // Affichage console
                System.out.println(String.format("  %-20s |  %d   | %12d | %15d | %6.1f%%",
                    configName, depth, etatsMinMax, etatsAlphaBeta, reduction));

                // Écrire dans CSV pour graphique à barres
                String configLabel = configName + "_D" + depth;
                writer.println(String.format("%s;%d;%d;%d;%.2f",
                    configLabel, depth, etatsMinMax, etatsAlphaBeta, reduction));
                writer.flush();
            }
            System.out.println("  " + "-".repeat(75));
        }

        System.out.println("\n  >>> AlphaBeta explore significativement moins d'états que MinMax !");
    }

    /**
     * Compare les fonctions d'évaluation (ancienne vs nouvelle basée sur l'article)
     * Génère un tableau des victoires pour chaque configuration
     */
    private static void comparerFonctionsEvaluation(PrintWriter writer) {
        // Configurations de jeux à tester
        int[][] configs = {
            {3, 3, 3},  // Morpion classique
            {4, 4, 3},  // 4x4 aligner 3
            {4, 4, 4},  // 4x4 aligner 4
            {5, 5, 4},  // 5x5 aligner 4
            {6, 6, 4},  // 6x6 aligner 4
        };

        int depth = 4;        // Profondeur de recherche
        int nbParties = 20;   // Nombre de parties par configuration

        System.out.println("  Profondeur de recherche : " + depth);
        System.out.println("  Nombre de parties par configuration : " + nbParties);
        System.out.println();

        // En-tête du tableau
        System.out.println("  " + "+".repeat(1) + "-".repeat(18) + "+" + "-".repeat(22) + "+" + "-".repeat(22) + "+" + "-".repeat(10) + "+");
        System.out.println(String.format("  | %-16s | %-20s | %-20s | %-8s |",
            "Configuration", "Victoires Nouv.Eval", "Victoires Anc.Eval", "Nuls"));
        System.out.println("  " + "+".repeat(1) + "-".repeat(18) + "+" + "-".repeat(22) + "+" + "-".repeat(22) + "+" + "-".repeat(10) + "+");

        for (int[] config : configs) {
            int rows = config[0];
            int cols = config[1];
            int streak = config[2];
            String configName = rows + "x" + cols + " align " + streak;

            int victoiresNouvelleEval = 0;
            int victoiresAncienneEval = 0;
            int nuls = 0;

            // Jouer nbParties parties en alternant qui commence
            for (int partie = 0; partie < nbParties; partie++) {
                boolean nouvelleEvalCommence = (partie % 2 == 0);
                String gagnant = jouerPartieEvaluation(rows, cols, streak, depth, nouvelleEvalCommence);

                if (gagnant.equals("NOUVELLE")) {
                    victoiresNouvelleEval++;
                } else if (gagnant.equals("ANCIENNE")) {
                    victoiresAncienneEval++;
                } else {
                    nuls++;
                }
            }

            // Affichage console
            System.out.println(String.format("  | %-16s | %-20d | %-20d | %-8d |",
                configName, victoiresNouvelleEval, victoiresAncienneEval, nuls));

            // Écrire dans CSV
            writer.println(String.format("%s;%d;%d;%d;%d;%d",
                configName, depth, nbParties, victoiresNouvelleEval, victoiresAncienneEval, nuls));
            writer.flush();
        }

        System.out.println("  " + "+".repeat(1) + "-".repeat(18) + "+" + "-".repeat(22) + "+" + "-".repeat(22) + "+" + "-".repeat(10) + "+");
        System.out.println("\n  >>> La nouvelle fonction d'évaluation (basée sur le potentiel) devrait gagner plus souvent !");
    }

    /**
     * Joue une partie entre nouvelle évaluation et ancienne évaluation
     * @return "NOUVELLE", "ANCIENNE" ou "NUL"
     */
    private static String jouerPartieEvaluation(int rows, int cols, int streak, int depth, boolean nouvelleEvalCommence) {
        Game gameNouvelleEval = new MnkGameEval(rows, cols, streak);
        Game gameAncienneEval = new MnkGame(rows, cols, streak);

        Player joueurNouvelleEval;
        Player joueurAncienneEval;
        Game gameUtilise;

        if (nouvelleEvalCommence) {
            // Nouvelle Eval joue en premier (P1 = MAX)
            gameUtilise = gameNouvelleEval;
            joueurNouvelleEval = new AlphaBetaPlayer(gameNouvelleEval, true, depth);
            joueurAncienneEval = new AlphaBetaPlayer(gameNouvelleEval, false, depth);
        } else {
            // Ancienne Eval joue en premier (P1 = MAX)
            gameUtilise = gameAncienneEval;
            joueurAncienneEval = new AlphaBetaPlayer(gameAncienneEval, true, depth);
            joueurNouvelleEval = new AlphaBetaPlayer(gameAncienneEval, false, depth);
        }

        GameEngine engine;
        if (nouvelleEvalCommence) {
            engine = new GameEngine(gameUtilise, joueurNouvelleEval, joueurAncienneEval);
        } else {
            engine = new GameEngine(gameUtilise, joueurAncienneEval, joueurNouvelleEval);
        }

        GameState endState = engine.gameLoopSilent();
        double value = endState.getGameValue();

        // Interpréter le résultat
        if (value == GameState.P1_WIN) {
            return nouvelleEvalCommence ? "NOUVELLE" : "ANCIENNE";
        } else if (value == GameState.P2_WIN) {
            return nouvelleEvalCommence ? "ANCIENNE" : "NOUVELLE";
        } else {
            return "NUL";
        }
    }

    /**
     * Joue une partie complète (mode silencieux)
     */
    private static void jouerPartie(Game game, Player p1, Player p2) {
        GameEngine engine = new GameEngine(game, p1, p2);
        engine.gameLoopSilent();
    }
}
