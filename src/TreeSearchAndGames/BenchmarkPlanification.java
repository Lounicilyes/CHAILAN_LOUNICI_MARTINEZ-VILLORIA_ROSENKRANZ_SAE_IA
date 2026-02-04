import ia.problemes.Dummy;
import ia.problemes.DummyState;
import ia.framework.recherche.SearchProblem;
import ia.framework.recherche.TreeSearch;
import ia.framework.recherche.SearchNode;
import ia.framework.common.State;
import ia.framework.common.Action;

import ia.algo.recherche.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Benchmark pour comparer les algorithmes de planification
 *
 * Ce programme teste BFS, DFS, UCS et A* sur des problèmes Dummy
 * de difficultés croissantes et enregistre les résultats dans un fichier CSV.
 *
 * Critères de comparaison :
 * - Temps d'exécution (ms)
 * - Nombre de nœuds explorés
 * - Coût de la solution trouvée
 * - Longueur de la solution (nombre d'actions)
 * - Résultat (succès/échec)
 */
public class BenchmarkPlanification {

    // Paramètres des tests
    // CAS DIFFICILE : N=10000, K=5
    private static final int[] TAILLES = {10000}; // N fixé à 10000
    private static final int K_FIXE = 5; // K fixé à 5 (cas difficile)
    private static final long GRAINE = 12345L; // Pour la reproductibilité
    private static final long TIMEOUT = 60000; // 60 secondes max par test

    public static void main(String[] args) {
        System.out.println("=== BENCHMARK DES ALGORITHMES DE PLANIFICATION ===\n");

        // Préparer le fichier CSV
        String csvFile = "resultats_cas_difficile_N10000_K5.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            // En-tête CSV
            writer.println("Algorithme;N;K;Graine;Temps_ms;Noeuds_Explores;Profondeur_Max;Cout_Solution;Longueur_Solution;Succes");

            System.out.println("Résultats enregistrés dans : " + csvFile);
            System.out.println("\nFormat : Algorithme;N;K;Graine;Temps_ms;Noeuds_Explores;Profondeur_Max;Cout_Solution;Longueur_Solution;Succes\n");
            System.out.println("-".repeat(120));

            // CAS DIFFICILE : N=10000, K=5
            int k = K_FIXE;
            System.out.println("=== CAS DIFFICILE : N=10000, K=" + k + " ===\n");

            for (int n : TAILLES) {
                System.out.println("\n>>> Test avec N=" + n + ", K=" + k + ", Graine=" + GRAINE);

                // Créer le problème
                SearchNode.getTotalSearchNodes(); // Reset implicit
                Dummy probleme = new Dummy(n, k, GRAINE);
                State etatInitial = Dummy.initialState();

                // Tester chaque algorithme
                String[] algoNames = {"BFS", "DFS", "UCS", "GFS", "AStar"};

                for (String algoName : algoNames) {
                    testAlgorithme(algoName, probleme, etatInitial, n, k, GRAINE, writer);
                }
            }

            System.out.println("\n" + "=".repeat(120));
            System.out.println("Benchmark terminé ! Résultats dans " + csvFile);

        } catch (IOException e) {
            System.err.println("Erreur d'écriture dans le fichier CSV : " + e.getMessage());
        }
    }

    /**
     * Teste un algorithme sur un problème donné
     */
    private static void testAlgorithme(String algoName, SearchProblem probleme,
                                       State etatInitial, int n, int k, long graine,
                                       PrintWriter writer) {
        try {
            // Créer l'algorithme
            TreeSearch algo = createAlgorithme(algoName, probleme, etatInitial);
            if (algo == null) {
                System.out.println("  " + algoName + " : Non implémenté");
                return;
            }

            // Reset le compteur de noeuds
            resetNodeCounter();

            // Exécuter avec timeout
            long startTime = System.currentTimeMillis();
            boolean solved = false;

            try {
                solved = algo.solve();
            } catch (OutOfMemoryError e) {
                System.out.println("  " + algoName + " : OUT OF MEMORY");
                writer.println(String.format("%s;%d;%d;%d;-1;-1;-1;-1;-1;OOM",
                    algoName, n, k, graine));
                writer.flush();
                return;
            }

            long endTime = System.currentTimeMillis();
            long temps = endTime - startTime;

            // Récupérer les métriques
            int noeudsExplores = SearchNode.getTotalSearchNodes();
            int profondeurMax = SearchNode.getMaxDepth();

            double coutSolution = -1;
            int longueurSolution = -1;

            if (solved && algo.getEndNode() != null) {
                coutSolution = algo.getEndNode().getCost();
                ArrayList<Action> solution = algo.getSolution();
                longueurSolution = (solution != null) ? solution.size() : -1;
            }

            // Afficher les résultats
            String resultat = String.format("  %-10s : %6d ms, %8d noeuds, prof_max=%4d, coût=%.2f, len=%d, %s",
                algoName, temps, noeudsExplores, profondeurMax, coutSolution, longueurSolution,
                solved ? "SUCCÈS" : "ÉCHEC");
            System.out.println(resultat);

            // Écrire dans le CSV
            writer.println(String.format("%s;%d;%d;%d;%d;%d;%d;%.4f;%d;%s",
                algoName, n, k, graine, temps, noeudsExplores, profondeurMax,
                coutSolution, longueurSolution, solved ? "SUCCESS" : "FAIL"));
            writer.flush();

        } catch (Exception e) {
            System.out.println("  " + algoName + " : ERREUR - " + e.getMessage());
            writer.println(String.format("%s;%d;%d;%d;-1;-1;-1;-1;-1;ERROR",
                algoName, n, k, graine));
            writer.flush();
        }
    }

    /**
     * Factory pour créer les algorithmes
     */
    private static TreeSearch createAlgorithme(String name, SearchProblem p, State s) {
        switch (name) {
            case "BFS":
                return new BFS(p, s);
            case "DFS":
                return new DFS(p, s);
            case "UCS":
                return new UCS(p, s);
            case "GFS":
                return new GFS(p, s);
            case "AStar":
                return new AStar(p, s);
            default:
                return null;
        }
    }

    /**
     * Reset le compteur de noeuds (via réflexion car le champ est statique)
     */
    private static void resetNodeCounter() {
        try {
            java.lang.reflect.Field countField = SearchNode.class.getDeclaredField("COUNT");
            countField.setAccessible(true);
            countField.setInt(null, 0);

            java.lang.reflect.Field depthField = SearchNode.class.getDeclaredField("DEPTH");
            depthField.setAccessible(true);
            depthField.setInt(null, 0);
        } catch (Exception e) {
            // Ignorer si ça ne marche pas
        }
    }
}
