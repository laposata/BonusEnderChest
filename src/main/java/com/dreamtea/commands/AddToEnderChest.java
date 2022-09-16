package com.dreamtea.commands;

import com.dreamtea.imixin.IEnderInv;
import com.dreamtea.imixin.IEnderLoot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Collection;

public class AddToEnderChest {
  public static final int MAX_STACKS = 100;

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
    dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder) CommandManager.literal("giveEnder").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess)).executes(context -> execute((ServerCommandSource)context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), EntityArgumentType.getPlayers(context, "targets"), 1))).then(CommandManager.argument("count", IntegerArgumentType.integer(1)).executes(context -> execute((ServerCommandSource)context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count")))))));
  }

  private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
    int i = item.getItem().getMaxCount();
    int j = i * 100;
    if (count > j) {
      source.sendError(Text.translatable("commands.give.failed.toomanyitems", j, item.createStack(count, false).toHoverableText()));
      return 0;
    }
    for (ServerPlayerEntity serverPlayerEntity : targets) {
      int k = count;
      while (k > 0) {
        int l = Math.min(i, k);
        k -= l;
        ItemStack itemStack = item.createStack(l, false);
        if(serverPlayerEntity instanceof IEnderLoot inv){
          inv.getPlayerEnderChest().insertLoot(itemStack);
        }
        serverPlayerEntity.currentScreenHandler.sendContentUpdates();
      }
    }
    if (targets.size() == 1) {
      source.sendFeedback(Text.translatable("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(Text.translatable("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.size()), true);
    }
    return targets.size();
  }
}
