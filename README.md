# Projet SAE - Perceptron Multi-Couches (MLP)

## Auteurs

- CHAILAN
- LOUNICI
- MARTINEZ-VILLORIA
- ROSENKRANZ

---

## Présentation du Projet

Ce projet implémente un **Perceptron Multi-Couches (MLP)** en Java, un type de réseau de neurones artificiels capable d'apprendre à partir d'exemples.

### Qu'est-ce qu'un MLP ?

Un MLP est un réseau de neurones composé de plusieurs couches :
- **Couche d'entrée** : reçoit les données brutes
- **Couches cachées** : effectuent des transformations non-linéaires
- **Couche de sortie** : produit la prédiction finale

Le réseau apprend grâce à l'algorithme de **rétropropagation du gradient** : on calcule l'erreur entre la sortie obtenue et la sortie souhaitée, puis on ajuste les poids des connexions pour réduire cette erreur.

---

## Structure des Fichiers

```
MLP/
├── MLP.java              # Classe principale du réseau de neurones
├── Sigmoid.java          # Fonction de transfert sigmoïde
├── Tanh.java             # Fonction de transfert tangente hyperbolique
├── Test.java             # Programme de test sur les portes logiques
├── TestFonctions.java    # Test des fonctions de transfert
├── ANALYSE_BIBLIOTHEQUE.md    # Analyse détaillée de la bibliothèque
└── PSEUDOCODE_SQUELETTE.md    # Pseudocode et squelette d'implémentation
```

### Description des Classes

| Classe | Description |
|--------|-------------|
| `TransferFunction` | Interface définissant une fonction de transfert |
| `Neuron` | Représente un neurone avec ses poids, biais et valeur |
| `Layer` | Représente une couche de neurones |
| `MLP` | Le réseau de neurones complet avec apprentissage |
| `Sigmoid` | Fonction sigmoïde : sortie entre 0 et 1 |
| `Tanh` | Fonction tangente hyperbolique : sortie entre -1 et 1 |

---

## Prérequis

- **Java JDK 8 ou supérieur**
- Un terminal/invite de commande

Pour vérifier votre version de Java :
```bash
java -version
```

---

## Compilation

Naviguez dans le dossier `MLP` et compilez tous les fichiers Java :

```bash
cd MLP
javac *.java
```

Si la compilation réussit, vous verrez apparaître des fichiers `.class`.

---

## Exécution

### Test complet (par défaut)

Lance tous les tests avec les deux fonctions de transfert sur les 3 portes logiques :

```bash
java Test
```

**Sortie attendue :**
```
=== TESTS MLP SUR TABLES LOGIQUES ===

>>> FONCTION DE TRANSFERT : SIGMOID <<<

--- Sortie 1 dimension ---
ET: 4/4 reussis, 1234 epoques, erreur=0.0099 [OK]
OU: 4/4 reussis, 567 epoques, erreur=0.0098 [OK]
XOR: 4/4 reussis, 2345 epoques, erreur=0.0095 [OK]
...
```

### Test paramétré

Vous pouvez personnaliser le test avec des arguments :

```bash
java Test [architecture] [fonction] [porte] [option]
```

**Paramètres :**
| Paramètre | Description | Exemples |
|-----------|-------------|----------|
| `architecture` | Nombre de neurones par couche, séparés par des virgules | `2,4,1` ou `2,8,4,1` |
| `fonction` | Fonction de transfert | `sigmoid` ou `tanh` |
| `porte` | Porte logique à tester | `ET`, `OU` ou `XOR` |
| `option` | Mode optionnel | `2d` ou `melange` |

**Exemples :**

```bash
# Architecture 2-4-1, Sigmoid, porte ET
java Test 2,4,1 sigmoid ET

# Architecture 2-8-4-1, Tanh, porte XOR
java Test 2,8,4,1 tanh XOR

# Sortie en 2 dimensions
java Test 2,4,2 sigmoid ET 2d

# Données mélangées à chaque époque
java Test 2,4,1 sigmoid XOR melange
```

### Test des fonctions de transfert

Affiche les valeurs de Sigmoid et Tanh pour des entrées typiques :

```bash
java TestFonctions
```

**Sortie attendue :**
```
=== TEST SIGMOID ===
entree		sigma		sigma'
-1.0		0.26894		0.19661
-0.5		0.37754		0.23500
0.0		0.50000		0.25000
0.5		0.62246		0.23500
1.0		0.73106		0.19661

=== TEST TANH ===
entree		tanh		tanh'
-1.0		-0.76159	0.41997
-0.5		-0.46212	0.78644
0.0		0.00000		1.00000
0.5		0.46212		0.78644
1.0		0.76159		0.41997
```

---

## Les Portes Logiques

Ce projet utilise les portes logiques comme exemples d'apprentissage car elles sont simples à comprendre :

### Porte ET (AND)
| Entrée A | Entrée B | Sortie |
|----------|----------|--------|
| 0 | 0 | 0 |
| 0 | 1 | 0 |
| 1 | 0 | 0 |
| 1 | 1 | **1** |

