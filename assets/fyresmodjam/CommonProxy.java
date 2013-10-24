package assets.fyresmodjam;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
	public void register() {
		//TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
	}

	public void sendPlayerMessage(String message) {}

	public void loadFromConfig(Configuration config) {
		ModjamMod.itemID = config.getItem("item_ids", ModjamMod.itemID + 4096).getInt() - 4096;
		ModjamMod.blockID = config.getBlock("block_ids",ModjamMod. blockID).getInt();
		
		ModjamMod.pillarGlow = config.get(config.CATEGORY_GENERAL, "pillar_glow", ModjamMod.pillarGlow).getBoolean(ModjamMod.pillarGlow);
		ModjamMod.spawnTraps = !(config.get(config.CATEGORY_GENERAL, "disable_traps", !ModjamMod.spawnTraps).getBoolean(!ModjamMod.spawnTraps));
		ModjamMod.spawnTowers = config.get(config.CATEGORY_GENERAL, "spawn_towers", ModjamMod.spawnTowers).getBoolean(ModjamMod.spawnTowers);
		ModjamMod.spawnRandomPillars = config.get(config.CATEGORY_GENERAL, "spawn_random_pillars", ModjamMod.spawnRandomPillars).getBoolean(ModjamMod.spawnRandomPillars);
		ModjamMod.disableDisadvantages = config.get(config.CATEGORY_GENERAL, "disable_disadvantages", ModjamMod.disableDisadvantages).getBoolean(ModjamMod.disableDisadvantages);
		ModjamMod.versionChecking = config.get(config.CATEGORY_GENERAL, "version_checking", ModjamMod.versionChecking).getBoolean(ModjamMod.versionChecking);
	}
}
