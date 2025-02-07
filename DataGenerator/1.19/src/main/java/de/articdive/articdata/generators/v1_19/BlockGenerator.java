package de.articdive.articdata.generators.v1_19;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.articdive.articdata.datagen.DataGenHolder;
import de.articdive.articdata.datagen.DataGenType;
import de.articdive.articdata.datagen.DataGenerator;
import de.articdive.articdata.datagen.annotations.GeneratorEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
@GeneratorEntry(name = "Offset", supported = false, description = "Now BlockState-specific (1.19).")
@GeneratorEntry(name = "Vertical Offset", supported = true)
@GeneratorEntry(name = "Horizontal Offset", supported = true)
@GeneratorEntry(name = "Sound Information", supported = true)
@GeneratorEntry(name = "Pick Block Information", supported = true)
public final class BlockGenerator extends DataGenerator<Block> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockGenerator.class);

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

        // Sort by ID
        List<ResourceLocation> blockRLs = Registry.BLOCK.keySet().stream().sorted(Comparator.comparingInt(value -> Registry.BLOCK.getId(Registry.BLOCK.get(value)))).toList();

        JsonObject blocks = new JsonObject();
        for (ResourceLocation blockRL : blockRLs) {
            Block b = Registry.BLOCK.get(blockRL);

            JsonObject block = new JsonObject();
            block.addProperty("id", Registry.BLOCK.getId(b));
            block.addProperty("mojangName", names.get(b));

            {
                // Block states
                JsonArray blockStates = new JsonArray();
                for (BlockState bs : b.getStateDefinition().getPossibleStates()) {
                    JsonObject state = new JsonObject();

                    state.addProperty("stateId", Block.BLOCK_STATE_REGISTRY.getId(bs));

                    // Default values
                    state.addProperty("offsetType", bs.getOffsetType().name());

                    // Shapes (Hitboxes)
                    state.addProperty("shape", bs.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("collisionShape", bs.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("interactionShape", bs.getInteractionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("visualShape", bs.getOcclusionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("renderShape", bs.getRenderShape().name());

                    blockStates.add(state);
                }
                block.add("states", blockStates);
                block.add("states", blockStates);
            }

            blocks.add(blockRL.toString(), block);
        }
        return blocks;
    }
}
