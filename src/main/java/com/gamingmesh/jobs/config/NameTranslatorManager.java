package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIEntityType;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIPotionType;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.NameList;
import com.gamingmesh.jobs.stuff.Util;

public class NameTranslatorManager {

    public ArrayList<NameList> ListOfNames = new ArrayList<>();
    public ArrayList<NameList> ListOfPotionNames = new ArrayList<>();
    public ArrayList<NameList> ListOfEntities = new ArrayList<>();
    public ArrayList<NameList> ListOfEnchants = new ArrayList<>();
    public ArrayList<NameList> ListOfColors = new ArrayList<>();

    public NameTranslatorManager() {
    }

    public String Translate(String materialName, JobInfo info) {
	// Translating name to user friendly
	if (Jobs.getGCManager().UseCustomNames)
	    switch (info.getActionType()) {
	    case BREAK:
	    case TNTBREAK:
	    case EAT:
	    case CRAFT:
	    case DYE:
	    case PLACE:
	    case SMELT:
	    case REPAIR:
	    case BREW:
	    case FISH:

		for (NameList one : ListOfNames) {
		    String ids = one.getName();
		    if (ids.equalsIgnoreCase(materialName)) {
			return one.getName();
		    }
		}
		for (NameList one : ListOfNames) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		for (NameList one : ListOfNames) {
		    String ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}
		break;
	    case BREED:
	    case KILL:
	    case MILK:
	    case TAME:
		for (NameList one : ListOfEntities) {
		    String ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		    ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		    ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(info.getName())) {
			return one.getName();
		    }
		}
		break;
	    case ENCHANT:
		for (NameList one : ListOfEnchants) {
		    String ids = one.getId();
		    ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(info.getName().contains(":") ? info.getName().split(":")[0] : info.getName())) {
			return one.getName() + (info.getName().contains(":") ? ":" + info.getName().split(":")[1] : "");
		    }

		    if (ids.equalsIgnoreCase(String.valueOf(info.getId()))) {
			return one.getName() + " " + info.getMeta();
		    }
		    ids = one.getId() + ":" + one.getMeta();
		    if (!one.getMeta().equalsIgnoreCase("") && ids.equalsIgnoreCase(info.getId() + ":" + info.getMeta()) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		    ids = one.getId();
		    if (ids.equalsIgnoreCase(String.valueOf(info.getId())) && !one.getId().equalsIgnoreCase("0")) {
			return one.getName();
		    }
		}

		break;
	    case CUSTOMKILL:
	    case EXPLORE:
		break;
	    case SHEAR:
		for (NameList one : ListOfColors) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(info.getName())) {
			return one.getName();
		    }
		}
		break;

	    case MMKILL:
		return Jobs.getMythicManager().getDisplayName(materialName);
	    case DRINK:
		for (NameList one : ListOfPotionNames) {
		    String ids = one.getMinecraftName();
		    if (ids.equalsIgnoreCase(info.getName())) {
			return one.getName();
		    }
		}
	    default:
		break;
	    }

	return materialName;
    }

    public void readFile() {

	YmlMaker ItemFile = new YmlMaker(Jobs.getInstance(), "TranslatableWords" + File.separator + "Words_" + Jobs.getGCManager().localeString + ".yml");
	ItemFile.saveDefaultConfig();

	if (ItemFile.getConfig().isConfigurationSection("ItemList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("ItemList");
	    Set<String> keys = section.getKeys(false);
	    ListOfNames.clear();
	    for (String one : keys) {

		String split = one.split("-")[0];
		String id = split.contains(":") ? split.split(":")[0] : split;
		String meta = split.contains(":") && split.split(":").length > 1 ? split.split(":")[1] : "";

		String MCName = one.contains("-") && one.split("-").length > 1 ? one.split("-")[1] : "";
		String Name = ItemFile.getConfig().getString("ItemList." + one);
		ListOfNames.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfNames.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfNames.size() + " custom item names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The ItemList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("EntityList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("EntityList");
	    Set<String> keys = section.getKeys(false);
	    ListOfEntities.clear();
	    for (String one : keys) {
		String split = one.split("-")[0];
		String id = split.contains(":") ? split.split(":")[0] : split;
		String meta = split.contains(":") ? split.split(":")[1] : "";
		String MCName = one.split("-")[1];
		String Name = ItemFile.getConfig().getString("EntityList." + one);
		ListOfEntities.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfEntities.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfEntities.size() + " custom entity names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The EntityList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("EnchantList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("EnchantList");
	    Set<String> keys = section.getKeys(false);
	    ListOfEnchants.clear();
	    for (String one : keys) {
		String name = section.getString(one);
		ListOfEnchants.add(new NameList(one, one, one, name));
	    }
	    if (ListOfEnchants.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfEnchants.size() + " custom enchant names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The EnchantList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("ColorList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("ColorList");
	    Set<String> keys = section.getKeys(false);
	    ListOfColors.clear();
	    for (String one : keys) {
		String id = one.split("-")[0];
		String meta = "";
		String MCName = one.split("-")[1];
		String Name = ItemFile.getConfig().getString("ColorList." + one);
		ListOfColors.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfColors.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfColors.size() + " custom color names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The ColorList section not found in " + ItemFile.fileName + " file.");

	if (ItemFile.getConfig().isConfigurationSection("PotionNamesList")) {
	    ConfigurationSection section = ItemFile.getConfig().getConfigurationSection("PotionNamesList");
	    Set<String> keys = section.getKeys(false);
	    ListOfPotionNames.clear();
	    for (String one : keys) {
		String id = one.split("-")[0];
		String meta = "";
		String MCName = one.split("-")[1];
		String Name = ItemFile.getConfig().getString("PotionNamesList." + one);
		ListOfPotionNames.add(new NameList(id, meta, Name, MCName));
	    }
	    if (ListOfPotionNames.size() > 0)
		Jobs.consoleMsg("&e[Jobs] Loaded " + ListOfPotionNames.size() + " custom potion names!");
	} else
	    Jobs.consoleMsg("&c[Jobs] The PotionNamesList section not found in " + ItemFile.fileName + " file.");
    }

    synchronized void load() {
	String ls = Jobs.getGCManager().localeString;

	if (ls == null || ls.equals(""))
	    return;

	File file = new File(Jobs.getFolder(), "TranslatableWords.yml");
	File file2 = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + ls + ".yml");
	if (file.exists())
	    file.renameTo(file2);

	// Just copying default language files, except en, that one will be generated
	List<String> languages = new ArrayList<>();

	// This should be present to copy over default files into TranslatableWords folder if file doesn't exist. Grabs all files from plugin file.
	try {
	    languages.addAll(LanguageManager.getClassesFromPackage("TranslatableWords", "Words_"));
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	for (Iterator<String> e1 = languages.iterator(); e1.hasNext();) {
	    String lang = e1.next();
	    YmlMaker langFile = new YmlMaker(Jobs.getInstance(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");
	    langFile.saveDefaultConfig();
	}
	//Up to here.

	languages.add("en");

	File customLocaleFile = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + ls + ".yml");
	if (!customLocaleFile.exists() && !ls.equalsIgnoreCase("en"))
	    languages.add(ls);

	for (String lang : languages) {

	    File f = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");

	    // Fail safe if file get corrupted and being created with corrupted data, we need to recreate it
	    if ((f.length() / 1024) > 1024) {
		f.delete();
		f = new File(Jobs.getFolder(), "TranslatableWords" + File.separator + "Words_" + lang + ".yml");
	    }
	    ConfigReader c = null;
	    try {
		c = new ConfigReader(f);
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	    if (c == null)
		continue;

	    c.copyDefaults(true);

	    for (CMIMaterial one : CMIMaterial.values()) {
		if (one.getMaterial() == null)
		    continue;

		String n = one.getLegacyId() + (one.getLegacyData() == -1 ? "" : ":" + one.getLegacyData());

		String name = null;

		if (c.getC().isConfigurationSection("ItemList." + n)) {
		    name = c.getC().getString("ItemList." + n + ".Name");
		}

		if (name == null) {
		    n = one.getLegacyId() + ":" + one.getLegacyData();
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getLegacyId());
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getId());
		    if (c.getC().isConfigurationSection("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n + ".Name");
		    }
		}

		if (name == null) {
		    n = one.getLegacyId() + ":" + one.getLegacyData() + "-" + one.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getLegacyId()) + "-" + one.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    n = String.valueOf(one.getId()) + "-" + one.getBukkitName();
		    if (c.getC().isString("ItemList." + n)) {
			name = c.getC().getString("ItemList." + n);
		    }
		}

		if (name == null) {
		    name = one.getName();
		}

		c.get("ItemList." + (one.getId() == -1 ? one.getLegacyId() : one.getId()) + "-" + one.getBukkitName(), name);
	    }

	    for (CMIEntityType one : CMIEntityType.values()) {
		if (!one.isAlive())
		    continue;

		String n = String.valueOf(one.getId());

		String name = null;

		if (c.getC().isConfigurationSection("EntityList." + n)) {
		    name = c.getC().getString("EntityList." + n + ".Name");
		}

		if (name == null) {
		    n = n + "-" + one.toString();
		    if (c.getC().isConfigurationSection("EntityList." + n)) {
			name = c.getC().getString("EntityList." + n);
		    }
		}

		if (name == null) {
		    name = one.getName();
		}

		c.get("EntityList." + one.getId() + "-" + one.toString(), name);
	    }

	    for (Enchantment one : Enchantment.values()) {

		if (one == null)
		    continue;
		if (one.getName() == null)
		    continue;

		String name = Util.firstToUpperCase(one.getName().toString()).replace("_", " ");
		if (c.getC().isConfigurationSection("EnchantList"))
		    for (String onek : c.getC().getConfigurationSection("EnchantList").getKeys(false)) {
			String old = c.getC().getString("EnchantList." + onek + ".MCName");
			if (old != null && old.equalsIgnoreCase(one.getName())) {
			    name = c.getC().getString("EnchantList." + onek + ".Name");
			    break;
			}
		    }
		c.get("EnchantList." + one.getName(), name);
	    }

//	    // Enchant list
//	    c.get("EnchantList.0.MCName", "PROTECTION_ENVIRONMENTAL");
//	    c.get("EnchantList.0.Name", "Protection");
//	    c.get("EnchantList.1.MCName", "PROTECTION_FIRE");
//	    c.get("EnchantList.1.Name", "Fire Protection");
//	    c.get("EnchantList.2.MCName", "PROTECTION_FALL");
//	    c.get("EnchantList.2.Name", "Feather Falling");
//	    c.get("EnchantList.3.MCName", "PROTECTION_EXPLOSIONS");
//	    c.get("EnchantList.3.Name", "Blast Protection");
//	    c.get("EnchantList.4.MCName", "ROTECTION_PROJECTILE");
//	    c.get("EnchantList.4.Name", "Projectile Protection");
//	    c.get("EnchantList.5.MCName", "OXYGEN");
//	    c.get("EnchantList.5.Name", "Respiration");
//	    c.get("EnchantList.6.MCName", "DIG_SPEED");
//	    c.get("EnchantList.6.Name", "Aqua Affinity");
//	    c.get("EnchantList.7.MCName", "THORNS");
//	    c.get("EnchantList.7.Name", "Thorns");
//	    c.get("EnchantList.8.MCName", "DEPTH_STRIDER");
//	    c.get("EnchantList.8.Name", "Depth Strider");
//	    c.get("EnchantList.9.MCName", "FROST_WALKER");
//	    c.get("EnchantList.9.Name", "Frost Walker");
//	    c.get("EnchantList.10.MCName", "CURSE_OF_BINDING");
//	    c.get("EnchantList.10.Name", "Curse of Binding");
//	    c.get("EnchantList.16.MCName", "DAMAGE_ALL");
//	    c.get("EnchantList.16.Name", "Sharpness");
//	    c.get("EnchantList.17.MCName", "DAMAGE_UNDEAD");
//	    c.get("EnchantList.17.Name", "Smite");
//	    c.get("EnchantList.18.MCName", "DAMAGE_ARTHROPODS");
//	    c.get("EnchantList.18.Name", "Bane of Arthropods");
//	    c.get("EnchantList.19.MCName", "KNOCKBACK");
//	    c.get("EnchantList.19.Name", "Knockback");
//	    c.get("EnchantList.20.MCName", "FIRE_ASPECT");
//	    c.get("EnchantList.20.Name", "Fire Aspect");
//	    c.get("EnchantList.21.MCName", "LOOT_BONUS_MOBS");
//	    c.get("EnchantList.21.Name", "Looting");
//	    c.get("EnchantList.22.MCName", "SWEEPING_EDGE");
//	    c.get("EnchantList.22.Name", "Sweeping Edge");
//	    c.get("EnchantList.32.MCName", "DIG_SPEED");
//	    c.get("EnchantList.32.Name", "Efficiency");
//	    c.get("EnchantList.33.MCName", "SILK_TOUCH");
//	    c.get("EnchantList.33.Name", "Silk Touch");
//	    c.get("EnchantList.34.MCName", "DURABILITY");
//	    c.get("EnchantList.34.Name", "Unbreaking");
//	    c.get("EnchantList.35.MCName", "LOOT_BONUS_BLOCKS");
//	    c.get("EnchantList.35.Name", "Fortune");
//	    c.get("EnchantList.48.MCName", "ARROW_DAMAGE");
//	    c.get("EnchantList.48.Name", "Power");
//	    c.get("EnchantList.49.MCName", "ARROW_KNOCKBACK");
//	    c.get("EnchantList.49.Name", "Punch");
//	    c.get("EnchantList.50.MCName", "ARROW_FIRE");
//	    c.get("EnchantList.50.Name", "Flame");
//	    c.get("EnchantList.51.MCName", "ARROW_INFINITE");
//	    c.get("EnchantList.51.Name", "Infinity");
//	    c.get("EnchantList.61.MCName", "LUCK");
//	    c.get("EnchantList.61.Name", "Luck of the Sea");
//	    c.get("EnchantList.62.MCName", "LURE");
//	    c.get("EnchantList.62.Name", "Lure");
//	    c.get("EnchantList.65.MCName", "LOYALTY");
//	    c.get("EnchantList.65.Name", "Loyalty");
//	    c.get("EnchantList.66.MCName", "IMPALING");
//	    c.get("EnchantList.66.Name", "Impaling");
//	    c.get("EnchantList.67.MCName", "RIPTIDE");
//	    c.get("EnchantList.67.Name", "Riptide");
//	    c.get("EnchantList.68.MCName", "CHANNELING");
//	    c.get("EnchantList.68.Name", "Channeling");
//	    c.get("EnchantList.70.MCName", "MENDING");
//	    c.get("EnchantList.70.Name", "Mending");
//	    c.get("EnchantList.71.MCName", "CURSE_OF_VANISHING");
//	    c.get("EnchantList.71.Name", "Curse Of Vanishing");
//	    c.get("EnchantList.72.MCName", "MULTISHOT");
//	    c.get("EnchantList.72.Name", "Multishot");
//	    c.get("EnchantList.73.MCName", "PIERCING");
//	    c.get("EnchantList.73.Name", "Piercing");
//	    c.get("EnchantList.74.MCName", "QUICK_CHARGE");
//	    c.get("EnchantList.74.Name", "Quick Charge");

	    // Color list
	    c.get("ColorList.0-white", "&fWhite");
	    c.get("ColorList.1-orange", "&6Orange");
	    c.get("ColorList.2-magenta", "&dMagenta");
	    c.get("ColorList.3-navy", "&9Navy");
	    c.get("ColorList.4-yellow", "&eYellow");
	    c.get("ColorList.5-lime", "&aLime");
	    c.get("ColorList.6-pink", "&dPink");
	    c.get("ColorList.7-gray", "&8Gray");
	    c.get("ColorList.8-silver", "&7Silver");
	    c.get("ColorList.9-cyan", "&3Cyan");
	    c.get("ColorList.10-purple", "&5Purple");
	    c.get("ColorList.11-blue", "&1Blue");
	    c.get("ColorList.12-brown", "&4Brown");
	    c.get("ColorList.13-green", "&2Green");
	    c.get("ColorList.14-red", "&cRed");
	    c.get("ColorList.15-black", "&0Black");
	    /**	    for (colorNames cn : colorNames.values()) {
	    		if (cn.getName() == null)
	    		    continue;
	    
	    		String n = cn.getId() + (cn.getId() == -1 ? "" : ":" + cn.getName());
	    
	    		String name = null;
	    
	    		if (c.getC().isConfigurationSection("ColorList." + n)) {
	    		    name = c.getC().getString("ColorList." + n + ".Name");
	    		}
	    
	    		if (name == null) {
	    		    n = cn.getId() + "-" + cn.toString();
	    		    if (c.getC().isConfigurationSection("ColorList." + n)) {
	    			name = c.getC().getString("ColorList." + n);
	    		    }
	    		}
	    
	    		if (name == null) {
	    		    name = cn.getName();
	    		}
	    
	    		c.get("ColorList." + cn.getId() + "-" + cn.toString(), name);
	    }*/

	    for (CMIPotionType one : CMIPotionType.values()) {
		String n = String.valueOf(one.getSubId());

		String name = null;

		if (c.getC().isConfigurationSection("PotionNamesList." + n))
		    name = c.getC().getString("PotionNamesList." + n + ".Name");

		if (name == null) {
		    n = n + "-" + one.toString();
		    if (c.getC().isConfigurationSection("PotionNamesList." + n))
			name = c.getC().getString("PotionNamesList." + n);
		}

		if (name == null)
		    name = one.getName();

		c.get("PotionNamesList." + one.getSubId() + "-" + one.toString(), name);
	    }

	    c.save();
	}
	readFile();
    }

}
