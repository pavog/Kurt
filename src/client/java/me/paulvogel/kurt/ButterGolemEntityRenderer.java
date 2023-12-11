package me.paulvogel.kurt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ButterGolemEntityRenderer extends MobEntityRenderer<ButterGolemEntity, ButterGolemEntityModel<ButterGolemEntity>> {
    public ButterGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ButterGolemEntityModel<>(context.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5F);
        this.addFeature(new ButterGolemPumpkinFeatureRenderer(this, context.getBlockRenderManager(), context.getItemRenderer()));
    }

    public Identifier getTexture(ButterGolemEntity ButterGolemEntity) {
        return new Identifier("kurt", "textures/entity/butter_golem.png");
    }
}