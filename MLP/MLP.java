import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Interface definissant une fonction de transfert pour les neurones.
 * Une fonction de transfert permet de transformer la somme ponderee des entrees
 * en une valeur de sortie (generalement entre 0 et 1 ou -1 et 1).
 * 
 * Les implementations courantes sont Sigmoid et Tanh.
 * 
 * @see Sigmoid
 * @see Tanh
 */
interface TransferFunction {
    /**
     * Function de transfert
     * 
     * @param value entrée
     * @return sortie de la fonction sur l'entrée
     */
    public double evaluate(double value);

    /**
     * Dérivée de la fonction de tranfert
     * 
     * @param value entrée
     * @return sortie de la fonction dérivée sur l'entrée
     */
    public double evaluateDer(double value);
}

/**
 * Represente un neurone artificiel dans le reseau de neurones.
 * 
 * Un neurone possede :
 * - Une valeur de sortie (Value) calculee lors de la propagation avant
 * - Des poids (Weights) pour chaque connexion avec la couche precedente
 * - Un biais (Bias) qui permet d'ajuster le seuil d'activation
 * - Un delta (Delta) utilise lors de la retropropagation pour corriger les
 * poids
 * 
 * Les poids et le biais sont initialises aleatoirement lors de la creation du
 * neurone.
 */
class Neuron {
    // Valeur de sortie du neurone apres application de la fonction de transfert
    public double Value;

    // Tableau des poids : un poids pour chaque neurone de la couche precedente
    public double[] Weights;

    // Biais du neurone : permet de decaler le seuil d'activation
    public double Bias;

    // Delta : gradient d'erreur utilise pour la retropropagation
    public double Delta;

    /**
     * Constructeur d'un neurone.
     * Initialise les poids et le biais avec des valeurs aleatoires.
     * 
     * @param prevLayerSize nombre de neurones dans la couche precedente
     *                      (determine le nombre de poids a creer)
     */
    public Neuron(int prevLayerSize) {
        // On cree un tableau de poids, un pour chaque neurone de la couche d'avant
        Weights = new double[prevLayerSize];

        // Le biais est initialise aleatoirement entre 0 et 1
        Bias = Math.random();

        // Delta et Value sont initialises a des valeurs tres petites
        // pour eviter des problemes numeriques au debut
        Delta = Math.random() / 10000000000000.0;
        Value = Math.random() / 10000000000000.0;

        // On initialise chaque poids avec une petite valeur aleatoire
        // Division par le nombre de poids pour normaliser
        for (int i = 0; i < Weights.length; i++)
            Weights[i] = Math.random() / Weights.length;
    }
}

/**
 * Represente une couche de neurones dans le reseau.
 * 
 * Une couche contient plusieurs neurones qui effectuent tous le meme type
 * de calcul (somme ponderee + fonction de transfert).
 * 
 * Le reseau MLP est compose de plusieurs couches :
 * - Couche d'entree : recoit les donnees brutes
 * - Couches cachees : effectuent les transformations intermediaires
 * - Couche de sortie : produit le resultat final
 */
class Layer {
    // Tableau contenant tous les neurones de cette couche
    public Neuron Neurons[];

    // Nombre de neurones dans cette couche
    public int Length;

    /**
     * Couche de Neurones
     * 
     * @param l    Taille de la couche (nombre de neurones a creer)
     * @param prev Taille de la couche précédente (pour dimensionner les poids)
     */
    public Layer(int l, int prev) {
        Length = l;
        Neurons = new Neuron[l];

        // On cree chaque neurone de la couche
        // Chaque neurone aura 'prev' poids (un par neurone de la couche precedente)
        for (int j = 0; j < Length; j++)
            Neurons[j] = new Neuron(prev);
    }
}

/**
 * Perceptron Multi-Couches (Multi-Layer Perceptron).
 * 
 * Cette classe implemente un reseau de neurones artificiel capable d'apprendre
 * a partir d'exemples via l'algorithme de retropropagation du gradient.
 * 
 * Le MLP est compose de :
 * - Une couche d'entree (recoit les donnees)
 * - Une ou plusieurs couches cachees (effectuent les calculs)
 * - Une couche de sortie (produit la prediction)
 * 
 * Processus d'apprentissage :
 * 1. Propagation avant (execute) : calcule la sortie pour une entree donnee
 * 2. Retropropagation (backPropagate) : ajuste les poids en fonction de
 * l'erreur
 * 
 * Exemple d'utilisation :
 * 
 * <pre>
 * // Creer un MLP avec 2 entrees, 4 neurones caches, 1 sortie
 * int[] architecture = { 2, 4, 1 };
 * MLP mlp = new MLP(architecture, 0.5, new Sigmoid());
 * 
 * // Entrainer le reseau
 * for (int i = 0; i < 10000; i++) {
 *     mlp.backPropagate(entree, sortieAttendue);
 * }
 * 
 * // Utiliser le reseau entraine
 * double[] resultat = mlp.execute(nouvelleEntree);
 * </pre>
 * 
 * @author Equipe SAE IA
 * @version 1.0
 */
