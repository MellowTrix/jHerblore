package scripts.jay_api.wastedbroGE.grand_exchange_api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.tribot.api.General;
import scripts.jay_api.wastedbroGE.IRsItemPriceService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Optional;

/**
 *
 */
public class GrandExchangePriceService implements IRsItemPriceService
{
    private static final String GE_URL = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";

    private boolean shouldCache = true;

    private HashMap<Integer, ExchangeItem> itemCache = new HashMap<>();

    public GrandExchangePriceService(){}
    public GrandExchangePriceService(boolean shouldCache)
    {
        this.shouldCache = shouldCache;
    }

    public Optional<ExchangeItem> tryGetItemData(int itemId)
    {
        if (shouldCache && itemCache.containsKey(itemId))
        {
            return Optional.of(itemCache.get(itemId));
        }

        try
        {
            Gson gson = new GsonBuilder().create();

            final URLConnection urlConnection = new URL(GE_URL + itemId).openConnection();
            urlConnection.setConnectTimeout(5000);
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));

            JsonParser parser = new JsonParser();
            JsonObject data = parser.parse(reader).getAsJsonObject();

            Optional<ExchangeItem> item = Optional.ofNullable(gson.fromJson(data.get("item"), ExchangeItem.class));

            if(shouldCache && item.isPresent())
                itemCache.put(itemId, item.get());

            reader.close();

            return item;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            General.println("Could not load RSItem (" + itemId + ")via Grand Exchange API");
        }

        return Optional.empty();
    }

    @Override
    public Optional<Integer> tryGetPrice(int itemId)
    {
        Optional<ExchangeItem> item = tryGetItemData(itemId);
        if(item.isPresent())
            return Optional.of(item.get().getPrice());
        return Optional.empty();
    }

    @Override
    public Optional<String> tryGetName(int itemId)
    {
        Optional<ExchangeItem> item = tryGetItemData(itemId);
        if(item.isPresent())
            return Optional.of(item.get().getName());
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> tryGetMembers(int itemId)
    {
        Optional<ExchangeItem> item = tryGetItemData(itemId);
        if(item.isPresent())
            return Optional.of(item.get().isMembers());
        return Optional.empty();
    }


}
