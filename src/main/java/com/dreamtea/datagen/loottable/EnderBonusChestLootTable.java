package com.dreamtea.datagen.loottable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

import static com.dreamtea.EnderBonusChestMod.NAMESPACE;

public class EnderBonusChestLootTable extends SimpleFabricLootTableProvider {
  public static final Identifier BASIC_ENDER_BONUS = new Identifier(NAMESPACE, "chests/ender_chest_bonus_1");
  public EnderBonusChestLootTable(FabricDataGenerator dataGenerator) {
    super(dataGenerator, LootContextTypes.CHEST);
  }
  @Override
  public void accept(BiConsumer<Identifier, LootTable.Builder> identifierBuilderBiConsumer) {
    identifierBuilderBiConsumer.accept(BASIC_ENDER_BONUS, basicEnderChestBonusLoot());
  }
  private static LootTable.Builder basicEnderChestBonusLoot(){
    return LootTable.builder()
      .pool(
        LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
          .with(ItemEntry.builder(Items.DIAMOND_PICKAXE).weight(5))
          .with(ItemEntry.builder(Items.DIAMOND)
            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3)))))
      .pool(
        LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
          .with(ItemEntry.builder(Items.DIAMOND_SWORD))
          .with(ItemEntry.builder(Items.DIAMOND)
            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2)))))
      .pool(
        LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
          .with(ItemEntry.builder(Items.TOTEM_OF_UNDYING)))
      .pool(
        LootPool.builder().rolls(ConstantLootNumberProvider.create(6.0F))
          .with(ItemEntry.builder(Items.COOKED_PORKCHOP).weight(5)
            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
          .with(ItemEntry.builder(Items.COOKED_BEEF).weight(5)
            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
          .with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(3)
            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
          .with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(1)
            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))))
      .pool(
        LootPool.builder().rolls(UniformLootNumberProvider.create(2f, 5f))
          .with(ItemEntry.builder(Items.POTION).weight(1)
            .apply(SetPotionLootFunction.builder(Potions.LONG_REGENERATION)))
          .with(ItemEntry.builder(Items.POTION).weight(3)
            .apply(SetPotionLootFunction.builder(Potions.REGENERATION)))
      );

  }
}
