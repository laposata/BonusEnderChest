package com.dreamtea.player;

import com.dreamtea.commands.AllowEnderOverflowGamerule;
import com.dreamtea.imixin.IEnderInv;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.dreamtea.datagen.loottable.EnderBonusChestLootTable.BASIC_ENDER_BONUS;


public class PlayerEnderChest {
  public static final String RECEIVED_LOOT = "received";
  public static final String EXTRA_LOOT = "extra_enderchest_loot";

  private boolean receivedLoot = false;
  private SimpleInventory loot;
  private ArrayList<ItemStack> lootReceived;
  public final PlayerEntity owner;

  public PlayerEnderChest(PlayerEntity owner) {
    this.owner = owner;
  }
  public PlayerEnderChest(NbtCompound data, PlayerEntity owner) {
    this.owner = owner;
    this.receivedLoot = data.getBoolean(RECEIVED_LOOT);
    this.loot = new SimpleInventory(27);
    this.loot.readNbtList(data.getList(EXTRA_LOOT, NbtElement.COMPOUND_TYPE));
    needsProcessing();
  }

  public void insertLoot(ItemStack item){
    if(lootReceived == null){
      lootReceived = new ArrayList<>();
    }
    lootReceived.add(item);
  }

  public void insertLoot(List<ItemStack> item){
    if(lootReceived == null){
      lootReceived = new ArrayList<>();
    }
    lootReceived.addAll(item);
  }

  public void writeNbt(NbtCompound data){
    needsProcessing();
    data.putBoolean(RECEIVED_LOOT, receivedLoot);
    NbtList list = new NbtList();
    if(lootReceived != null) {
      for (ItemStack item : lootReceived) {
        list.add(item.writeNbt(new NbtCompound()));
      }
    }
    data.put(EXTRA_LOOT,list);
  }

  public boolean reset(){
    if(this.receivedLoot) {
      this.receivedLoot = false;
      return true;
    }
    return false;
  }

  public int remove(Predicate<ItemStack> shouldRemove, int maxCount) {
    int i = 0;
    boolean bl = maxCount == 0;
    i += Inventories.remove(owner.getEnderChestInventory(), shouldRemove, maxCount - i, bl);
    for(ItemStack item: lootReceived) {
      i += Inventories.remove(item, shouldRemove, maxCount - i, bl);
    }
    ItemStack itemStack = this.owner.currentScreenHandler.getCursorStack();
    i += Inventories.remove(itemStack, shouldRemove, maxCount - i, bl);
    if (itemStack.isEmpty()) {
      this.owner.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
    }
    return i;
  }

  public void onOpen() {
    getLoot((ServerWorld) owner.getWorld(), owner.getRandom());
    disperseReceived(getEnderChestInv(), owner.getRandom());
    if(!owner.getWorld().getGameRules().getBoolean(AllowEnderOverflowGamerule.ENDER_CHEST_OVERFLOW)){
      lootReceived = null;
    }
  }

  private EnderChestInventory getEnderChestInv(){
    return ((IEnderInv)owner).getBaseEnderchestInventory();
  }

  private void getLoot(ServerWorld world, Random random){
    if(!receivedLoot){
      loot = new SimpleInventory(27);
      LootTable lootTable = world.getServer().getLootManager().getTable(BASIC_ENDER_BONUS);
      if (owner instanceof ServerPlayerEntity) {
        Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)owner, BASIC_ENDER_BONUS);
      }
      LootContext.Builder builder = new LootContext.Builder(world).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(owner.getBlockPos())).random(random);
      builder.luck(owner.getLuck()).parameter(LootContextParameters.THIS_ENTITY, owner);
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
}
