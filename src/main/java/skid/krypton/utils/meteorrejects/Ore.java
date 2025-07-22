package skid.krypton.utils.meteorrejects;

import anticope.rejects.mixin.CountPlacementModifierAccessor;
import anticope.rejects.mixin.HeightRangePlacementModifierAccessor;
import anticope.rejects.mixin.RarityFilterPlacementModifierAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;

import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import skid.krypton.utils.Dimension;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Ore {

    public static Map<RegistryKey<Biome>, List<Ore>> getRegistry(Dimension dimension) {

        RegistryWrapper.WrapperLookup registry = BuiltinRegistries.createWrapperLookup();
        RegistryWrapper.Impl<PlacedFeature> features = registry.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
        var reg = registry.getWrapperOrThrow(RegistryKeys.WORLD_PRESET).getOrThrow(WorldPresets.DEFAULT).value().createDimensionsRegistryHolder().dimensions();

        var dim = reg.get(DimensionOptions.NETHER);


        var biomes = dim.chunkGenerator().getBiomeSource().getBiomes();
        var biomes1 = biomes.stream().toList();

        List<PlacedFeatureIndexer.IndexedFeatures> indexer = PlacedFeatureIndexer.collectIndexedFeatures(
                biomes1, biomeEntry -> biomeEntry.value().getGenerationSettings().getFeatures(), true
        );


        Map<PlacedFeature, Ore> featureToOre = new HashMap<>();
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_DEBRIS_SMALL, 7, new Color(209, 27, 245));
        registerOre(featureToOre, indexer, features, OrePlacedFeatures.ORE_ANCIENT_DEBRIS_LARGE, 7, new Color(209, 27, 245));


        Map<RegistryKey<Biome>, List<Ore>> biomeOreMap = new HashMap<>();

        biomes1.forEach(biome -> {
            biomeOreMap.put(biome.getKey().get(), new ArrayList<>());
            biome.value().getGenerationSettings().getFeatures().stream()
                    .flatMap(RegistryEntryList::stream)
                    .map(RegistryEntry::value)
                    .filter(featureToOre::containsKey)
                    .forEach(feature -> {
                        biomeOreMap.get(biome.getKey().get()).add(featureToOre.get(feature));
                    });
        });
        return biomeOreMap;
    }

    private static void registerOre(
            Map<PlacedFeature, Ore> map,
            List<PlacedFeatureIndexer.IndexedFeatures> indexer,
            RegistryWrapper.Impl<PlacedFeature> oreRegistry,
            RegistryKey<PlacedFeature> oreKey,
            int genStep,
            Color color
    ) {
        var orePlacement = oreRegistry.getOrThrow(oreKey).value();

        int index = indexer.get(genStep).indexMapping().applyAsInt(orePlacement);

        Ore ore = new Ore(orePlacement, genStep, index, color);

        map.put(orePlacement, ore);
    }

    public int step;
    public int index;
    public IntProvider count = ConstantIntProvider.create(1);
    public HeightProvider heightProvider;
    public HeightContext heightContext;
    public float rarity = 1;
    public float discardOnAirChance;
    public int size;
    public Color color;
    public boolean scattered;

    private Ore(PlacedFeature feature, int step, int index, Color color) {
        this.step = step;
        this.index = index;
        this.color = color;
        int bottom = MinecraftClient.getInstance().world.getBottomY();
        int height = MinecraftClient.getInstance().world.getDimension().logicalHeight();
        this.heightContext = new HeightContext(null, HeightLimitView.create(bottom, height));


        for (PlacementModifier modifier : feature.placementModifiers()) {
            if (modifier instanceof CountPlacementModifier) {
                this.count = ((CountPlacementModifierAccessor) modifier).getCount();

            } else if (modifier instanceof HeightRangePlacementModifier) {
                this.heightProvider = ((HeightRangePlacementModifierAccessor) modifier).getHeight();

            } else if (modifier instanceof RarityFilterPlacementModifier) {
                this.rarity = ((RarityFilterPlacementModifierAccessor) modifier).getChance();
            }
        }

        FeatureConfig featureConfig = feature.feature().value().config();

        if (featureConfig instanceof OreFeatureConfig oreFeatureConfig) {
            this.discardOnAirChance = oreFeatureConfig.discardOnAirChance;
            this.size = oreFeatureConfig.size;
        } else {
            throw new IllegalStateException("config for " + feature + "is not OreFeatureConfig.class");
        }

        if (feature.feature().value().feature() instanceof ScatteredOreFeature) {
            this.scattered = true;
        }
    }
}