package scripts.jay_api;

import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GrandExchange;
import org.tribot.api2007.Interfaces;
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
			
			if(!jGeneral.get().deselect())
				return false;
			
			if (!Timing.waitCondition(() -> {
		        return Banking.openBankBooth();
		    }, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Bank screen is not open.");
				return false;
			}

			jGeneral.get().defaultDynamicSleep();
		}
		
		return true;
	}
	
	public static boolean openBankBanker() {
		if (!Banking.isBankScreenOpen()) {
			if(GrandExchange.getWindowState() != null && !Exchanger.close())
				return false;			
			
			if(!jGeneral.get().deselect())
				return false;
			
			if (!Timing.waitCondition(() -> {
		        return Banking.openBankBanker();
		    }, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Bank screen is not open.");
				return false;
			}

			jGeneral.get().defaultDynamicSleep();
		}
		
		return true;
	}
	
	public static boolean openBank() {
		if (!Banking.isBankScreenOpen()) {
			if(GrandExchange.getWindowState() != null && !Exchanger.close())
				return false;
			
			if(!jGeneral.get().deselect())
				return false;
			
			if (!Timing.waitCondition(() -> {
		        return Banking.openBank();
		    }, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Bank screen is not open.");
				return false;
			}

			jGeneral.get().defaultDynamicSleep();
		}
		
		return true;
	}
	
	public static boolean openTab(int index) {
		if (Banking.openTab(index)) {
			if (General.randomBoolean())
				jGeneral.get().superDynamicSleeper(300, 550, 1200, 2200, 6, 13, true);
			else
				jGeneral.get().superDynamicSleeper(300, 550, 600, 1100, 3, 5, true);

			return true;
		}
		
		General.println("AutoBanker_Error - Could not open bank tab.");
		return false;
	}
	
	public static boolean close() {
		if (Banking.close()) {
			jGeneral.get().defaultDynamicSleep();
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

		if (handlerXML.get().isDepositingEquipment()) {
			Banking.depositEquipment();
			jGeneral.get().defaultDynamicSleep();
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
		
		if (handlerXML.get().isDepositing()) {
			if (handlerXML.get().getDepositingExceptions().size() > 0) {
				
				int inven_length = Banking.getAll().length;
				
				int inven_cnt = 0;
				int setup_cnt = 0;
				for (int i = 0; i < handlerXML.get().getDepositingExceptions().size(); i++) {
					
					if (handlerXML.get().isWithdrawing() && handlerXML.get().getWithdrawingItems().size() > 0 && handlerXML.get().getWithdrawingAmount().size() > 0) {
						if (handlerXML.get().getWithdrawingItems().contains(handlerXML.get().getDepositingExceptions().get(i))) {
							setup_cnt = handlerXML.get().getWithdrawingAmount().get(handlerXML.get().getWithdrawingItems().indexOf(handlerXML.get().getDepositingExceptions().get(i)));
							inven_cnt = Inventory.getCount(handlerXML.get().getDepositingExceptions().get(i));
							if (inven_cnt > setup_cnt)
								Banking.deposit(inven_cnt - setup_cnt, handlerXML.get().getDepositingExceptions().get(i));
						}
					}
					
					if (handlerXML.get().isDepositingNoted()) {
						RSItem item = RS.Inventory_find(handlerXML.get().getDepositingExceptions().get(i));
						if (item != null) {
							RSItem item_noted = RS.Inventory_find(item.getDefinition().getNotedItemID());
							if(item_noted != null)
								Banking.depositItem(item_noted, 0);
						}
					}
				}
				
				Banking.depositAllExcept(handlerXML.get().getDepositingExceptions().stream()
						.mapToInt(Integer::intValue)
						.toArray());
				
				if (inven_length == Inventory.getAll().length)
					if (jGeneral.get().waitInventory(inven_length))
						return true; // We already slept after depositing so lets not do it again.					
			}
			else {
				Banking.depositAll();		
				if (jGeneral.get().waitInventory())
					return true; // We already slept after depositing so lets not do it again.
			}
		}

		jGeneral.get().defaultDynamicSleep();
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
		
		if (jGeneral.get().waitInventory())
			return true; // We already slept after depositing so lets not do it again.

		jGeneral.get().defaultDynamicSleep();
		return true;
	}
	
	public static boolean withdraw() {

		if (!openBank() || !isBankLoaded())
			return false;

		if (handlerXML.get().isWithdrawing() && handlerXML.get().getWithdrawingItems().size() > 0 && handlerXML.get().getWithdrawingAmount().size() > 0) {
			
			int setup_cnt = 0;
			int inven_cnt = 0;
			for (int i = 0; i < handlerXML.get().getWithdrawingItems().size(); i++) {
				if (!RSItemDefinition.get(handlerXML.get().getWithdrawingItems().get(i)).isStackable()) {
					setup_cnt += handlerXML.get().getWithdrawingItems().get(i);
					inven_cnt += Inventory.getCount(handlerXML.get().getWithdrawingItems().get(i));
				}
			}
				
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (setup_cnt - inven_cnt)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
			
			int withdrawAmount = 0; 
			int currAmount = 0;
			for (int i = 0; i < handlerXML.get().getWithdrawingItems().size(); i++) {
				withdrawAmount = handlerXML.get().getWithdrawingAmount().get(i);
				
				if (Inventory.findList(handlerXML.get().getWithdrawingItems().get(i)).size() > 0) {
					currAmount = Inventory.getCount(handlerXML.get().getWithdrawingItems().get(i));
					if (currAmount >= withdrawAmount)
						continue;
				}
				else
					currAmount = 0;

				if (RS.Banking_find(handlerXML.get().getWithdrawingItems().get(i)) == null) {
					General.println("AutoBanker_Error - Item not found in the bank.");
					return false;
				}
				else if (RS.Banking_find(handlerXML.get().getWithdrawingItems().get(i)).getStack() <  withdrawAmount - currAmount) {
					General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
					return false;
				}
				else {
					Banking.withdraw(withdrawAmount - currAmount, handlerXML.get().getWithdrawingItems().get(i));
					if (General.randomBoolean())
						jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
					else
						jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
				}
			}
		}

		jGeneral.get().defaultDynamicSleep();
		return true;		
	}
	
	// Supports withdrawing of noted items.
	public static boolean withdraw(int id, int amount) {
		
		if (!openBank() || !isBankLoaded())
			return false;

		boolean noted = false;
		if (RSItemDefinition.get(id).isNoted()) {
			noted = true;
			id = RSItemDefinition.get(id).getSwitchNoteItemID();
		}

		if (RS.Banking_find(id) == null) {
			General.println("AutoBanker_Error - Item not found in the bank.");
			return false;
		}
		
		int currAmount = 0;
		if (!noted) {
			if (!RSItemDefinition.get(id).isStackable()) {	
				if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
					General.println("AutoBanker_Error - Not enough inventory space.");
					return false;
				}
			}
		}
		else {
			Interfaces.get(12, 22).click();
			jGeneral.get().defaultDynamicSleep();
		}

		if (RS.Banking_find(id).getStack() <  (amount - currAmount)) {
			General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
			return false;
		}
		else if (amount > currAmount) {
			Banking.withdraw(amount - currAmount, id);
			if (General.randomBoolean())
				jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);		
		}

		return true;
	}

	// Supports withdrawing of noted items.
	public static boolean withdraw(int amount, List<Integer> ids) {
		
		if (!openBank() || !isBankLoaded())
			return false;

		int inven_length = 0;
		for (int id : ids) {			
			boolean noted = false;
			if (RSItemDefinition.get(id).isNoted()) {
				noted = true;
				id = RSItemDefinition.get(id).getSwitchNoteItemID();
			}

			if (RS.Banking_find(id) == null) {
				General.println("AutoBanker_Error - Item not found in the bank.");
				return false;
			}
		
			int currAmount = 0;
			if (!noted) {
				if (!RSItemDefinition.get(id).isStackable()) {	
					if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
						General.println("AutoBanker_Error - Not enough inventory space.");
						return false;
					}
				}
			}
			else {
				Interfaces.get(12, 22).click();
				jGeneral.get().defaultDynamicSleep();
			}

			if (RS.Banking_find(id).getStack() <  (amount - currAmount)) {
				General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
				return false;
			}
			else if (amount > currAmount) {
				inven_length = Banking.getAll().length;
				Banking.withdraw(amount - currAmount, id);
				if (General.randomBoolean())
					jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
				else
					jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
				
				if (inven_length == Inventory.getAll().length)
					jGeneral.get().waitInventory(inven_length);
				
				if (noted) {
					Interfaces.get(12, 20).click();
					jGeneral.get().defaultDynamicSleep();
				}
			}
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
				jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
		}

		return true;		
	}
	
	public static boolean withdraw_stackException() {
		
		if (!openBank() || !isBankLoaded())
			return false;

		if (handlerXML.get().isWithdrawing() && handlerXML.get().getWithdrawingItems().size() > 0 && handlerXML.get().getWithdrawingItems().size() > 0) {
			
			int setup_cnt = 0;
			int inven_cnt = 0;
			for (int i = 0; i < handlerXML.get().getWithdrawingItems().size(); i++) {
				if (!RSItemDefinition.get(handlerXML.get().getWithdrawingItems().get(i)).isStackable()) {
					setup_cnt += handlerXML.get().getWithdrawingItems().get(i);
					inven_cnt += Inventory.getCount(handlerXML.get().getWithdrawingItems().get(i));
				}
			}
				
			if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (setup_cnt - inven_cnt)) {
				General.println("AutoBanker_Error - Not enough inventory space.");
				return false;
			}
			
			int withdrawAmount = 0; 
			int currAmount = 0;
			for (int i = 0; i < handlerXML.get().getWithdrawingItems().size(); i++) {
				withdrawAmount = handlerXML.get().getWithdrawingAmount().get(i);
				
				if (Inventory.findList(handlerXML.get().getWithdrawingItems().get(i)).size() > 0) {
					currAmount = Inventory.getCount(handlerXML.get().getWithdrawingItems().get(i));
					if (currAmount >= withdrawAmount)
						continue;
				}
				else
					currAmount = 0;

				if (RS.Banking_find(handlerXML.get().getWithdrawingItems().get(i)) == null) {
					General.println("AutoBanker_Error - Item not found in the bank.");
					return false;
				}
				else {
					Banking.withdraw(withdrawAmount - currAmount, handlerXML.get().getWithdrawingItems().get(i));
					if (General.randomBoolean())
						jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
					else
						jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
				}
			}
		}

		jGeneral.get().defaultDynamicSleep();
		return true;
	}
	
	// Supports withdrawing of noted items.
	public static boolean withdraw_stackException(int id, int amount) {
	
		if (!openBank() || !isBankLoaded())
			return false;

		boolean noted = false;
		if (RSItemDefinition.get(id).isNoted()) {
			noted = true;
			id = RSItemDefinition.get(id).getSwitchNoteItemID();
		}

		if (RS.Banking_find(id) == null) {
			General.println("AutoBanker_Error - Item not found in the bank.");
			return false;
		}
		
		int currAmount = 0;
		if (!noted) {
			if (!RSItemDefinition.get(id).isStackable()) {	
				currAmount = Inventory.getCount(id);
				if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
					General.println("AutoBanker_Error - Not enough inventory space.");
					return false;
				}
			}
		}
		else {
			Interfaces.get(12, 22).click();
			jGeneral.get().defaultDynamicSleep();
		}

		if (amount > currAmount) {
			Banking.withdraw(amount - currAmount, id);
			if (General.randomBoolean())
				jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);

			if (noted) {
				Interfaces.get(12, 20).click();
				jGeneral.get().defaultDynamicSleep();
			}
		}

		return true;
	}
	
	// Supports withdrawing of noted items.
	public static boolean withdraw_stackException(int amount, List<Integer> ids) {
		
		if (!openBank() || !isBankLoaded())
			return false;

		int inven_length = 0;
		for (int id : ids) {			
			boolean noted = false;
			if (RSItemDefinition.get(id).isNoted()) {
				noted = true;
				id = RSItemDefinition.get(id).getSwitchNoteItemID();
			}

			if (RS.Banking_find(id) == null) {
				General.println("AutoBanker_Error - Item not found in the bank.");
				return false;
			}
		
			int currAmount = 0;
			if (!noted) {
				if (!RSItemDefinition.get(id).isStackable()) {	
					if ((28 - Inventory.getAllList().size()) == 0 || (28 - Inventory.getAllList().size()) < (amount - currAmount)) {
						General.println("AutoBanker_Error - Not enough inventory space.");
						return false;
					}
				}
			}
			else {
				Interfaces.get(12, 22).click();
				jGeneral.get().defaultDynamicSleep();
			}

			if (amount > currAmount) {
				inven_length = Inventory.getAll().length;
				Banking.withdraw(amount - currAmount, id);
				if (General.randomBoolean())
					jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
				else
					jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);

				if (inven_length == Inventory.getAll().length)
					jGeneral.get().waitInventory(inven_length);
				
				if (noted) {
					Interfaces.get(12, 20).click();
					jGeneral.get().defaultDynamicSleep();
				}
			}
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
				jGeneral.get().superDynamicSleeper(300, 550, 1000, 1800, 12, 22, true);
			else
				jGeneral.get().superDynamicSleeper(300, 550, 500, 800, 5, 8, true);
		}

		return true;		
	}
	
	public static boolean walkToBank() {
		if (handlerXML.get().isWalkingToBank() == false)
			return true;
			
		if (!Banking.isInBank()) {
			jGeneral.get().setCount(0);
			if (Timing.waitCondition(() -> {
				jGeneral.get().occurenceCounter();
	            General.sleep(50); // To save CPU power
	            return DaxWalker.walkToBank();
	        }, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
		}

		General.println("AutoBanker_Error - Could not generate path to bank.");
		return false;
	}
	
	public static boolean walkToBank(WalkingCondition condition) {
		if (handlerXML.get().isWalkingToBank() == false)
			return true;
			
		if (!Banking.isInBank()) {
			jGeneral.get().setCount(0);
			if (Timing.waitCondition(() -> {
				jGeneral.get().occurenceCounter();
	            General.sleep(50); // To save CPU power
	            return DaxWalker.walkToBank(condition);
	        }, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
		}

		General.println("AutoBanker_Error - Could not generate path to bank.");
		return false;	
	}
	
	public static boolean walkToBank(RunescapeBank bank) {
		if (handlerXML.get().isWalkingToBank() == false)
			return true;

		if (bank != null) {
			jGeneral.get().setCount(0);
			if (Timing.waitCondition(() -> {
				jGeneral.get().occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
		}
		
		if (!Banking.isInBank()) 
			General.println("AutoBanker_Error - Could not generate path to bank.");

		return false;
	}
	
	public static boolean walkToBank(RunescapeBank bank, WalkingCondition condition) {
		if (handlerXML.get().isWalkingToBank() == false)
			return true;

		if (bank != null) {
			jGeneral.get().setCount(0);
			if (Timing.waitCondition(() -> {
				jGeneral.get().occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank, condition);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
		}
		
		if (!Banking.isInBank()) 
			General.println("AutoBanker_Error - Could not generate path to bank.");

		return false;		
	}

	// Walks to specified bank if not set to null, otherwise walk to nearest bank.
	public static boolean walkToBank_default(RunescapeBank bank) {
		if (handlerXML.get().isWalkingToBank() == false)
			return true;
		
		if (bank != null) {
			jGeneral.get().setCount(0);
			if (Timing.waitCondition(() -> {
				jGeneral.get().occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
			General.println("AutoBanker_Error - Could not generate path to bank.");
		}
		else if (walkToBank())
			return true;

		return false;
	}
	
	public static boolean walkToBank_default(RunescapeBank bank, WalkingCondition condition) {
		if (handlerXML.get().isWalkingToBank() == false)
			return true;
		
		if (bank != null) {
			jGeneral.get().setCount(0);
			if (Timing.waitCondition(() -> {
				jGeneral.get().occurenceCounter();
				General.sleep(50); // To save CPU power
				return DaxWalker.walkToBank(bank, condition);
			}, 300))
				return true;

			General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
			General.println("AutoBanker_Error - Could not generate path to bank.");
		}
		else if (walkToBank(condition))
			return true;

		return false;
	}
}
