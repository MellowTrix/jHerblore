package scripts.jTasker.jay_api;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.GrandExchange.COLLECT_METHOD;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSGEOffer.TYPE;

import scripts.jTasker.jay_api.Banker;
import scripts.jTasker.jay_api.RS;
import scripts.jTasker.jay_api.jGeneral;
import scripts.jTasker.jay_api.utils.Variables;
import scripts.jTasker.jay_api.utils.GEConditions;
import scripts.jTasker.jay_api.wastedbroGE.GrandExchangeService;

/* timToWait = time to wait before the offer is completed if it isn't already.
 * multiplier = price multiplier.
 * trackProfit = if we wish to track the profit gains/losses for our paints profit tracker.
*/

public class Exchanger {	
    private static final Exchanger EXCHANGER = new Exchanger();
    public static Exchanger get() {
        return EXCHANGER;
    }

	private List<Integer> SkippedItemList = new ArrayList<Integer>();
	
	public static boolean withdrawGP(int amount) {
		if (close() && Banker.withdraw(amount, 995))
			return true;

		General.println("AutoGE_Error - Not enough gold in bank.");
		return false;
	}
		
	public static boolean open() {
		if (GrandExchange.getWindowState() == null) {
			if (!Banker.close())
				return false;

			RSNPC npc = RS.NPCs_findNearest("Grand Exchange Clerk");
			if (npc != null) {
				if(!jGeneral.deselect())
					return false;

				if (Timing.waitCondition(() -> {
					General.sleep(50);
					return DynamicClicking.clickRSNPC(npc, "Exchange Grand Exchange Clerk");
			    }, General.random(8000, 10000))) {
					jGeneral.defaultDynamicSleep();
					return true;
				}

				General.println("AutoBanker_Error - Failed to open GE.");
				return false;
			}
			
			General.println("AutoGE_Error - Could not find GE Clerk");
			return false;
		}
		
		return true;
	}
	
	public static boolean close() {
		if (GrandExchange.getWindowState() == null)
			return true;
		
		if (jGeneral.deselect() && Timing.waitCondition(() -> {		
			General.sleep(50);
			return GrandExchange.close();		
	    }, General.random(8000, 10000))) {
			jGeneral.defaultDynamicSleep();
			return true;
		}
		
		General.println("AutoBanker_Error - Failed to close GE.");
		return false;
	}

