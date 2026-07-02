import java.util.*;

public class DeckRecommender
{
    static int bestScore = Integer.MIN_VALUE;
    static List<String> bestDeck = new ArrayList<String>();
    static int nodesVisited = 0;
    static int branchesPruned = 0;

    static List<String> recommend(List<String> cards, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy, int deckSize, Set<String> includedCards, Set<String> excludedCards)
    {
        List<String> includedDeck = new ArrayList<String>();

        for (String card : cards)
        {
            if (includedCards.contains(card) && !excludedCards.contains(card))
            {
                includedDeck.add(card);
            }
        }

        List<String> usableCards = orderCards(cards, strength, synergy, includedCards, excludedCards);

        bestScore = Integer.MIN_VALUE;
        bestDeck = new ArrayList<String>();
        nodesVisited = 0;
        branchesPruned = 0;

        int startingScore = scoreDeck(includedDeck, strength, synergy);

        search(usableCards, strength, synergy, deckSize, 0, includedDeck, startingScore);
        return bestDeck;
    }

    static void search(List<String> cards, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy, int deckSize, int index, List<String> currentDeck, int currentScore)
    {
        nodesVisited++;

        if (currentDeck.size() == deckSize)
        {
            if (currentScore > bestScore)
            {
                bestScore = currentScore;
                bestDeck = new ArrayList<String>(currentDeck);
            }
            return;
        }

        if (index == cards.size())
        {
            return;
        }

        int remaining = cards.size() - index;
        int needed = deckSize - currentDeck.size();

        if (remaining < needed)
        {
            branchesPruned++;
            return;
        }

        int bestPossibleScore = currentScore + getBestMaxScore(cards, strength, synergy, index, currentDeck, needed);
        if (bestPossibleScore <= bestScore)
        {
            branchesPruned++;
            return;
        }

        String card = cards.get(index);
        int addedScore = strength.get(card);

        for (String selected : currentDeck)
        {
            addedScore += getSynergy(card, selected, synergy);
        }

        currentDeck.add(card);
        search(cards, strength, synergy, deckSize, index + 1, currentDeck, currentScore + addedScore);
        currentDeck.remove(currentDeck.size() - 1);

        search(cards, strength, synergy, deckSize, index + 1, currentDeck, currentScore);
    }

    static List<String> orderCards(List<String> cards, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy, Set<String> includedCards, Set<String> excludedCards)
    {
        Set<String> excluded = new HashSet<String>();
        Set<String> included = new HashSet<String>();

        for (String card : includedCards)
        {
            included.add(card.toLowerCase());
        }

        for (String card : excludedCards)
        {
            excluded.add(card.toLowerCase());
        }

        List<String> usableCards = new ArrayList<String>();

        for (String card : cards)
        {
            String lowerCard = card.toLowerCase();

            if (!excluded.contains(lowerCard) && !included.contains(lowerCard))
            {
                usableCards.add(card);
            }
        }

        PriorityQueue<String> queue = new PriorityQueue<String>(new Comparator<String>()
        {
            public int compare(String card1, String card2)
            {
                int score1 = priorityScore(card1, usableCards, strength, synergy);
                int score2 = priorityScore(card2, usableCards, strength, synergy);
                return score2 - score1;
            }
        });

        for (String card : usableCards)
        {
            queue.add(card);
        }

        List<String> orderedCards = new ArrayList<String>();

        while (!queue.isEmpty())
        {
            orderedCards.add(queue.poll());
        }

        return orderedCards;
    }

    static int priorityScore(String card, List<String> cards, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy)
    {
        int score = strength.get(card);

        for (String other : cards)
        {
            if (!card.equals(other))
            {
                int pairScore = getSynergy(card, other, synergy);

                if (pairScore > 0)
                {
                    score += pairScore;
                }
            }
        }

        return score;
    }

