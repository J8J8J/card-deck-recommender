import java.io.*;
import java.util.*;

public class DeckData
{
    String dataName;
    List<String> cards;
    Map<String, Integer> strength;
    Map<String, Map<String, Integer>> synergy;

    public DeckData(String dataName)
    {
        this.dataName = dataName;
        cards = new ArrayList<String>();
        strength = new HashMap<String, Integer>();
        synergy = new HashMap<String, Map<String, Integer>>();
    }

    public static DeckData load(String dataName) throws IOException
    {
        DeckData data = new DeckData(dataName);

        // Hardcoded for convenience.
        loadCards(data, "data/" + dataName + "_cards.csv");
        loadSynergy(data, "data/" + dataName + "_synergy.csv");
        return data;
    }

    public static List<String> getSampleDecks()
    {
        List<String> samples = new ArrayList<String>();

        // Hardcoded for convenience.
        samples.add("basic");
        samples.add("animals");
        samples.add("weapons");
        return samples;
    }

    static void loadCards(DeckData data, String fileName) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();

        while ((line = reader.readLine()) != null)
        {
            line = line.trim();

            if (line.length() > 0)
            {
                String[] parts = line.split(",");
                String card = parts[0].trim();
                int value = Integer.parseInt(parts[1].trim());

                data.cards.add(card);
                data.strength.put(card, value);
            }
        }

        reader.close();
    }

    static void loadSynergy(DeckData data, String fileName) throws IOException
    {
        BufferedReader bReader = new BufferedReader(new FileReader(fileName));
        String line = bReader.readLine();

        while ((line = bReader.readLine()) != null)
        {
            line = line.trim();

            if (line.length() > 0)
            {
                String[] parts = line.split(",");
                String card1 = parts[0].trim();
                String card2 = parts[1].trim();
                int value = Integer.parseInt(parts[2].trim());

                addSynergy(data.synergy, card1, card2, value);
            }
        }

        bReader.close();
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
}
