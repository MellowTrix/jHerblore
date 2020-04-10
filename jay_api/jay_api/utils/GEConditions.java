package scripts.jay_api.utils;

import scripts.jay_api.GEInterfaces;

public class GEConditions {
	
	public static Condition GEBuyWindowVisible() {
		return new Condition(() -> GEInterfaces.BUY_WINDOW.isVisible());
	}

	private interface BooleanLambda{
        boolean active();
    }

    private static class Condition extends org.tribot.api.types.generic.Condition{
        private BooleanLambda lambda;

        @SuppressWarnings("deprecation")
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
