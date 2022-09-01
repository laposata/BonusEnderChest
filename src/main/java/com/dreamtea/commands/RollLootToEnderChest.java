package com.dreamtea.commands;

import com.dreamtea.imixin.IEnderLoot;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class RollLootToEnderChest {

  public static void init(){
    CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> register(dispatcher)));
  }

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
      (CommandManager.literal("loot").requires(source -> source.hasPermissionLevel(2)))
        .then(CommandManager.literal("enderChest")
            .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).executes(context -> relootEnderChest(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets")) ))));}

  public static int relootEnderChest(ServerCommandSource context, Collection<GameProfile> targets) throws CommandSyntaxException {
    PlayerManager playerManager = context.getServer().getPlayerManager();
    int count = 0;
    for(GameProfile gp: targets){
      ServerPlayerEntity serverPlayer = playerManager.getPlayer(gp.getId());
      if(serverPlayer instanceof IEnderLoot iel){
        if(iel.getPlayerEnderChest().reset()){
          count ++;
        }
      }
    }
    return count;
  }



}
