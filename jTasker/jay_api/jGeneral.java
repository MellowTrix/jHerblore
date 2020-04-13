package scripts.jTasker.jay_api;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Magic;
import org.tribot.api2007.types.RSItem;

public class jGeneral {
	
    private jGeneral() {}
    private static final jGeneral GENERAL = new jGeneral();
    public static jGeneral get() {
        return GENERAL;
    }
    
	private ABCUtil util = new ABCUtil();
	private int Count = 0, randomSeed = 0;
	
	public ABCUtil getUtil() {
		return util;
	}
	
	public int getCount() {
		return Count;
	}
	
	public void setCount(int val) {
		Count = val;	
	}
	
	public void occurenceCounter() {
		Count++;
	}
	
	public void generateSeed() {
		randomSeed = General.random(0, 100);
	}
	
	// If true, checks the amount of cash in the bank. If false, checks the amount of cash in the inventory.
	public static int getCash(boolean place) {
		RSItem item = null;
		if (place) {
			if (Banker.openBank())
				item = RS.Banking_find(995);
		}
		else
			item = RS.Inventory_find(995);
		
		if (item != null && item.getStack() != 0)
			return item.getStack();

		return 0;
	}
	
	public static boolean waitInventory(int val) {
		if (Timing.waitCondition(() -> {
			General.sleep(300, 600);
			return Inventory.getAll().length != val;
		}, General.random(3000, 5000))) {
			return true;
		}

		return false;
	}

	public static boolean waitInventory() {
		if (Timing.waitCondition(() -> {
			General.sleep(50);
			return Inventory.getAll().length == 0;
		}, General.random(3000, 5000))) {
			return true;
		}

		return false;
	}
	
	public static boolean waitInventoryCount(int val, boolean reverse) {	
		if (Timing.waitCondition(() -> {
			General.sleep(50);
			int count = 0;
			for (RSItem item : Inventory.getAllList()) {
				count += Inventory.getCount(item.getID());
			}
			if (reverse)
				return val != count;
			
			return val == count;
		}, General.random(3000, 5000))) {
			return true;
		}

		return false;
	}
	
	public static boolean waitInventoryCount(int val, int id, boolean reverse) {	
		if (Timing.waitCondition(() -> {
			General.sleep(50);
			int count = Inventory.getCount(id);
			if (reverse)
				return val != count;
			
			return val == count;
		}, General.random(3000, 5000))) {
			return true;
		}

		return false;
	}
	
	public static boolean waitInventoryCount(int val, String name, boolean reverse) {	
		if (Timing.waitCondition(() -> {
			General.sleep(50);
			int count = Inventory.getCount(name);
			if (reverse)
				return val != count;
			
			return val == count;
		}, General.random(3000, 5000))) {
			return true;
		}

		return false;
	}
	
	public static boolean deselect() {

		if (Game.getItemSelectionState() != 0 || Magic.isSpellSelected()) {
		    if (Timing.waitCondition(() -> {
				get().getUtil().moveMouse();
				General.sleep(200, 400);
				Mouse.click(3);
				General.sleep(200, 700);
				ChooseOption.select("Cancel");
				General.sleep(200, 700);
		        return Game.getItemSelectionState() == 0 && !Magic.isSpellSelected();
		    }, General.random(7000, 10000)) == false) {
		    	General.println("AutoGeneral_Error - Could not deselect mouse.");
		    	return false;
		    }
		}

		return true;
	}
	
	public static void superDynamicSleeper(int min_often, int max_often, int min_seldom, int max_seldom, int min_occurence, int max_occurence, boolean shift)
	{
		if (min_often >= max_often || min_often>= min_seldom || max_often >= max_seldom ||
			min_seldom >= max_seldom || min_occurence >= max_occurence || min_occurence == 0)
		{
			General.println("AutoGeneral_Error - Invalid parameter values.");
			return;
		}
		
		int occurence = General.random(min_occurence, max_occurence);

		// shift randomizes occurence even further so that the occurence does not always vary between the same min/max occurence.
		if (shift) {		
			if (min_occurence > 5 && max_occurence - min_occurence > 6)
				occurence = General.random(General.random(min_occurence - General.random(0, 3), min_occurence + General.random(1, 3)),
								           General.random(max_occurence - General.random(0, 3), max_occurence + General.random(1, 3)));
			else if (min_occurence > 2 && max_occurence - min_occurence > 4)
				occurence = General.random(General.random(min_occurence - General.random(0, 2), min_occurence + General.random(1, 2)),
									       General.random(max_occurence - General.random(0, 2), max_occurence + General.random(1, 2)));
			else if (min_occurence > 2) {
				occurence = General.random(General.random(min_occurence - General.random(0, 2), min_occurence + General.random(1, 2)),
					       				   General.random(max_occurence - General.random(0, 2), max_occurence + General.random(1, 2)));
			}
			else if (min_occurence == 2)
				occurence = General.random(General.random(General.random(min_occurence - General.random(0, 1), min_occurence + 1),
												   		  General.random(max_occurence - General.random(0, 1), max_occurence + 1)),   
										   General.random(General.random(min_occurence - General.random(1, 1), max_occurence + General.random(1, 2)),
												   		  General.random(max_occurence - General.random(1, 1), max_occurence + General.random(1, 2))));									
		}
		
		int rand = General.random(0, occurence);
		
		if (rand != occurence)
			General.sleep(min_often, max_often);
		else
			General.sleep(min_seldom, max_seldom);
	}

	public static void superDynamicSleeper(int min_often, int max_often, int min_seldom, int max_seldom, int min_occurence, int max_occurence)
	{
		superDynamicSleeper(min_often,  max_often, min_seldom, max_seldom, min_occurence, max_occurence, false);
	}
	
	public static void defaultDynamicSleep()
	{
		int seed = get().randomSeed;
		if (General.randomBoolean())
			superDynamicSleeper(330+seed, 570+seed, (330+seed)*4, (570+seed)*4, 6, 13, true);
		else
			superDynamicSleeper(330+seed, 570+seed, (330+seed)*2, (570+seed)*2, 3, 5, true);
	}
	
	public static void defaultDynamicSleep_2()
	{
		int seed = get().randomSeed;
		if (General.randomBoolean())
			superDynamicSleeper(280+seed, 530+seed, (280+seed)*4, (530+seed)*4, 6, 13, true);
		else
			superDynamicSleeper(280+seed, 530+seed, (280+seed)*2, (530+seed)*2, 3, 5, true);
	}
	
	public static void shortDynamicSleep() {
		int seed = get().randomSeed/2;
		if (General.randomBoolean())
			superDynamicSleeper(130+seed, 270+seed, (130+seed)*4, (270+seed)*4, 6, 13, true);
		else
			superDynamicSleeper(130+seed, 270+seed, (130+seed)*2, (270+seed)*2, 3, 5, true);
	}
}
