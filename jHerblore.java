package scripts;

import org.tribot.api.General;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;
import org.tribot.util.Util;
import scripts.dax_api.api_lib.DaxWalker;
import scripts.dax_api.api_lib.models.DaxCredentials;
import scripts.dax_api.api_lib.models.DaxCredentialsProvider;
import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.jay_api.Banker;
import scripts.jay_api.Exchanger;
import scripts.jay_api.RS;
import scripts.jay_api.Walker;
import scripts.jay_api.handlerXML;
import scripts.jay_api.jGeneral;
import scripts.jay_api.fluffeespaint.FluffeesPaint;
import scripts.jay_api.fluffeespaint.PaintInfo;
import scripts.jay_api.fluffeespaint.SkillsHelper;
import scripts.jay_api.jaysgui.jaysgui;
import scripts.jay_api.wastedbroGE.GrandExchange;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
@ScriptManifest(
		authors = "Jaywalker",
		category = "Herblore",
		name = "jHerblore",
		description = "Add Jaywalker#9754 on Discord for assistance.",
		version = 1.0)

public class jHerblore extends Script implements Arguments, Painting, PaintInfo, Starting, Ending {

    private File newFile = null;
    
	private final FluffeesPaint FLUFFEES_PAINT = new FluffeesPaint(this, FluffeesPaint.PaintLocations.TOP_LEFT_CHATBOX, new Color[]{new Color(255, 251, 255)}, "Trebuchet MS", new Color[]{new Color(0, 100, 0, 189)},
            new Color[]{new Color(39, 95, 175)}, 1, false, 5, 3, 0);
	
    @Override
    public String[] getPaintInfo() {
        return new String[]{"jHerlbore v" + String.format("%.2f", getClass().getAnnotation(ScriptManifest.class).version()), "Time ran: " + FLUFFEES_PAINT.getRuntimeString(handlerXML.START_TIME),
        					SkillsHelper.getPrettySkillName(SkillsHelper.getSkillWithMostIncrease(SKILLS.HERBLORE)) + " XP (P/H): " + SkillsHelper.getReceivedXP(SkillsHelper.getSkillWithMostIncrease(SKILLS.HERBLORE)) +
        					" (" + SkillsHelper.getAmountPerHour(SkillsHelper.getReceivedXP(SkillsHelper.getSkillWithMostIncrease(SKILLS.HERBLORE)), this.getRunningTime()) + ")"};
    }
	
	@Override
    public void onPaint(Graphics graphics) {
		if (jaysgui.startScript)
			FLUFFEES_PAINT.paint(graphics);
    }
	
	@Override
    public void onStart() {  	
        DaxWalker.setCredentials(new DaxCredentialsProvider() {
            @Override
            public DaxCredentials getDaxCredentials() {
                return new DaxCredentials("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY");
            }
        });
        
        // Create our settings file
        handlerXML.createFile(newFile);
        
        if (!handlerXML.skipGUI(newFile))
        {
        	jaysgui.main(newFile);
    	
    		while (!jaysgui.startScript)
    			General.sleep(150);
        }
        else
        	handlerXML.loadSettings(newFile);
    }
	
    @Override
    public void passArguments(HashMap<String, String> hashMap) { 	
    	// Getting our input
        String clientSelect = hashMap.get("custom_input");
        String clientStarter = hashMap.get("autostart");
        String input = clientStarter != null ? clientStarter : clientSelect;
        String path = Util.getAppDataDirectory().getPath() + File.separator + "jScripts" + File.separator + "jHerblore" + File.separator + "last_saved";
        new File(path).mkdirs();
        newFile = new File(path + File.separator + "settings.xml");
        
        // Parsing the passed arguments
        Pattern p = Pattern.compile("skip=(true|false)");
        Matcher m = p.matcher(input);
        if (m.find()) {	
        	if (m.group(0).contains("true"))
        		handlerXML.skipGUI(newFile, true);
        	else
        		handlerXML.skipGUI(newFile, false);
        }
    }
    
    @Override
    public void run() {
    	if (jaysgui.endScript)
    		return;
    	
        while (Login.getLoginState() != Login.STATE.INGAME)
            General.sleep(400);
    	
    	SkillsHelper.setStartSkills();
    	
    	while(true) {
    		try {
    			Banker.walkToBank(Walker.condition_enableRun);
    			Banker.depositItemsAll();			
    			if (Banker.withdraw_stackException(handlerXML.setup_withdrawing_items.get(0), 28)) {
    				if (Banker.close()) {
    					if (!jGeneral.clickAll(0, 20))
    						return;
    				}
    				else
    					return;
    			}
    			else if (handlerXML.GE_restocking) {

    				if (Player.getPosition().distanceTo(new RSTile(3165, 3487, 0)) > 7) {			
    					if (Player.getPosition().distanceTo(new RSTile(3165, 3487, 0)) > 60) { // If we are a bit further away from GE then check the bank for RoW, will speed up the traveling.
    						if (!Banker.withdraw("Ring of wealth (1)", 1)) {
    							if (!Banker.withdraw("Ring of wealth (2)", 1)) {
    								if (!Banker.withdraw("Ring of wealth (3)", 1)) {
    									if (!Banker.withdraw("Ring of wealth (4)", 1)) {
    										Banker.withdraw("Ring of wealth (5)", 1);
    									}
    								}
    							}
    						}
    					}
    					
    					if (!Banker.walkToBank(RunescapeBank.GRAND_EXCHANGE, Walker.condition_enableRun))
    						return;
    				}
    				if (Exchanger.buy(handlerXML.setup_withdrawing_items.get(0), GrandExchange.tryGetPrice(handlerXML.setup_withdrawing_items.get(0)).get(), handlerXML.GE_restocking_amount, handlerXML.GE_mult_buy)) {
    					if (!Exchanger.collectBuy(handlerXML.setup_withdrawing_items.get(0), handlerXML.GE_restocking_amount))
    						return;
    				}
    				else if ((jGeneral.getCash(true) + jGeneral.getCash(false)) < (GrandExchange.tryGetPrice(handlerXML.setup_withdrawing_items.get(0)).get() * handlerXML.GE_restocking_amount)) {
    					String item_name = RSItemDefinition.get(handlerXML.setup_withdrawing_items.get(0)).getName().substring(6);
    					RSItem item = RS.Banking_find(item_name.substring(0, 1).toUpperCase() + item_name.substring(1)); // Removing "Grimy" part of the string to fetch the clean version.
    					if (item != null) {
    						if (item.getStack() == 0) {
    							General.println("AutoBanker_Error - Item not found in the bank.");
    							return;
    						}
    					}
    					else {
    						General.println("AutoBanker_Error - Item not found in the bank.");
    						return;
    					}
    						
    					if (Exchanger.sell(handlerXML.setup_withdrawing_items.get(0), GrandExchange.tryGetPrice(handlerXML.setup_withdrawing_items.get(0)).get(), item.getStack(), handlerXML.GE_mult_sell)) {
    						if (!Exchanger.collectSell(handlerXML.setup_withdrawing_items.get(0), item.getStack()))
    							return;
    					}
    					else
    						return;
    				}
    				else
    					return;
    			}
    			else
    				return;		
    							
    		}catch(Exception e) {
    			e.printStackTrace();
    			return;
    		}
    	}
    }

	@Override
	public void onEnd() {
		General.println("Thank you for using jHerblore!");
	}
}