package me.paulvogel.kurt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ButterGolemEntityModel<T extends Entity> extends SinglePartEntityModel<T> {
    private static final String UPPER_BODY = "upper_body";
    private final ModelPart root;
    private final ModelPart upperBody;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public ButterGolemEntityModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.upperBody = root.getChild("upper_body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 4.0F;
        Dilation dilation = new Dilation(-0.5F);
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.pivot(0.0F, 4.0F, 0.0F));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, dilation);
        modelPartData.addChild("left_arm", modelPartBuilder, ModelTransform.of(5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 1.0F));
        modelPartData.addChild("right_arm", modelPartBuilder, ModelTransform.of(-5.0F, 6.0F, -1.0F, 0.0F, 3.1415927F, -1.0F));
        modelPartData.addChild("upper_body", ModelPartBuilder.create().uv(0, 16).cuboid(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, dilation), ModelTransform.pivot(0.0F, 13.0F, 0.0F));
        modelPartData.addChild("lower_body", ModelPartBuilder.create().uv(0, 36).cuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, dilation), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
        this.upperBody.yaw = headYaw * 0.017453292F * 0.25F;
        float f = MathHelper.sin(this.upperBody.yaw);
        float g = MathHelper.cos(this.upperBody.yaw);
        this.leftArm.yaw = this.upperBody.yaw;
        this.rightArm.yaw = this.upperBody.yaw + 3.1415927F;
        this.leftArm.pivotX = g * 5.0F;
        this.leftArm.pivotZ = -f * 5.0F;
        this.rightArm.pivotX = -g * 5.0F;
        this.rightArm.pivotZ = f * 5.0F;
    }

    public ModelPart getPart() {
        return this.root;
    }

    public ModelPart getHead() {
        return this.head;
    }
}
