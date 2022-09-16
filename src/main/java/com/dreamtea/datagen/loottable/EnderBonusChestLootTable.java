package com.dreamtea.datagen.loottable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

import static com.dreamtea.EnderBonusChestMod.NAMESPACE;
import static net.minecraft.enchantment.Enchantments.EFFICIENCY;
import static net.minecraft.enchantment.Enchantments.MENDING;
import static net.minecraft.enchantment.Enchantments.SHARPNESS;
import static net.minecraft.enchantment.Enchantments.SWIFT_SNEAK;
import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.item.Items.BOOK;
import static net.minecraft.item.Items.ENCHANTED_BOOK;

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
        LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
          .with(ItemEntry.builder(BOOK))
            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2f)))
            .apply(new SetEnchantmentsLootFunction.Builder().enchantment(MENDING, ConstantLootNumberProvider.create(1)))
          .with(ItemEntry.builder(BOOK)).apply(new SetEnchantmentsLootFunction.Builder().enchantment(SHARPNESS, ConstantLootNumberProvider.create(5)))
          .with(ItemEntry.builder(BOOK)).apply(new SetEnchantmentsLootFunction.Builder().enchantment(SWIFT_SNEAK, ConstantLootNumberProvider.create(3)))
          .with(ItemEntry.builder(BOOK))
            .apply(new SetEnchantmentsLootFunction.Builder().enchantment(UNBREAKING, ConstantLootNumberProvider.create(3)))
            .apply(new SetEnchantmentsLootFunction.Builder().enchantment(EFFICIENCY, ConstantLootNumberProvider.create(4))))
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
        LootPool.builder().rolls(UniformLootNumberProvider.create(1f, 2f))
          .with(ItemEntry.builder(Items.POTION).weight(1)
            .apply(SetPotionLootFunction.builder(Potions.LONG_REGENERATION)))
          .with(ItemEntry.builder(Items.POTION).weight(3)
            .apply(SetPotionLootFunction.builder(Potions.REGENERATION)))
      );

  }
}