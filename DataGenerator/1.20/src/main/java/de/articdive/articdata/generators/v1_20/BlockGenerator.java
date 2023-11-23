package de.articdive.articdata.generators.v1_20;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.articdive.articdata.datagen.annotations.GeneratorEntry;
import de.articdive.articdata.generators.v1_19_3.common.DataGenerator_1_19_3;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Comparator;

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
public final class BlockGenerator extends DataGenerator_1_19_3<Block> {
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
    public JsonObject generate() {

        JsonObject blocks = new JsonObject();
        for (ResourceLocation blockRL : BLOCK_REGISTRY.keySet().stream().sorted(Comparator.comparingInt(value -> BLOCK_REGISTRY.getId(BLOCK_REGISTRY.get(value)))).toList()) {
            Block b = BLOCK_REGISTRY.get(blockRL);

            JsonObject block = new JsonObject();
            block.addProperty("id", BLOCK_REGISTRY.getId(b));
            block.addProperty("mojangName", names.get(b));

            {
                // Block states
                JsonArray blockStates = new JsonArray();
                for (BlockState bs : b.getStateDefinition().getPossibleStates()) {
                    JsonObject state = new JsonObject();

                    state.addProperty("stateId", Block.BLOCK_STATE_REGISTRY.getId(bs));


                    state.addProperty("offsetType", getOffsetType(bs).name());

                    // Shapes (Hitboxes)
                    state.addProperty("shape", bs.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("collisionShape", bs.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("interactionShape", bs.getInteractionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("visualShape", bs.getOcclusionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).toAabbs().toString());
                    state.addProperty("renderShape", bs.getRenderShape().name());

                    blockStates.add(state);
                }
                block.add("states", blockStates);
            }

            blocks.add(blockRL.toString(), block);
        }
        return blocks;
    }

    public static BlockBehaviour.OffsetType getOffsetType(BlockState state) {
        Vec3 vec3 = state.getOffset(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        Vec3 xz_offset = Blocks.BAMBOO_SAPLING.defaultBlockState().getOffset(EmptyBlockGetter.INSTANCE, BlockPos.ZERO); //OFFSET_TYPE = XZ
        Vec3 xyz_offset = Blocks.SMALL_DRIPLEAF.defaultBlockState().getOffset(EmptyBlockGetter.INSTANCE, BlockPos.ZERO); //OFFSET_TYPE = XYZ

        if (vec3.x == xz_offset.x && vec3.y == xz_offset.y && vec3.z == xz_offset.z) {
            return BlockBehaviour.OffsetType.XZ;
        } else if (vec3.x == xyz_offset.x && vec3.y == xyz_offset.y && vec3.z == xyz_offset.z) {
            return BlockBehaviour.OffsetType.XYZ;
        } else {
            return BlockBehaviour.OffsetType.NONE;
        }
    }
}
