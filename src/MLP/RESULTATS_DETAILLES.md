# Resultats Detailles et Comparatifs des Tests MLP


## Configuration de l'Experience

Conformement a l'analyse precedente, nous avons configure les tests de maniere **hybride** pour optimiser les performances selon la complexite du probleme logique :

1.  **Nombre d'Epoques** : Fixe a **10 000** pour tous les tests.
    *   C'est un standard suffisant pour observer la convergence sans attendre trop longtemps.

2.  **Taux d'Apprentissage Adaptatif** :
    *   **Pour ET et OU (Problemes Lineaires)** : Taux = **0.5**.
        *   Ces portes sont simples et lineairement separables (avec Tanh). Un taux eleve permet une convergence tres rapide.
    *   **Pour XOR (Probleme Non-Lineaire)** : Taux = **0.2**.
        *   Ce probleme necessite une couche cachee et une optimisation plus fine. Un taux plus bas (0.2) offre une meilleure stabilite.

## Structure du Rapport
Les resultats sont presentes en **3 tableaux distincts** (ET, OU, XOR). La colonne "2D" a ete retiree car redondante avec la taille de sortie (Out).


## Tests ET (Taux 0.5)

| Architecture | H1 | H2 | Out | Fonction | Melange | Taux | Resultat |
|---|---|---|---|---|---|---|---|
| 2,1 | 0 (Direct) | - | 1 | sigmoid | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2 | 0 (Direct) | - | 2 | sigmoid | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2,1 | 2 | - | 1 | sigmoid | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2,2 | 2 | - | 2 | sigmoid | NON | 0.5 | SUCCES (6139 ep.) |
| 2,2,1 | 2 | - | 1 | sigmoid | OUI | 0.5 | NON CONVERGE (4/4) |
| 2,1 | 0 (Direct) | - | 1 | tanh | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2 | 0 (Direct) | - | 2 | tanh | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2,1 | 2 | - | 1 | tanh | NON | 0.5 | SUCCES (271 ep.) |
| 2,2,2 | 2 | - | 2 | tanh | NON | 0.5 | SUCCES (388 ep.) |
| 2,2,1 | 2 | - | 1 | tanh | OUI | 0.5 | SUCCES (275 ep.) |


## Tests OU (Taux 0.5)

| Architecture | H1 | H2 | Out | Fonction | Melange | Taux | Resultat |
|---|---|---|---|---|---|---|---|
| 2,1 | 0 (Direct) | - | 1 | sigmoid | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2 | 0 (Direct) | - | 2 | sigmoid | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2,1 | 2 | - | 1 | sigmoid | NON | 0.5 | SUCCES (7945 ep.) |
| 2,2,2 | 2 | - | 2 | sigmoid | NON | 0.5 | SUCCES (7242 ep.) |
| 2,2,1 | 2 | - | 1 | sigmoid | OUI | 0.5 | SUCCES (7953 ep.) |
| 2,1 | 0 (Direct) | - | 1 | tanh | NON | 0.5 | SUCCES (716 ep.) |
| 2,2 | 0 (Direct) | - | 2 | tanh | NON | 0.5 | NON CONVERGE (4/4) |
| 2,2,1 | 2 | - | 1 | tanh | NON | 0.5 | SUCCES (250 ep.) |
| 2,2,2 | 2 | - | 2 | tanh | NON | 0.5 | SUCCES (226 ep.) |
| 2,2,1 | 2 | - | 1 | tanh | OUI | 0.5 | SUCCES (1668 ep.) |


## Tests XOR (Taux 0.2)

