# Card Deck Recommender

This project recommends the best card deck from a card dataset. Each card has a strength score, and card pairs can have positive or negative synergy scores. The program supports included cards, excluded cards, deck size selection, and tests against a brute-force search.

## Requirements

- Java installed
- A terminal, command prompt, or IDE that can run Java files

## Project files

- `Main.java` - interactive menu for choosing data, including/excluding cards, changing deck size, and finding the best deck
- `DeckRecommender.java` - best deck recommendation algorithm
- `DeckData.java` - loads card and synergy data from the CSV files
- `Test.java` - test suite that compares the recommendation algorithm to a brute-force algorithm
- `data/` - CSV datasets used by the program

## Data files

The program expects these files in a folder named `data`:

```text
basic_cards.csv
basic_synergy.csv
animals_cards.csv
animals_synergy.csv
weapons_cards.csv
weapons_synergy.csv
```

Card CSV format:

```text
card,strength
Card Name,10
```

Synergy CSV format:

```text
card1,card2,synergy
Card A,Card B,5
```

Positive synergy means two cards work well together. Negative synergy means they don't. Missing synergy pairs are treated as a synergy score of 0.

## Build

Compile the project with:

```bash
javac Main.java DeckData.java DeckRecommender.java Test.java
```

## Run the interactive program

```bash
java Main
```

## Run the tests

```bash
java Test
```

The tests compare the algorithm against a brute-force algorithm. The expected score is the brute-force best score, and the actual score is the algorithm's score.

Expected final line:

```text
All tests passed: 3
```
