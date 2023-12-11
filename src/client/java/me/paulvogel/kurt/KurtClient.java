package me.paulvogel.kurt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class KurtClient implements ClientModInitializer {

	public static final EntityModelLayer MODEL_BUTTER_GOLEM_LAYER = new EntityModelLayer(new Identifier("kurt", "butter_golem"), "main");

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(Kurt.BUTTER_GOLEM, ButterGolemEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_BUTTER_GOLEM_LAYER, ButterGolemEntityModel::getTexturedModelData);
	}
}