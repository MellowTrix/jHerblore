package scripts.jTasker.jay_api.wastedbroGE.rsbuddy;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.tribot.api.General;
import org.tribot.util.Util;
import scripts.jTasker.jay_api.wastedbroGE.IRsItemPriceService;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * This Price Service gets info by downloading the entire RSBuddy Exchange API Summary and caching the result
 * in the file system and RAM.
 *
 * Will work when the RSBuddy API is down as long as a cached copy of the data exists in the filesystem and is not considered "outdated"
 */
public class RsBuddyPriceService implements IRsItemPriceService
{
    private final String CACHE_FILEPATH = Util.getWorkingDirectory() + File.separator + "RsBuddyApiCache" + File.separator + "RsBuddyApiCache.json";

    private int updateIntervalHours = 5, outdatedHours = 96;

    private HashMap<Integer, RsBuddyItem> dataCache = new HashMap<>();
    private HashMap<String, Integer> itemNameIdPairs = new HashMap<>();

    public RsBuddyPriceService()
    {
        updateCacheIfNecessary();
    }

    /**
     * Initializes a new instance of RSBuddyPriceService
     * @param updateIntervalHours This is the number of hours in which the cache will update (upon calling one of the methods in this class)
     * @param outdatedHours This is the age in hours in which the cache will be considered unusable.
     */
    public RsBuddyPriceService(int updateIntervalHours, int outdatedHours)
    {
        this.updateIntervalHours = updateIntervalHours;
        this.outdatedHours = outdatedHours;

        updateCacheIfNecessary();
    }

    public Optional<RsBuddyItem> tryGetItemData(int itemId)
    {
        updateCacheIfNecessary();

        if(isCacheOlderThan(outdatedHours) || !dataCache.containsKey(itemId))
            return Optional.empty();
        else
            return Optional.ofNullable(dataCache.get(itemId));
    }

    public Optional<RsBuddyItem> tryGetItemData(String itemName)
    {
        updateCacheIfNecessary();

        if(isCacheOlderThan(outdatedHours) || !itemNameIdPairs.containsKey(itemName))
            return Optional.empty();
        else
            return Optional.ofNullable(dataCache.get(itemNameIdPairs.get(itemName)));
    }

    public Optional<Integer> tryGetPrice(String itemName)
    {
        if(!itemNameIdPairs.containsKey(itemName))
            return Optional.empty();

        return tryGetItemData(itemNameIdPairs.get(itemName))
                .map(RsBuddyItem::getAveragePrice);
    }

    @Override
    public Optional<Integer> tryGetPrice(int itemId)
    {
        return tryGetItemData(itemId).map(RsBuddyItem::getAveragePrice);
    }

    @Override
    public Optional<String> tryGetName(int itemId)
    {
        return tryGetItemData(itemId).map(RsBuddyItem::getName);
    }

    @Override
    public Optional<Boolean> tryGetMembers(int itemId)
    {
        return tryGetItemData(itemId).map(RsBuddyItem::isMembers);
    }

    private void updateCacheIfNecessary()
    {
        if(isCacheOlderThan(updateIntervalHours))
        {
            //General.println("Updated RSBuddy Exchange data cache");
            try
            {
                saveUrl(CACHE_FILEPATH, "https://rsbuddy.com/exchange/summary.json");

                try
                {
                    parseCache();
                }
                catch (Exception e)
                {
                    General.println("Failed to update RSBuddy Cache");
                    return;
                }

                //General.println("Cache now updated with " + dataCache.size() + " RS Items from the Exchange API");
            }
            catch (Exception e)
            {
                General.println("Could not update RSBuddy Exchange Data from internet.");
            }
        }

        if(dataCache.isEmpty() && new File(CACHE_FILEPATH).exists())
        {
            try
            {
                parseCache();
            }
            catch (Exception e)
            {
                General.println("Failed to update RSBuddy Cache");
            }
        }
    }


    private void parseCache() throws IOException
    {
        Gson gson = new GsonBuilder().create();
        JsonParser parser = new JsonParser();
        JsonReader reader = new JsonReader(new FileReader(CACHE_FILEPATH));

        JsonObject data = parser.parse(reader).getAsJsonObject();

        for(Map.Entry<String, JsonElement> entry : data.entrySet())
        {
            RsBuddyItem itemData = gson.fromJson(entry.getValue(), RsBuddyItem.class);
            itemNameIdPairs.put(itemData.getName(), itemData.getId());
            dataCache.put(itemData.getId(), itemData);
        }
    }

    private boolean isCacheOlderThan(int hours)
    {
        File cacheFile = new File(CACHE_FILEPATH);

        if(cacheFile.exists())
        {
            Date lastModified = new Date(cacheFile.lastModified());

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, hours*-1);

            Date updateTime = calendar.getTime();

            return lastModified.before(updateTime) || cacheFile.length() == 0;
        }

        return true;
    }

    /**
     * Saves a file from a URL into the specified file
     */
    private void saveUrl(final String filename, final String urlString) throws IOException
    {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try
        {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            if(!file.exists()) file.createNewFile();

            final URLConnection urlConnection = new URL(urlString).openConnection();
            urlConnection.setConnectTimeout(5000);

            in = new BufferedInputStream(urlConnection.getInputStream());
            fout = new FileOutputStream(file);

            final byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        }
        catch (Exception e){

        }
        finally
        {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
}

