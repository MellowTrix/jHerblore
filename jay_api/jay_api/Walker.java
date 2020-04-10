package scripts.jay_api;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.Options;

import scripts.dax_api.api_lib.DaxWalker;
import scripts.dax_api.walker_engine.WalkingCondition;

public class Walker {
	
    private Walker() {}
    private static final Walker WALKER = new Walker();
    public static Walker get() {
        return WALKER;
    }
	
	private int generatedRun = jGeneral.get().getUtil().generateRunActivation();
	
	public WalkingCondition condition_enableRun = new WalkingCondition() {

		@Override
		public State action() {
			if (!Game.isRunOn() && Game.getRunEnergy() >= generatedRun) {
				Options.setRunEnabled(true);
				generatedRun = jGeneral.get().getUtil().generateRunActivation();
            	return State.CONTINUE_WALKER;
			}

            return State.CONTINUE_WALKER;
        }
	};
	
	public boolean walkTo(Positionable positionable) {
		jGeneral.get().setCount(0);
		if (Timing.waitCondition(() -> {
			jGeneral.get().occurenceCounter();
            General.sleep(50); // To save CPU power
            return DaxWalker.walkTo(positionable);
        }, 500))
			return true;
		
		General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
		General.println("AutoWalker_Error - Could not generate path to location.");
		return false;
	}
	
	public boolean walkTo(Positionable positionable, WalkingCondition condition) {
		jGeneral.get().setCount(0);
		if (Timing.waitCondition(() -> {
			jGeneral.get().occurenceCounter();
            General.sleep(50); // To save CPU power
            return DaxWalker.walkTo(positionable, condition);
        }, 500))
			return true;
		
		General.println("AutoBanker_Error - Method call count: " + jGeneral.get().getCount());
		General.println("AutoWalker_Error - Could not generate path to location.");
		return false;
	}
}
