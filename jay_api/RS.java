package scripts.jay_api;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;

public class RS {

	public static RSItem getItem(RSItem[] items) {
		for (RSItem item : items) {
			if (item != null) {
				return item;
			}
		}
		
		return null;
	}
	
	public static RSItem Banking_find(int... ids) {
		RSItem[] items = Banking.find(ids);
		
		for (RSItem item : items) {
			if (item != null) {
				return item;
			}
		}
		
		return null;
	}
	
	public static RSItem Banking_find(String... names) {
		RSItem[] items = Banking.find(names);
		
		for (RSItem item : items) {
			if (item != null) {
				return item;
			}
		}
		
		return null;
	}
	
	public static RSItem Inventory_find(int... ids) {
		RSItem[] items = Inventory.find(ids);
		
		for (RSItem item : items) {
			if (item != null) {
				return item;
			}
		}
		
		return null;
	}
	
	public static RSNPC NPCs_findNearest(String name) {
		RSNPC[] npcs = NPCs.findNearest(name);
		
		for (RSNPC npc : npcs)
			if (npc != null)
				return npc;
		
		return null;
	}
}
