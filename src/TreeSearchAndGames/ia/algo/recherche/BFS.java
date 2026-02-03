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
 * Implémentation de l'algorithme de recherche en largeur (Breadth-First Search)
 *
 * BFS explore tous les nœuds à une profondeur donnée avant de passer
 * à la profondeur suivante. Utilise une file FIFO pour la frontière.
 *
 * Proprietes :
 * - Complet : Oui (si le facteur de branchement est fini)
 * - Optimal : Oui (si tous les couts sont egaux)
 * - Complexite temps : O(b^d) ou b = facteur de branchement, d = profondeur solution
 * - Complexite espace : O(b^d)
 */
public class BFS extends TreeSearch {

    /**
     * Crée un algorithme BFS
     * @param prob Le problème à résoudre
     * @param initial_state L'état initial
     */
    public BFS(SearchProblem prob, State initial_state) {
        super(prob, initial_state);
        // Utilise une LinkedList comme file FIFO
        this.frontier = new LinkedList<SearchNode>();
    }

    /**
     * Résout le problème en utilisant la recherche en largeur
     * @return true si une solution est trouvée, false sinon
     */
    @Override
    public boolean solve() {
        // 1. Créer un noeud correspondant à l'état initial
        SearchNode root_node = SearchNode.makeRootSearchNode(initial_state);

        // 2. Initialiser la frontière avec ce noeud (file FIFO)
        this.frontier.clear();
        this.frontier.add(root_node);

        // 3. Initialiser l'ensemble des états visités à vide
        this.explored.clear();

        // 4. Tant que la frontière n'est pas vide
        while (!this.frontier.isEmpty()) {

            // 5. Retirer le premier noeud de la frontière (FIFO - caractéristique de BFS)
            SearchNode cur_node = ((LinkedList<SearchNode>) this.frontier).removeFirst();

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
                    // 12. L'insérer à la FIN de la frontière (FIFO)
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