public class MLP {
    // Taux d'apprentissage : controle la vitesse d'ajustement des poids
    // Une valeur trop elevee peut empecher la convergence
    // Une valeur trop faible ralentit l'apprentissage
    protected double fLearningRate = 0.6;

    // Tableau contenant toutes les couches du reseau
    protected Layer[] fLayers;

    // Fonction de transfert utilisee par tous les neurones
    protected TransferFunction fTransferFunction;

    /**
     * Constructeur du MLP.
     * Cree et initialise toutes les couches du reseau.
     * 
     * @param layers       tableau indiquant le nb de neurones par couche
     *                     Ex: {2, 4, 1} = 2 entrees, 4 caches, 1 sortie
     * @param learningRate taux d'apprentissage (entre 0 et 1, typiquement 0.1 a
     *                     0.6)
     * @param fun          fonction de transfert (Sigmoid ou Tanh)
     */
    public MLP(int[] layers, double learningRate, TransferFunction fun) {
        fLearningRate = learningRate;
        fTransferFunction = fun;

        // On cree autant de couches que demande dans le tableau
        fLayers = new Layer[layers.length];
        for (int i = 0; i < layers.length; i++) {
            if (i != 0) {
                // Couche normale : doit connaitre la taille de la couche precedente
                fLayers[i] = new Layer(layers[i], layers[i - 1]);
            } else {
                // Couche d'entree : pas de couche precedente (donc 0 poids)
                fLayers[i] = new Layer(layers[i], 0);
            }
        }
    }

    /**
     * Propage une entree a travers le reseau et retourne la sortie.
     * C'est la phase de "prediction" du reseau.
     * 
     * Fonctionnement :
     * 1. Les valeurs d'entree sont placees dans la premiere couche
     * 2. Pour chaque couche suivante, on calcule la somme ponderee des entrees
     * 3. On applique la fonction de transfert sur cette somme
     * 4. La sortie de la derniere couche est retournee
     * 
     * @param input l'entrée à tester (tableau de valeurs numeriques)
     * @return résultat de l'exécution (tableau des valeurs de sortie)
     */
    public double[] execute(double[] input) {
        int i, j, k;
        double new_value;

        // Tableau pour stocker la sortie (taille = nb neurones derniere couche)
        double output[] = new double[fLayers[fLayers.length - 1].Length];

        // ETAPE 1 : Placer les valeurs d'entree dans la premiere couche
        // Chaque neurone de la couche d'entree recoit une valeur du tableau input
        for (i = 0; i < fLayers[0].Length; i++) {
            fLayers[0].Neurons[i].Value = input[i];
        }

        // ETAPE 2 : Propager les valeurs a travers les couches cachees et de sortie
        // On commence a k=1 car la couche 0 est deja remplie avec les entrees
        for (k = 1; k < fLayers.length; k++) {
            // Pour chaque neurone de la couche courante
            for (i = 0; i < fLayers[k].Length; i++) {
                new_value = 0.0;

                // Calculer la somme ponderee : sum(poids * valeur)
                // On parcourt tous les neurones de la couche precedente
                for (j = 0; j < fLayers[k - 1].Length; j++)
                    new_value += fLayers[k].Neurons[i].Weights[j] * fLayers[k - 1].Neurons[j].Value;

                // Soustraire le biais (equivalent a ajouter un seuil)
                new_value -= fLayers[k].Neurons[i].Bias;

                // Appliquer la fonction de transfert (sigmoid ou tanh)
                // Cela "ecrase" la valeur entre 0 et 1 (ou -1 et 1 pour tanh)
                fLayers[k].Neurons[i].Value = fTransferFunction.evaluate(new_value);
            }
        }

        // ETAPE 3 : Recuperer les valeurs de sortie de la derniere couche
        for (i = 0; i < fLayers[fLayers.length - 1].Length; i++) {
            output[i] = fLayers[fLayers.length - 1].Neurons[i].Value;
        }
        return output;
    }

