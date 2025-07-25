package skid.krypton.module.modules.donut;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import skid.krypton.event.EventListener;
import skid.krypton.event.events.*;
import skid.krypton.module.Category;
import skid.krypton.module.Module;
import skid.krypton.module.setting.BooleanSetting;
import skid.krypton.module.setting.NumberSetting;
import skid.krypton.utils.BlockUtil;
import skid.krypton.utils.Dimension;
import skid.krypton.utils.EncryptedString;
import skid.krypton.utils.RenderUtils;
import skid.krypton.utils.meteor.Ore;
import skid.krypton.utils.meteor.Seed;
import skid.krypton.utils.modules.ModuleTogglers;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class OreSim extends Module {

    private final Map<Long, Map<Ore, Set<Vec3d>>> chunkRenderers = new ConcurrentHashMap<>();
    private Seed worldSeed = null;
    private Map<RegistryKey<Biome>, List<Ore>> oreConfig;


    private final NumberSetting horizontalRadius = new NumberSetting("Chunk Range", 1, 10, 5, 1);
    private final BooleanSetting checkIfAir = new BooleanSetting("Check if air", true);
    private final NumberSetting alpha = new NumberSetting("Alpha value", 0, 255, 125, 1);
    private final BooleanSetting tracers = new BooleanSetting("Render Tracers", true);


    public OreSim() {
        super(EncryptedString.of("Netherite Finder"), EncryptedString.of("Finds netherites"), -1, Category.DONUT);
        this.addSettings(this.horizontalRadius, this.alpha, this.tracers);
        this.worldSeed = Seed.of(6608149111735331168L);
    }

    @EventListener
    private void onRender(Render3DEvent event) {
        if (mc.player == null || oreConfig == null) {
            return;
        }
        int chunkX = mc.player.getChunkPos().x;
        int chunkZ = mc.player.getChunkPos().z;

        int rangeVal = horizontalRadius.getIntValue();
        for (int range = 0; range <= rangeVal; range++) {
            for (int x = -range + chunkX; x <= range + chunkX; x++) {
                renderChunk(x, chunkZ + range - rangeVal, event);
            }
            for (int x = (-range) + 1 + chunkX; x < range + chunkX; x++) {
                renderChunk(x, chunkZ - range + rangeVal + 1, event);
            }
        }
    }

    @EventListener
    private void onJoin(GameJoinedEvent event) {
        ModuleTogglers.toggleOreSim();
    }

    private void renderChunk(int x, int z, Render3DEvent event) {
        long chunkKey = ChunkPos.toLong(x, z);

        if (!chunkRenderers.containsKey(chunkKey)) return;

        Map<Ore, Set<Vec3d>> chunk = chunkRenderers.get(chunkKey);

        for (Map.Entry<Ore, Set<Vec3d>> oreEntry : chunk.entrySet()) {
            for (Vec3d pos : oreEntry.getValue()) {
                BlockPos blockPos = new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
                BlockState state = mc.world.getBlockState(blockPos);

                boolean isAir = state.isAir();

                if (!isAir || checkIfAir.getValue()) {
                    renderOreBox(event, event.matrixStack, pos, getColor(alpha.getIntValue()));
                }
            }
        }
    }

    private Color getColor(int alpha) {
        return new Color(191, 64, 191, alpha);
    }

    private void renderOreBox(Render3DEvent event, MatrixStack matrixStack, Vec3d position, Color color) {
        matrixStack.push();

        Camera camera = mc.gameRenderer.getCamera();
        if (camera != null) {
            Vec3d camPos = RenderUtils.getCameraPos();
            MatrixStack matrices = event.matrixStack;
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
            matrices.translate(-camPos.x, -camPos.y, -camPos.z);
        }

        RenderUtils.renderFilledBox(matrixStack,
                (float) position.getX(), (float) position.getY(), (float) position.getZ(),
                (float) position.getX()+1, (float) position.getY()+1, (float) position.getZ()+1,
                color);
        if (tracers.getValue()) {
            RenderUtils.renderLine(event.matrixStack, color, mc.crosshairTarget.getPos(), new Vec3d(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5));
        }


        matrixStack.pop();
    }


    @EventListener
    private void onBlockUpdate(SetBlockStateEvent event) {
        if (event.newState.isOpaque()) return;

        long chunkKey = ChunkPos.toLong(event.pos);
        if (chunkRenderers.containsKey(chunkKey)) {
            Vec3d pos = Vec3d.of(event.pos);
            for (var ore : chunkRenderers.get(chunkKey).values()) {
                ore.remove(pos);
            }
        }
    }


    public void onEnable() {
        reload();
    }

    public void onDisable() {
        this.chunkRenderers.clear();
        this.oreConfig = null;
    }

    @EventListener
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        reload();
    }

    private void loadVisibleChunks() {
        if (mc.player == null) {
            return;
        }
        if (getDimension() != Dimension.Nether) return;

        for (WorldChunk chunk : BlockUtil.getLoadedChunks().toList()) {
            doMathOnChunk(chunk);
        }
    }

    private void reload() {
        oreConfig = Ore.getRegistry(getDimension());
        chunkRenderers.clear();
        if (mc.world != null && worldSeed != null) {
            loadVisibleChunks();
        }
    }

    @EventListener
    public void onChunkData(ChunkDataEvent event) {
        doMathOnChunk(event.chunk());
    }

    private void doMathOnChunk(WorldChunk chunk) {

        var chunkPos = chunk.getPos();
        long chunkKey = chunkPos.toLong();

        ClientWorld world = mc.world;

        if (chunkRenderers.containsKey(chunkKey) || world == null) {
            return;
        }

        Set<RegistryKey<Biome>> biomes = new HashSet<>();
        ChunkPos.stream(chunkPos, 1).forEach(chunkPosx -> {
            Chunk chunkxx = world.getChunk(chunkPosx.x, chunkPosx.z, ChunkStatus.BIOMES, false);
            if (chunkxx == null) return;

            for(ChunkSection chunkSection : chunkxx.getSectionArray()) {
                chunkSection.getBiomeContainer().forEachValue(entry -> biomes.add(entry.getKey().get()));
            }
        });
        Set<Ore> oreSet = biomes.stream().flatMap(b -> getDefaultOres(b).stream()).collect(Collectors.toSet());

        int chunkX = chunkPos.x << 4;
        int chunkZ = chunkPos.z << 4;
        ChunkRandom random = new ChunkRandom(ChunkRandom.RandomProvider.XOROSHIRO.create(0));

        long populationSeed = random.setPopulationSeed(worldSeed.seed, chunkX, chunkZ);
        HashMap<Ore, Set<Vec3d>> h = new HashMap<>();

        for (Ore ore : oreSet) {

            HashSet<Vec3d> ores = new HashSet<>();

            random.setDecoratorSeed(populationSeed, ore.index, ore.step);

            int repeat = ore.count.get(random);

            for (int i = 0; i < repeat; i++) {

                if (ore.rarity != 1F && random.nextFloat() >= 1/ore.rarity) {
                    continue;
                }

                int x = random.nextInt(16) + chunkX;
                int z = random.nextInt(16) + chunkZ;
                int y = ore.heightProvider.get(random, ore.heightContext);
                BlockPos origin = new BlockPos(x,y,z);

                RegistryKey<Biome> biome = chunk.getBiomeForNoiseGen(x,y,z).getKey().get();

                if (!getDefaultOres(biome).contains(ore)) {
                    continue;
                }

                if (ore.scattered) {
                    ores.addAll(generateHidden(world, random, origin, ore.size));
                } else {
                    ores.addAll(generateNormal(world, random, origin, ore.size, ore.discardOnAirChance));
                }
            }
            if (!ores.isEmpty()) {
                h.put(ore, ores);
            }
        }
        chunkRenderers.put(chunkKey, h);
    }

    private List<Ore> getDefaultOres(RegistryKey<Biome> biomeRegistryKey) {
        if (oreConfig.containsKey(biomeRegistryKey)) {
            return oreConfig.get(biomeRegistryKey);
        } else {
            return this.oreConfig.values().stream().findAny().get();
        }
    }

    // ====================================
    // Mojang code
    // ====================================

    private ArrayList<Vec3d> generateNormal(ClientWorld world, ChunkRandom random, BlockPos blockPos, int veinSize, float discardOnAir) {
        float f = random.nextFloat() * 3.1415927F;
        float g = (float) veinSize / 8.0F;
        int i = MathHelper.ceil(((float) veinSize / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d = (double) blockPos.getX() + Math.sin(f) * (double) g;
        double e = (double) blockPos.getX() - Math.sin(f) * (double) g;
        double h = (double) blockPos.getZ() + Math.cos(f) * (double) g;
        double j = (double) blockPos.getZ() - Math.cos(f) * (double) g;
        double l = (blockPos.getY() + random.nextInt(3) - 2);
        double m = (blockPos.getY() + random.nextInt(3) - 2);
        int n = blockPos.getX() - MathHelper.ceil(g) - i;
        int o = blockPos.getY() - 2 - i;
        int p = blockPos.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);

        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o <= world.getTopY(Heightmap.Type.MOTION_BLOCKING, s, t)) {
                    return this.generateVeinPart(world, random, veinSize, d, e, h, j, l, m, n, o, p, q, r, discardOnAir);
                }
            }
        }

        return new ArrayList<>();
    }

    private ArrayList<Vec3d> generateVeinPart(ClientWorld world, ChunkRandom random, int veinSize, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i, float discardOnAir) {

        BitSet bitSet = new BitSet(size * i * size);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double[] ds = new double[veinSize * 4];

        ArrayList<Vec3d> poses = new ArrayList<>();

        int n;
        double p;
        double q;
        double r;
        double s;
        for (n = 0; n < veinSize; ++n) {
            float f = (float) n / (float) veinSize;
            p = MathHelper.lerp(f, startX, endX);
            q = MathHelper.lerp(f, startY, endY);
            r = MathHelper.lerp(f, startZ, endZ);
            s = random.nextDouble() * (double) veinSize / 16.0D;
            double m = ((double) (MathHelper.sin(3.1415927F * f) + 1.0F) * s + 1.0D) / 2.0D;
            ds[n * 4] = p;
            ds[n * 4 + 1] = q;
            ds[n * 4 + 2] = r;
            ds[n * 4 + 3] = m;
        }

        for (n = 0; n < veinSize - 1; ++n) {
            if (!(ds[n * 4 + 3] <= 0.0D)) {
                for (int o = n + 1; o < veinSize; ++o) {
                    if (!(ds[o * 4 + 3] <= 0.0D)) {
                        p = ds[n * 4] - ds[o * 4];
                        q = ds[n * 4 + 1] - ds[o * 4 + 1];
                        r = ds[n * 4 + 2] - ds[o * 4 + 2];
                        s = ds[n * 4 + 3] - ds[o * 4 + 3];
                        if (s * s > p * p + q * q + r * r) {
                            if (s > 0.0D) {
                                ds[o * 4 + 3] = -1.0D;
                            } else {
                                ds[n * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for (n = 0; n < veinSize; ++n) {
            double u = ds[n * 4 + 3];
            if (!(u < 0.0D)) {
                double v = ds[n * 4];
                double w = ds[n * 4 + 1];
                double aa = ds[n * 4 + 2];
                int ab = Math.max(MathHelper.floor(v - u), x);
                int ac = Math.max(MathHelper.floor(w - u), y);
                int ad = Math.max(MathHelper.floor(aa - u), z);
                int ae = Math.max(MathHelper.floor(v + u), ab);
                int af = Math.max(MathHelper.floor(w + u), ac);
                int ag = Math.max(MathHelper.floor(aa + u), ad);

                for (int ah = ab; ah <= ae; ++ah) {
                    double ai = ((double) ah + 0.5D - v) / u;
                    if (ai * ai < 1.0D) {
                        for (int aj = ac; aj <= af; ++aj) {
                            double ak = ((double) aj + 0.5D - w) / u;
                            if (ai * ai + ak * ak < 1.0D) {
                                for (int al = ad; al <= ag; ++al) {
                                    double am = ((double) al + 0.5D - aa) / u;
                                    if (ai * ai + ak * ak + am * am < 1.0D) {
                                        int an = ah - x + (aj - y) * size + (al - z) * size * i;
                                        if (!bitSet.get(an)) {
                                            bitSet.set(an);
                                            mutable.set(ah, aj, al);
                                            if (aj >= -64 && aj < 320 && (world.getBlockState(mutable).isOpaque())) {
                                                if (shouldPlace(world, mutable, discardOnAir, random)) {
                                                    poses.add(new Vec3d(ah, aj, al));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return poses;
    }

    private boolean shouldPlace(ClientWorld world, BlockPos orePos, float discardOnAir, ChunkRandom random) {
        if (discardOnAir == 0F || (discardOnAir != 1F && random.nextFloat() >= discardOnAir)) {
            return true;
        }

        for (Direction direction : Direction.values()) {
            if (!world.getBlockState(orePos.add(direction.getVector())).isOpaque() && discardOnAir != 1F) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Vec3d> generateHidden(ClientWorld world, ChunkRandom random, BlockPos blockPos, int size) {

        ArrayList<Vec3d> poses = new ArrayList<>();

        int i = random.nextInt(size + 1);

        for (int j = 0; j < i; ++j) {
            size = Math.min(j, 7);
            int x = this.randomCoord(random, size) + blockPos.getX();
            int y = this.randomCoord(random, size) + blockPos.getY();
            int z = this.randomCoord(random, size) + blockPos.getZ();
            if (world.getBlockState(new BlockPos(x, y, z)).isOpaque()) {
                if (shouldPlace(world, new BlockPos(x, y, z), 1F, random)) {
                    poses.add(new Vec3d(x, y, z));
                }
            }
        }

        return poses;
    }

    private int randomCoord(ChunkRandom random, int size) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float) size);
    }

    public static Dimension getDimension() {
        if (mc.world == null) return Dimension.Overworld;

        return switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };
    }
}