> La sortie est 1 seulement si les deux entrées sont 1.

### Porte OU (OR)
| Entrée A | Entrée B | Sortie |
|----------|----------|--------|
| 0 | 0 | 0 |
| 0 | 1 | **1** |
| 1 | 0 | **1** |
| 1 | 1 | **1** |

> La sortie est 1 si au moins une entrée est 1.

### Porte XOR (OU exclusif)
| Entrée A | Entrée B | Sortie |
|----------|----------|--------|
| 0 | 0 | 0 |
| 0 | 1 | **1** |
| 1 | 0 | **1** |
| 1 | 1 | 0 |

> La sortie est 1 si **une seule** des entrées est 1.

**Note importante :** Le XOR n'est pas linéairement séparable, ce qui signifie qu'un simple perceptron (sans couche cachée) ne peut pas l'apprendre. C'est pourquoi on utilise un MLP !

---

## Fonctions de Transfert

Les fonctions de transfert "écrasent" la somme pondérée dans un intervalle borné :

### Sigmoïde
```
σ(x) = 1 / (1 + e^(-x))
σ'(σ) = σ × (1 - σ)
```
- Sortie entre **0 et 1**
- Utile pour les probabilités

### Tangente Hyperbolique (Tanh)
```
σ(x) = tanh(x)
σ'(σ) = 1 - σ²
```
- Sortie entre **-1 et 1**
- Centrée autour de zéro (peut accélérer l'apprentissage)

---

## Paramètres d'Apprentissage

| Paramètre | Valeur par défaut | Description |
|-----------|-------------------|-------------|
| Taux d'apprentissage | 0.5 | Vitesse d'ajustement des poids |
| Époques max | 10 000 | Nombre max de passages sur les données |
| Seuil d'erreur | 0.01 | Erreur en dessous de laquelle on arrête |

### Conseils de réglage

- **Taux d'apprentissage trop élevé** → Le réseau oscille et ne converge pas
- **Taux d'apprentissage trop faible** → L'apprentissage est très lent
- **Pas assez de neurones cachés** → Le réseau ne peut pas apprendre des patterns complexes
- **Trop de neurones cachés** → Risque de sur-apprentissage

---

## Architecture du Réseau

L'architecture est définie par un tableau d'entiers. Par exemple :

```java
int[] architecture = {2, 4, 1};
```

Signifie :
- 2 neurones d'entrée (2 valeurs en entrée)
- 4 neurones dans la couche cachée
- 1 neurone de sortie (1 valeur en sortie)

Représentation visuelle :
```
    [O]         [O]
         \     /   \
    [I]---[H]---[H]---[O]
         /     \   /
    [I]---[H]---[H]
         /     \
    [O]         [O]

Entrée (2)    Caché (4)    Sortie (1)
```

---

## Utilisation dans votre Code

### Créer un MLP

```java
// Architecture : 2 entrées, 4 neurones cachés, 1 sortie
int[] architecture = {2, 4, 1};
double tauxApprentissage = 0.5;
TransferFunction fonction = new Sigmoid();

MLP mlp = new MLP(architecture, tauxApprentissage, fonction);
```

### Entraîner le réseau

```java
double[][] entrees = {{0,0}, {0,1}, {1,0}, {1,1}};
double[][] sorties = {{0}, {1}, {1}, {0}};  // XOR

for (int epoque = 0; epoque < 10000; epoque++) {
    for (int i = 0; i < entrees.length; i++) {
        double erreur = mlp.backPropagate(entrees[i], sorties[i]);
    }
}
```

### Utiliser le réseau entraîné

```java
double[] resultat = mlp.execute(new double[]{1, 0});
System.out.println("1 XOR 0 = " + resultat[0]);  // Devrait afficher ~1.0
```

---

## Génération de la Javadoc

Pour générer la documentation HTML :

```bash
javadoc -d doc *.java
```

La documentation sera créée dans le dossier `doc/`. Ouvrez `doc/index.html` dans un navigateur.

---

## Dépannage

### Le réseau ne converge pas
- Essayez d'augmenter le nombre de neurones cachés
- Diminuez le taux d'apprentissage
- Augmentez le nombre d'époques max

### Erreur de compilation
- Vérifiez que vous êtes dans le bon dossier (`MLP/`)
- Vérifiez que Java est correctement installé

### Résultats différents à chaque exécution
- C'est normal ! Les poids sont initialisés aléatoirement
- Le réseau peut converger plus ou moins vite selon l'initialisation

---

## Ressources Complémentaires

- [ANALYSE_BIBLIOTHEQUE.md](MLP/ANALYSE_BIBLIOTHEQUE.md) : Analyse détaillée des classes et méthodes
- [PSEUDOCODE_SQUELETTE.md](MLP/PSEUDOCODE_SQUELETTE.md) : Pseudocode et squelette d'implémentation

---

*Projet réalisé dans le cadre de la SAE Intelligence Artificielle*

Auteur Joey Rosenkranz