	// The code for buying and selling differs a lot because tribots offer API sometimes selects wrong item -
	// - to buy if they part of their name is contained within another item. So we had to code our own offering method.
	public boolean buy(List<Integer> ids, int amount, float multiplier) {

		if (ids.size() == 0) {
			General.println("AutoGE_Error - List is empty.");
			return false;
		}
		
		List<Integer> prices = new ArrayList<Integer>();
		int requiredCash = 0;

		int skipBuy = -1;
		for (int i = 0; i < ids.size(); i++) {
			RSItem bankItem = RS.Banking_find(ids.get(i));
			if (bankItem != null && bankItem.getStack() >= amount) {
				skipBuy = i;
				prices.add(0);
				continue;
			}
			
			if (ids.get(i) == 227) { // Vial of water
				prices.add((int) (GrandExchangeService.tryGetPrice(ids.get(i)).get() * 2.0f));
				requiredCash += GrandExchangeService.tryGetPrice(ids.get(i)).get() * 2.0f;
				continue;
			}
			else if (ids.get(i) == 221) { // Eye of newt
				prices.add((int) (GrandExchangeService.tryGetPrice(ids.get(i)).get() * 5.0f));
				requiredCash += GrandExchangeService.tryGetPrice(ids.get(i)).get() * 5.0f;
				continue;
			}

			prices.add((int) (GrandExchangeService.tryGetPrice(ids.get(i)).get() * multiplier));
			requiredCash += GrandExchangeService.tryGetPrice(ids.get(i)).get() * multiplier;
		}

		int currCash = jGeneral.getCash(false);
		if ((currCash < requiredCash*amount && !withdrawGP(requiredCash*amount - currCash)) ||
			!open() || !jGeneral.deselect()) // deselect() In case GE was already open and we had a spell selected.
			return false;

		for (int j = 0; j < ids.size(); j++) {
			if (skipBuy == j)
				continue;

			// Opens up the buy window.
			int x = 0, rand = General.random(100, 120);
			while (!GEInterfaces.BUY_WINDOW.isVisible()) {
				for(int i = 7; i < 15 ; i++) {
					RSInterface type = Interfaces.get(465, i, 16);
					if(type != null && !type.isHidden() && type.getText().contains("Empty")) {
						RSInterface buyBtn = Interfaces.get(465, i, 3);
						if(buyBtn != null && !buyBtn.isHidden()) {
							if(buyBtn.click()) {
								if (!Timing.waitCondition(GEConditions.GEBuyWindowVisible(), General.random(4000, 7000))) {
									General.println("AutoGE_Error - Buy window not visible.");
									return false;
								}

								jGeneral.defaultDynamicSleep();
								break;
							}
							else {
								General.println("AutoGE_Error - Could not click the buy button.");
								return false;
							}
						}
					}
				}
				
				x++;
				if (x == rand) {
					General.println("AutoGE_Error - Could not find the buy button.");
					return false;
				}

				General.sleep(50);
			}
		
			// Sets up the offer and confirms it.
			if(GEInterfaces.SEARCH_ITEM_INPUT_TEXT.isVisible()) {
				String item = RSItemDefinition.get(ids.get(j)).getName();
				if (item != null) {
					if (!Timing.waitCondition(() -> {
						General.sleep(50);
						return !Interfaces.get(162, 45).isHidden();
					}, General.random(8000, 10000))) {
						General.println("AutoGE_Error - Failed to get ITEM_INPUT_TEXT_BOX.");
						return false;
					}

					Keyboard.typeString(item);
					if(Timing.waitCondition(() -> {
						General.sleep(200);
				        return clickSearchTarget(item);
				    }, 2000)) {
						jGeneral.defaultDynamicSleep();
						if (GrandExchange.setPrice(prices.get(j))) {
							jGeneral.shortDynamicSleep();
							if (GrandExchange.setQuantity(amount)) {
								jGeneral.shortDynamicSleep();
								if (GrandExchange.confirmOffer(true)) {
									jGeneral.defaultDynamicSleep();
									SkippedItemList.add(ids.get(j));
									continue;
								}
							}
						}
					}
				}
			}
			
			General.println("AutoGE_Error - Failed to setup GE offer.");
			return false;
		}
		
		return true;
	}
	
	public static boolean buy(int id, int price, int amount, float multiplier) {
		return buy(id, (int) (price*multiplier), amount);
	}
	
	// The code for buying and selling differs a lot because tribots offer API sometimes selects wrong item -
	// - to buy if they part of their name is contained within another item. So we had to code our own offering method.
	public static boolean buy(int id, int price, int amount) {

		int currCash = jGeneral.getCash(false);
		if ((currCash < price*amount && !withdrawGP(price*amount - currCash)) ||
			!open() || !jGeneral.deselect()) // deselect() In case GE was already open and we had a spell selected.
			return false;	

		jGeneral.defaultDynamicSleep();
		
		// Opens up the buy window.
		int x = 0, rand = General.random(100, 120);
		while (!GEInterfaces.BUY_WINDOW.isVisible()) {
			for(int i = 7; i < 15 ; i++) {
				RSInterface type = Interfaces.get(465, i, 16);
				if(type != null && !type.isHidden() && type.getText().contains("Empty")) {
					RSInterface buyBtn = Interfaces.get(465, i, 3);
					if(buyBtn != null && !buyBtn.isHidden()) {
						if(buyBtn.click()) {
							if (!Timing.waitCondition(GEConditions.GEBuyWindowVisible(), General.random(4000, 7000))) {
								General.println("AutoGE_Error - Buy window not visible.");
								return false;
							}

							jGeneral.defaultDynamicSleep();
							break;
						}
						else {
							General.println("AutoGE_Error - Could not click the buy button.");
							return false;
						}
					}
				}
			}

			x++;
			if (x == rand) {
				General.println("AutoGE_Error - Could not find the buy button.");
				return false;
			}

			General.sleep(50);
		}
		
		// Sets up the offer and confirms it.
		if(GEInterfaces.SEARCH_ITEM_INPUT_TEXT.isVisible()) {
			String item = RSItemDefinition.get(id).getName();
			if (item != null) {
				if (!Timing.waitCondition(() -> {
					General.sleep(50);
					return !Interfaces.get(162, 45).isHidden();
				}, General.random(4000, 5000))) {
					General.println("AutoGE_Error - Failed to get ITEM_INPUT_TEXT_BOX.");
					return false;
				}

				Keyboard.typeString(item);
				if(Timing.waitCondition(() -> {
					General.sleep(200);
			        return clickSearchTarget(item);
			    }, 2000)) {
					jGeneral.defaultDynamicSleep();
					if (GrandExchange.setPrice(price)) {
						jGeneral.shortDynamicSleep();
						if (GrandExchange.setQuantity(amount)) {
							jGeneral.shortDynamicSleep();
							if (GrandExchange.confirmOffer(true)) {
								jGeneral.defaultDynamicSleep();
								return true;
							}
						}
					}
				}
			}
		}

		General.println("AutoGE_Error - Failed to setup GE offer.");
		return false;
	}
	
