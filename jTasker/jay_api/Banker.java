package scripts.jTasker.jay_api;

import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.dax_api.api_lib.DaxWalker;
import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.dax_api.walker_engine.WalkingCondition;

public class Banker {
	
    private static final Banker BANKER = new Banker();
    public static Banker get() {
        return BANKER;
    }

	private boolean clicked = false;
	
	public static boolean openBankBooth() {
		return openBankMethod(2);
	}
	
	public static boolean openBankBanker() {
		return openBankMethod(1);
	}

	public static boolean openBank() {
		return openBankMethod(0);
	}

	// Called by openBank(), openBankBanker(), and openBankBooth().
	public static boolean openBankMethod(int i) {
		if (!Banking.isBankScreenOpen()) {		
			if(!Exchanger.close() || !jGeneral.deselect())
				return false;
			
			if (!Timing.waitCondition(() -> {
				if (i == 0)
					return Banking.openBank();
				else if (i == 1)
					return Banking.openBankBanker();

				General.sleep(50);
				return Banking.openBankBooth();
		    }, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Could not open bank.");
				return false;
			}
			
			jGeneral.defaultDynamicSleep();
		}
		
		return true;
	}
	
	public static boolean openTab(int index) {
		if (!Timing.waitCondition(() -> {
			General.sleep(50);
			return Banking.openTab(index);
	    }, General.random(8000, 10000))) {
			General.println("AutoBanker_Error - Could not open bank tab.");
			return false;
		}
		
		jGeneral.defaultDynamicSleep();
		return true;
	}
	
	public static boolean close() {
		if (Banking.isBankScreenOpen()) {
			if (!Timing.waitCondition(() -> {
				General.sleep(50);		
				return Banking.close();
			}, General.random(8000, 10000))) {
				General.println("AutoBanker_Error - Failed to close bank.");
				return false;
			}
			
			jGeneral.defaultDynamicSleep();
		}

		get().clicked = false;
		return true;
	}

	// Used only after DaxWalking or other types of walking, otherwise use the standard tribot version.
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
			return true;
		}
			
		if (!openBank() || !isBankLoaded())
			return false;

		if (!Timing.waitCondition(() -> {
			General.sleep(50);
			return Banking.depositEquipment();
	    }, General.random(8000, 10000))) {
			General.println("AutoBanker_Error - Failed to deposit equipment.");
			return false;
		}
		
