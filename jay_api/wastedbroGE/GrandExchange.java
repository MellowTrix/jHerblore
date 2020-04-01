package scripts.jay_api.wastedbroGE;

import scripts.jay_api.wastedbroGE.grand_exchange_api.GrandExchangePriceService;
import scripts.jay_api.wastedbroGE.rsbuddy.RsBuddyPriceService;

import java.util.Optional;

/**
 * Provides a GrandExchange wrapper that includes static methods for looking up item prices and info
 *
 * By default, uses the RSBuddy API service primarily, and the GE API as a backup
 */
public class GrandExchange extends org.tribot.api2007.GrandExchange
{
    private static IRsItemPriceService priceService = new RsItemPriceService.Builder()
    		.addPriceService(new GrandExchangePriceService())
            //.addPriceService(new RsBuddyPriceService())
            .build();

    /**
     * Attempts to get the price of the item via the RSBuddy API or GE API (whichever is accessible)
     * @param itemId The item to lookup
     * @return The current price of the item, or an empty Optional if it can't be found
     */
    public static Optional<Integer> tryGetPrice(int itemId)
    {
        return priceService.tryGetPrice(itemId);
    }

    /**
     * Attempts to get the name of the item via the RSBuddy API or GE API (whichever is accessible)
     * @param itemId The item to lookup
     * @return The name of the item, or an empty Optional if it can't be found
     */
    public static Optional<String> tryGetName(int itemId)
    {
        return priceService.tryGetName(itemId);
    }

    /**
     * Attempts to get the members status of the item via the RSBuddy API or GE API (whichever is accessible)
     * @param itemId The item to lookup
     * @return The members status of the item, or an empty Optional if it can't be found
     */
    public static Optional<Boolean> tryGetMembers(int itemId)
    {
        return priceService.tryGetMembers(itemId);
    }

    /**
     * Attempts to get the price of the item via the RSBuddy API or GE API (whichever is accessible)
     * @param itemId The item to lookup
     * @return The current price of the item, or 0 if it can't be found
     */
    public static int getPrice(int itemId)
    {
        return priceService.tryGetPrice(itemId).orElse(0);
    }

    /**
     * Attempts to get the name of the item via the RSBuddy API or GE API (whichever is accessible)
     * @param itemId The item to lookup
     * @return The name of the item, or null if it can't be found
     */
    public static String getName(int itemId)
    {
        return priceService.tryGetName(itemId).orElse(null);
    }

    /**
     * Attempts to get the members status of the item via the RSBuddy API or GE API (whichever is accessible)
     * @param itemId The item to lookup
     * @return The members status of the item, or false if it can't be found
     */
    public static boolean isMembers(int itemId)
    {
        return priceService.tryGetMembers(itemId).orElse(false);
    }

}
