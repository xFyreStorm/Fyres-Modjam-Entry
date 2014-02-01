package fyresmodjam;

import net.minecraftforge.common.Configuration;

import static fyresmodjam.ModjamMod.*;

public class CommonProxy
{
    public void register()
    {
        //TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
    }

    public void sendPlayerMessage(String message) {}

    public void loadFromConfig(Configuration config)
    {
        itemID = config.getItem("item_ids", itemID).getInt();
        blockID = config.getBlock("block_ids", blockID).getInt();

        pillarGlow = config.get(Configuration.CATEGORY_GENERAL, "pillar_glow", pillarGlow).getBoolean(pillarGlow);

        pillarGenChance = config.get(Configuration.CATEGORY_GENERAL, "pillar_gen_difficulty", pillarGenChance).getInt();
        maxPillarsPerChunk = config.get(Configuration.CATEGORY_GENERAL, "max_pillars_per_chunk", maxPillarsPerChunk).getInt();
        towerGenChance = config.get(Configuration.CATEGORY_GENERAL, "tower_gen_difficulty", towerGenChance).getInt();
        trapGenChance = config.get(Configuration.CATEGORY_GENERAL, "trap_gen_difficulty", trapGenChance).getInt();
        mushroomReplaceChance = config.get(Configuration.CATEGORY_GENERAL, "mushroom_replace_difficulty", mushroomReplaceChance).getInt();

        spawnTraps = !(config.get(Configuration.CATEGORY_GENERAL, "disable_traps", !spawnTraps).getBoolean(!spawnTraps));
        spawnTowers = config.get(Configuration.CATEGORY_GENERAL, "spawn_towers", spawnTowers).getBoolean(spawnTowers);
        spawnRandomPillars = config.get(Configuration.CATEGORY_GENERAL, "spawn_random_pillars", spawnRandomPillars).getBoolean(spawnRandomPillars);
        disableDisadvantages = config.get(Configuration.CATEGORY_GENERAL, "disable_disadvantages", disableDisadvantages).getBoolean(disableDisadvantages);
        versionChecking = config.get(Configuration.CATEGORY_GENERAL, "version_checking", versionChecking).getBoolean(versionChecking);

        showAllPillarsInCreative = config.get(Configuration.CATEGORY_GENERAL, "show_all_pillars_in_creative", showAllPillarsInCreative).getBoolean(showAllPillarsInCreative);

        enableMobKillStats = config.get(Configuration.CATEGORY_GENERAL, "enable_mob_kill_stats", enableMobKillStats).getBoolean(enableMobKillStats);
        enableWeaponKillStats = config.get(Configuration.CATEGORY_GENERAL, "enable_weapon_kill_stats", enableWeaponKillStats).getBoolean(enableWeaponKillStats);
        enableCraftingStats = config.get(Configuration.CATEGORY_GENERAL, "enable_crafting_stats", enableCraftingStats).getBoolean(enableCraftingStats);

        trapsBelowGroundOnly = config.get(Configuration.CATEGORY_GENERAL, "traps_below_ground_only", trapsBelowGroundOnly).getBoolean(trapsBelowGroundOnly);
    }
}