    static int getBestMaxScore(List<String> cards, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy, int index, List<String> currentDeck, int needed)
    {
        List<Integer> possibleScores = new ArrayList<Integer>();

        for (int i = index; i < cards.size(); i++)
        {
            String card = cards.get(i);
            int possibleScore = strength.get(card);

            for (String selected : currentDeck)
            {
                int pairScore = getSynergy(card, selected, synergy);

                if (pairScore > 0)
                {
                    possibleScore += pairScore;
                }
            }

            for (int j = index; j < cards.size(); j++)
            {
                if (i != j)
                {
                    int pairScore = getSynergy(card, cards.get(j), synergy);

                    if (pairScore > 0)
                    {
                        possibleScore += pairScore;
                    }
                }
            }

            possibleScores.add(possibleScore);
        }

        Collections.sort(possibleScores, Collections.reverseOrder());

        int total = 0;

        for (int i = 0; i < needed && i < possibleScores.size(); i++)
        {
            total += possibleScores.get(i);
        }

        return total;
    }

    static List<String> bruteForceRecommend(List<String> cards, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy, int deckSize, Set<String> includedCards, Set<String> excludedCards)
    {
        List<String> includedDeck = new ArrayList<String>();

        for (String card : cards)
        {
            if (includedCards.contains(card) && !excludedCards.contains(card))
            {
                includedDeck.add(card);
            }
        }

        List<String> usableCards = new ArrayList<String>();

        for (String card : cards)
        {
            if (!excludedCards.contains(card) && !includedCards.contains(card))
            {
                usableCards.add(card);
            }
        }

        bestScore = Integer.MIN_VALUE;
        bestDeck = new ArrayList<String>();

        int startingScore = scoreDeck(includedDeck, strength, synergy);

        bruteForceSearch(usableCards, strength, synergy, deckSize, 0, includedDeck, startingScore);
        return bestDeck;
    }

    static void bruteForceSearch(List<String> cards, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy, int deckSize, int index, List<String> currentDeck, int currentScore)
    {
        if (currentDeck.size() == deckSize)
        {
            if (currentScore > bestScore)
            {
                bestScore = currentScore;
                bestDeck = new ArrayList<String>(currentDeck);
            }
            return;
        }

        if (index == cards.size())
        {
            return;
        }

        if (cards.size() - index < deckSize - currentDeck.size())
        {
            return;
        }

        String card = cards.get(index);
        int addedScore = strength.get(card);

        for (String selected : currentDeck)
        {
            addedScore += getSynergy(card, selected, synergy);
        }

        currentDeck.add(card);
        bruteForceSearch(cards, strength, synergy, deckSize, index + 1, currentDeck, currentScore + addedScore);
        currentDeck.remove(currentDeck.size() - 1);

        bruteForceSearch(cards, strength, synergy, deckSize, index + 1, currentDeck, currentScore);
    }

    static int scoreDeck(List<String> deck, Map<String, Integer> strength, Map<String, Map<String, Integer>> synergy)
    {
        int score = 0;

        for (int i = 0; i < deck.size(); i++)
        {
            String card = deck.get(i);
            score += strength.get(card);

            for (int j = 0; j < i; j++)
            {
                score += getSynergy(card, deck.get(j), synergy);
            }
        }

        return score;
    }

    static void addSynergy(Map<String, Map<String, Integer>> synergy, String card1, String card2, int value)
    {
        if (!synergy.containsKey(card1))
        {
            synergy.put(card1, new HashMap<String, Integer>());
        }

        if (!synergy.containsKey(card2))
        {
            synergy.put(card2, new HashMap<String, Integer>());
        }

        synergy.get(card1).put(card2, value);
        synergy.get(card2).put(card1, value);
    }

    static int getSynergy(String card1, String card2, Map<String, Map<String, Integer>> synergy)
    {
        if (!synergy.containsKey(card1))
        {
            return 0;
        }

        if (!synergy.get(card1).containsKey(card2))
        {
            return 0;
        }

        return synergy.get(card1).get(card2);
    }
}
