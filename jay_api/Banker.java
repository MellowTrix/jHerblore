package scripts.jay_api;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.dax_api.api_lib.DaxWalker;
import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.jay_api.RS;
import scripts.jay_api.jGeneral;

public class Banker {
	
	public static boolean openBankBooth() {
		if (!Banking.isBankScreenOpen()) {
			if(GrandExchange.getWindowState() != null && !Exchanger.close())
				return false;			
			
			if(!jGeneral.deselect())
				return false;
			
			if (!Timing.waitCondition(() -> {
		        return Banking.openBankBooth();
		    }, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Bank screen is not open.");
				return false;
			}

			jGeneral.defaultDynamicSleep();
		}
		
		return true;
	}
	
	public static boolean openBankBanker() {
		if (!Banking.isBankScreenOpen()) {
			if(GrandExchange.getWindowState() != null && !Exchanger.close())
				return false;			
			
			if(!jGeneral.deselect())
				return false;
			
			if (!Timing.waitCondition(() -> {
		        return Banking.openBankBanker();
		    }, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Bank screen is not open.");
				return false;
			}

			jGeneral.defaultDynamicSleep();
		}
		
		return true;
	}
	
	public static boolean openBank() {
		if (!Banking.isBankScreenOpen()) {
			if(GrandExchange.getWindowState() != null && !Exchanger.close())
				return false;
			
			if(!jGeneral.deselect())
				return false;
			
			if (!Timing.waitCondition(() -> {
		        return Banking.openBank();
		    }, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Bank screen is not open.");
				return false;
			}

			jGeneral.defaultDynamicSleep();
		}
		
		return true;
	}
	
	public static boolean openTab(int index) {
		if (Banking.openTab(index)) {
			if (General.randomBoolean())
				jGeneral.superDynamicSleeper(300, 550, 1200, 2200, 6, 13, true);
			else
				jGeneral.superDynamicSleeper(300, 550, 600, 1100, 3, 5, true);

			return true;
		}
		
		General.println("AutoBanker_Error - Could not open bank tab.");
		return false;
	}
	
	public static boolean close() {
		if (Banking.close()) {
			jGeneral.defaultDynamicSleep();
			return true;
		}
		
		General.println("AutoBanker_Error - Failed to close bank.");
		return false;
	}
	
	public static boolean isInBank() {
		if (Timing.waitCondition(() -> {
	        General.sleep(200, 800);
	        return Banking.isInBank();
	    }, General.random(12000, 15000)))
			return true;

		return false;
	}
	
	public static boolean isBankLoaded() {
		if (!Banking.isBankLoaded()) {
			Banking.waitUntilLoaded(General.random(1000,3000));
			if (!Banking.isBankLoaded()) {
				General.println("AutoBanker_Error - Bank is not loaded.");
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean depositEquipment() {

		if (Equipment.getItems().length == 0) {
			General.println("AutoBanker_Error - You have no items equipped.");
			return false;
		}
			
		if (!openBank() || !isBankLoaded())
			return false;
		
		if (handlerXML.setup_depositing_equipment) {
			Banking.depositEquipment();
			jGeneral.defaultDynamicSleep();
		}

		return true;
	}
	
	// Takes 'setup_depositing_exceptions' into account
	public static boolean depositItems() {
		
		if (Inventory.getAll().length == 0) {
			General.println("AutoBanker_Error - You have no items in your inventory.");
			return false;
		}
		
		if (!openBank() || !isBankLoaded())
			return false;
		
		if (handlerXML.setup_depositing) {
			if (handlerXML.setup_depositing_exceptions.size() > 0) {
				
				int inven_length = Banking.getAll().length;
				
				int inven_cnt = 0;
				int setup_cnt = 0;
				for (int i = 0; i < handlerXML.setup_depositing_exceptions.size(); i++) {
					
					if (handlerXML.setup_withdrawing && handlerXML.setup_withdrawing_items.size() > 0 && handlerXML.setup_withdrawing_amount.size() > 0) {
						if (handlerXML.setup_withdrawing_items.contains(handlerXML.setup_depositing_exceptions.get(i))) {
							setup_cnt = handlerXML.setup_withdrawing_amount.get(handlerXML.setup_withdrawing_items.indexOf(handlerXML.setup_depositing_exceptions.get(i)));
							inven_cnt = Inventory.getCount(handlerXML.setup_depositing_exceptions.get(i));
							if (inven_cnt > setup_cnt)
								Banking.deposit(inven_cnt - setup_cnt, handlerXML.setup_depositing_exceptions.get(i));
						}
					}
					
					if (handlerXML.setup_depositing_noted) {
						RSItem item = RS.Inventory_find(handlerXML.setup_depositing_exceptions.get(i));
						if (item != null) {
							RSItem item_noted = RS.Inventory_find(item.getDefinition().getNotedItemID());
							if(item_noted != null)
								Banking.depositItem(item_noted, 0);
						}
					}
				}
				
				Banking.depositAllExcept(handlerXML.setup_depositing_exceptions.stream()
						.mapToInt(Integer::intValue)
						.toArray());
				
				if (inven_length == Inventory.getAll().length)
					if (jGeneral.waitInventory(inven_length))
						return true; // We already slept after depositing so lets not do it again.					
			}
			else {
				Banking.depositAll();		
				if (jGeneral.waitInventory())
					return true; // We already slept after depositing so lets not do it again.
			}
		}

		jGeneral.defaultDynamicSleep();
		return true;	
	}	

	// Does not take 'setup_depositing_exceptions' into account
	public static boolean depositItemsAll() {
		if (Inventory.getAll().length == 0) {
			General.println("AutoBanker_Error - You have no items in your inventory.");
			return false;
		}
		
		if (!openBank() || !isBankLoaded())
			return false;
		
		Banking.depositAll();
		
		if (jGeneral.waitInventory())
			return true; // We already slept after depositing so lets not do it again.
		
		jGeneral.defaultDynamicSleep();
		return true;
	}
	
	public static boolean withdraw() {

		if (!openBank() || !isBankLoaded())
			return false;

		if (handlerXML.setup_withdrawing && handlerXML.setup_withdrawing_items.size() > 0 && handlerXML.setup_withdrawing_amount.size() > 0) {
			
			int setup_cnt = 0;
			int inven_cnt = 0;
			for (int i = 0; i < handlerXML.setup_withdrawing_items.size(); i++) {
				if (!RSItemDefinition.get(handlerXML.setup_withdrawing_items.get(i)).isStackable()) {
					setup_cnt += handlerXML.setup_withdrawing_amount.get(i);
					inven_cnt += Inventory.getCount(handlerXML.setup_withdrawing_items.get(i));
				}
			}
				
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (setup_cnt - inven_cnt)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
			
			int withdrawAmount = 0; 
			int currAmount = 0;
			for (int i = 0; i < handlerXML.setup_withdrawing_items.size(); i++) {
				withdrawAmount = handlerXML.setup_withdrawing_amount.get(i);
				
				if (Inventory.findList(handlerXML.setup_withdrawing_items.get(i)).size() > 0) {
					currAmount = Inventory.getCount(handlerXML.setup_withdrawing_items.get(i));
					if (currAmount >= withdrawAmount)
						continue;
				}
				else
					currAmount = 0;

				if (RS.Banking_find(handlerXML.setup_withdrawing_items.get(i)) == null) {
					General.println("AutoBanker_Error - Item not found in the bank.");
					return false;
				}
				else if (RS.Banking_find(handlerXML.setup_withdrawing_items.get(i)).getStack() <  withdrawAmount - currAmount) {
					General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
					return false;
				}
				else {
					Banking.withdraw(withdrawAmount - currAmount, handlerXML.setup_withdrawing_items.get(i));
					if (General.randomBoolean())
						jGeneral.superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
					else
						jGeneral.superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
				}
			}
		}

		jGeneral.defaultDynamicSleep();
		return true;		
	}
	
	public static boolean withdraw(int id, int amount) {
		
		if (!openBank() || !isBankLoaded())
			return false;

		if (RS.Banking_find(id) == null) {
			General.println("AutoBanker_Error - Item not found in the bank.");
			return false;
		}
		
		int currAmount = 0;
		if (!RSItemDefinition.get(id).isStackable()) {	
			currAmount = Inventory.getCount(id);
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
		}

		if (RS.Banking_find(id).getStack() <  (amount - currAmount)) {
			General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
			return false;
		}
		else if (amount > currAmount) {
			Banking.withdraw(amount - currAmount, id);
			if (General.randomBoolean())
				jGeneral.superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
		}

		return true;
	}
	
	public static boolean withdraw(String name, int amount) {

		if (!openBank() || !isBankLoaded())
			return false;

		RSItem item = RS.Banking_find(name);
		if (item == null) {
			General.println("AutoBanker_Error - Item not found in the bank.");
			return false;
		}
		
		int currAmount = 0;
		if (!RSItemDefinition.get(item.getID()).isStackable()) {	
			currAmount = Inventory.getCount(item.getID());
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
		}

		if (item.getStack() <  (amount - currAmount)) {
			General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
			return false;
		}
		else if (amount > currAmount) {
			Banking.withdraw(amount - currAmount, item.getID());
			if (General.randomBoolean())
				jGeneral.superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
		}

		return true;		
	}
	
	public static boolean withdraw_stackException() {
		
		if (!openBank() || !isBankLoaded())
			return false;

		if (handlerXML.setup_withdrawing && handlerXML.setup_withdrawing_items.size() > 0 && handlerXML.setup_withdrawing_amount.size() > 0) {
			
			int setup_cnt = 0;
			int inven_cnt = 0;
			for (int i = 0; i < handlerXML.setup_withdrawing_items.size(); i++) {
				if (!RSItemDefinition.get(handlerXML.setup_withdrawing_items.get(i)).isStackable()) {
					setup_cnt += handlerXML.setup_withdrawing_amount.get(i);
					inven_cnt += Inventory.getCount(handlerXML.setup_withdrawing_items.get(i));
				}
			}
				
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (setup_cnt - inven_cnt)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
			
			int withdrawAmount = 0; 
			int currAmount = 0;
			for (int i = 0; i < handlerXML.setup_withdrawing_items.size(); i++) {
				withdrawAmount = handlerXML.setup_withdrawing_amount.get(i);
				
				if (Inventory.findList(handlerXML.setup_withdrawing_items.get(i)).size() > 0) {
					currAmount = Inventory.getCount(handlerXML.setup_withdrawing_items.get(i));
					if (currAmount >= withdrawAmount)
						continue;
				}
				else
					currAmount = 0;

				if (RS.Banking_find(handlerXML.setup_withdrawing_items.get(i)) == null) {
					General.println("AutoBanker_Error - Item not found in the bank.");
					return false;
				}
				else {
					Banking.withdraw(withdrawAmount - currAmount, handlerXML.setup_withdrawing_items.get(i));
					if (General.randomBoolean())
						jGeneral.superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
					else
						jGeneral.superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
				}
			}
		}

		jGeneral.defaultDynamicSleep();
		return true;
	}
	
	public static boolean withdraw_stackException(int id, int amount) {
	
		if (!openBank() || !isBankLoaded())
			return false;

		if (RS.Banking_find(id) == null) {
			General.println("AutoBanker_Error - Item not found in the bank.");
			return false;
		}
		
		int currAmount = 0;
		if (!RSItemDefinition.get(id).isStackable()) {
			currAmount = Inventory.getCount(id);
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
		}

		if (amount > currAmount) {
			Banking.withdraw(amount - currAmount, id);
			if (General.randomBoolean())
				jGeneral.superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
		}

		return true;
	}

	public static boolean withdraw_stackException(String name, int amount) {
		
		if (!openBank() || !isBankLoaded())
			return false;

		RSItem item = RS.Banking_find(name);
		if (item == null) {
			General.println("AutoBanker_Error - Item not found in the bank.");
			return false;
		}

		int currAmount = 0;
		if (!RSItemDefinition.get(item.getID()).isStackable()) {
			currAmount = Inventory.getCount(item.getID());
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
		}

		if (amount > currAmount) {
			Banking.withdraw(amount - currAmount, item.getID());
			if (General.randomBoolean())
				jGeneral.superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
		}

		return true;		
	}
	
	public static boolean walkToBank() {
		if (handlerXML.walking_walktobank == true)
			return true;
			
		if (!Banking.isInBank()) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
	            General.sleep(50); // To save CPU power
	            return DaxWalker.walkToBank();
	        }, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
		}

		return false;
	}
	
	public static boolean walkToBank(WalkingCondition condition) {
		if (handlerXML.walking_walktobank == true)
			return true;
			
		if (!Banking.isInBank()) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
	            General.sleep(50); // To save CPU power
	            return DaxWalker.walkToBank(condition);
	        }, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
		}

		return false;	
	}
	
	public static boolean walkToBank(RunescapeBank bank) {
		if (handlerXML.walking_walktobank == true)
			return true;

		if (bank != null) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
		}
		
		if (!Banking.isInBank()) 
			General.println("AutoBanker_Error - Could not generate path to bank.");

		return false;
	}
	
	public static boolean walkToBank(RunescapeBank bank, WalkingCondition condition) {
		if (handlerXML.walking_walktobank == true)
			return true;

		if (bank != null) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank, condition);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
		}
		
		if (!Banking.isInBank()) 
			General.println("AutoBanker_Error - Could not generate path to bank.");

		return false;		
	}

	// Walks to specified bank if not set to null, otherwise walk to nearest bank.
	public static boolean walkToBank_default(RunescapeBank bank) {
		if (handlerXML.walking_walktobank == true)
			return true;
		
		if (bank != null) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
		}
		else if (!Banking.isInBank()) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank();
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
			General.println("AutoBanker_Error - Could not generate path to bank.");
		}

		return false;
	}
	
	public static boolean walkToBank_default(RunescapeBank bank, WalkingCondition condition) {
		if (handlerXML.walking_walktobank == true)
			return true;
		
		if (bank != null) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank, condition);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
		}
		else if (!Banking.isInBank()) {
			jGeneral.Count = 0;
			if (Timing.waitCondition(() -> {
				jGeneral.occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank();
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.Count);
			General.println("AutoBanker_Error - Could not generate path to bank.");
		}

		return false;
	}
}
