package scripts.jTasker.jay_api.utils;

import scripts.jTasker.jay_api.wastedbroGE.GrandExchangeService;

public class Variables {

    // Instance manipulation
    private Variables() {}
    private static final Variables VARIABLES = new Variables();
    public static Variables get() {
        return VARIABLES;
    }
    
    private long START_TIME = System.currentTimeMillis();  
    private int profit = 0, items_created = 0;

    public long getTime() {
        return START_TIME;
    }
    
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
    	removeFromProfit(itemID, itemCount, 1.0f, 0);
    }
    
    public void removeFromProfit(int itemID, int itemCount, float multiplier) {
    	removeFromProfit(itemID, itemCount, multiplier, 0);
    }
    
    public void removeFromProfit(int itemID, int itemCount, float multiplier, int fixedDeduct) {
    	profit -= GrandExchangeService.tryGetPrice(Integer.valueOf(itemID)).get() * itemCount * multiplier - fixedDeduct;
    }
    
    public void removeFromProfit(int amount) {
    	profit -= amount;
    }
    
    public int getItemsCreated() {
        return items_created;
    }
    
    public void addToCreated(int amount) {
    	items_created += amount;
    }
}
