package me.paulvogel.kurt;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ButterGolemEntity extends GolemEntity implements Shearable, RangedAttackMob {
    private static final TrackedData<Byte> BUTTER_GOLEM_FLAGS;
    private static final byte HAS_PUMPKIN_FLAG = 16;

    public ButterGolemEntity(EntityType<? extends ButterGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0F));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0, 1.0000001E-5F));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, MobEntity.class, 10, true, false, (entity) -> entity instanceof Monster));
    }

    public static DefaultAttributeContainer.Builder createButterGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BUTTER_GOLEM_FLAGS, (byte) 16);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Pumpkin", this.hasPumpkin());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Pumpkin")) {
            this.setHasPumpkin(nbt.getBoolean("Pumpkin"));
        }

    }

    public boolean hurtByWater() {
        return true;
    }

    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient) {
            if (this.getWorld().getBiome(this.getBlockPos()).isIn(BiomeTags.SNOW_GOLEM_MELTS)) {
                this.damage(this.getDamageSources().onFire(), 1.0f);
            }

            if (!this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return;
            }

            BlockState blockState = Kurt.BUTTER_PUDDLE.getDefaultState();

            for (int i = 0; i < 4; ++i) {
                int j = MathHelper.floor(this.getX() + (double) ((float) (i % 2 * 2 - 1) * 0.25f));
                int k = MathHelper.floor(this.getY());
                int l = MathHelper.floor(this.getZ() + (double) ((float) (i / 2 % 2 * 2 - 1) * 0.25f));
                BlockPos blockPos = new BlockPos(j, k, l);
                if (!this.getWorld().getBlockState(blockPos).isAir() || !blockState.canPlaceAt(this.getWorld(), blockPos)) {
                    continue;
                }
                this.getWorld().setBlockState(blockPos, blockState);
                this.getWorld().emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this, blockState));
            }
        }

    }

    public void shootAt(LivingEntity target, float pullProgress) {
        SnowballEntity snowballEntity = new SnowballEntity(this.getWorld(), this);
        double d = target.getEyeY() - 1.100000023841858;
        double e = target.getX() - this.getX();
        double f = d - snowballEntity.getY();
        double g = target.getZ() - this.getZ();
        double h = Math.sqrt(e * e + g * g) * 0.20000000298023224;
        snowballEntity.setVelocity(e, f + h, g, 1.6F, 12.0F);
        this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(snowballEntity);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.7F;
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
            this.sheared(SoundCategory.PLAYERS);
            this.emitGameEvent(GameEvent.SHEAR, player);
            if (!this.getWorld().isClient) {
                itemStack.damage(1, player, (playerx) -> {
                    playerx.sendToolBreakStatus(hand);
                });
            }

            return ActionResult.success(this.getWorld().isClient);
        } else {
            return ActionResult.PASS;
        }
    }

    public void sheared(SoundCategory shearedSoundCategory) {
        this.getWorld().playSoundFromEntity( null, this, SoundEvents.ENTITY_SNOW_GOLEM_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
        if (!this.getWorld().isClient()) {
            this.setHasPumpkin(false);
            this.dropStack(new ItemStack(Items.CARVED_PUMPKIN), 1.7F);
        }

    }

    public boolean isShearable() {
        return this.isAlive() && this.hasPumpkin();
    }

    public boolean hasPumpkin() {
        return (this.dataTracker.get(BUTTER_GOLEM_FLAGS) & 16) != 0;
    }

    public void setHasPumpkin(boolean hasPumpkin) {
        byte b = this.dataTracker.get(BUTTER_GOLEM_FLAGS);
        if (hasPumpkin) {
            this.dataTracker.set(BUTTER_GOLEM_FLAGS, (byte) (b | 16));
        } else {
            this.dataTracker.set(BUTTER_GOLEM_FLAGS, (byte) (b & -17));
        }

    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SNOW_GOLEM_AMBIENT;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
    }

    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, (double) (0.75F * this.getStandingEyeHeight()), (double) (this.getWidth() * 0.4F));
    }

    static {
        BUTTER_GOLEM_FLAGS = DataTracker.registerData(ButterGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
