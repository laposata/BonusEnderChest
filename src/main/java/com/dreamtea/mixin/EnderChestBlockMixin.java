package com.dreamtea.mixin;

import com.dreamtea.imixin.IEnderLoot;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.OptionalInt;

import static com.dreamtea.commands.EnderChestBonusGamerule.ENDER_LOOT_BONUS_CHESTS;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestBlockMixin {

  @Shadow @Final private static Text CONTAINER_NAME;

  @Redirect(method = "onUse",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/player/PlayerEntity;openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;"
    )
  )
  public OptionalInt onOpenAddLoot(PlayerEntity instance, NamedScreenHandlerFactory factory){
    if(instance.getWorld().getGameRules().getBoolean(ENDER_LOOT_BONUS_CHESTS) && instance instanceof IEnderLoot iel){
      return instance.openHandledScreen(iel.getPlayerEnderChest().handle(CONTAINER_NAME));
    }
    return instance.openHandledScreen(factory);
  }
}
