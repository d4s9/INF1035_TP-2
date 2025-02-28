# Sérialisation et désérialisation d'objets en JSON en Java

## Description
Ce projet implémente un système de sérialisation et de désérialisation d'objets en JSON en Java, sans bibliothèques externes, en utilisant la réflexion pour gérer :
- Les types primitifs
- Les objets imbriqués
- Les collections (List, Array)
- Les annotations personnalisées

## Fonctionnalités
- Sérialisation des objets en format JSON
- Désérialisation des objets à partir du format JSON
- Gestion des annotations personnalisées :
  - `@Alias`: Permet de renommer un champ lors de la sérialisation
  - `@Ignored`: Permet d'ignorer un champ lors de la sérialisation
- Détection des références circulaires pour éviter les boucles infinies
- Gestion des types primitifs et des classes enveloppes (`int`, `Integer`, `double`, `Double`, etc.)
- Prise en charge des objets imbriqués récursifs
- Sérialisation et désérialisation des collections génériques
- Validation de format JSON avec nettoyage automatique des espaces

## Prérequis
- Java 8+

## Structure du projet
```bash
src/
├─ com/
│  └─ company/
│     ├─ annotations/    # Contient les annotations @Alias et @Ignored
│     ├─ reflector/      # Contient la logique de réflexion pour la sérialisation/désérialisation
│     │  ├─ SerialisationReflector.java    # Gère la sérialisation
│     │  ├─ DeSerialisationReflector.java  # Gère la désérialisation
│     │  ├─ FieldRef.java                 # Représente une référence à un champ
│     │  ├─ SimpleFieldRef.java           # Gère les types simples
│     │  ├─ ObjectFieldRef.java           # Gère les objets imbriqués
│     │  └─ CollectionFieldRef.java       # Gère les collections
│     └─ test/           # Contient les classes de test Person et Address
└─ Main.java             # Point d'entrée pour exécuter les tests
```

## Comment exécuter le projet
1. Cloner le projet :
```bash
git clone https://github.com/votre-repo/INF1035_TP-2.git
```
2. Compiler le projet :
```bash
javac src/**/*.java
```
3. Exécuter les tests :
```bash
java src.Main
```

## Exemple d'utilisation
### Sérialisation
```java
Person person = new Person("John", "Doe", 30, true, 50000, 12345, 'J', 75, new Address("123 Main St", "New York"), List.of("Alice", "Bob"), new String[]{"123-456-7890"}, null);
SerialisationReflector reflector = new SerialisationReflector(person);
String json = reflector.toJson();
System.out.println(json);
```
### Désérialisation
```java
String json = "{\"Nom_De_Personne\":\"John\",\"age\":30}";
DeSerialisationReflector<Person> deserializer = new DeSerialisationReflector<>(Person.class);
Person deserializedPerson = deserializer.fromJson(json, Person.class);
System.out.println(deserializedPerson);
```

## Tests
- Sérialisation avec des types primitifs
- Désérialisation avec des objets imbriqués
- Gestion des références circulaires
- Sérialisation de collections avec différentes tailles
- Désérialisation avec des champs ignorés
- Gestion des valeurs `null`
- Erreurs de format JSON

## Auteur
- Derek Stevens