		jGeneral.defaultDynamicSleep();
		return true;
	}
	
	public static boolean depositAllExcept(List<Integer> ids) {
		return depositAllExcept(ids, true);
	}

	// Used for passing lists where we want to deposit the noted versions of the items in the list if they are present in the inventory as well.
	public static boolean depositAllExcept(List<Integer> ids, boolean depositNoted) {		
		if (Inventory.getAll().length == 0) {
			//General.println("AutoBanker_Error - You have no items in your inventory.");
			return true;
		}
		
		if (!openBank() || !isBankLoaded())
			return false;

		if (ids.size() > 0) {
			if (!depositNoted) {
				List<Integer> temp_list = ids;
				for (int i = 0; i < temp_list.size(); i++) {
					RSItemDefinition item = RSItemDefinition.get(ids.get(i));
					if (item != null) {
						RSItem item_noted = RS.Inventory_find(item.getNotedItemID());
						if(item_noted != null) {
							ids.add(item_noted.getID());
						}
					}
				}
			}
			
			int inven_count = 0;
			for (RSItem item : Inventory.getAllList()) {
				inven_count += Inventory.getCount(item.getID());
			}

			int count = Banking.depositAllExcept(ids.stream()
					.mapToInt(Integer::intValue)
					.toArray());
			
			jGeneral.get().setCount(0);
			if (Timing.waitCondition(() -> {
				if (jGeneral.get().getCount() != 0)
					General.sleep(200); jGeneral.defaultDynamicSleep();

				jGeneral.get().occurenceCounter();
				return 	Banking.depositAllExcept(ids.stream()
								.mapToInt(Integer::intValue)
								.toArray()) == 0;
			}, 3000)) {
				if (count != 0 && !jGeneral.waitInventoryCount(inven_count - count, false)) {
					General.println("AutoBanker_Error - Failed to deposit all items.");
					return false;
				}

				jGeneral.defaultDynamicSleep();
				return true;
			}
			
			General.println("AutoBanker_Error - Failed to deposit all items.");
			return false;
		}

		General.println("AutoBanker_Error - No items specified for depositing.");
		return false;	
	}	

	public static boolean depositAll() {
		if (Inventory.getAll().length == 0) {
			//General.println("AutoBanker_Error - You have no items in your inventory.");
			return true;
		}
		
		if (!openBank() || !isBankLoaded())
			return false;

		int inven_count = 0;
		for (RSItem item : Inventory.getAllList()) {
			inven_count += Inventory.getCount(item.getID());
		}

		// Banking.depositAll() takes item stack into account, so we have to use Inventory.getCount() on all our items in our inventory to get the equivalent number.
		final int count = inven_count;
		
		jGeneral.get().setCount(0);
		if (Timing.waitCondition(() -> {
			if (jGeneral.get().getCount() != 0) {
				// Banking.depositAll() can return close to half a second before the Inventory gets updated.
				General.sleep(200); jGeneral.defaultDynamicSleep();
				
				if (Inventory.getAll().length == 0)
					return true;
			}

			jGeneral.get().occurenceCounter();
			return count == Banking.depositAll();
		}, 3000) && jGeneral.waitInventory()) {
			jGeneral.defaultDynamicSleep();
			return true;
		}

		General.println("AutoBanker_Error - Failed to deposit all items.");
		return false;
	}

	public static boolean deposit(int amount, int id) {
		if (Inventory.getAll().length == 0) {
			//General.println("AutoBanker_Error - You have no items in your inventory.");
			return true;
		}

		if (!openBank() || !isBankLoaded())
			return false;

		int count = Inventory.getCount(id);

		jGeneral.get().setCount(0);
		if (Timing.waitCondition(() -> {
			if (jGeneral.get().getCount() != 0)
				General.sleep(200, 300);

			jGeneral.get().occurenceCounter();
			return Banking.deposit(amount, id);
		}, 3000) && jGeneral.waitInventoryCount(count, id, true)) {
			jGeneral.shortDynamicSleep();
			return true;
		}

		General.println("AutoBanker_Error - Failed to deposit the specified item.");
		return false;
	}
	
	public static boolean deposit(int amount, String name) {
		if (Inventory.getAll().length == 0) {
			//General.println("AutoBanker_Error - You have no items in your inventory.");
			return true;
		}

		if (!openBank() || !isBankLoaded())
			return false;

		int count = Inventory.getCount(name);

		jGeneral.get().setCount(0);
		if (Timing.waitCondition(() -> {
			if (jGeneral.get().getCount() != 0)
				General.sleep(200, 300);

			jGeneral.get().occurenceCounter();
			return Banking.deposit(amount, name);
		}, 3000) && jGeneral.waitInventoryCount(count, name, true)) {
			jGeneral.shortDynamicSleep();
			return true;
		}

		General.println("AutoBanker_Error - Failed to deposit the specified item.");
		return false;
	}
	
	public static boolean withdraw(int amount, int id) {
		return withdraw(amount, id, false);
	}
	
	// If we do not have the required amount in the bank. Withdraw what is left.
	public static boolean withdraw(int amount, int id, boolean stackException) {
		return withdrawMethod(amount, id, false, null, stackException);
	}

	public static boolean withdraw(int amount, String name) {	
		return withdraw(amount, name, false);
	}
	
	// If we do not have the required amount in the bank. Withdraw what is left.
	public static boolean withdraw(int amount, String name, boolean stackException) {
		return withdrawMethod(amount, 0, false, name, stackException);
	}
	
	// Withdraws items in noted form.
	public static boolean withdrawNoted(int amount, int id) {
		return withdrawNoted(amount, id, false);
	}
	
	// Withdraws items in noted form. If we do not have the required amount in the bank. Withdraw what is left.
	public static boolean withdrawNoted(int amount, int id, boolean stackException) {
		return withdrawMethod(amount, id, true, null, stackException);
	}
	
	// Withdraws items in noted form.
	public static boolean withdrawNoted(int amount, String name) {
		return withdrawNoted(amount, name, false);
	}
	
	// Withdraws items in noted form. If we do not have the required amount in the bank. Withdraw what is left.
	public static boolean withdrawNoted(int amount, String name, boolean stackException) {
		return withdrawMethod(amount, 0, true, name, stackException);
	}

	// Called by withdraw(), withdrawNoted, and their string variants. Should never be called on its own!
	public static boolean withdrawMethod(int amount, int id, boolean withdrawNoted, String name, boolean stackException) {	
		if (!openBank() || !isBankLoaded())
			return false;
 
		RSItem item;
		RSItemDefinition definition;
		if (name == null) {
			definition = RSItemDefinition.get(id);
			if (definition != null && definition.isNoted())
				id = definition.getSwitchNoteItemID();

			item = RS.Banking_find(id);
			if (item == null) {
				General.println("AutoBanker_Error - Item not found in the bank.");
				return false;
			}
		}
		else {
			item = RS.Banking_find(name);
			if (item == null) {
				General.println("AutoBanker_Error - Item not found in the bank.");
				return false;
			}
			
			id = item.getID();
			definition = RSItemDefinition.get(id);
		}

		if (Inventory.isFull() && RS.Inventory_find(id) == null) {
			General.println("AutoBanker_Error - Not enough inventory space.");
			return false;
		}
		else if (!withdrawNoted && definition != null && !definition.isStackable() &&
		((28 - Inventory.getAllList().size()) < amount || (item.getStack() > (28 - Inventory.getAllList().size()) && (amount == -1 || amount == 0))))
			withdrawNoted = true;

		if (withdrawNoted && !get().clicked) {
			RSInterfaceChild interfaceChild = Interfacer.get(12, 22);
			if (interfaceChild == null || !interfaceChild.click())
					return false;
			
			jGeneral.defaultDynamicSleep();
			get().clicked = true;
		}
		else if (!withdrawNoted && get().clicked) {
			RSInterfaceChild interfaceChild = Interfacer.get(12, 20);
			if (interfaceChild == null || !interfaceChild.click())
					return false;

			jGeneral.defaultDynamicSleep();
			get().clicked = false;
		}

		if (!stackException && item.getStack() <  amount) {
			General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
			return false;
		}

		jGeneral.get().setCount(0);
		final int fixedAmount = amount, fixedId = id;
		if (!Timing.waitCondition(() -> {
			if (jGeneral.get().getCount() != 0)
				General.sleep(200, 300);

			jGeneral.get().occurenceCounter();
			return Banking.withdraw(fixedAmount, fixedId);
		}, General.random(3000, 5000))) {
			General.println("AutoBanker_Error - Could not withdraw itemID: (" + fixedId + ")." );
			return false;
		}

		jGeneral.defaultDynamicSleep_2();
		return true;
	}

	public static boolean withdrawByIds(int amount, List<Integer> ids) {
		return withdrawByIds(amount, ids, false);
	}
	
	public static boolean withdrawByIds(int amount, List<Integer> ids, boolean stackException) {
		return withdrawMethod(amount, ids, null, false, stackException);
	}
	
	// Withdraws items in noted form.
	public static boolean withdrawByIds_Noted(int amount, List<Integer> ids) {
		return withdrawByIds_Noted(amount, ids, false);
	}
	
	// Withdraws items in noted form.
	public static boolean withdrawByIds_Noted(int amount, List<Integer> ids, boolean stackException) {
		return withdrawMethod(amount, ids, null, true, stackException);
	}
	
	public static boolean withdrawByNames(int amount, List<String> names) {
		return withdrawByNames(amount, names, false);
	}
	
	public static boolean withdrawByNames(int amount, List<String> names, boolean stackException) {
		return withdrawMethod(amount, null, names, false, stackException);
	}
	
	// Withdraws items in noted form.
	public static boolean withdrawByNames_Noted(int amount, List<String> names) {
		return withdrawByNames_Noted(amount, names, false);
	}

	// Withdraws items in noted form.
	public static boolean withdrawByNames_Noted(int amount, List<String> names, boolean stackException) {
		return withdrawMethod(amount, null, names, true, stackException);
	}
	
	// Called by withdrawByIds(), withdrawByIds_Noted(), and their string variants. Do not call it on its own.
	public static boolean withdrawMethod(int amount, List<Integer> ids, List<String> names, boolean withdrawNoted, boolean stackException) {
		if (!openBank() || !isBankLoaded())
			return false;

		RSItem item;
		RSItemDefinition definition;
		if (ids != null) { // For the id variants.
			boolean noted; // If we have noted item ids in our list but we are not withdrawing items in noted form, then still withdraw them as noted items.
			for (int id : ids) {
				definition = RSItemDefinition.get(id);
				if (definition != null && definition.isNoted()) {
					id = definition.getSwitchNoteItemID();
					noted = true;
				}
				else
					noted = false;
				
				item = RS.Banking_find(id);
				if (item == null) {
					General.println("AutoBanker_Error - Item not found in the bank.");
					return false;
				}

				if (withdrawNoted && !withdrawMethod(amount, item, withdrawNoted, stackException, definition)) {
					return false;
				}
				else if (!withdrawNoted && !withdrawMethod(amount, item, noted, stackException, definition))
					return false;
			}
		}
		else {	// For the string variants.	
			for (String name : names) {
				item = RS.Banking_find(name);
				if (item == null) {
					General.println("AutoBanker_Error - Item not found in the bank.");
					return false;
				}
				
				definition = RSItemDefinition.get(item.getID());
				if (!withdrawMethod(amount, item, withdrawNoted, stackException, definition))
					return false;
			}

		}

		return true;
	}

	// Should never be called on its own!
	public static boolean withdrawMethod(int amount, RSItem item, boolean withdrawNoted, boolean stackException, RSItemDefinition definition) {
		if (Inventory.isFull() && RS.Inventory_find(item.getID()) == null) {
			General.println("AutoBanker_Error - Not enough inventory space.");
			return false;
		}
		else if (!withdrawNoted && definition != null && !definition.isStackable() &&
		((28 - Inventory.getAllList().size()) < amount || (item.getStack() > (28 - Inventory.getAllList().size()) && (amount == -1 || amount == 0))))
			withdrawNoted = true;

		if (withdrawNoted && !get().clicked) {
			RSInterfaceChild interfaceChild = Interfacer.get(12, 22);
			if (interfaceChild == null || !interfaceChild.click())
					return false;
			
			jGeneral.defaultDynamicSleep();
			get().clicked = true;
		}
		else if (!withdrawNoted && get().clicked) {
			RSInterfaceChild interfaceChild = Interfacer.get(12, 20);
			if (interfaceChild == null || !interfaceChild.click())
					return false;

			jGeneral.defaultDynamicSleep();
			get().clicked = false;
		}
		
		if (!stackException && item.getStack() <  amount) {
			General.println("AutoBanker_Error - Not enough quantities to withdraw of said item.");
			return false;
		}

		jGeneral.get().setCount(0);
		final int fixedAmount = amount, fixedId = item.getID();
		if (!Timing.waitCondition(() -> {
			if (jGeneral.get().getCount() != 0)
				General.sleep(200, 300);

			jGeneral.get().occurenceCounter();
			return Banking.withdraw(fixedAmount, fixedId);
		}, General.random(3000, 5000))) {
			General.println("AutoBanker_Error - Could not withdraw itemID: (" + fixedId + ")." );
			return false;
		}

		jGeneral.defaultDynamicSleep_2();
		return true;
	}
	
	public static boolean walkToBank() {		
		return walkToBank(null, null);
	}
	
	public static boolean walkToBank(WalkingCondition condition) {	
		return walkToBank(null, condition);
	}
	
	public static boolean walkToBank(RunescapeBank bank) {
		return walkToBank(bank, null);
	}
	
	// DaxWalks to specified bank if not set to null, otherwise DaxWalk to nearest bank.
	public static boolean walkToBank(RunescapeBank bank, WalkingCondition condition) {
		jGeneral.get().setCount(0);

		if (bank != null && Timing.waitCondition(() -> {
			jGeneral.get().occurenceCounter();
			General.sleep(50); // To save CPU power
	        if (condition != null)
	        	return DaxWalker.walkToBank(bank, condition);
	            
	        return DaxWalker.walkToBank(condition);
		}, 300))
			return true;

		else if (!Banking.isInBank() && Timing.waitCondition(() -> {
			jGeneral.get().occurenceCounter();
	        General.sleep(50); // To save CPU power
	        if (condition != null)
	        	return DaxWalker.walkToBank(condition);
	            
	        return DaxWalker.walkToBank();
		}, 300))
			return true;

		General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
		General.println("AutoBanker_Error - Could not generate path to bank.");
		return false;		
	}
}
