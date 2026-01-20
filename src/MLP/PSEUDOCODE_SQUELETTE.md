# Pseudo-code : Squelette d'utilisation de la bibliothèque MLP

## 1. Configuration du Perceptron Multi-Couches

```
DÉBUT Programme
    // CONFIGURATION DU MLP
    
    // Définir l'architecture du réseau
    architecture ← [2, 4, 1]          // 2 entrées, 4 neurones cachés, 1 sortie
    
    // Définir le taux d'apprentissage
    tauxApprentissage ← 0.5
    
    // Choisir la fonction d'activation
    fonctionActivation ← nouvelle Sigmoide()   // ou nouvelle TangenteHyperbolique()
    
    // APPEL : Constructeur MLP(int[], double, TransferFunction)
    // BUT   : Créer et initialiser le réseau avec l'architecture choisie
    // ENTRÉE: architecture, tauxApprentissage, fonctionActivation
    // SORTIE: objet MLP prêt à être entraîné
    
    mlp ← nouveau MLP(architecture, tauxApprentissage, fonctionActivation)

FIN Configuration
```

---

## 2. Spécialisation par Apprentissage

```
DÉBUT Apprentissage
    // PRÉPARATION DES DONNÉES D'ENTRAÎNEMENT
    
    // Exemples d'apprentissage : paires (entrée, sortie attendue)
    exemplesEntrées ← [[0,0], [0,1], [1,0], [1,1]]
    exemplesSorties ← [[0],   [1],   [1],   [0]]      // Exemple : XOR
    
    // Paramètres d'apprentissage
    nombreEpoquesMax ← 10000
    seuilErreur ← 0.01
    
    // BOUCLE D'APPRENTISSAGE
    
    POUR epoque DE 1 À nombreEpoquesMax FAIRE
        
        erreurTotale ← 0
        
        POUR i DE 0 À (nombre d'exemples - 1) FAIRE
            
            // APPEL : backPropagate(double[], double[])
            // BUT   : Ajuster les poids du réseau pour un exemple
            // ENTRÉE: exemplesEntrées[i] (données), exemplesSorties[i] (label)
            // SORTIE: erreur moyenne pour cet exemple
            
            erreur ← mlp.backPropagate(exemplesEntrées[i], exemplesSorties[i])
            erreurTotale ← erreurTotale + erreur
            
        FIN POUR
        
        erreurMoyenne ← erreurTotale / (nombre d'exemples)
        
        // Condition d'arrêt : erreur suffisamment faible
        SI erreurMoyenne < seuilErreur ALORS
            // Convergence atteinte, sortir de la boucle
            SORTIR DE LA BOUCLE
        FIN SI
        
    FIN POUR

FIN Apprentissage
```

---

## 3. Utilisation du Perceptron Spécialisé (Test)

```
DÉBUT Test
    // TEST SUR LES EXEMPLES D'APPRENTISSAGE OU NOUVEAUX EXEMPLES
   
    exemplesTest ← [[0,0], [0,1], [1,0], [1,1]]
    sortiesAttendues ← [[0], [1], [1], [0]]
    
    nombreReussis ← 0
    
    POUR i DE 0 À (nombre d'exemples test - 1) FAIRE
        
        // APPEL : execute(double[])
        // BUT   : Obtenir la prédiction du réseau pour une entrée
        // ENTRÉE: exemplesTest[i] (données à classifier)
        // SORTIE: tableau double[] contenant les valeurs de sortie
        sortieCalculee ← mlp.execute(exemplesTest[i])
        
        // Évaluer si la prédiction est correcte (seuillage à 0.5)
        prediction ← SI sortieCalculee[0] > 0.5 ALORS 1 SINON 0
        
        // Afficher le résultat
        COMPARER sortieCalculee AVEC sortiesAttendues[i] 
        
        SI prediction = sortiesAttendues[i][0] ALORS
            nombreReussis ← nombreReussis + 1
        FIN SI
        
    FIN POUR
    
    tauxReussite ← nombreReussis / (nombre d'exemples test) × 100
    AFFICHER "Taux de réussite:", tauxReussite, "%"

FIN Test
```

---

## 4. Résumé des Appels à la Bibliothèque

| Méthode | But | Entrées | Sorties |
|---------|-----|---------|---------|
| `MLP(int[], double, TransferFunction)` | Créer le réseau | Architecture, taux, fonction | Objet MLP |
| `backPropagate(double[], double[])` | Entraîner (1 exemple) | Entrée + sortie attendue | Erreur (double) |
| `execute(double[])` | Prédire | Entrée à classifier | Sortie calculée (double[]) |

---

## 5. Flux des Données

```
                    ┌─────────────────────────────────────────┐
                    │           CONFIGURATION                 │
                    │  architecture, tauxApprentissage,       │
                    │  fonctionActivation                     │
                    └───────────────┬─────────────────────────┘
                                    │
                                    ▼
                    ┌─────────────────────────────────────────┐
                    │         new MLP(...)                    │
                    │    → Création du réseau                 │
                    └───────────────┬─────────────────────────┘
                                    │
          ┌─────────────────────────┴─────────────────────────┐
          │                                                   │
          ▼                                                   ▼
┌─────────────────────┐                           ┌─────────────────────┐
│   APPRENTISSAGE     │                           │       TEST          │
│                     │                           │                     │
│  POUR chaque exemple│                           │  POUR chaque entrée │
│    backPropagate(   │                           │    execute(entrée)  │
│      entrée,        │                           │    → double[]       │
│      sortieAttendue │                           │                     │
│    )                │                           │  Comparer avec      │
│    → erreur         │                           │  sortie attendue    │
│  FIN POUR           │                           │  FIN POUR           │
└─────────────────────┘                           └─────────────────────┘
```
