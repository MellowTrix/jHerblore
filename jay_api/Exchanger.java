package scripts.jay_api;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.GrandExchange.COLLECT_METHOD;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSGEOffer.TYPE;

public class Exchanger {
	
	public static boolean withdrawGP(int amount) {

		if (GrandExchange.getWindowState() != null)
			if (!close())
				return false;
		else if (!Banker.openBank())
			return false;

		if (Banker.withdraw(995, amount)) {
			jGeneral.defaultDynamicSleep();
			return true;
		}
		
		General.println("AutoGE_Error - Not enough gold in bank.");
		return false;
	}
	
	public static boolean open() {
		if (GrandExchange.getWindowState() == null) {
			if (Banking.isBankScreenOpen())
				if (!Banker.close())
					return false;
				
			RSNPC npc = RS.NPCs_findNearest("Grand Exchange Clerk");
			if (npc != null) {
				if(!jGeneral.deselect())
					return false;
				
				if (DynamicClicking.clickRSNPC(npc, "Exchange Grand Exchange Clerk")) {
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
		if (!jGeneral.deselect())
			return false;
			
		if (GrandExchange.close()) {
			jGeneral.defaultDynamicSleep();

			return true;
		}
		
		General.println("AutoBanker_Error - Failed to close GE.");
		return false;
	}

	public static boolean buy(int id, int price, int amount) {

		int currCash = jGeneral.getCash(false);
		if (currCash < price*amount)
			if (!withdrawGP(price*amount - currCash))
				return false;	
		
		if (!open())
			return false;
		
		if (!jGeneral.deselect()) // In case GE was already open and we had a spell selected.
			return false;
		
		String item = RSItemDefinition.get(id).getName();
		if (item != null) {
			if (Timing.waitCondition(() -> {
				General.sleep(50);
		        return GrandExchange.offer(item, price, amount, false);
		    }, 2000)) {
				jGeneral.defaultDynamicSleep();
				return true;
			}

			General.println("AutoGE_Error - Failed to setup GE offer.");
			return false;
		}	

		General.println("AutoGE_Error - Could not find item.");
		return false;
	}

	public static boolean buy(int id, int price, int amount, float multiplier) {
		return buy(id, (int) (price*multiplier), amount);
	}
	
	public static boolean sell(int id, int price, int amount) {
		
		int currAmount = Inventory.getCount(id);
		if (currAmount < amount) {
			if (!Banker.withdraw(id, amount))
				return false;
		}
		
		if (!open())
			return false;
		
		if (!jGeneral.deselect()) // In case GE was already open and we had a spell selected.
			return false;
		
		String item = RSItemDefinition.get(id).getName();
		if (item != null) {
			if (Timing.waitCondition(() -> {
				General.sleep(50);
		        return GrandExchange.offer(item, price, amount, true);
		    }, 2000)) {
				jGeneral.defaultDynamicSleep();
				return true;
			}

			General.println("AutoGE_Error - Failed to setup GE offer.");
			return false;
		}	

		General.println("AutoGE_Error - Could not find item.");
		return false;
	}

	public static boolean sell(int id, int price, int amount, float multiplier) {
		return buy(id, (int) (price*multiplier), amount);
	}
	
	public static boolean collectBuy(int id, int amount) {

		if (open()) {

			if (!jGeneral.deselect()) // In case GE was already open and we had a spell selected.
				return false;

			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.BUY);
		    }, 2000))
				return false;

			if (!getCompleted())
				return false;

			RSItem[] items = GrandExchange.getCollectItems();
			if (items != null) {
				if (GrandExchange.collectItems(COLLECT_METHOD.BANK, items)) {
					jGeneral.defaultDynamicSleep();
					return true;
				}
			}
					
			General.println("AutoGE_Error - Could not collect the items");
			return false;
		}

		return false;
	}

	public static boolean collectSell(int id, int amount) {

		if (open()) {

			if (!jGeneral.deselect()) // In case GE was already open and we had a spell selected.
				return false;

			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.SELL);
		    }, 2000))
				return false;

			if (!getCompleted())
				return false;

			RSItem[] items = GrandExchange.getCollectItems();
			if (items != null) {
				if (GrandExchange.collectItems(COLLECT_METHOD.BANK, items)) {
					jGeneral.defaultDynamicSleep();
					return true;
				}
			}
					
			General.println("AutoGE_Error - Could not collect the items");
			return false;
		}

		return false;
	}
	
	public static boolean clickOffer(int id, int amount, TYPE type) {
		for (RSGEOffer offer : GrandExchange.getOffers()) {
			if (offer.getStatus() != STATUS.EMPTY) {
				if (offer.getItemID() == id && offer.getQuantity() == amount && offer.getType() == type) {
					if (offer.click()) {
						jGeneral.defaultDynamicSleep();
						return true;
					}
				}
			}		
		}
		
		General.println("AutoGE_Error - Failed to click the offer window.");
		return false;
	}
	
	public static boolean getCompleted() {
		if (Timing.waitCondition(() -> {
			General.sleep(50);
	        return GrandExchange.getStatus() == STATUS.COMPLETED;
	    }, 2000))
			return true;
		
		General.println("AutoGE_Error - Offer is not completed yet.");
		return false;		
	}
}
