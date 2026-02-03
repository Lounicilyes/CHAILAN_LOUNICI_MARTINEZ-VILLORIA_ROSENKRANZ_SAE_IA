package MLP;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ChargementMNIST {

    /**
     * donne les noms de fichier, retourne un tableau d'imagettes
     * @param imageFile nom du fichier avec imagettes
     * @param labelFile nom du fichier avec les labels
     * @param max nombre de valeurs maximales a charger
     * @return tableau des imagettes avec leurs labels
     * @throws IOException probleme de lecture
     */
    public Imagette[] charger(String imageFile, String labelFile, int max) throws IOException {
        // ouverture du fichier image
        DataInputStream di = new DataInputStream(new FileInputStream(imageFile));
        // ouverture du fichier label
        DataInputStream dLabel = new DataInputStream(new FileInputStream(labelFile));

        // ##########################################
        // ouverture des images

        // lecture donnees initiales
        int magicNumber = di.readInt();
        if (magicNumber != 2051)
            throw new Error("pas le bon fichier = magic number" + magicNumber);

        // lecture nb image
        int nbImage = di.readInt();

        // lecture taille des imagettes
        int lignes = di.readInt();
        int cols = di.readInt();

        // ##########################################
        // ouverture des labels

        // lecture donnees initiales
        int magicNumberLabel = dLabel.readInt();
        if (magicNumberLabel != 2049)
            throw new Error("pas le bon fichier = magic number" + magicNumber);

        // lecture nb label
        int nbLabel = dLabel.readInt();

        // si different= erreur
        if (nbLabel != nbImage)
            throw new Error("pas le meme nombre d'elements");

        // ##########################################
        // lecture des imagettes

        // gere max d'images à charger (si max demandé est plus petit que nombre d'images)
        if ((max > 0)&&(max < nbImage)){
            nbImage = max;
        }

        // construction du tableau imagette
        Imagette[] tab = new Imagette[nbImage];

        // pour chaque imagette
        for (int idx = 0; idx < nbImage; idx++) {
            // lecture du label avec readUnsignedByte
            int label = dLabel.readUnsignedByte();

            // creation imagette
            Imagette imagette = new Imagette(lignes, cols, label);

            // construction imagette pixel par pixel (avec readUnsignedByte)
            for (int i = 0; i < lignes; i++) {
                for (int j = 0; j < cols; j++) {
                    int valeur = di.readUnsignedByte();
                    imagette.modifierValeur(i, j, valeur);
                }
            }

            // ajout au tableau d'imagettes
            tab[idx] = imagette;

            // Affichage de la barre de progression
            int percent = (int)(((idx + 1) * 100.0) / nbImage);
            int barLength = 40;
            int filled = (int)(barLength * (idx + 1) / (double)nbImage);
            StringBuilder bar = new StringBuilder();
            bar.append("[");
            for (int b = 0; b < barLength; b++) {
                if (b < filled) bar.append("#");
                else bar.append("-");
            }
            bar.append("] ");
            bar.append(percent).append("%");
            System.out.print("\rChargement des images : " + bar.toString());
        }
        System.out.println(); // Saut de ligne après la barre

        // fermeture fichier image
        // fermeture fichier label
        di.close();
        dLabel.close();

        // retourne tableau imagettes
        return tab;
    }


    static void main() throws IOException {
        ChargementMNIST c = new ChargementMNIST();

        Imagette[] imagettes = c.charger("C:\\Users\\cypri\\Documents\\Workspace\\S5\\Optimisation\\TP01_etudiant\\data\\train-images.idx3-ubyte", "C:\\Users\\cypri\\Documents\\Workspace\\S5\\Optimisation\\TP01_etudiant\\data\\train-labels.idx1-ubyte", 0);

        System.out.println("La premiere imagette est un " + imagettes[0].chiffre);
        System.out.println("La derniere imagette est un " + imagettes[imagettes.length-1].chiffre);

        imagettes[0].sauverImage("data/test0.png");
        imagettes[imagettes.length-1].sauverImage("data/test1.png");

        System.out.println("image sauver");

    }

}
