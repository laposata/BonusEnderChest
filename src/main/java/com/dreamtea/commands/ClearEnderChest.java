package com.dreamtea.commands;

import com.dreamtea.imixin.IEnderLoot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ClearCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;



public class ClearEnderChest {

  private static final DynamicCommandExceptionType FAILED_SINGLE_EXCEPTION = new DynamicCommandExceptionType(playerName -> Text.translatable("clear.failed.single", playerName));
  private static final DynamicCommandExceptionType FAILED_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(playerCount -> Text.translatable("clear.failed.multiple", playerCount));

  public static void init(){
    CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> register(dispatcher, registryAccess)));
  }
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
    dispatcher.register((CommandManager.literal("clearEnderchest").requires(source -> source.hasPermissionLevel(2)))
          .executes(context -> execute(context.getSource(), Collections.singleton(context.getSource().getPlayerOrThrow()), stack -> true, -1))
      .then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players())
        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), stack -> true, -1)))
      .then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate(commandRegistryAccess))
        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), ItemPredicateArgumentType.getItemStackPredicate(context, "item"), -1)))
      .then(CommandManager.argument("maxCount", IntegerArgumentType.integer(0))
        .executes(context -> execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), ItemPredicateArgumentType.getItemStackPredicate(context, "item"), IntegerArgumentType.getInteger(context, "maxCount")))))));
  }

  private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Predicate<ItemStack> item, int maxCount) throws CommandSyntaxException {
    int i = 0;
    for (ServerPlayerEntity serverPlayerEntity : targets) {
      if (serverPlayerEntity instanceof IEnderLoot iel) {
        i += iel.getPlayerEnderChest().remove(item, maxCount);
      }
    }
    if (i == 0) {
      if (targets.size() == 1) {
        throw FAILED_SINGLE_EXCEPTION.create(targets.iterator().next().getName());
      }
      throw FAILED_MULTIPLE_EXCEPTION.create(targets.size());
    }
    if (maxCount == 0) {
      if (targets.size() == 1) {
        source.sendFeedback(Text.translatable("commands.clear.test.single", i, targets.iterator().next().getDisplayName()), true);
      } else {
        source.sendFeedback(Text.translatable("commands.clear.test.multiple", i, targets.size()), true);
      }
    } else if (targets.size() == 1) {
      source.sendFeedback(Text.translatable("commands.clear.success.single", i, targets.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(Text.translatable("commands.clear.success.multiple", i, targets.size()), true);
    }
    return i;
  }
}
