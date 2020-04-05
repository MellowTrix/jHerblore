package scripts.jay_api.fluffeespaint;

import scripts.jay_api.wastedbroGE.GrandExchangeService;

public class Variables {

    // Instance manipulation
    private Variables() {}
    private static final Variables VARIABLES = new Variables();
    public static Variables get() {
        return VARIABLES;
    }
    
    private int profit = 0, items_created = 0;
	
    public int getProfit() {
        return profit;
    }

    public void addToProfit(int itemID, int itemCount) {
        profit += GrandExchangeService.tryGetPrice(Integer.valueOf(itemID)).get() * itemCount;
    }
    
    public void addToProfit(int amount) {
        profit += amount;
    }

    public void removeFromProfit(int itemID, int itemCount) {
    	profit -= GrandExchangeService.tryGetPrice(Integer.valueOf(itemID)).get() * itemCount;
    }
    
    public void removeFromProfit(int itemID, int itemCount, float multiplier, int fixedDeduct) {
    	profit -= GrandExchangeService.tryGetPrice(Integer.valueOf(itemID)).get() * itemCount * multiplier - fixedDeduct;
    }
    
    public int getItemsCreated() {
        return items_created;
    }
    
    public void addToCreated(int amount) {
    	items_created += amount;
    }
}
