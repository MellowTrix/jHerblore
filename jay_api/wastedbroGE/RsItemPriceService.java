package scripts.jay_api.wastedbroGE;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Provides a helper class for creating price services that utilize multiple sources.
 *
 * Includes functionality to return proper values for "Coins" despite them not being a part of the Grand Exchange
 */
public class RsItemPriceService implements IRsItemPriceService
{
    private ArrayList<IRsItemPriceService> priceServices = new ArrayList<>();

    private boolean isDebugging = false;

    private RsItemPriceService(){}

    /**
     * Attempts to grab the price of the item by checking each price service in the order they were added.
     * Once the price is found, no further service is checked.
     * @param itemId The item ID to lookup
     * @return The price of the item found. If the item can't be found in any of the services, an empty Optional is returned.
     */
    @Override
    public Optional<Integer> tryGetPrice(int itemId)
    {
        if(itemId == 995) // Coins
            return Optional.of(1);

        for (IRsItemPriceService service : priceServices)
        {
            log("Attempting to get price of [" + itemId + "] from: " + service.getClass().getName());

            Optional<Integer> price = service.tryGetPrice(itemId);

            if (price.isPresent())
            {
                log("Successfully retrieved price!");
                return price;
            }
            else
                log("Failed to find price from: " + service.getClass().getName());
        }

        return Optional.empty();
    }

    /**
     * Attempts to grab the name of the item by checking each price service in the order they were added.
     * Once the name is found, no further service is checked.
     * @param itemId The item ID to lookup
     * @return The name of the item found. If the item can't be found in any of the services, an empty Optional is returned.
     */
    @Override
    public Optional<String> tryGetName(int itemId)
    {
        if(itemId == 995) // Coins
            return Optional.of("Coins");

        for (IRsItemPriceService service : priceServices)
        {
            log("Attempting to get name of [" + itemId + "] from: " + service.getClass().getName());

            Optional<String> name = service.tryGetName(itemId);

            if (name.isPresent())
            {
                log("Successfully retrieved name!");
                return name;
            }
            else
                log("Failed to find name from: " + service.getClass().getName());
        }

        return Optional.empty();
    }

    /**
     * Attempts to grab the members status of the item by checking each price service in the order they were added.
     * Once the members status is found, no further service is checked.
     * @param itemId The item ID to lookup
     * @return The members status of the item found. If the item can't be found in any of the services, an empty Optional is returned.
     */
    @Override
    public Optional<Boolean> tryGetMembers(int itemId)
    {
        if(itemId == 995) // Coins
            return Optional.of(false);

        for (IRsItemPriceService service : priceServices)
        {
            log("Attempting to get Member Status of [" + itemId + "] from: " + service.getClass().getName());

            Optional<Boolean> members = service.tryGetMembers(itemId);

            if (members.isPresent())
            {
                log("Successfully retrieved members status!");
                return members;
            }
            else
                log("Failed to find members status from: " + service.getClass().getName());
        }

        return Optional.empty();
    }

    private void log(String s)
    {
        if(isDebugging)
            System.out.println(s);
    }

    //region Getters/Setters

    public ArrayList<IRsItemPriceService> getPriceServices()
    {
        return priceServices;
    }

    public void addPriceService(IRsItemPriceService service)
    {
        priceServices.add(service);
    }

    public void setPriceServices(ArrayList<IRsItemPriceService> priceServices)
    {
        this.priceServices = priceServices;
    }

    public boolean isDebugging()
    {
        return isDebugging;
    }

    public void setDebugging(boolean debugging)
    {
        isDebugging = debugging;
    }

    //endregion

    public static class Builder
    {
        private RsItemPriceService priceService = new RsItemPriceService();

        public Builder(){}

        public Builder addPriceService(IRsItemPriceService service)
        {
            priceService.addPriceService(service);
            return this;
        }

        public Builder setDebugging(boolean shouldDebug)
        {
            priceService.setDebugging(shouldDebug);
            return this;
        }

        public RsItemPriceService build()
        {
            return this.priceService;
        }
    }
}
