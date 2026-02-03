package ia.algo.recherche;

import java.util.ArrayList;
import java.util.LinkedList;

import ia.framework.common.State;
import ia.framework.common.Action;
import ia.framework.common.ArgParse;

import ia.framework.recherche.TreeSearch;
import ia.framework.recherche.SearchProblem;
import ia.framework.recherche.SearchNode;

/**
 * Implémentation de l'algorithme de recherche en profondeur (Depth-First Search)
 *
 * DFS explore le plus profondément possible avant de revenir en arrière.
 * Utilise une pile LIFO pour la frontière.
 *
 * Proprietes :
 * - Complet : Non (peut boucler infiniment dans les graphes infinis)
 * - Optimal : Non (ne garantit pas le chemin le plus court)
 * - Complexite temps : O(b^m) ou b = facteur de branchement, m = profondeur max
 * - Complexite espace : O(b*m) - beaucoup plus econome que BFS
 */
public class DFS extends TreeSearch {

    /**
     * Crée un algorithme DFS
     * @param prob Le problème à résoudre
     * @param initial_state L'état initial
     */
    public DFS(SearchProblem prob, State initial_state) {
        super(prob, initial_state);
        // Utilise une LinkedList comme pile LIFO
        this.frontier = new LinkedList<SearchNode>();
    }

    /**
     * Résout le problème en utilisant la recherche en profondeur
     * @return true si une solution est trouvée, false sinon
     */
    @Override
    public boolean solve() {
        // 1. Créer un noeud correspondant à l'état initial
        SearchNode root_node = SearchNode.makeRootSearchNode(initial_state);

        // 2. Initialiser la frontière avec ce noeud (pile LIFO)
        this.frontier.clear();
        this.frontier.add(root_node);

        // 3. Initialiser l'ensemble des états visités à vide
        this.explored.clear();

        // 4. Tant que la frontière n'est pas vide
        while (!this.frontier.isEmpty()) {

            // 5. Retirer le DERNIER noeud de la frontière (LIFO - caractéristique de DFS)
            SearchNode cur_node = ((LinkedList<SearchNode>) this.frontier).removeLast();

            // 6. Si le noeud contient un état but
            State cur_state = cur_node.getState();
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
                System.out.print(cur_state + " (" + actions.size() + " actions) -> {");

            // 10. Pour chaque action possible
            for (Action a : actions) {
                // Créer le noeud enfant
                SearchNode child_node = SearchNode.makeChildSearchNode(problem, cur_node, a);
                State child_state = child_node.getState();

                if (ArgParse.DEBUG)
                    System.out.print("(" + a + ", " + child_state + ")");

                // 11. S'il n'est pas dans la frontière et si son état n'a pas été visité
                if (!frontier.contains(child_node) && !explored.contains(child_state)) {
                    // 12. L'insérer à la FIN de la frontière (sera retiré en premier = LIFO)
                    frontier.add(child_node);

                    if (ArgParse.DEBUG)
                        System.out.print("[A] ");
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
