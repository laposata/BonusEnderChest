package com.dreamtea.player;

import com.dreamtea.imixin.IEnderLoot;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import static com.dreamtea.datagen.loottable.EnderBonusChestLootTable.BASIC_ENDER_BONUS;


public class PlayerEnderChest {
  private boolean receivedLoot = false;
  private SimpleInventory loot;
  private ArrayList<ItemStack> lootReceived;
  public boolean reset(){
    if(this.receivedLoot) {
      this.receivedLoot = false;
      return true;
    }
    return false;
  }

  public EnderChestScreenHandlerFactory handle(Text containerName){
    return new EnderChestScreenHandlerFactory(containerName);
  }
  public void onOpen(PlayerEntity player) {
    getLoot((ServerWorld) player.getWorld(), player, player.getRandom());
    disperseReceived(player.getEnderChestInventory(), player.getRandom());
  }

  private void getLoot(ServerWorld world, PlayerEntity player, Random random){
    if(!receivedLoot){
      loot = new SimpleInventory(27);
      LootTable lootTable = world.getServer().getLootManager().getTable(BASIC_ENDER_BONUS);
      if (player instanceof ServerPlayerEntity) {
        Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, BASIC_ENDER_BONUS);
      }
      LootContext.Builder builder = new LootContext.Builder(world).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(player.getBlockPos())).random(random);
      builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
      lootTable.supplyInventory(loot, builder.build(LootContextTypes.CHEST));
      receivedLoot = true;
    }
  }

  private List<Integer> getFreeSlots(Inventory inventory, Random random) {
    ObjectArrayList<Integer> objectArrayList = new ObjectArrayList<Integer>();
    for (int i = 0; i < inventory.size(); ++i) {
      if (!inventory.getStack(i).isEmpty()) continue;
      objectArrayList.add(i);
    }
    Util.shuffle(objectArrayList, random);
    return objectArrayList;
  }

  private boolean needsProcessing(){
    if(lootReceived != null && lootReceived.isEmpty()){
      lootReceived = null;
    }
    if(loot == null && lootReceived == null) return false;
    if(loot != null){
      if(lootReceived == null){
        lootReceived = new ArrayList<>();
      }
      for(int i = 0; i < loot.size(); i ++){
        ItemStack st = loot.getStack(i);
        if(!st.isEmpty()){
          lootReceived.add(st);
        }
      }
      loot = null;
    }
    return true;
  }

  private void disperseReceived(EnderChestInventory inventory, Random random) {
    if(!needsProcessing()) return;
    List<Integer> freeSlots = getFreeSlots(inventory, random);
    if(freeSlots.size() < lootReceived.size()){
      disperseCondensed(inventory, random);
      return;
    }
    for(ItemStack item: lootReceived){
      inventory.setStack(freeSlots.remove(0), item);
    }
    lootReceived = null;
  }

  private void disperseCondensed(EnderChestInventory inventory, Random random){
    List<Integer> freeSlots = getFreeSlots(inventory, random);
    List<ItemStack> toMerge = new ArrayList<>();
    int countReceived = this.lootReceived.size();
    for(int i = 0; i < countReceived; i++){
      //pop front of the list
      ItemStack processing = lootReceived.remove(0);
      //if any stacks in the inventory can be merged with popped item, save that stack to toMerge
      if(inventory.containsAny(
        (stack) -> {
         if(ItemStack.canCombine(stack, processing)
          && ((stack.getItem().getMaxCount() - stack.getCount()) > processing.getCount())){
           toMerge.add(stack);
           return true;
         }
         return false;
        }
      )){
        //if to merge is here, add the items, then discard the pointer
        toMerge.get(0).increment(processing.getCount());
        toMerge.clear();
        continue;
      }
      if(!freeSlots.isEmpty()){
        inventory.setStack(freeSlots.remove(0), processing);
        continue;
      }
      lootReceived.add(processing);
    }

  }

  public static class EnderChestScreenHandlerFactory
    implements NamedScreenHandlerFactory {
    private final Text name;

    public EnderChestScreenHandlerFactory( Text name) {
      this.name = name;
    }

    @Override
    public Text getDisplayName() {
      return this.name;
    }

    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      if(playerEntity instanceof IEnderLoot iel){
        iel.getPlayerEnderChest().onOpen(playerEntity);
      }
      return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, playerEntity.getEnderChestInventory());
    }
  }
}
