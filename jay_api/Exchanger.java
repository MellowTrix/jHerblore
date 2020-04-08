package scripts.jay_api;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Banking;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.GrandExchange.COLLECT_METHOD;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;

import scripts.jay_api.fluffeespaint.Variables;
import scripts.jay_api.utils.GEConditions;
import scripts.jay_api.wastedbroGE.GrandExchangeService;

import org.tribot.api2007.types.RSGEOffer.STATUS;
import org.tribot.api2007.types.RSGEOffer.TYPE;
import org.tribot.api2007.types.RSInterface;

public class Exchanger {
	
    private static final Exchanger EXCHANGER = new Exchanger();
    public static Exchanger get() {
        return EXCHANGER;
    }
    
    private int skipBuy = 0;
    private List<Integer> SkippedItemList = new ArrayList<Integer>();

	public static boolean withdrawGP(int amount) {

		if (GrandExchange.getWindowState() != null) {
			if (!close())
				return false;
		}
		else if (!Banker.openBank())
			return false;

		if (Banker.withdraw(995, amount)) {
			jGeneral.get().defaultDynamicSleep();
			return true;
		}
		
		General.println("AutoGE_Error - Not enough gold in bank.");
		return false;
	}
	
	public static boolean open() {
		if (GrandExchange.getWindowState() == null) {
			if (Banking.isBankScreenOpen() && !Banker.close())
				return false;
				
			RSNPC npc = RS.NPCs_findNearest("Grand Exchange Clerk");
			if (npc != null) {
				if(!jGeneral.get().deselect())
					return false;
				
				if (DynamicClicking.clickRSNPC(npc, "Exchange Grand Exchange Clerk")) {
					jGeneral.get().defaultDynamicSleep();
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
		if (jGeneral.get().deselect() && GrandExchange.close()) {
			jGeneral.get().defaultDynamicSleep();
			return true;
		}
		
		General.println("AutoBanker_Error - Failed to close GE.");
		return false;
	}

	// The code for buying and selling differs a lot because tribots offer API sometimes selects wrong item -
	// - to buy if they part of their name of contained in another item. So we had to code our own offering method.
	public static boolean buy(int id, int price, int amount) {

		int currCash = jGeneral.get().getCash(false);
		if ((currCash < price*amount && !withdrawGP(price*amount - currCash)) ||
			!open() || !jGeneral.get().deselect()) // deselect() In case GE was already open and we had a spell selected.
			return false;	

		// Opens up the buy window.
		jGeneral.get().defaultDynamicSleep();
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

						jGeneral.get().defaultDynamicSleep();
					}
					else {
						General.println("AutoGE_Error - Could not click the buy button.");
						return false;
					}
				}
			}
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
					jGeneral.get().defaultDynamicSleep();
					if (GrandExchange.setPrice(price)) {
						jGeneral.get().shortDynamicSleep();
						if (GrandExchange.setQuantity(amount)) {
							jGeneral.get().shortDynamicSleep();
							if (GrandExchange.confirmOffer(true)) {
								jGeneral.get().defaultDynamicSleep();
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

	public static boolean buy(int id, int price, int amount, float multiplier) {
		return buy(id, (int) (price*multiplier), amount);
	}
	
	// The code for buying and selling differs a lot because tribots offer API sometimes selects wrong item -
	// - to buy if they part of their name of contained in another item. So we had to code our own offering method.
	public boolean buy(List<Integer> ids, int amount, float multiplier) {

		if (ids.size() == 0) {
			General.println("AutoGE_Error - List is empty.");
			return false;
		}
		
		List<Integer> prices = new ArrayList<Integer>();
		int requiredCash = 0;

		skipBuy = -1;
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

		int currCash = jGeneral.get().getCash(false);
		if ((currCash < requiredCash*amount && !withdrawGP(requiredCash*amount - currCash)) ||
			!open() || !jGeneral.get().deselect()) // deselect() In case GE was already open and we had a spell selected.
			return false;
		
		jGeneral.get().defaultDynamicSleep();

		for (int j = 0; j < ids.size(); j++) {
			if (skipBuy == j)
				continue;
				
			// Opens up the buy window.
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

							jGeneral.get().defaultDynamicSleep();
						}
						else {
							General.println("AutoGE_Error - Could not click the buy button.");
							return false;
						}
					}
				}
			}
		
			// Sets up the offer and confirms it.
			if(GEInterfaces.SEARCH_ITEM_INPUT_TEXT.isVisible()) {
				String item = RSItemDefinition.get(ids.get(j)).getName();
				if (item != null) {
					if (!Timing.waitCondition(() -> {
						General.sleep(50);
						return !Interfaces.get(162, 45).isHidden();
					}, General.random(2000, 3000))) {
						General.println("AutoGE_Error - Failed to get ITEM_INPUT_TEXT_BOX.");
						return false;
					}
					
					Keyboard.typeString(item);
					if(Timing.waitCondition(() -> {
						General.sleep(200);
				        return clickSearchTarget(item);
				    }, 2000)) {
						jGeneral.get().defaultDynamicSleep();
						if (GrandExchange.setPrice(prices.get(j))) {
							jGeneral.get().shortDynamicSleep();
							if (GrandExchange.setQuantity(amount)) {
								jGeneral.get().shortDynamicSleep();
								if (GrandExchange.confirmOffer(true)) {
									jGeneral.get().defaultDynamicSleep();
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
	
	public static boolean sell(int id, int price, int amount) {
		
		RSItemDefinition def = RSItemDefinition.get(id);
		if (def != null) {
			if (!def.isStackable())
				id = def.getSwitchNoteItemID();
		}
		else {
			General.println("AutoGE_Error - Invalid ID.");
			return false;
		}

		if ((Inventory.getCount(id) < amount && !Banker.withdraw(id, amount)) || 
		    !open() || !jGeneral.get().deselect()) // deselect() In case GE was already open and we had a spell selected.
				return false;

		String item = RSItemDefinition.get(id).getName();
		if (Timing.waitCondition(() -> {
			General.sleep(50);
	        return GrandExchange.offer(item, price, amount, true);
	    }, 2000)) {
			jGeneral.get().defaultDynamicSleep();
			return true;
		}

		General.println("AutoGE_Error - Failed to setup GE offer.");
		return false;
	}

	public static boolean sell(int id, int price, int amount, float multiplier) {
		return sell(id, (int) (price*multiplier), amount);
	}
	
	public boolean collectBuy(int id, int amount) {
		boolean skip = false;
		if (SkippedItemList.size() > 0) {
			skip = true;
			for (int i : SkippedItemList) {
				if (i == id)
					skip = false;
			}
		}
		
		if (skip)
			return true;
		
		if (open() && jGeneral.get().deselect()) {

			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.BUY);
		    }, 2000) || !getCompleted())
				return false;

			RSItem[] items = GrandExchange.getCollectItems();			
			if (items != null && GrandExchange.collectItems(COLLECT_METHOD.BANK, items)) {
				if (RS.getItem_specific(items, 995) != null)
					jGeneral.get().waitInventory(Inventory.getAll().length);

				jGeneral.get().defaultDynamicSleep();
				if (SkippedItemList.size() > 0) {
					for (int i : SkippedItemList) {
						General.println(i);
						if (i == id)
							return true;
					}

					SkippedItemList.remove(SkippedItemList.indexOf(id));
				}
				return true;
			}
					
			General.println("AutoGE_Error - Could not collect the items");
		}

		return false;
	}
	
	// If we want to subtract profit.
	public boolean collectBuy_removeProfit(int id, int amount) {
		boolean skip = false;
		if (SkippedItemList.size() > 0) {
			skip = true;
			for (int i : SkippedItemList) {
				if (i == id)
					skip = false;
			}
		}
		
		if (skip)
			return true;
			
		if (open() && jGeneral.get().deselect()) {

			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.BUY);
		    }, 2000) || !getCompleted())
				return false;

			RSItem[] items = GrandExchange.getCollectItems();			
			if (items != null && GrandExchange.collectItems(COLLECT_METHOD.BANK, items)) {
				RSItem gold = RS.getItem_specific(items, 995);
				if (gold != null)
					Variables.get().removeFromProfit(id, amount, handlerXML.get().getGE_mult_buy(), gold.getStack());
				else
					Variables.get().removeFromProfit(id, amount, handlerXML.get().getGE_mult_buy(), 0);
				
				jGeneral.get().waitInventory(Inventory.getAll().length);				
				jGeneral.get().defaultDynamicSleep();
				if (SkippedItemList.size() > 0) {
					for (int i : SkippedItemList) {
						General.println(i);
						if (i == id)
							return true;
					}

					SkippedItemList.remove(SkippedItemList.indexOf(id));
				}
				return true;
			}
					
			General.println("AutoGE_Error - Could not collect the items");
		}

		return false;
	}
	
