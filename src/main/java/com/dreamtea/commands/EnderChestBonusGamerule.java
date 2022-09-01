package com.dreamtea.commands;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class EnderChestBonusGamerule {

  public static final String ENDER_LOOT_BONUS_RULE = "enderChestBonusLoot";
  public static GameRules.Key<GameRules.BooleanRule> ENDER_LOOT_BONUS_CHESTS;

  public static void initRule(){
    ENDER_LOOT_BONUS_CHESTS = GameRuleRegistry.register(ENDER_LOOT_BONUS_RULE, GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));
  }
}

