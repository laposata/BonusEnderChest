package com.dreamtea.commands;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class AllowEnderOverflowGamerule {
  public static final String ENDER_CHEST_OVERFLOW_RULE = "enderChestOverflow";
  public static GameRules.Key<GameRules.BooleanRule> ENDER_CHEST_OVERFLOW;

  public static void initRule(){
    ENDER_CHEST_OVERFLOW = GameRuleRegistry.register(ENDER_CHEST_OVERFLOW_RULE, GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
  }
}