    /**
     * Effectue la retropropagation du gradient pour ajuster les poids.
     * C'est l'algorithme d'apprentissage du reseau.
     * 
     * Fonctionnement :
     * 1. Execute le reseau sur l'entree pour obtenir la sortie actuelle
     * 2. Calcule l'erreur entre sortie obtenue et sortie souhaitee
     * 3. Propage l'erreur de la sortie vers l'entree (retropropagation)
     * 4. Ajuste les poids en fonction de cette erreur
     * 
     * Plus l'erreur est grande, plus les poids sont modifies.
     * Plus le taux d'apprentissage est eleve, plus les modifications sont
     * importantes.
     * 
     * @param input  L'entrée courante (donnee d'entrainement)
     * @param output Sortie souhaitée (apprentissage supervisé !)
     * @return Erreur moyenne : difference entre sortie calculée et sortie souhaitée
     */
    public double backPropagate(double[] input, double[] output) {
        // D'abord on execute le reseau pour voir ce qu'il predit actuellement
        double new_output[] = execute(input);
        double error;
        int i, j, k;

        // === ETAPE 1 : Calcul du delta pour la couche de sortie ===
        // Le delta represente "de combien on s'est trompe"
        for (i = 0; i < fLayers[fLayers.length - 1].Length; i++) {
            // Erreur = ce qu'on voulait - ce qu'on a obtenu
            error = output[i] - new_output[i];

            // Delta = erreur * derivee de la fonction de transfert
            // La derivee nous dit dans quelle direction ajuster les poids
            fLayers[fLayers.length - 1].Neurons[i].Delta = error * fTransferFunction.evaluateDer(new_output[i]);
        }

        // === ETAPE 2 : Retropropager l'erreur vers les couches cachees ===
        // On remonte de la derniere couche vers la premiere
        for (k = fLayers.length - 2; k >= 0; k--) {

            // Calcul du delta pour chaque neurone de la couche courante
            // L'erreur d'un neurone depend des deltas de la couche suivante
            for (i = 0; i < fLayers[k].Length; i++) {
                error = 0.0;

                // On somme les contributions de tous les neurones de la couche suivante
                // Chaque contribution = delta du neurone * poids de la connexion
                for (j = 0; j < fLayers[k + 1].Length; j++)
                    error += fLayers[k + 1].Neurons[j].Delta * fLayers[k + 1].Neurons[j].Weights[i];

                // On applique la derivee de la fonction de transfert
                fLayers[k].Neurons[i].Delta = error * fTransferFunction.evaluateDer(fLayers[k].Neurons[i].Value);
            }

            // === ETAPE 3 : Mise a jour des poids de la couche suivante ===
            // Formule : nouveau_poids = ancien_poids + taux * delta * valeur_entree
            for (i = 0; i < fLayers[k + 1].Length; i++) {
                for (j = 0; j < fLayers[k].Length; j++)
                    fLayers[k + 1].Neurons[i].Weights[j] += fLearningRate * fLayers[k + 1].Neurons[i].Delta *
                            fLayers[k].Neurons[j].Value;

                // On met aussi a jour le biais
                fLayers[k + 1].Neurons[i].Bias -= fLearningRate * fLayers[k + 1].Neurons[i].Delta;
            }
        }

        // === ETAPE 4 : Calcul de l'erreur moyenne pour le suivi ===
        // Permet de verifier si le reseau apprend bien (erreur doit diminuer)
        error = 0.0;
        for (i = 0; i < output.length; i++) {
            error += Math.abs(new_output[i] - output[i]);
        }
        // On retourne l'erreur moyenne sur tous les neurones de sortie
        error = error / output.length;
        return error;
    }

    /**
     * Retourne le taux d'apprentissage actuel.
     * 
     * @return le taux d'apprentissage (entre 0 et 1)
     */
    public double getLearningRate() {
        return fLearningRate;
    }

    /**
     * Modifie le taux d'apprentissage.
     * Un taux eleve accelere l'apprentissage mais peut empecher la convergence.
     * Un taux faible est plus stable mais plus lent.
     *
     * @param rate nouveau taux d'apprentissage (entre 0 et 1)
     */
    public void setLearningRate(double rate) {
        fLearningRate = rate;
    }

    /**
     * Change la fonction de transfert du reseau.
     * Attention : changer la fonction en cours d'apprentissage peut perturber le
     * reseau.
     * 
     * @param fun nouvelle fonction de tranfert (Sigmoid ou Tanh)
     */
    public void setTransferFunction(TransferFunction fun) {
        fTransferFunction = fun;
    }

    /**
     * Retourne la taille de la couche d'entree.
     * Correspond au nombre d'entrees que le reseau attend.
     * 
     * @return nombre de neurones dans la couche d'entree
     */
    public int getInputLayerSize() {
        return fLayers[0].Length;
    }

    /**
     * Retourne la taille de la couche de sortie.
     * Correspond au nombre de valeurs que le reseau produit.
     * 
     * @return nombre de neurones dans la couche de sortie
     */
    public int getOutputLayerSize() {
        return fLayers[fLayers.length - 1].Length;
    }
}
