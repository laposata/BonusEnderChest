package com.dreamtea.mixin;

import com.dreamtea.imixin.IEnderInv;
import com.dreamtea.imixin.IEnderLoot;
import com.dreamtea.player.PlayerEnderChest;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IEnderLoot, IEnderInv {

  private static final String ENDERCHEST_LOOT = "enderchest_loot";
  private PlayerEnderChest enderChestLoot;

  @Inject(method = "<init>", at = @At("TAIL"))
  public void initEnderChestLoot(MinecraftServer server, ServerWorld world, GameProfile profile, PlayerPublicKey publicKey, CallbackInfo ci){
    this.enderChestLoot = new PlayerEnderChest((ServerPlayerEntity)(Object)this);
  }
  @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
  public void readNbt(NbtCompound nbt, CallbackInfo ci){
    this.enderChestLoot = new PlayerEnderChest(nbt, (ServerPlayerEntity)(Object)this);
  }

  @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
  public void writeNbt(NbtCompound nbt, CallbackInfo ci){
    this.enderChestLoot.writeNbt(nbt);
  }

  @Override
  public PlayerEnderChest getPlayerEnderChest() {
    return enderChestLoot;
  }
}
