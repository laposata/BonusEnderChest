package com.dreamtea.mixin;

import com.dreamtea.imixin.IEnderInv;
import com.dreamtea.imixin.IEnderLoot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.dreamtea.commands.EnderChestBonusGamerule.ENDER_LOOT_BONUS_CHESTS;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IEnderInv {
  @Shadow protected EnderChestInventory enderChestInventory;

  @Inject(method = "getEnderChestInventory", at = @At("HEAD"))
  public void getEnderChest(CallbackInfoReturnable<EnderChestInventory> cir){
    if((PlayerEntity)(Object) this instanceof ServerPlayerEntity spe) {
      if (spe.getWorld().getGameRules().getBoolean(ENDER_LOOT_BONUS_CHESTS)) {
        if (this instanceof IEnderLoot iel) {
          iel.getPlayerEnderChest().onOpen();
        }
      }
    }
  }

  @Override
  public EnderChestInventory getBaseEnderchestInventory() {
    return this.enderChestInventory;
  }
}
