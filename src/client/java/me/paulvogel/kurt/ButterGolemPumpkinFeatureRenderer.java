package me.paulvogel.kurt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class ButterGolemPumpkinFeatureRenderer extends FeatureRenderer<ButterGolemEntity, ButterGolemEntityModel<ButterGolemEntity>> {
    private final BlockRenderManager blockRenderManager;
    private final ItemRenderer itemRenderer;

    public ButterGolemPumpkinFeatureRenderer(FeatureRendererContext<ButterGolemEntity, ButterGolemEntityModel<ButterGolemEntity>> context, BlockRenderManager blockRenderManager, ItemRenderer itemRenderer) {
        super(context);
        this.blockRenderManager = blockRenderManager;
        this.itemRenderer = itemRenderer;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ButterGolemEntity butterGolemEntity, float f, float g, float h, float j, float k, float l) {
        if (butterGolemEntity.hasPumpkin()) {
            boolean bl = MinecraftClient.getInstance().hasOutline(butterGolemEntity) && butterGolemEntity.isInvisible();
            if (!butterGolemEntity.isInvisible() || bl) {
                matrixStack.push();
                ((ButterGolemEntityModel)this.getContextModel()).getHead().rotate(matrixStack);
                float m = 0.625F;
                matrixStack.translate(0.0, -0.34375, 0.0);
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                matrixStack.scale(0.625F, -0.625F, -0.625F);
                ItemStack itemStack = new ItemStack(Blocks.CARVED_PUMPKIN);
                if (bl) {
                    BlockState blockState = Blocks.CARVED_PUMPKIN.getDefaultState();
                    BakedModel bakedModel = this.blockRenderManager.getModel(blockState);
                    int n = LivingEntityRenderer.getOverlay(butterGolemEntity, 0.0F);
                    matrixStack.translate(-0.5, -0.5, -0.5);
                    this.blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), blockState, bakedModel, 0.0F, 0.0F, 0.0F, i, n);
                } else {
                    this.itemRenderer.renderItem(butterGolemEntity, itemStack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, butterGolemEntity.world, i, LivingEntityRenderer.getOverlay(butterGolemEntity, 0.0F), butterGolemEntity.getId());
                }

                matrixStack.pop();
            }
        }
    }
}
