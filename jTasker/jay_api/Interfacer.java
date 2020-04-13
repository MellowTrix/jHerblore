package scripts.jTasker.jay_api;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;

import scripts.jTasker.jay_api.jGeneral;

public class Interfacer {
	public static RSInterfaceChild get(int index, int child) {
		if (Timing.waitCondition(() -> {
			General.sleep(50);
			return Interfaces.get(index, child) != null;
		}, 5000)) {
			return Interfaces.get(index, child);
		}
		
		General.println("AutoInterfacer_Error - Could not find the interface.");
		return null;
	}
	
	public static boolean disappeared(RSInterfaceChild interfaceChild) {
		long startTime = (System.currentTimeMillis() / 1000) % 60;
		interfaceChild.click();
		if (Timing.waitCondition(() -> {
			if ((System.currentTimeMillis() / 1000) % 60 - startTime > General.random(1000, 1300)) {
				interfaceChild.click();
				setTimer(startTime, (System.currentTimeMillis() / 1000) % 60);
			}

			General.sleep(50);
			return interfaceChild == null;
		}, 5000)) {
			jGeneral.defaultDynamicSleep();
			return true;
		}
	
		General.println("AutoInterfacer_Error - Interface is still there.");
		return false;
	}
	
	public static long setTimer (long timer, long time) {
		return timer = time;
	}
}