	// If we want to add profit.
	public static boolean collectSell_addProfit(int id, int amount) {

		if (open() && jGeneral.get().deselect()) {

			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.SELL);
		    }, 2000) || !getCompleted())
				return false;

			RSItem gold = RS.getItem_specific(GrandExchange.getCollectItems(), 995);
			if (gold != null && GrandExchange.collectItems(COLLECT_METHOD.BANK, gold)) {
				Variables.get().addToProfit(gold.getStack());
				jGeneral.get().waitInventory(Inventory.getAll().length);
				return true;
			}
					
			General.println("AutoGE_Error - Could not collect the items");
		}

		return false;
	}
	
	public static boolean collectSell(int id, int amount) {

		if (open() && jGeneral.get().deselect()) {

			if (!Timing.waitCondition(() -> {
				General.sleep(50);
		        return clickOffer(id, amount, TYPE.SELL);
		    }, 2000) || !getCompleted())
				return false;

			RSItem gold = RS.getItem_specific(GrandExchange.getCollectItems(), 995);
			if (gold != null && GrandExchange.collectItems(COLLECT_METHOD.BANK, gold)) {
				jGeneral.get().waitInventory(Inventory.getAll().length);
				return true;
			}
					
			General.println("AutoGE_Error - Could not collect the items");
		}

		return false;
	}
	
	public static boolean clickOffer(int id, int amount, TYPE type) {
		for (RSGEOffer offer : GrandExchange.getOffers()) {
			if (offer.getStatus() != STATUS.EMPTY && offer.getItemID() == id
			 && offer.getQuantity() == amount && offer.getType() == type
			 && offer.click()) {
				jGeneral.get().defaultDynamicSleep();
				return true;
			}		
		}
		
		General.println("AutoGE_Error - Failed to click the offer window.");
		return false;
	}
	
	public static boolean getCompleted() {
		if (Timing.waitCondition(() -> {
			General.sleep(50);
	        return GrandExchange.getStatus() == STATUS.COMPLETED;
	    }, 10000))
			return true;
		
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
