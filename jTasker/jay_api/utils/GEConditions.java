package scripts.jTasker.jay_api.utils;

import scripts.jTasker.jay_api.GEInterfaces;

public class GEConditions {
	
	public static Condition GEBuyWindowVisible() {
		return new Condition(() -> GEInterfaces.BUY_WINDOW.isVisible());
	}

	private interface BooleanLambda{
        boolean active();
    }

    @SuppressWarnings("deprecation")
	private static class Condition extends org.tribot.api.types.generic.Condition{
        private BooleanLambda lambda;

        public Condition(BooleanLambda lambda){
            super();
            this.lambda = lambda;
        }

        @Override
        public boolean active() {
            return lambda.active();
        }
    }
	
}
