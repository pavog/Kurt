package me.paulvogel.kurt;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kurt implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("kurt");

    public static final Item BUTTER = new Item(new FabricItemSettings());
    public static final Block BUTTER_BLOCK = new Block(FabricBlockSettings.create().strength(1.5f));
    public static final Item BUTTER_BLOCK_ITEM = new BlockItem(BUTTER_BLOCK, new FabricItemSettings());
    public static final Block BUTTER_PUDDLE = new ButterPuddleBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.CLEAR)
                    .notSolid()
                    .replaceable()
                    .ticksRandomly()
                    .strength(0.1F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.SNOW)
                    .blockVision((state, world, pos) -> state.get(ButterPuddleBlock.LAYERS) >= 8)
                    .pistonBehavior(PistonBehavior.DESTROY));

    public static final EntityType<ButterGolemEntity> BUTTER_GOLEM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("kurt", "butter_golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ButterGolemEntity::new).dimensions(EntityDimensions.fixed(0.75f, 1.95f)).build()
    );

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, new Identifier("kurt", "butter"), BUTTER);
        Registry.register(Registries.ITEM, new Identifier("kurt", "butter_block"), BUTTER_BLOCK_ITEM);
        Registry.register(Registries.BLOCK, new Identifier("kurt", "butter_block"), BUTTER_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("kurt", "butter_puddle"), BUTTER_PUDDLE);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.add(BUTTER_BLOCK_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.add(BUTTER);
        });


        FabricDefaultAttributeRegistry.register(BUTTER_GOLEM, ButterGolemEntity.createButterGolemAttributes());

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Kurt mod loaded!");
    }
}