	public static boolean sell(int id, int price, int amount, float multiplier) {
		return sell(id, (int) (price*multiplier), amount);
	}
	
	public static boolean sell(int id, int price, int amount) {
		int count = Inventory.getCount(id);
		if (amount < 1 && !Banker.withdraw(amount, id))
			return false;
		else if (count < amount && !Banker.withdraw(amount - count, id))
			return false;
				
		if (!open() || !jGeneral.deselect()) // deselect() In case GE was already open and we had a spell selected.
				return false;
		
		if (amount == 0)  // GrandExchange.offer() has no option for amount 0 like Banking.withdraw() does.
			amount = -1;
		
		final int newAmount = amount; // ints must be final inside waitconditions.
		String item = RSItemDefinition.get(id).getName();
		if (Timing.waitCondition(() -> {
			General.sleep(50);
	        return GrandExchange.offer(item, price, newAmount, true);
	    }, 2000)) {
			jGeneral.defaultDynamicSleep();
			return true;
		}

		General.println("AutoGE_Error - Failed to setup GE offer.");
		return false;
	}
	
	// Tracks profit by default.
	public static boolean collectBuy(int id, int amount, int timeToWait) {
		return collectBuy(id, amount, timeToWait, true);
	}
	
	// If we want to track profit we can choose to do so.
	public static boolean collectBuy(int id, int amount, int timeToWait, boolean trackProfit) {
		boolean skip = false;
		if (get().SkippedItemList.size() > 0) {
			skip = true;
			for (int i : get().SkippedItemList) {
				if (i == id) {
					skip = false;
					break;
				}
			}
			
			if (skip)
				return true;
		}

		if (open() && jGeneral.deselect()) {
			// Clicks the offer.
			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.BUY);
		    }, General.random(8000, 10000)) || !getCompleted(timeToWait)) {
				General.println("AutoGE_Error - Failed to click the offer window.");
				return false;
			}

			// Collects the items.
			RSItem[] items = GrandExchange.getCollectItems();
			if (items.length > 0) {
				if (trackProfit)
					Variables.get().removeFromProfit(GrandExchange.getPrice()*amount);
					
				int count = 0;
				RSItem gold = RS.getItem_specific(items, 995);
				if (gold != null) {
					if (trackProfit)
						Variables.get().addToProfit(gold.getStack());

					RSItem invGold = RS.Inventory_find(995);
					if (invGold != null)
						count = invGold.getStack();
					else if (Inventory.isFull()) {
						General.println("AutoGE_Error - Not enough inventory space.");
						return false;
					}
				}
			
				if (Timing.waitCondition(() -> {
					General.sleep(50);
					return GrandExchange.collectItems(COLLECT_METHOD.BANK, items);
				}, General.random(8000, 10000))) {
					if (gold != null && !jGeneral.waitInventoryCount(count, 995, true)) {
						General.println("AutoGE_Error - Could not collect the gold");
						return false;
					}
						
					jGeneral.defaultDynamicSleep_2();
					if (get().SkippedItemList.size() > 0) {
						for (int i : get().SkippedItemList) {
							if (i == id)
								return true;
						}

						get().SkippedItemList.remove(get().SkippedItemList.indexOf(id));
					}
					return true;
				}
			}
					
			General.println("AutoGE_Error - Could not collect the items");
		}

		return false;
	}
	
	// Tracks profit by default.
	public static boolean collectSell(int id, int amount, int timeToWait) {
		return collectSell(id, amount, timeToWait, true);
	}
	
	// If we want to track profit we can choose to do so.
	public static boolean collectSell(int id, int amount, int timeToWait, boolean trackProfit) {
		RSItem invGold = RS.Inventory_find(995);
		if (Inventory.isFull() && invGold == null) {
			General.println("AutoGE_Error - Not enough inventory space.");
			return false;
		}
		
		if (open() && jGeneral.deselect()) {
			// Clicks the offer.
			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.SELL);
		    }, General.random(8000, 10000)) || !getCompleted(timeToWait)) {
				General.println("AutoGE_Error - Failed to click the offer window.");
				return false;
			}

			// Collects the gold.
			RSItem gold = RS.getItem_specific(GrandExchange.getCollectItems(), 995);
			if (gold != null) {
				if (trackProfit)
					Variables.get().addToProfit(gold.getStack());
				
				int count = 0;
				if (invGold != null)
					count = invGold.getStack();
				
				if (Timing.waitCondition(() -> {
					General.sleep(50);
					return GrandExchange.collectItems(COLLECT_METHOD.ITEMS, gold);
				}, General.random(8000, 10000)) && jGeneral.waitInventoryCount(count, 995, true)) {
					jGeneral.defaultDynamicSleep_2();
					return true;
				}
			}
					
			General.println("AutoGE_Error - Could not collect the items");
		}

		return false;
	}
	
	public static boolean clickOffer(int id, int amount, TYPE type) {		
		for (RSGEOffer offer : GrandExchange.getOffers()) {
			if (offer.getStatus() != STATUS.EMPTY && offer.getItemID() == id
			 && (offer.getQuantity() == amount || amount == -1) && offer.getType() == type
			 && offer.click()) {		
				jGeneral.defaultDynamicSleep();
				return true;
			}
			
			General.sleep(150); // This is the minimum mandatory sleep, or else the code will fail.
		}

		return false;
	}
	
	public static boolean getCompleted(int timeToWait) {
		if (GrandExchange.getStatus() != STATUS.IN_PROGRESS &&
			GrandExchange.getStatus() != STATUS.COMPLETED && !Timing.waitCondition(() -> {
			General.sleep(50);
			return GrandExchange.getStatus() == STATUS.IN_PROGRESS || GrandExchange.getStatus() == STATUS.COMPLETED;
		}, General.random(2000, 3000))) {
			General.println("AutoGE_Error - There is no offer.");
			return false;
		}

		long startTime = System.currentTimeMillis();
		if (Timing.waitCondition(() -> {
			General.sleep(50);
	        return GrandExchange.getStatus() == STATUS.COMPLETED;
	    }, timeToWait)) {
			// If we sleep a bit after a longer wait, it will look more human.
			if (((System.currentTimeMillis() - startTime) / 1000.0f) % 60 > 2) {
				jGeneral.defaultDynamicSleep();
			}

			return true;
		}
		
		General.println("AutoGE_Error - Offer is not completed yet.");
		return false;		
	}
	
	public static boolean clickSearchTarget(String item) {
		if(GEInterfaces.SEARCH_RESULT_1.isVisible() &&
		   GEInterfaces.SEARCH_RESULT_1.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_1.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_2.isVisible() &&
				GEInterfaces.SEARCH_RESULT_2.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_2.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_3.isVisible() &&
				GEInterfaces.SEARCH_RESULT_3.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_3.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_4.isVisible() &&
				GEInterfaces.SEARCH_RESULT_4.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_4.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_5.isVisible() &&
				GEInterfaces.SEARCH_RESULT_5.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_5.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_6.isVisible() &&
				GEInterfaces.SEARCH_RESULT_6.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_6.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_7.isVisible() &&
				GEInterfaces.SEARCH_RESULT_7.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_7.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_8.isVisible() &&
				GEInterfaces.SEARCH_RESULT_8.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_8.get().click();
		}
		else if(GEInterfaces.SEARCH_RESULT_9.isVisible() &&
				GEInterfaces.SEARCH_RESULT_9.get().getText().equals(item)) {
			return GEInterfaces.SEARCH_RESULT_9.get().click();
		}

		return false;
	}
}