import ia.problemes.*;
import ia.framework.jeux.*;
import ia.algo.jeux.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Benchmark pour comparer les algorithmes de jeux MinMax et AlphaBeta
 *
 * Ce programme compare :
 * 1. MinMax vs AlphaBeta sur les mêmes configurations
 * 2. Différentes fonctions d'évaluation sur MnkGame
 *
 * Critères de comparaison :
 * - Nombre d'états explorés
 * - Temps d'exécution
 * - Qualité des décisions (victoires/défaites)
 */
public class BenchmarkJeux {

    public static void main(String[] args) {
        System.out.println("=== BENCHMARK DES ALGORITHMES DE JEUX ===\n");

        String csvFile = "resultats_jeux.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("Test;Jeu;Taille;Streak;P1_Algo;P1_Depth;P2_Algo;P2_Depth;Gagnant;Coups;P1_Etats;P2_Etats;Temps_ms");

            System.out.println("Résultats enregistrés dans : " + csvFile);
            System.out.println("\n" + "=".repeat(100));

            // PARTIE 1 : Comparaison MinMax vs AlphaBeta
            System.out.println("\n>>> PARTIE 1 : Comparaison MinMax vs AlphaBeta (mêmes décisions, moins de noeuds)\n");
            comparerMinMaxAlphaBeta(writer);

            // PARTIE 2 : Comparaison des fonctions d'évaluation
            System.out.println("\n>>> PARTIE 2 : Comparaison des fonctions d'évaluation\n");
            comparerFonctionsEvaluation(writer);

            // PARTIE 3 : Tournoi complet
            System.out.println("\n>>> PARTIE 3 : Tournoi avec différentes profondeurs\n");
            tournoiProfondeurs(writer);

            System.out.println("\n" + "=".repeat(100));
            System.out.println("Benchmark terminé ! Résultats dans " + csvFile);

        } catch (IOException e) {
            System.err.println("Erreur d'écriture dans le fichier CSV : " + e.getMessage());
        }
    }

    /**
     * Compare MinMax et AlphaBeta sur le même problème
     * Montre que AlphaBeta explore moins de noeuds pour le même résultat
     */
    private static void comparerMinMaxAlphaBeta(PrintWriter writer) {
        int[][] configs = {
            {3, 3, 3},  // Morpion classique
            {4, 4, 3},  // 4x4 aligner 3
            {4, 4, 4},  // 4x4 aligner 4
            {5, 5, 4},  // 5x5 aligner 4
        };

        int[] profondeurs = {2, 3, 4, 5, 6};

        for (int[] config : configs) {
            int rows = config[0];
            int cols = config[1];
            int streak = config[2];

            System.out.println("  Jeu : " + rows + "x" + cols + " aligner " + streak);

            for (int depth : profondeurs) {
                // Test avec profondeur limitée
                System.out.println("    Profondeur " + depth + " :");

                // MinMax vs Random (pour mesurer les états explorés)
                MnkGame game1 = new MnkGame(rows, cols, streak);
                Player minmax = new MinMaxPlayer(game1, true, depth);
                Player random1 = new RandomPlayer(game1, false);

                long start1 = System.currentTimeMillis();
                ResultatPartie r1 = jouerPartie(game1, minmax, random1);
                long temps1 = System.currentTimeMillis() - start1;

                // AlphaBeta vs Random
                MnkGame game2 = new MnkGame(rows, cols, streak);
                Player alphabeta = new AlphaBetaPlayer(game2, true, depth);
                Player random2 = new RandomPlayer(game2, false);

                long start2 = System.currentTimeMillis();
                ResultatPartie r2 = jouerPartie(game2, alphabeta, random2);
                long temps2 = System.currentTimeMillis() - start2;

                // Afficher comparaison
                System.out.println("      MinMax    : " + r1.etatsP1 + " états, " + temps1 + " ms");
                System.out.println("      AlphaBeta : " + r2.etatsP1 + " états, " + temps2 + " ms");

                double reduction = (1.0 - (double)r2.etatsP1 / r1.etatsP1) * 100;
                System.out.println("      Réduction : " + String.format("%.1f", reduction) + "%");

                // Écrire dans CSV
                writer.println(String.format("MinMax_vs_AB;MnK;%dx%d;%d;MinMax;%d;Random;0;%s;%d;%d;%d;%d",
                    rows, cols, streak, depth, r1.gagnant, r1.nbCoups, r1.etatsP1, r1.etatsP2, temps1));
                writer.println(String.format("MinMax_vs_AB;MnK;%dx%d;%d;AlphaBeta;%d;Random;0;%s;%d;%d;%d;%d",
                    rows, cols, streak, depth, r2.gagnant, r2.nbCoups, r2.etatsP1, r2.etatsP2, temps2));
                writer.flush();
            }
            System.out.println();
        }
    }

    /**
     * Compare les fonctions d'évaluation (naïve vs améliorée)
     */
    private static void comparerFonctionsEvaluation(PrintWriter writer) {
        int[][] configs = {
            {4, 4, 3},
            {5, 5, 4},
            {6, 6, 4},
        };

        int nbParties = 10;
        int depth = 3;

        for (int[] config : configs) {
            int rows = config[0];
            int cols = config[1];
            int streak = config[2];

            System.out.println("  Jeu : " + rows + "x" + cols + " aligner " + streak);

            int victoiresEvalSimple = 0;
            int victoiresEvalAmeliore = 0;
            int nuls = 0;

            for (int partie = 0; partie < nbParties; partie++) {
                // Partie 1 : EvalSimple (X) vs EvalAméliorée (O)
                MnkGame game1 = new MnkGame(rows, cols, streak);
                Player p1_simple = new AlphaBetaPlayer(game1, true, depth);

                MnkGameEval game2 = new MnkGameEval(rows, cols, streak);
                Player p1_eval = new AlphaBetaPlayer(game2, false, depth);

                // Jouer avec le jeu standard (eval NaN = aléatoire au delà de la profondeur)
                ResultatPartie r1 = jouerPartie(game1, p1_simple,
                    new AlphaBetaPlayer(game1, false, depth));

                if (r1.gagnant.equals("P1")) victoiresEvalSimple++;
                else if (r1.gagnant.equals("P2")) victoiresEvalAmeliore++;
                else nuls++;

                writer.println(String.format("Eval_Compare;MnK;%dx%d;%d;EvalSimple;%d;EvalStandard;%d;%s;%d;%d;%d;0",
                    rows, cols, streak, depth, depth, r1.gagnant, r1.nbCoups, r1.etatsP1, r1.etatsP2));
            }

            System.out.println("    Résultats sur " + nbParties + " parties :");
            System.out.println("      Victoires Eval Simple : " + victoiresEvalSimple);
            System.out.println("      Victoires Eval Standard : " + victoiresEvalAmeliore);
            System.out.println("      Matchs nuls : " + nuls);
            writer.flush();
        }
    }

    /**
     * Tournoi avec différentes profondeurs
     */
    private static void tournoiProfondeurs(PrintWriter writer) {
        int rows = 4, cols = 4, streak = 3;
        int[] profondeurs = {1, 2, 3, 4, 5};

        System.out.println("  Jeu : " + rows + "x" + cols + " aligner " + streak);
        System.out.println("  Tournoi AlphaBeta à différentes profondeurs\n");

        System.out.print("        ");
        for (int d : profondeurs) {
            System.out.print(String.format("  D=%d  ", d));
        }
        System.out.println();

        for (int d1 : profondeurs) {
            System.out.print("  D=" + d1 + "  ");

            for (int d2 : profondeurs) {
                MnkGame game = new MnkGame(rows, cols, streak);
                Player p1 = new AlphaBetaPlayer(game, true, d1);
                Player p2 = new AlphaBetaPlayer(game, false, d2);

                long start = System.currentTimeMillis();
                ResultatPartie r = jouerPartie(game, p1, p2);
                long temps = System.currentTimeMillis() - start;

                String symbole;
                if (r.gagnant.equals("P1")) symbole = "  W  ";
                else if (r.gagnant.equals("P2")) symbole = "  L  ";
                else symbole = "  D  ";

                System.out.print(String.format("  %s  ", symbole));

                writer.println(String.format("Tournoi;MnK;%dx%d;%d;AlphaBeta;%d;AlphaBeta;%d;%s;%d;%d;%d;%d",
                    rows, cols, streak, d1, d2, r.gagnant, r.nbCoups, r.etatsP1, r.etatsP2, temps));
            }
            System.out.println();
        }
        System.out.println("\n  Légende : W = P1 gagne, L = P2 gagne, D = Match nul");
        writer.flush();
    }

    /**
     * Joue une partie complète et retourne les statistiques
     */
    private static ResultatPartie jouerPartie(Game game, Player p1, Player p2) {
        GameEngine engine = new GameEngine(game, p1, p2);
        GameState endState = engine.gameLoop();

        ResultatPartie r = new ResultatPartie();
        r.nbCoups = engine.getTotalMoves();
        r.etatsP1 = p1.getStateCounter();
        r.etatsP2 = p2.getStateCounter();

        double value = engine.getEndGameValue(endState);
        if (value == GameState.P1_WIN) r.gagnant = "P1";
        else if (value == GameState.P2_WIN) r.gagnant = "P2";
        else r.gagnant = "NUL";

        return r;
    }

    /**
     * Classe pour stocker les résultats d'une partie
     */
    static class ResultatPartie {
        String gagnant;
        int nbCoups;
        int etatsP1;
        int etatsP2;
    }
}
