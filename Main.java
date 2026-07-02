import java.util.*;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Scanner scanner = new Scanner(System.in);
        DeckData data = DeckData.load("basic");
        Set<String> includedCards = new HashSet<String>();
        Set<String> excludedCards = new HashSet<String>();
        int deckSize = 3;
        boolean running = true;

        while (running)
        {
            printMenu(data.dataName, deckSize, includedCards, excludedCards);
            int input = scanner.nextInt();
            scanner.nextLine();

            if (input == 1)
            {
                data = chooseDeckData(scanner, data);
                includedCards.clear();
                excludedCards.clear();
            }
            else if (input == 2)
            {
                showCards(data, includedCards, excludedCards);
            }
            else if (input == 3)
            {
                deckSize = changeDeckSize(scanner, deckSize);
            }
            else if (input == 4)
            {
                includeCard(scanner, data, includedCards, excludedCards);
            }
            else if (input == 5)
            {
                excludeCard(scanner, data, includedCards, excludedCards);
            }
            else if (input == 6)
            {
                includedCards.clear();
                excludedCards.clear();
                System.out.println("Included and excluded cards were cleared.");
            }
            else if (input == 7)
            {
                findBestDeck(data, deckSize, includedCards, excludedCards);
            }
            else if (input == 8)
            {
                running = false;
            }
            else
            {
                System.out.println("Invalid input.");
            }
        }

        scanner.close();
    }

    static void printMenu(String dataName, int deckSize, Set<String> includedCards, Set<String> excludedCards)
    {
        System.out.println();
        System.out.println("Current deck data: " + dataName);
        System.out.println("Current deck size: " + deckSize);
        System.out.println("Included cards: " + includedCards);
        System.out.println("Excluded cards: " + excludedCards);
        System.out.println("1. Choose deck data");
        System.out.println("2. Show cards");
        System.out.println("3. Change deck size");
        System.out.println("4. Include a card");
        System.out.println("5. Exclude a card");
        System.out.println("6. Clear included and excluded cards");
        System.out.println("7. Find best deck");
        System.out.println("8. Quit");
        System.out.print("Input: ");
    }

    static DeckData chooseDeckData(Scanner scanner, DeckData currentData)
    {
        List<String> samples = DeckData.getSampleDecks();

        System.out.println("Available sample decks:");
        for (int i = 0; i < samples.size(); i++)
        {
            System.out.println((i + 1) + ". " + samples.get(i));
        }

        System.out.print("Input sample number: ");
        int input = scanner.nextInt();
        scanner.nextLine();

        if (input < 1 || input > samples.size())
        {
            System.out.println("Invalid sample number.");
            return currentData;
        }

        String dataName = samples.get(input - 1);

        try
        {
            return DeckData.load(dataName);
        }
        catch (Exception e)
        {
            System.out.println("Deck data couldn't load.");
            return currentData;
        }
    }

    static void showCards(DeckData data, Set<String> includedCards, Set<String> excludedCards)
    {
        for (int i = 0; i < data.cards.size(); i++)
        {
            String card = data.cards.get(i);
            String status = "";

            if (includedCards.contains(card))
            {
                status = " included";
            }
            else if (excludedCards.contains(card))
            {
                status = " excluded";
            }

            System.out.println((i + 1) + ". " + card + " (Strength: " + data.strength.get(card) + ")" + status);
        }
    }

    static int changeDeckSize(Scanner scanner, int currentDeckSize)
    {
        System.out.print("New deck size: ");
        int deckSize = scanner.nextInt();
        scanner.nextLine();

        if (deckSize > 0)
        {
            return deckSize;
        }

        System.out.println("Deck size was not changed.");
        return currentDeckSize;
    }

    static void includeCard(Scanner scanner, DeckData data, Set<String> includedCards, Set<String> excludedCards)
    {
        showCards(data, includedCards, excludedCards);
        System.out.print("Input card number to include: ");

        int input = scanner.nextInt();
        scanner.nextLine();

        String card = findCard(data, input);

        if (card == null)
        {
            System.out.println("Card not found.");
        }
        else
        {
            includedCards.add(card);
            excludedCards.remove(card);
            System.out.println(card + " was included.");
        }
    }

    static void excludeCard(Scanner scanner, DeckData data, Set<String> includedCards, Set<String> excludedCards)
    {
        showCards(data, includedCards, excludedCards);
        System.out.print("Input card number to exclude: ");

        int input = scanner.nextInt();
        scanner.nextLine();

        String card = findCard(data, input);

        if (card == null)
        {
            System.out.println("Card not found.");
        }
        else
        {
            excludedCards.add(card);
            includedCards.remove(card);
            System.out.println(card + " was excluded.");
        }
    }

    static String findCard(DeckData data, int cardNumber)
    {
        if (cardNumber >= 1 && cardNumber <= data.cards.size())
        {
            return data.cards.get(cardNumber - 1);
        }

        return null;
    }

    static int countUsableCards(List<String> cards, Set<String> excludedCards)
    {
        int count = 0;

        for (String card : cards)
        {
            if (!excludedCards.contains(card))
            {
                count++;
            }
        }

        return count;
    }

    static void findBestDeck(DeckData data, int deckSize, Set<String> includedCards, Set<String> excludedCards)
    {
        if (includedCards.size() > deckSize)
        {
            System.out.println("There are more included cards than the deck size.");
            return;
        }

        if (deckSize > countUsableCards(data.cards, excludedCards))
        {
            System.out.println("Deck size is bigger than the number of usable cards.");
            return;
        }

        long startTime = System.nanoTime();
        List<String> deck = DeckRecommender.recommend(data.cards, data.strength, data.synergy, deckSize, includedCards, excludedCards);
        long endTime = System.nanoTime();
        double runtimeMs = (endTime - startTime) / 1000000.0;

        System.out.println("Best deck: " + deck);
        System.out.println("Best deck score: " + DeckRecommender.bestScore);
        System.out.println("Nodes visited: " + DeckRecommender.nodesVisited);
        System.out.println("Branches pruned: " + DeckRecommender.branchesPruned);
        System.out.println("Runtime ms: " + runtimeMs);
    }
}