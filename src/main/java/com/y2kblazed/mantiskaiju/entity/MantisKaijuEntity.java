package com.y2kblazed.mantiskaiju.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Hides as a shulker until a player hits it, then the box bursts open and the colossal mantis emerges.
 */
public class MantisKaijuEntity extends Monster {
    private static final EntityDataAccessor<Boolean> DORMANT =
            SynchedEntityData.defineId(MantisKaijuEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions DORMANT_SIZE = EntityDimensions.fixed(1.0F, 1.0F);
    private static final EntityDimensions AWAKE_SIZE = EntityDimensions.fixed(4.5F, 9.0F);

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            Component.translatable("entity.mantiskaiju.mantis_kaiju"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);

    /** Client-side emerge animation timer (0..20). */
    public int awakenTicks;

    public MantisKaijuEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        this.bossEvent.setVisible(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DORMANT, true);
    }

    public boolean isDormant() {
        return this.entityData.get(DORMANT);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, true) {
            @Override public boolean canUse() { return !isDormant() && super.canUse(); }
        });
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D) {
            @Override public boolean canUse() { return !isDormant() && super.canUse(); }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 32.0F) {
            @Override public boolean canUse() { return !isDormant() && super.canUse(); }
        });
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override public boolean canUse() { return !isDormant() && super.canUse(); }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override public boolean canUse() { return !isDormant() && super.canUse(); }
        });
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isDormant()) {
            if (source.getEntity() instanceof Player player && !player.isSpectator() && !player.isCreative() || source.getEntity() instanceof Player p2 && p2.isCreative()) {
                if (!this.level().isClientSide) awaken((Player) source.getEntity());
                return super.hurt(source, amount);
            }
            if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        }
        return super.hurt(source, amount);
    }

    private void awaken(Player player) {
        this.entityData.set(DORMANT, false);
        this.playSound(SoundEvents.SHULKER_OPEN, 4.0F, 0.5F);
        this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 5.0F, 1.3F);
        this.bossEvent.setVisible(true);
        this.setTarget(player);
        this.setPersistenceRequired();
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.REVERSE_PORTAL, getX(), getY() + 1.0D, getZ(), 300, 1.5D, 3.0D, 1.5D, 0.4D);
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5D, getZ(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DORMANT.equals(key)) this.refreshDimensions();
        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return isDormant() ? DORMANT_SIZE : AWAKE_SIZE;
    }

    @Override
    public void tick() {
        super.tick();
        if (isDormant()) {
            this.setDeltaMovement(0.0D, Math.min(0.0D, getDeltaMovement().y), 0.0D);
            this.yBodyRot = 0.0F;
            this.yHeadRot = 0.0F;
            this.setYRot(0.0F);
        } else if (awakenTicks < 20) {
            awakenTicks++;
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 1), this);
        }
        return hit;
    }

    @Override public boolean isPushable() { return !isDormant() && super.isPushable(); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean canBeLeashed(Player player) { return false; }
    @Override public float getVoicePitch() { return 0.5F; }

    @Override protected SoundEvent getAmbientSound() { return isDormant() ? SoundEvents.SHULKER_AMBIENT : SoundEvents.SPIDER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return isDormant() ? SoundEvents.SHULKER_HURT_CLOSED : SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Dormant", isDormant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Dormant")) this.entityData.set(DORMANT, tag.getBoolean("Dormant"));
        this.bossEvent.setVisible(!isDormant());
        if (this.hasCustomName()) this.bossEvent.setName(this.getDisplayName());
    }
}