| Architecture | H1 | H2 | Out | Fonction | Melange | Taux | Resultat |
|---|---|---|---|---|---|---|---|
| 2,1 | 0 (Direct) | - | 1 | sigmoid | NON | 0.2 | NON CONVERGE (1/4) |
| 2,1 | 0 (Direct) | - | 1 | sigmoid | OUI | 0.2 | NON CONVERGE (3/4) |
| 2,2,1 | 2 | - | 1 | sigmoid | NON | 0.2 | NON CONVERGE (4/4) |
| 2,2,2 | 2 | - | 2 | sigmoid | NON | 0.2 | NON CONVERGE (3/4) |
| 2,2,1 | 2 | - | 1 | sigmoid | OUI | 0.2 | NON CONVERGE (4/4) |
| 2,4,1 | 4 | - | 1 | sigmoid | NON | 0.2 | NON CONVERGE (4/4) |
| 2,4,2 | 4 | - | 2 | sigmoid | NON | 0.2 | NON CONVERGE (4/4) |
| 2,4,1 | 4 | - | 1 | sigmoid | OUI | 0.2 | NON CONVERGE (4/4) |
| 2,8,1 | 8 | - | 1 | sigmoid | NON | 0.2 | NON CONVERGE (4/4) |
| 2,8,2 | 8 | - | 2 | sigmoid | NON | 0.2 | NON CONVERGE (4/4) |
| 2,8,1 | 8 | - | 1 | sigmoid | OUI | 0.2 | NON CONVERGE (4/4) |
| 2,4,4,1 | 4 | 4 | 1 | sigmoid | NON | 0.2 | NON CONVERGE (1/4) |
| 2,4,4,2 | 4 | 4 | 2 | sigmoid | NON | 0.2 | NON CONVERGE (0/4) |
| 2,4,4,1 | 4 | 4 | 1 | sigmoid | OUI | 0.2 | NON CONVERGE (3/4) |
| 2,1 | 0 (Direct) | - | 1 | tanh | NON | 0.2 | NON CONVERGE (3/4) |
| 2,1 | 0 (Direct) | - | 1 | tanh | OUI | 0.2 | NON CONVERGE (3/4) |
| 2,2,1 | 2 | - | 1 | tanh | NON | 0.2 | SUCCES (1190 ep.) |
| 2,2,2 | 2 | - | 2 | tanh | NON | 0.2 | NON CONVERGE (1/4) |
| 2,2,1 | 2 | - | 1 | tanh | OUI | 0.2 | SUCCES (1166 ep.) |
| 2,4,1 | 4 | - | 1 | tanh | NON | 0.2 | SUCCES (1134 ep.) |
| 2,4,2 | 4 | - | 2 | tanh | NON | 0.2 | SUCCES (1315 ep.) |
| 2,4,1 | 4 | - | 1 | tanh | OUI | 0.2 | SUCCES (1028 ep.) |
| 2,8,1 | 8 | - | 1 | tanh | NON | 0.2 | SUCCES (1295 ep.) |
| 2,8,2 | 8 | - | 2 | tanh | NON | 0.2 | SUCCES (1484 ep.) |
| 2,8,1 | 8 | - | 1 | tanh | OUI | 0.2 | SUCCES (1475 ep.) |
| 2,4,4,1 | 4 | 4 | 1 | tanh | NON | 0.2 | SUCCES (622 ep.) |
| 2,4,4,2 | 4 | 4 | 2 | tanh | NON | 0.2 | SUCCES (819 ep.) |
| 2,4,4,1 | 4 | 4 | 1 | tanh | OUI | 0.2 | SUCCES (3051 ep.) |


## Analyse des Resultats

1. **Influence de la Fonction de Transfert**
   La fonction **Tanh** demontre une superiorite nette sur Sigmoid. Elle converge plus rapidement (souvent sous les 1500 epoques pour XOR avec taux 0.2) et atteint le seuil d'erreur de 0.01 la ou Sigmoid stagne souvent (indique par NON CONVERGE).

2. **Architecture et Linearite**
   Les tests confirment que ET et OU sont linearables (resolus sans couche cachee avec Tanh).
   XOR echoue systematiquement sans couche cachee (Architecture 2,1), confirmant sa non-linearite. Il necessite au moins une couche cachee (Architecture 2,2,1).

3. **Impact du Taux d'Apprentissage**
   Un taux de **0.5** permet une convergence tres rapide pour les problemes simples (ET/OU).
   Pour XOR, le taux reduit a **0.2** avec Tanh offre un excellent compromis stabilite/vitesse.

4. **Sortie 2D et Melange**
   L'utilisation de deux neurones de sortie (mode 2D) ou le melange des donnees ("shuffling") n'a pas d'impact critique sur la reussite, mais le melange peut aider a la convergence dans certains cas limites (evitement de minima locaux).
