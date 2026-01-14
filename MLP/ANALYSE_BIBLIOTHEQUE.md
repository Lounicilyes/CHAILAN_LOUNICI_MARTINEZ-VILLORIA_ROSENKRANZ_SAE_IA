# Analyse de la Bibliothèque MLP

## 1. Interface `TransferFunction`

**Rôle** : Définit le contrat pour les fonctions d'activation du réseau.

| Méthode | Entrée | Sortie | Description |
|---------|--------|--------|-------------|
| `evaluate(double value)` | `double` - valeur brute | `double` - valeur activée | Applique la fonction de transfert |
| `evaluateDer(double value)` | `double` - **résultat de σ(x)** | `double` - dérivée | Calcule la dérivée (⚠️ reçoit σ(x), pas x) |

---

## 2. Constructeur `MLP`

```java
public MLP(int[] layers, double learningRate, TransferFunction fun)
```

### Paramètres d'entrée

| Paramètre | Type | Description | Exemple |
|-----------|------|-------------|---------|
| `layers` | `int[]` | Architecture du réseau (neurones par couche) | `{2, 4, 1}` = 2 entrées, 4 cachés, 1 sortie |
| `learningRate` | `double` | Taux d'apprentissage (η) | `0.5` |
| `fun` | `TransferFunction` | Fonction d'activation | `new Sigmoid()` |

### Rôle
- Initialise l'architecture complète du réseau
- Crée les couches avec leurs neurones
- Initialise les poids aléatoirement (normalisés par la taille de la couche précédente)
- Initialise les biais aléatoirement

### Sortie
- Aucune (constructeur) - crée l'objet MLP configuré

---

## 3. Méthode `execute()`

```java
public double[] execute(double[] input)
```

### Rôle
**Propagation avant** (forward propagation) : calcule la sortie du réseau pour une entrée donnée.

### Entrée

| Paramètre | Type | Contrainte |
|-----------|------|------------|
| `input` | `double[]` | Taille = nombre de neurones de la couche d'entrée |

### Algorithme
1. Copie les valeurs d'entrée dans la première couche
2. Pour chaque couche cachée et sortie :
   - Calcule la somme pondérée : `Σ(poids × valeur_précédente) - biais`
   - Applique la fonction de transfert : `σ(somme)`
3. Retourne les valeurs de la couche de sortie

### Sortie

| Type | Description |
|------|-------------|
| `double[]` | Valeurs des neurones de sortie (taille = couche de sortie) |

---

## 4. Méthode `backPropagate()`

```java
public double backPropagate(double[] input, double[] output)
```

### Rôle
**Rétropropagation du gradient** : ajuste les poids pour minimiser l'erreur entre sortie calculée et sortie souhaitée.

### Entrées

| Paramètre | Type | Description |
|-----------|------|-------------|
| `input` | `double[]` | Données d'entrée (exemple d'apprentissage) |
| `output` | `double[]` | Sortie souhaitée (étiquette/label) |

### Algorithme
1. **Forward pass** : exécute `execute(input)` pour obtenir la sortie actuelle
2. **Calcul des deltas de sortie** :
   - `erreur = sortie_souhaitée - sortie_calculée`
   - `delta = erreur × σ'(sortie_calculée)`
3. **Rétropropagation des deltas** (couches cachées) :
   - `erreur = Σ(delta_suivant × poids)`
   - `delta = erreur × σ'(valeur_neurone)`
4. **Mise à jour des poids** :
   - `poids += η × delta × valeur_précédente`
   - `biais -= η × delta`

### Sortie

| Type | Description |
|------|-------------|
| `double` | Erreur moyenne absolue : `Σ|sortie_souhaitée - sortie_calculée| / nb_sorties` |

---

## 5. Méthodes Auxiliaires

| Méthode | Type retour | Description |
|---------|-------------|-------------|
| `getLearningRate()` | `double` | Retourne le taux d'apprentissage |
| `setLearningRate(double)` | `void` | Modifie le taux d'apprentissage |
| `setTransferFunction(TransferFunction)` | `void` | Change la fonction d'activation |
| `getInputLayerSize()` | `int` | Taille de la couche d'entrée |
| `getOutputLayerSize()` | `int` | Taille de la couche de sortie |

---

## 6. Résumé des Types

### Entrées transmises à la bibliothèque

| Élément | Type | Transmission |
|---------|------|--------------|
| Architecture | `int[]` | Constructeur |
| Taux d'apprentissage | `double` | Constructeur ou setter |
| Fonction d'activation | `TransferFunction` | Constructeur ou setter |
| Données d'entrée | `double[]` | `execute()` ou `backPropagate()` |
| Sorties attendues | `double[]` | `backPropagate()` |

### Sorties fournies par la bibliothèque

| Méthode | Type retour | Signification |
|---------|-------------|---------------|
| `execute()` | `double[]` | Prédiction du réseau |
| `backPropagate()` | `double` | Erreur d'apprentissage |

---

## 7. Point Important : Dérivée de la fonction de transfert

> ⚠️ **ATTENTION** : La méthode `evaluateDer(value)` reçoit **σ(x)** (le résultat de la fonction), **pas x** lui-même !

Cela signifie que les dérivées doivent être exprimées en fonction de σ :
- **Sigmoïde** : `σ'(σ) = σ × (1 - σ)`
- **Tanh** : `σ'(σ) = 1 - σ²`
