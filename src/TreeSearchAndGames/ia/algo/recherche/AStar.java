package ia.algo.recherche;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

import ia.framework.common.State;
import ia.framework.common.Action;
import ia.framework.common.ArgParse;

import ia.framework.recherche.TreeSearch;
import ia.framework.recherche.SearchProblem;
import ia.framework.recherche.SearchNode;

/**
 * Implémentation de l'algorithme A*
 *
 * A* combine le coût du chemin g(n) et l'heuristique h(n) pour explorer
 * les nœuds les plus prometteurs en premier.
 * f(n) = g(n) + h(n)
 *
 * Proprietes (avec heuristique admissible) :
 * - Complet : Oui
 * - Optimal : Oui (si h est admissible, c'est-a-dire ne surestime jamais)
 * - Complexite temps : O(b^d) dans le pire cas, mais generalement bien meilleur
 * - Complexite espace : O(b^d)
 */
public class AStar extends TreeSearch {

    /**
     * Crée un algorithme A*
     * @param prob Le problème à résoudre
     * @param initial_state L'état initial
     */
    public AStar(SearchProblem prob, State initial_state) {
        super(prob, initial_state);
        // File de priorité ordonnée par f(n) = g(n) + h(n)
        this.frontier = new PriorityQueue<SearchNode>(
            Comparator.comparingDouble(n -> n.getCost() + n.getHeuristic())
        );
    }

    /**
     * Calcule f(n) = g(n) + h(n) pour un noeud
     * @param node Le noeud à évaluer
     * @return La valeur de f(n)
     */
    private double getF(SearchNode node) {
        return node.getCost() + node.getHeuristic();
    }

    /**
     * Résout le problème en utilisant A*
     * @return true si une solution est trouvée, false sinon
     */
    @Override
    public boolean solve() {
        // 1. Créer un noeud correspondant à l'état initial
        SearchNode root_node = SearchNode.makeRootSearchNode(initial_state);

        // 2. Initialiser la frontière avec ce noeud
        this.frontier.clear();
        this.frontier.add(root_node);

        // 3. Initialiser l'ensemble des états visités à vide
        this.explored.clear();
        this.visited.clear();
        this.visited.put(initial_state, root_node);

        // 4. Tant que la frontière n'est pas vide
        while (!this.frontier.isEmpty()) {

            // 5. Retirer le noeud avec le PLUS PETIT f(n) de la frontière
            SearchNode cur_node = ((PriorityQueue<SearchNode>) this.frontier).poll();

            State cur_state = cur_node.getState();

            // 6. Si le noeud contient un état but
            if (problem.isGoalState(cur_state)) {
                // 7. Retourner vrai - solution trouvée
                this.end_node = cur_node;
                return true;
            }

            // 8. Sinon, ajouter son état à l'ensemble des états visités
            this.explored.add(cur_state);

            // 9. Étendre les enfants du noeud
            ArrayList<Action> actions = problem.getActions(cur_state);

            if (ArgParse.DEBUG) {
                System.out.print(cur_state + " (g=" + cur_node.getCost() +
                    ", h=" + cur_node.getHeuristic() +
                    ", f=" + getF(cur_node) + ", " + actions.size() + " actions) -> {");
            }

            // 10. Pour chaque action possible
            for (Action a : actions) {
                // Créer le noeud enfant
                SearchNode child_node = SearchNode.makeChildSearchNode(problem, cur_node, a);
                State child_state = child_node.getState();

                if (ArgParse.DEBUG) {
                    System.out.print("(" + a + ", " + child_state +
                        ", f=" + getF(child_node) + ")");
                }

                // 11. S'il n'a pas été visité
                if (!explored.contains(child_state)) {
                    // Vérifier s'il est déjà dans la frontière avec un f plus élevé
                    SearchNode existing = visited.get(child_state);

                    if (existing == null) {
                        // Nouveau noeud - l'ajouter
                        frontier.add(child_node);
                        visited.put(child_state, child_node);

                        if (ArgParse.DEBUG)
                            System.out.print("[A] ");
                    } else if (getF(child_node) < getF(existing)) {
                        // Meilleur chemin trouvé - remplacer
                        frontier.remove(existing);
                        frontier.add(child_node);
                        visited.put(child_state, child_node);

                        if (ArgParse.DEBUG)
                            System.out.print("[U] "); // Updated
                    } else {
                        if (ArgParse.DEBUG)
                            System.out.print("[I] ");
                    }
                } else {
                    if (ArgParse.DEBUG)
                        System.out.print("[I] ");
                }
            }

            if (actions.size() > 0 && ArgParse.DEBUG)
                System.out.println("}");
        }

        // Aucune solution trouvée
        return false;
    }
}
