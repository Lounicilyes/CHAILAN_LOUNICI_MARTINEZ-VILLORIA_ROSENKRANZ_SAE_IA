package MLP;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * represente une imagette issue de MNIST
 * - des donnees
 * - un label
 */
public class Imagette {

    /**
     * tableau de couleurs
     */
    int[][] tab;

    /**
     * taille imagette
     */
    private int lignes;
    private int colonnes;

    /**
     * etiquette de l'imagette
     */
    int chiffre;

    /**
     * constructeur
     * construit imagette a la bonne taille
     *
     * @param nLignes nombre de lignes
     * @param nCols   nombre de colonnes
     * @param label   etiquette de l'imagette
     */
    public Imagette(int nLignes, int nCols, int label) {
        // creation de la taille
        this.setLignes(nLignes);
        this.setColonnes(nCols);

        // ajoute le label
        this.chiffre = label;

        // creation du tableau
        tab = new int[nLignes][nCols];
    }

    /**
     * modifie une valeur de l'imagette
     *
     * @param ligne  ligne
     * @param col    colonne
     * @param valeur valeur du pixel
     */
    public void modifierValeur(int ligne, int col, int valeur) {
        this.tab[ligne][col] = valeur;
    }

    /**
     * recupere une valeur de l'imagette
     * @param l ligne
     * @param c colonne
     * @return couleur du pixel
     */
    public int getValeur(int l, int c) {
        return this.tab[l][c];
    }

    /**
     * recupere etiquette de l'imagette
     * @return
     */
    public int getLabel() {
        return this.chiffre;
    }

    // #######################################################################
    // GESTION DES IMAGES
    // #######################################################################

    /**
     * construit une image
     *
     * @return retourne image associee
     */
    public RenderedImage creerImage() {

        int tailleX = this.getColonnes();
        int tailleY = this.getLignes();
        BufferedImage bfImage = new BufferedImage(tailleX, tailleY, BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < tailleX; x++)
            for (int y = 0; y < tailleX; y++) {
                int value = this.tab[y][x];
                int rgb = new Color(value, value, value).getRGB();
                bfImage.setRGB(x, y, rgb);
            }

        return bfImage;
    }

    /**
     * sauve imagette cree
     *
     * @param nomFichier nom du fichier de sauvegarde
     * @throws IOException probleme sauvegarde
     */
    public void sauverImage(String nomFichier) throws IOException {
        // creation des repertoires
        String[] rep = nomFichier.split("/");
        String total = "";

        // itere pour creer repertoire et sous repertoires s'ils n'existent pas
        for (int i = 0; i < rep.length - 1; i++) {

            // construction du chemin relatif
            total = total + rep[0];
            File f = new File(total);

            // si le repertoire n'existe pas
            if (!f.exists())
                // le cree
                f.mkdir();
        }

        // creer l'imagette et la sauver dans le repertoire existant
        RenderedImage res = this.creerImage();
        ImageIO.write(res, "PNG", new File(nomFichier));
    }

    public int getLignes() {
        return lignes;
    }

    public void setLignes(int lignes) {
        this.lignes = lignes;
    }

    public int getColonnes() {
        return colonnes;
    }

    public void setColonnes(int colonnes) {
        this.colonnes = colonnes;
    }

}
