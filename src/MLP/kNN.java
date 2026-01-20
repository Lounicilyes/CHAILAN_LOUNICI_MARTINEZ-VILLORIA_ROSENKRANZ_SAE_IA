package MLP;

import java.util.*;

/**
 * Algorithme k-plus-proches voisins (k-NN)
 */
public class kNN extends AlgoClassification {
    private int k;

    public kNN(Imagette[] donneesEntrainement, int k) {
        super(donneesEntrainement);
        this.k = k;
    }

    private double calculerDistance(Imagette img, Imagette trainImg) {
        double sum = 0.0;
        for (int i = 0; i < img.getLignes(); i++) {
            for (int j = 0; j < img.getColonnes(); j++) {
                int diff = img.getValeur(i, j) - trainImg.getValeur(i, j);
                sum += diff * diff;
            }
        }
        return Math.sqrt(sum);
    }

    @Override
    public int predire(Imagette imgTest) {
        PriorityQueue<ImagetteDistance> voisins = new PriorityQueue<>(k,
                Comparator.comparingDouble(v -> -v.distance));

        for (Imagette imgTrain : donneesEntrainement) {
            double dist = calculerDistance(imgTrain, imgTest);

            if (voisins.size() < k) {
                voisins.add(new ImagetteDistance(imgTrain, dist));
            } else if (dist < voisins.peek().distance) {
                voisins.poll();
                voisins.add(new ImagetteDistance(imgTrain, dist));
            }
        }

        Map<Integer, Integer> mapFrequence = new HashMap<>();
        for (ImagetteDistance v : voisins) {
            int etiq = v.imagette.getLabel();
            mapFrequence.put(etiq, mapFrequence.getOrDefault(etiq, 0) + 1);
        }

        int maxEtiquette = -1;
        int maxCount = -1;
        for (Map.Entry<Integer, Integer> entry : mapFrequence.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxEtiquette = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return maxEtiquette;
    }

    private static class ImagetteDistance {
        Imagette imagette;
        double distance;

        ImagetteDistance(Imagette img, double dist) {
            this.imagette = img;
            this.distance = dist;
        }
    }
}
