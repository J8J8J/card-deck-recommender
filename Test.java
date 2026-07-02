import java.util.*;

public class Test
{
    static int testsPassed = 0;

    public static void main(String[] args) throws Exception
    {
        testDeck("basic", 3, new HashSet<String>(), new HashSet<String>());

        Set<String> includedAnimals = new HashSet<String>();
        includedAnimals.add("Lion");
        testDeck("animals", 10, includedAnimals, new HashSet<String>());

        Set<String> excludedWeapons = new HashSet<String>();
        excludedWeapons.add("Rusty Knife");
        excludedWeapons.add("Wooden Stick");
        testDeck("weapons", 3, new HashSet<String>(), excludedWeapons); //Brute force can take a while.

        System.out.println("All tests passed: " + testsPassed);
    }

    static void testDeck(String deckName, int deckSize, Set<String> includedCards, Set<String> excludedCards) throws Exception
    {
        DeckData data = DeckData.load(deckName);
        compareToBruteForce(deckName + " deck test", data, deckSize, includedCards, excludedCards);
    }

    static int countSynergies(Map<String, Map<String, Integer>> synergy)
    {
        int count = 0;

        for (String card : synergy.keySet())
        {
            count += synergy.get(card).size();
        }

        return count / 2;
    }

    static void compareToBruteForce(String testName, DeckData data, int deckSize, Set<String> includedCards, Set<String> excludedCards)
    {
        int totalCards = data.cards.size();
        int synergyPairs = countSynergies(data.synergy);
        
        long startTime = System.nanoTime();
        DeckRecommender.recommend(data.cards, data.strength, data.synergy, deckSize, includedCards, excludedCards);
        long endTime = System.nanoTime();

        double runtimeMs = (endTime - startTime) / 1000000.0;
        int nodesVisited = DeckRecommender.nodesVisited;
        int branchesPruned = DeckRecommender.branchesPruned;

        int recommendedScore = DeckRecommender.bestScore;
        List<String> recommendedDeck = new ArrayList<String>(DeckRecommender.bestDeck);

        DeckRecommender.bruteForceRecommend(data.cards, data.strength, data.synergy, deckSize, includedCards, excludedCards);
        int bruteForceScore = DeckRecommender.bestScore;

        if (recommendedScore != bruteForceScore)
        {
            System.out.println(testName + " failed");
            System.out.println("Input dataset: " + data.dataName);
            System.out.println("Input total cards: " + totalCards);
            System.out.println("Input synergy pairs: " + synergyPairs);
            System.out.println("Input deck size: " + deckSize);
            System.out.println("Input included cards: " + includedCards);
            System.out.println("Input excluded cards: " + excludedCards);
            System.out.println("Expected score: " + bruteForceScore);
            System.out.println("Actual score: " + recommendedScore);
            System.out.println("Actual deck: " + recommendedDeck);
            System.out.println("Actual deck card count: " + recommendedDeck.size());
            System.out.println("Runtime ms: " + runtimeMs);
            System.out.println("Nodes visited: " + nodesVisited);
            System.out.println("Branches pruned: " + branchesPruned);
            System.out.println();

            throw new RuntimeException("Test failed: " + testName);
        }

        for (String card : includedCards)
        {
            if (!recommendedDeck.contains(card))
            {
                throw new RuntimeException("Test failed: included card wasn't used: " + card);
            }
        }

        for (String card : excludedCards)
        {
            if (recommendedDeck.contains(card))
            {
                throw new RuntimeException("Test failed: excluded card was used: " + card);
            }
        }

        System.out.println(testName + " passed");
        System.out.println("Input dataset: " + data.dataName);
        System.out.println("Input total cards: " + totalCards);
        System.out.println("Input synergy pairs: " + synergyPairs);
        System.out.println("Input deck size: " + deckSize);
        System.out.println("Input included cards: " + includedCards);
        System.out.println("Input excluded cards: " + excludedCards);
        System.out.println("Expected score: " + bruteForceScore);
        System.out.println("Actual score: " + recommendedScore);
        System.out.println("Actual deck: " + recommendedDeck);
        System.out.println("Actual deck card count: " + recommendedDeck.size());
        System.out.println("Runtime ms: " + runtimeMs);
        System.out.println("Nodes visited: " + nodesVisited);
        System.out.println("Branches pruned: " + branchesPruned);
        System.out.println();

        testsPassed++;
    }
}