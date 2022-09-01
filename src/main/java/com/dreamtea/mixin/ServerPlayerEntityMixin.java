package com.dreamtea.mixin;

import com.dreamtea.imixin.IEnderLoot;
import com.dreamtea.player.PlayerEnderChest;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IEnderLoot {

  private PlayerEnderChest enderChestLoot;

  @Inject(method = "<init>", at = @At("TAIL"))
  public void initEnderChestLoot(MinecraftServer server, ServerWorld world, GameProfile profile, PlayerPublicKey publicKey, CallbackInfo ci){
    this.enderChestLoot = new PlayerEnderChest();
  }

  @Override
  public PlayerEnderChest getPlayerEnderChest() {
    return enderChestLoot;
  }
}
