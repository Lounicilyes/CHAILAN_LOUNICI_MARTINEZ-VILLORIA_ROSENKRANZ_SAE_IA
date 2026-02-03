package ia.problemes;


/**
 * Représente un état d'un jeu MnK avec une fonction d'évaluation améliorée
 *
 * Cette fonction d'évaluation est inspirée de l'article "Le morpion, simple comme
 * un jeu d'enfant" et compte les lignes potentielles de victoire plutôt que
 * simplement le nombre de pions.
 *
 * Principe :
 * - Une ligne "potentiellement gagnante" est une ligne qui ne contient aucun pion adverse
 * - On pondère les lignes par le nombre de pions déjà placés dessus
 * - Score = (Potentiel MAX) - (Potentiel MIN)
 */
public class MnkGameStateEval extends AbstractMnkGameState {

    // Poids pour les différentes configurations
    // Plus une ligne contient de pions, plus elle est valuable
    private static final double[] WEIGHTS = {0, 1, 10, 100, 1000, 10000, 100000};

    /**
     * Construire une grille vide de la bonne taille
     *
     * @param r nombre de lignes
     * @param c nombre de colonnes
     * @param s nombre de pions à aligner pour gagner
     */
    public MnkGameStateEval(int r, int c, int s) {
        super(r, c, s);
    }

    @Override
    public MnkGameStateEval cloneState() {
        MnkGameStateEval new_s = new MnkGameStateEval(this.rows, this.cols, this.streak);
        new_s.board = this.board.clone();
        new_s.player_to_move = player_to_move;
        new_s.game_value = game_value;
        if (this.last_action != null)
            new_s.last_action = this.last_action.clone();
        for (Pair p : this.winning_move)
            new_s.winning_move.add(p.clone());
        return new_s;
    }

    /**
     * Fonction d'évaluation améliorée basée sur le potentiel de victoire
     *
     * Pour chaque ligne possible (horizontale, verticale, diagonale) :
     * - Si elle contient des pions des deux joueurs : valeur = 0 (ligne morte)
     * - Sinon : valeur = poids[nombre_de_pions_du_joueur]
     *
     * @return Score positif favorable à X (MAX), négatif favorable à O (MIN)
     */
    @Override
    protected double evaluationFunction() {
        double scoreX = 0; // Score pour le joueur X (MAX)
        double scoreO = 0; // Score pour le joueur O (MIN)

        // Évaluer toutes les lignes horizontales possibles
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols - streak; c++) {
                int[] count = countInLine(r, c, 0, 1);
                scoreX += evaluateLine(count[0], count[1]);
                scoreO += evaluateLine(count[1], count[0]);
            }
        }

        // Évaluer toutes les lignes verticales possibles
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r <= rows - streak; r++) {
                int[] count = countInLine(r, c, 1, 0);
                scoreX += evaluateLine(count[0], count[1]);
                scoreO += evaluateLine(count[1], count[0]);
            }
        }

        // Évaluer toutes les diagonales (45 degrés)
        for (int r = 0; r <= rows - streak; r++) {
            for (int c = 0; c <= cols - streak; c++) {
                int[] count = countInLine(r, c, 1, 1);
                scoreX += evaluateLine(count[0], count[1]);
                scoreO += evaluateLine(count[1], count[0]);
            }
        }

        // Évaluer toutes les diagonales (-45 degrés)
        for (int r = streak - 1; r < rows; r++) {
            for (int c = 0; c <= cols - streak; c++) {
                int[] count = countInLine(r, c, -1, 1);
                scoreX += evaluateLine(count[0], count[1]);
                scoreO += evaluateLine(count[1], count[0]);
            }
        }

        return scoreX - scoreO;
    }

    /**
     * Compte le nombre de X et de O dans une ligne de longueur 'streak'
     *
     * @param startRow Ligne de départ
     * @param startCol Colonne de départ
     * @param deltaRow Incrément de ligne
     * @param deltaCol Incrément de colonne
     * @return Tableau [countX, countO]
     */
    private int[] countInLine(int startRow, int startCol, int deltaRow, int deltaCol) {
        int countX = 0;
        int countO = 0;

        for (int k = 0; k < streak; k++) {
            char piece = getValueAt(startRow + k * deltaRow, startCol + k * deltaCol);
            if (piece == X) {
                countX++;
            } else if (piece == O) {
                countO++;
            }
        }

        return new int[]{countX, countO};
    }

    /**
     * Évalue une ligne pour un joueur donné
     *
     * @param playerCount Nombre de pions du joueur dans la ligne
     * @param opponentCount Nombre de pions de l'adversaire dans la ligne
     * @return Score de la ligne (0 si ligne morte)
     */
    private double evaluateLine(int playerCount, int opponentCount) {
        // Si l'adversaire a des pions dans cette ligne, elle est "morte" pour nous
        if (opponentCount > 0) {
            return 0;
        }

        // La ligne est encore gagnante potentiellement
        // Plus on a de pions, plus la ligne a de valeur
        if (playerCount < WEIGHTS.length) {
            return WEIGHTS[playerCount];
        }
        return WEIGHTS[WEIGHTS.length - 1];
    }
}
