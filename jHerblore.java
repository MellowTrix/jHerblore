package scripts;

import org.tribot.api.General;
import org.tribot.api2007.Inventory;
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
import scripts.jay_api.fluffeespaint.Variables;
import scripts.jay_api.jaysgui.jaysgui;
import scripts.jay_api.wastedbroGE.GrandExchangeService;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
@ScriptManifest(
		authors = "Jaywalker",
		category = "Herblore",
		name = "jHerblore",
		description = "Add Jaywalker#9754 on Discord for assistance.",
		version = 1.32)

public class jHerblore extends Script implements Arguments, Painting, PaintInfo, Starting, Ending {

    private File newFile = null;
    private String method_track = "Potions mixed: ", method_track_2 = " potions/hour)";
    
	private final FluffeesPaint FLUFFEES_PAINT = new FluffeesPaint(this, FluffeesPaint.PaintLocations.TOP_LEFT_CHATBOX, new Color[]{new Color(255, 251, 255)}, "Trebuchet MS", new Color[]{new Color(0, 100, 0, 189)},
            new Color[]{new Color(39, 95, 175)}, 1, false, 5, 3, 0);
	
    @Override
    public String[] getPaintInfo() {
        return new String[]{"jHerlbore v" + String.format("%.2f", getClass().getAnnotation(ScriptManifest.class).version()), "Time ran: " + FLUFFEES_PAINT.getRuntimeString(handlerXML.get().getTime()),
        					"Profit/Loss: " + Variables.get().getProfit() + " (" + SkillsHelper.getAmountPerHour(Variables.get().getProfit(), this.getRunningTime()) + " gp/hour)",
        					SkillsHelper.getPrettySkillName(SkillsHelper.getSkillWithMostIncrease(SKILLS.HERBLORE)) + " XP: " + SkillsHelper.getReceivedXP(SkillsHelper.getSkillWithMostIncrease(SKILLS.HERBLORE)) +
        					" (" + SkillsHelper.getAmountPerHour(SkillsHelper.getReceivedXP(SkillsHelper.getSkillWithMostIncrease(SKILLS.HERBLORE)), this.getRunningTime()) + " xp/hour)",
        					method_track +  Variables.get().getItemsCreated() + " (" + SkillsHelper.getAmountPerHour(Variables.get().getItemsCreated(), this.getRunningTime())  + method_track_2};
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
        handlerXML.get().createFile(newFile);
        
        if (!handlerXML.get().skipGUI(newFile))
        {
        	jaysgui.main(newFile);
    	
    		while (!jaysgui.startScript)
    			General.sleep(150);
        }
        else
        	handlerXML.get().loadSettings(newFile);
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
        		handlerXML.get().skipGUI(newFile, true);
        	else
        		handlerXML.get().skipGUI(newFile, false);
        }
    }

    public int getPot(int id) {
    	if (id == 0)
    		id = 121;
    	else if (id == 1)
    		id = 175;
    	else if (id == 2)
    		id = 115;
    	else if (id == 3)
    		id = 3410;
    	else if (id == 4)
    		id = 6472;
    	else if (id == 5)
    		id = 127;
    	else if (id == 6)
    		id = 3010;
    	else if (id == 7)
    		id = 133;
    	else if (id == 8)
    		id = 3034;
    	else if (id == 9)
    		id = 9741;
    	else if (id == 10)
    		id = 139;
    	else if (id == 11)
    		id = 145;
    	else if (id == 12)
    		id = 181;
    	else if (id == 13)
    		id = 151;
    	else if (id == 14)
    		id = 3018;
    	else if (id == 15)
    		id = 10000;
    	else if (id == 16)
    		id = 157;
    	else if (id == 17)
    		id = 187;
    	else if (id == 18)
    		id = 3026;
    	else if (id == 19)
    		id = 163;
    	else if (id == 20)
    		id = 2454;
    	else if (id == 21)
    		id = 169;
    	else if (id == 22)
    		id = 3042;
    	else if (id == 23)
    		id = 189;
    	else if (id == 24)
    		id = 6687;
    	else if (id == 25)
    		id = 12913;
    		
    	return id;
    }
    
    public int getUnfPot(int id) {
    	if (id == 249)
    		id = 91;
    	else if (id == 251)
    		id = 93;
    	else if (id == 253)
    		id = 95;
    	else if (id == 255)
    		id = 97;
    	else if (id == 257)
    		id = 99;
    	else if (id == 2998)
    		id = 3002;
    	else if (id == 259)
    		id = 101;
    	else if (id == 261)
    		id = 103;
    	else if (id == 263)
    		id = 105;
    	else if (id == 3000)
    		id = 3004;
    	else if (id == 265)
    		id = 107;
    	else if (id == 2481)
    		id = 2483;
    	else if (id == 267)
    		id = 109;
    	else if (id == 269)
    		id = 111;
    		
    	return id;
    }

    public int getCleanHerb(int id) {
    	if (id == 199)
    		id = 249;
    	else if (id == 201)
    		id = 251;
    	else if (id == 203)
    		id = 253;
    	else if (id == 205)
    		id = 255;
    	else if (id == 207)
    		id = 257;
    	else if (id == 3049)
    		id = 2998;
    	else if (id == 209)
    		id = 259;
    	else if (id == 211)
    		id = 261;
    	else if (id == 213)
    		id = 263;
    	else if (id == 3051)
    		id = 3000;
    	else if (id == 215)
    		id = 265;
    	else if (id == 2485)
    		id = 2481;
    	else if (id == 217)
    		id = 267;
    	else if (id == 219)
    		id = 269;
    		
    	return id;
    }

    public List<Integer> getIngredients(int id) {
    	switch(id) {
    		// Ingredients for:
    		case 0: // Attack potion
    			return Arrays.asList(91, 221);
    		case 1: // Antipoison
    			return Arrays.asList(93, 235);
    		case 2: // Strength potion
    			return Arrays.asList(95, 225);
    		case 3: // Serum 207
    			return Arrays.asList(95, 592);
    		case 4: // Compost potion
    			return Arrays.asList(97, 21622);
    		case 5: // Restore potion
    			return Arrays.asList(97, 223);
    		case 6: // Energy potion
    			return Arrays.asList(97, 1975);
    		case 7: // Defence potion
    			return Arrays.asList(99, 239);
    		case 8: // Agility potion
    			return Arrays.asList(3002, 2152);
    		case 9: // Combat potion
    			return Arrays.asList(97, 9736);
    		case 10: // Prayer potion
    			return Arrays.asList(99, 231);
    		case 11: // Super attack
    			return Arrays.asList(101, 221);
    		case 12: // Superantipoison
    			return Arrays.asList(101, 235);
    		case 13: // Fishing potion
    			return Arrays.asList(103, 231);
    		case 14: // Super energy potion
    			return Arrays.asList(103, 2970);
    		case 15: // Hunter potion
    			return Arrays.asList(103, 1011);
    		case 16: // Super strength
    			return Arrays.asList(105, 225);
    		case 17: // Weapon poison
    			return Arrays.asList(105, 241);
    		case 18: // Super restore
    			return Arrays.asList(3004, 223);
    		case 19: // Super defence
    			return Arrays.asList(107, 239);
    		case 20: // Antifire potion
    			return Arrays.asList(2483, 241);
    		case 21: // Ranging potion
    			return Arrays.asList(109, 245);
    		case 22: // Magic potion
    			return Arrays.asList(2483, 3138);
    		case 23: // Zamorak brew
    			return Arrays.asList(11, 247);
    		case 24: // Saradomin brew
    			return Arrays.asList(3002, 6693);
    		case 25: // Anti-venom+
    			return Arrays.asList(12905, 111);
    		default:
    			return null;
    	}
    }
    
    public void cleaning() {
    	while(true) {
    		try {
    			Banker.walkToBank(Walker.get().condition_enableRun);
    			if (!Banker.depositItemsAll() && !Banker.depositItemsAll()) // Attempt depositing once more if it for some very odd reason it failed the first time.(happens very rarely)
    				return;
 
    			if (Banker.withdraw_stackException(handlerXML.get().getWithdrawingItems().get(0), 28)) {
    				if (Banker.close()) {
    					if (!jGeneral.get().clickAll(0, 25, true))
    						return;
    				}
    				else
    					return;
    			}
    			else if (handlerXML.get().isRestocking()) {
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
    					
    					if (!Banker.walkToBank(RunescapeBank.GRAND_EXCHANGE, Walker.get().condition_enableRun))
    						return;
    				}

    				if (Exchanger.buy(handlerXML.get().getWithdrawingItems().get(0), GrandExchangeService.tryGetPrice(handlerXML.get().getWithdrawingItems().get(0)).get(), handlerXML.get().getRestockingAmount(), handlerXML.get().getGE_mult_buy())) {
    					if (!Exchanger.collectBuy_removeProfit(handlerXML.get().getWithdrawingItems().get(0), handlerXML.get().getRestockingAmount()))
    						return;
    				}
    				else if ((jGeneral.get().getCash(true) + jGeneral.get().getCash(false)) < (GrandExchangeService.tryGetPrice(handlerXML.get().getWithdrawingItems().get(0)).get()
    						* handlerXML.get().getRestockingAmount() * handlerXML.get().getGE_mult_buy())) {

    					String item_name = RSItemDefinition.get(handlerXML.get().getWithdrawingItems().get(0)).getName().substring(6);
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

    					if (Exchanger.sell(item.getID(), GrandExchangeService.tryGetPrice(item.getID()).get(), item.getStack(), handlerXML.get().getGE_mult_sell())) {
    						if (!Exchanger.collectSell_addProfit(item.getID(), item.getStack()))
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
    			
    		} catch(Exception e) {
    			e.printStackTrace();
    			return;
    		}
    	}
    }
   
    public void mixing_unf() {
		handlerXML.get().getWithdrawingItems().add(getCleanHerb(handlerXML.get().getWithdrawingItems().get(0)));
    	handlerXML.get().getWithdrawingItems().add(227);
    	handlerXML.get().getWithdrawingItems().remove(0);

    	while(true) {
    		try {
    			Banker.walkToBank(Walker.get().condition_enableRun);
    			if (!Banker.depositItemsAll() && !Banker.depositItemsAll()) // Attempt depositing once more if it for some very odd reason it failed the first time.(happens very rarely)
    				return;

    			if (Banker.withdraw_stackException(14, handlerXML.get().getWithdrawingItems())) {
    				if (Banker.close()) {
    					if (!jGeneral.get().clickMix(handlerXML.get().getWithdrawingItems().get(0), 227, getUnfPot(handlerXML.get().getWithdrawingItems().get(0)), true, true))
    						return;
    				}
    				else
    					return;
    			}
    			else if (handlerXML.get().isRestocking()) {

    				if (Inventory.getAll().length != 0) {
    					Banker.depositItemsAll();
    				}
    				
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
    					
    					if (!Banker.walkToBank(RunescapeBank.GRAND_EXCHANGE, Walker.get().condition_enableRun))
    						return;
    				}

    				if (Exchanger.buy(handlerXML.get().getWithdrawingItems(), handlerXML.get().getRestockingAmount(), handlerXML.get().getGE_mult_buy())) {
    					if (!Exchanger.collectBuy_removeProfit(handlerXML.get().getWithdrawingItems().get(0), handlerXML.get().getRestockingAmount()) || !Exchanger.collectBuy_removeProfit(227, handlerXML.get().getRestockingAmount()))
    						return;
    				}
    				else if ((jGeneral.get().getCash(true) + jGeneral.get().getCash(false)) < (GrandExchangeService.tryGetPrice(handlerXML.get().getWithdrawingItems().get(0)).get()
    						* handlerXML.get().getRestockingAmount() * handlerXML.get().getGE_mult_buy() + GrandExchangeService.tryGetPrice(227).get() * handlerXML.get().getRestockingAmount() * 2.0f)) {

    					RSItem item = RS.Banking_find(getUnfPot(handlerXML.get().getWithdrawingItems().get(0)));
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

    					if (Exchanger.sell(item.getID(), GrandExchangeService.tryGetPrice(item.getID()).get(), item.getStack(), handlerXML.get().getGE_mult_sell())) {
    						if (!Exchanger.collectSell_addProfit(item.getID(), item.getStack()))
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
    			
    		} catch(Exception e) {
    			e.printStackTrace();
    			return;
    		}
    	}
    }

    public void mixing() {
    	List<Integer> mixingItemsList = getIngredients(handlerXML.get().getHerbPotion());
    	if (mixingItemsList == null) {
    		General.println("jHerblore_Error - Could not lookup ingridients");
    		return;
    	}

    	while(true) {
    		try {
    			Banker.walkToBank(Walker.get().condition_enableRun);
    			if (!Banker.depositItemsAll() && !Banker.depositItemsAll()) // Attempt depositing once more if it for some very odd reason it failed the first time.(happens very rarely)
    				return;

    			if (Banker.withdraw_stackException(14, mixingItemsList)) {
    				if (Banker.close()) {
    					if (!jGeneral.get().clickMix(mixingItemsList.get(0), mixingItemsList.get(1), getPot(handlerXML.get().getHerbPotion()), true, false))
    						return;
    				}
    				else
    					return;
    			}
    			else if (handlerXML.get().isRestocking()) {
    				
    				if (Inventory.getAll().length != 0) {
    					Banker.depositItemsAll();
    				}
    				
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
    					
    					if (!Banker.walkToBank(RunescapeBank.GRAND_EXCHANGE, Walker.get().condition_enableRun))
    						return;
    				}

    				if (Exchanger.buy(mixingItemsList, handlerXML.get().getRestockingAmount(), handlerXML.get().getGE_mult_buy())) {
    					if (!Exchanger.collectBuy_removeProfit(mixingItemsList.get(0), handlerXML.get().getRestockingAmount()) || !Exchanger.collectBuy_removeProfit(mixingItemsList.get(1), handlerXML.get().getRestockingAmount()))
    						return;
    				}
    				else if ((jGeneral.get().getCash(true) + jGeneral.get().getCash(false)) < ((GrandExchangeService.tryGetPrice(mixingItemsList.get(0)).get()
    						+ GrandExchangeService.tryGetPrice(mixingItemsList.get(1)).get()) * handlerXML.get().getRestockingAmount() * handlerXML.get().getGE_mult_buy())) {

    					RSItem item = RS.Banking_find(getPot(handlerXML.get().getHerbPotion()));
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

    					if (Exchanger.sell(item.getID(), GrandExchangeService.tryGetPrice(item.getID()).get(), item.getStack(), handlerXML.get().getGE_mult_sell())) {
    						if (!Exchanger.collectSell_addProfit(item.getID(), item.getStack()))
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
    			
    		} catch(Exception e) {
    			e.printStackTrace();
    			return;
    		}
    	}
    }
    
	@Override
    public void run() {
    	if (jaysgui.endScript)
    		return;
    	
        while (Login.getLoginState() != Login.STATE.INGAME)
            General.sleep(400);
    	
    	SkillsHelper.setStartSkills();
    	
    	if (handlerXML.get().getHerbMethod() == 0)
    		mixing();
    	else if (handlerXML.get().getHerbMethod() == 1)
    		mixing_unf();
    	else {
    		method_track = "Herbs cleaned: "; method_track_2 = " herbs/hour)";
    		cleaning();
    	}
    }

	@Override
	public void onEnd() {
		General.println("Thank you for using jHerblore!");
		jaysgui.endScript = false;
	}
}