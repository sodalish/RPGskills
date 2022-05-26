package sodlish.rpgskills;

import sodlish.rpgskills.common.skills.Requirement;
import sodlish.rpgskills.common.skills.Skill;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Configuration
{
    private static final ForgeConfigSpec CONFIG_SPEC;
    private static final ForgeConfigSpec.BooleanValue DISABLE_WOOL;
    private static final ForgeConfigSpec.BooleanValue DEATH_RESET;
    private static final ForgeConfigSpec.IntValue STARTING_COST;
    private static final ForgeConfigSpec.IntValue COST_INCREASE;
    private static final ForgeConfigSpec.IntValue MAXIMUM_LEVEL;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SKILL_LOCKS;
    
    private static boolean disableWool;
    private static boolean deathReset;
    private static int startingCost;
    private static int costIncrease;
    private static int maximumLevel;
    private static final Map<String, Requirement[]> skillLocks = new HashMap<>();
    
    static
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        builder.comment("Disable wool drops to force the player to get shears.");
        DISABLE_WOOL = builder.define("disableWoolDrops", true);
    
        builder.comment("Reset all skills to 1 when a player dies.");
        DEATH_RESET = builder.define("deathSkillReset", false);
        
        builder.comment("Starting cost of upgrading to level 2, in levels.");
        STARTING_COST = builder.defineInRange("startingCost", 2, 0, 10);
        
        builder.comment("Amount of levels added to the cost with each upgrade (use 0 for constant cost).");
        COST_INCREASE = builder.defineInRange("costIncrease", 1, 0, 10);
        
        builder.comment("Maximum level each skill can be upgraded to.");
        MAXIMUM_LEVEL = builder.defineInRange("maximumLevel", 32, 2, 100);
        
        builder.comment("List of item and block skill requirements.", "Format: mod:id skill:level", "Valid skills: power, defence, mining, cooking, farming, crafting, focus, magic");
        SKILL_LOCKS = builder.defineList("skillLocks", Arrays.asList(
                //pick
                "minecraft:iron_pickaxe mining:5",

                //sword
                "minecraft:iron_sword power:5",
                "minecraft:iron_shovel farming:5",
                "minecraft:iron_axe power:5", "minecraft:iron_hoe farming:5", "minecraft:iron_helmet defence:5", "minecraft:iron_chestplate defence:5", "minecraft:iron_leggings defence:5", "minecraft:iron_boots defence:5", "minecraft:diamond_sword power:15", "minecraft:diamond_shovel farming:15", "minecraft:diamond_pickaxe mining:15", "minecraft:diamond_axe power:15", "minecraft:diamond_hoe farming:15", "minecraft:diamond_helmet defence:15", "minecraft:diamond_chestplate defence:15", "minecraft:diamond_leggings defence:15", "minecraft:diamond_boots defence:15", "minecraft:netherite_sword power:30", "minecraft:netherite_shovel farming:30", "minecraft:netherite_pickaxe mining:30", "minecraft:netherite_axe power:30", "minecraft:netherite_hoe farming:30", "minecraft:netherite_helmet defence:30", "minecraft:netherite_chestplate defence:30", "minecraft:netherite_leggings defence:30", "minecraft:netherite_boots defence:30", "minecraft:fishing_rod focus:10", "minecraft:shears farming:5", "minecraft:lead focus:5", "minecraft:bow power:5 focus:3", "minecraft:turtle_helmet defence:10", "minecraft:shield defence:5", "minecraft:crossbow power:5 focus:5", "minecraft:trident power:15 focus:10", "minecraft:golden_apple magic:5", "minecraft:enchanted_golden_apple magic:10", "minecraft:ender_pearl magic:5", "minecraft:ender_eye magic:10", "minecraft:piston crafting:5", "minecraft:sticky_piston crafting:10", "minecraft:tnt crafting:5", "minecraft:ender_chest magic:15", "minecraft:enchanting_table magic:10", "minecraft:anvil crafting:5", "minecraft:chipped_anvil crafting:5", "minecraft:damaged_anvil crafting:5", "minecraft:smithing_table crafting:10", "minecraft:end_crystal magic:20", "minecraft:boat focus:5", "minecraft:minecart focus:10", "minecraft:elytra focus:20", "minecraft:horse focus:10", "minecraft:donkey focus:10", "minecraft:mule focus:10",
                "minecraft:strider focus:15"), obj -> true);

        CONFIG_SPEC = builder.build();
    }
    
    // Initialise
    
    public static void load()
    {
        disableWool = DISABLE_WOOL.get();
        deathReset = DEATH_RESET.get();
        startingCost = STARTING_COST.get();
        costIncrease = COST_INCREASE.get();
        maximumLevel = MAXIMUM_LEVEL.get();
        
        for (String line : SKILL_LOCKS.get())
        {
            String[] entry = line.split(" ");
            Requirement[] requirements = new Requirement[entry.length - 1];
            
            for (int i = 1; i < entry.length; i++)
            {
                String[] req = entry[i].split(":");
                
                if (req[0].equalsIgnoreCase("defense"))
                {
                    req[0] = "defence";
                }
                
                requirements[i - 1] = new Requirement(Skill.valueOf(req[0].toUpperCase()), Integer.parseInt(req[1]));
            }
            
            skillLocks.put(entry[0], requirements);
        }
    }
    
    // Get Properties
    
    public static boolean getDisableWool()
    {
        return disableWool;
    }
    
    public static boolean getDeathReset()
    {
        return deathReset;
    }
    
    public static int getStartCost()
    {
        return startingCost;
    }
    
    public static int getCostIncrease()
    {
        return costIncrease;
    }
    
    public static int getMaxLevel()
    {
        return maximumLevel;
    }
    
    public static Requirement[] getRequirements(ResourceLocation key)
    {
        return skillLocks.get(key.toString());
    }
    
    public static ForgeConfigSpec getConfig()
    {
        return CONFIG_SPEC;
    }
}