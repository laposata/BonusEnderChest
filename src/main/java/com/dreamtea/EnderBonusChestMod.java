package com.dreamtea;

import com.dreamtea.commands.EnderChestBonusGamerule;
import com.dreamtea.commands.RollLootToEnderChest;
import net.fabricmc.api.ModInitializer;
import net.minecraft.inventory.EnderChestInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dreamtea.commands.EnderChestBonusGamerule.ENDER_LOOT_BONUS_RULE;

public class EnderBonusChestMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String NAMESPACE = "ender-bonus-chest";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		RollLootToEnderChest.init();
		EnderChestBonusGamerule.initRule();
		LOGGER.info("Hello Fabric world!");
	}
}
