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
 * Implementation de l'algorithme de recherche a cout uniforme (Uniform Cost Search)
 * Aussi connu comme l'algorithme de Dijkstra
 *
 * UCS explore les noeuds dans l'ordre croissant de leur cout depuis l'etat initial.
 * Utilise une file de priorite ordonnee par g(n) = cout du chemin.
 *
 * Proprietes :
 * - Complet : Oui (si le cout minimum est positif)
 * - Optimal : Oui (trouve toujours le chemin de cout minimum)
 * - Complexite temps : O(b^(1 + C_opt / epsilon))
 * - Complexite espace : O(b^(1 + C_opt / epsilon))
 */
public class UCS extends TreeSearch {

    /**
     * Crée un algorithme UCS (Dijkstra)
     * @param prob Le problème à résoudre
     * @param initial_state L'état initial
     */
    public UCS(SearchProblem prob, State initial_state) {
        super(prob, initial_state);
        // File de priorité ordonnée par le coût du chemin (g(n))
        this.frontier = new PriorityQueue<SearchNode>(
            Comparator.comparingDouble(SearchNode::getCost)
        );
    }

    /**
     * Résout le problème en utilisant la recherche à coût uniforme
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

            // 5. Retirer le noeud avec le PLUS PETIT COÛT de la frontière
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

            if (ArgParse.DEBUG)
                System.out.print(cur_state + " (cost=" + cur_node.getCost() + ", " + actions.size() + " actions) -> {");

            // 10. Pour chaque action possible
            for (Action a : actions) {
                // Créer le noeud enfant
                SearchNode child_node = SearchNode.makeChildSearchNode(problem, cur_node, a);
                State child_state = child_node.getState();

                if (ArgParse.DEBUG)
                    System.out.print("(" + a + ", " + child_state + ", cost=" + child_node.getCost() + ")");

                // 11. S'il n'a pas été visité
                if (!explored.contains(child_state)) {
                    // Vérifier s'il est déjà dans la frontière avec un coût plus élevé
                    SearchNode existing = visited.get(child_state);

                    if (existing == null) {
                        // Nouveau noeud - l'ajouter
                        frontier.add(child_node);
                        visited.put(child_state, child_node);

                        if (ArgParse.DEBUG)
                            System.out.print("[A] ");
                    } else if (child_node.getCost() < existing.getCost()) {
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
