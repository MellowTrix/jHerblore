package scripts.jay_api;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.Options;

import scripts.dax_api.api_lib.DaxWalker;
import scripts.dax_api.walker_engine.WalkingCondition;

public class Walker extends jGeneral {
	
	public static int generatedRun = util.generateRunActivation();
	
	public static WalkingCondition condition_enableRun = new WalkingCondition() {

		@Override
		public State action() {
			if (!Game.isRunOn() && Game.getRunEnergy() >= generatedRun) {
				Options.setRunEnabled(true);
				generatedRun = util.generateRunActivation();
            	return State.CONTINUE_WALKER;
			}

            return State.CONTINUE_WALKER;
        }
	};
	
	public static boolean walkTo(Positionable positionable) {
		if (Timing.waitCondition(() -> {
            General.sleep(50); // To save CPU power
            return DaxWalker.walkTo(positionable);
        }, 500))
			return true;
		else
			General.println("Something went wrong.");
	
		General.println("AutoWalker_Error - Could not generate path to location.");
		return false;
	}
	
	public static boolean walkTo(Positionable positionable, WalkingCondition condition) {
		if (DaxWalker.walkTo(positionable, condition))
			return true;
		
		General.println("AutoWalker_Error - Could not generate path to location.");
		return false;
	}
}
