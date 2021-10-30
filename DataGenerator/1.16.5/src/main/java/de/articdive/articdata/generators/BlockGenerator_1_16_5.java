package de.articdive.articdata.generators;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.articdive.articdata.datagen.DataGenHolder;
import de.articdive.articdata.datagen.DataGenType;
import de.articdive.articdata.datagen.annotations.GeneratorEntry;
import de.articdive.articdata.generators.common.DataGenerator_1_16_5;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GeneratorEntry(name = "Protocol ID", supported = true)
@GeneratorEntry(name = "Namespace ID", supported = true)
@GeneratorEntry(name = "Mojang Name", supported = true)
@GeneratorEntry(name = "Translation Key", supported = true)
@GeneratorEntry(name = "Loot Table Key", supported = true)
@GeneratorEntry(name = "Block States", supported = true)
@GeneratorEntry(name = "BlockState Properties", supported = true)
@GeneratorEntry(name = "Block Entities", supported = true)
@GeneratorEntry(name = "Hardness", supported = true)
@GeneratorEntry(name = "Explosion Resistance", supported = true)
@GeneratorEntry(name = "Friction", supported = true)
@GeneratorEntry(name = "Speed & Jump Factor", supported = true)
@GeneratorEntry(name = "Default Block State", supported = true)
@GeneratorEntry(name = "Corresponding Item", supported = true)
@GeneratorEntry(name = "Corresponding Map Color", supported = true)
@GeneratorEntry(name = "Solid, Liquid, Blocking etc.", supported = true)
@GeneratorEntry(name = "Piston Push Reaction", supported = true)
@GeneratorEntry(name = "Gravity", supported = true)
@GeneratorEntry(name = "Respawn Eligiblity", supported = true)
@GeneratorEntry(name = "Tool Require For Drops", supported = true)
@GeneratorEntry(name = "Large Collision Shape", supported = true)
@GeneratorEntry(name = "Collision Shape Full Block", supported = true)
@GeneratorEntry(name = "Occlusion", supported = true)
@GeneratorEntry(name = "Hitbox", supported = true)
@GeneratorEntry(name = "Collision Hitbox", supported = true)
@GeneratorEntry(name = "Interaction Hitbox", supported = true)
@GeneratorEntry(name = "Occlusion Hitbox", supported = true)
@GeneratorEntry(name = "Visual Hitbox", supported = true)
@GeneratorEntry(name = "Dynamic Shape", supported = true)
@GeneratorEntry(name = "Solid Render", supported = true)
@GeneratorEntry(name = "Light Emission", supported = true)
@GeneratorEntry(name = "Light Block", supported = true)
@GeneratorEntry(name = "Propagates Skylight Down", supported = true)
@GeneratorEntry(name = "Shape for Light Occlusion", supported = true)
@GeneratorEntry(name = "Opacity", supported = true)
@GeneratorEntry(name = "Conditional Opacity", supported = true)
@GeneratorEntry(name = "Render Shape", supported = true)
@GeneratorEntry(name = "Sound Information", supported = true)
public final class BlockGenerator_1_16_5 extends DataGenerator_1_16_5<Block> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockGenerator_1_16_5.class);

    @Override
    public void generateNames() {
        for (Field declaredField : Blocks.class.getDeclaredFields()) {
            if (!Block.class.isAssignableFrom(declaredField.getType())) {
                continue;
            }
            try {
                Block b = (Block) declaredField.get(null);
                names.put(b, declaredField.getName());
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to map block naming system.", e);
                return;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonObject generate() {
        Map<SoundEvent, String> soundNames = (Map<SoundEvent, String>) DataGenHolder.getNameMap(DataGenType.SOUNDS);
        Map<Property<?>, String> bsPropertyNames = (Map<Property<?>, String>) DataGenHolder.getNameMap(DataGenType.BLOCK_PROPERTIES);

        Set<ResourceLocation> blockRLs = Registry.BLOCK.keySet();
        JsonObject blocks = new JsonObject();
        for (ResourceLocation blockRL : blockRLs) {
            Block b = Registry.BLOCK.get(blockRL);

            JsonObject block = new JsonObject();
            block.addProperty("id", Registry.BLOCK.getId(b));
            block.addProperty("mojangName", names.get(b));
            block.addProperty("translationKey", b.getDescriptionId());
            block.addProperty("explosionResistance", b.getExplosionResistance());
            block.addProperty("friction", b.getFriction());
            block.addProperty("speedFactor", b.getSpeedFactor());
            block.addProperty("jumpFactor", b.getJumpFactor());
            block.addProperty("dynamicShape", b.hasDynamicShape());
            block.addProperty("defaultStateId", Block.BLOCK_STATE_REGISTRY.getId(b.defaultBlockState()));
            block.addProperty("lootTableLocation", b.getLootTable().toString());

            Item correspondingItem = Item.BY_BLOCK.getOrDefault(b, null);
            if (correspondingItem != null) {
                block.addProperty("correspondingItem", Registry.ITEM.getKey(correspondingItem).toString());
            }
            block.addProperty("blockEntity", b instanceof EntityBlock);
            block.addProperty("gravity", b instanceof FallingBlock);
            block.addProperty("canRespawnIn", b.isPossibleToRespawnInThis());

            // Block proprties
            {
                JsonArray properties = new JsonArray();
                for (Property<?> p : b.getStateDefinition().getProperties()) {
                    properties.add(bsPropertyNames.get(p));
                }
                block.add("properties", properties);
            }
            {
                // Block states
                JsonArray blockStates = new JsonArray();
                for (BlockState bs : b.getStateDefinition().getPossibleStates()) {
                    JsonObject state = new JsonObject();

                    {
                        JsonObject properties = new JsonObject();
                        for (Map.Entry<Property<?>, Comparable<?>> entry : bs.getValues().entrySet()) {
                            Class<?> valClass = entry.getKey().getValueClass();
                            if (valClass.equals(Integer.class)) {
                                properties.addProperty(entry.getKey().getName(), (Integer) entry.getValue());
                            } else if (valClass.equals(Boolean.class)) {
                                properties.addProperty(entry.getKey().getName(), (Boolean) entry.getValue());
                            } else {
                                properties.addProperty(entry.getKey().getName(), String.valueOf(entry.getValue()));
                            }
                        }
                        state.add("properties", properties);
                    }

                    state.addProperty("stateId", Block.BLOCK_STATE_REGISTRY.getId(bs));

                    // Default values
                    final boolean conditionallyFullyOpaque = bs.canOcclude() & bs.useShapeForLightOcclusion();
                    final int lightBlock = bs.getLightBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                    state.addProperty("hardness", bs.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
                    state.addProperty("lightEmission", bs.getLightEmission());
                    state.addProperty("occludes", bs.canOcclude());
                    state.addProperty("useShapeForLightOcclusion", bs.useShapeForLightOcclusion());
                    state.addProperty("propagatesSkylightDown", bs.propagatesSkylightDown(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
                    state.addProperty("lightBlock", lightBlock);
                    state.addProperty("conditionallyFullyOpaque", conditionallyFullyOpaque);
                    state.addProperty("opacity", conditionallyFullyOpaque ? -1 : lightBlock);
                    state.addProperty("pushReaction", bs.getPistonPushReaction().name());
                    state.addProperty("mapColorId", bs.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).id);
                    state.addProperty("blocksMotion", bs.getMaterial().blocksMotion());
                    state.addProperty("flammable", bs.getMaterial().isFlammable());
                    state.addProperty("air", bs.isAir());
                    state.addProperty("liquid", bs.getMaterial().isLiquid());
                    state.addProperty("replaceable", bs.getMaterial().isReplaceable());
                    state.addProperty("solid", bs.getMaterial().isSolid());
                    state.addProperty("solidBlocking", bs.getMaterial().isSolidBlocking());
                    state.addProperty("toolRequired", bs.requiresCorrectToolForDrops());
                    {
                        JsonObject sounds = new JsonObject();
                        {
                            try {
                                Field breakSoundF = SoundType.class.getDeclaredField("breakSound");
                                Field hitSoundF = SoundType.class.getDeclaredField("hitSound");
                                breakSoundF.setAccessible(true);
                                hitSoundF.setAccessible(true);

                                SoundEvent breakSound = (SoundEvent) breakSoundF.get(bs.getSoundType());
                                SoundEvent hitSound = (SoundEvent) hitSoundF.get(bs.getSoundType());

                                sounds.addProperty("breakSound", soundNames.get(breakSound));
                                sounds.addProperty("hitSound", soundNames.get(hitSound));
                            } catch (IllegalAccessException | NoSuchFieldException e) {
                                LOGGER.error("Failed to access block sounds.", e);
                            }
                        }
                        sounds.addProperty("stepSound", soundNames.get(bs.getSoundType().getStepSound()));
                        sounds.addProperty("fallSound", soundNames.get(bs.getSoundType().getFallSound()));
                        sounds.addProperty("placeSound", soundNames.get(bs.getSoundType().getPlaceSound()));
                        sounds.addProperty("pitch", bs.getSoundType().getPitch());
                        sounds.addProperty("volume", bs.getSoundType().getVolume());

                        state.add("sounds", sounds);
                    }
                    // Shapes (Hitboxes)
                    state.addProperty("largeCollisionShape", bs.hasLargeCollisionShape());
                    state.addProperty("collisionShapeFullBlock", bs.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
                    state.addProperty("solidRender", bs.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
                    state.addProperty("shape", bs.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("collisionShape", bs.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("interactionShape", bs.getInteractionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("occlusionShape", bs.getOcclusionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("visualShape", bs.getOcclusionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("renderShape", bs.getRenderShape().name());
                    state.addProperty("fluidState", Registry.FLUID.getKey(bs.getFluidState().getType()).toString());

                    blockStates.add(state);
                }
                block.add("states", blockStates);
            }

            blocks.add(blockRL.toString(), block);
        }
        return blocks;
    }
}
