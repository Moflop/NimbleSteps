package mod.arcomit.parkour.v2.content.data;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * 数据生成处理器。
 *
 * @author Mitok
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class DataGenerationHandler {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        NsBlockTagsProvider blockTags = new NsBlockTagsProvider(packOutput, lookupProvider, ParkourMod.MODID ,existingFileHelper);
        dataGenerator.addProvider(event.includeServer(), blockTags);
    }
}
