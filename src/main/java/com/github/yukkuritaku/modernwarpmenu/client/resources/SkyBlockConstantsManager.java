package com.github.yukkuritaku.modernwarpmenu.client.resources;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.SkyBlockConstants;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.WarpMessages;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkyBlockConstantsManager implements IdentifiableResourceReloadListener {

    private static final ResourceLocation SKY_BLOCK_CONSTANTS_LOCATION =
            ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                    "constants/skyblock_constants.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    private SkyBlockConstants skyBlockConstants;

    public SkyBlockConstants getSkyBlockConstants() {
        return skyBlockConstants;
    }

    private static void handleLoadException(Resource resource, ResourceLocation location, Exception e) {
        CrashReport crashReport = new CrashReport("Your Modern Warp Menu resource pack may be outdated, or something wrong with json", e);
        CrashReportCategory resourceCategory = crashReport.addCategory("Resource");
        CrashReportCategory resourcePackCategory = crashReport.addCategory("Resource Pack");
        resourceCategory.setDetail("Path", location.toString());
        String resourceString;
        try (PackResources pack = resource.source()) {
            resourceString = pack.location().title().getString();
        }
        resourcePackCategory.setDetail("Name", resourceString);
        throw new ReportedException(crashReport);
    }

    private static SkyBlockConstants loadSkyBlockConstants(Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement jsonElement = GSON.fromJson(reader, JsonElement.class);
            return SkyBlockConstants.CODEC.codec().parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
        } catch (Exception e) {
            handleLoadException(resource, SKY_BLOCK_CONSTANTS_LOCATION, e);
        }
        return new SkyBlockConstants(
                Maps.newHashMap(),
                new WarpMessages(Lists.newArrayList(), Maps.newHashMap()),
                Lists.newArrayList(),
                "");
    }

    protected CompletableFuture<SkyBlockConstants> prepare(ResourceManager resourceManager, Executor io) {
        try {
            Resource resource = resourceManager.getResourceOrThrow(SKY_BLOCK_CONSTANTS_LOCATION);
            return CompletableFuture.supplyAsync(() -> loadSkyBlockConstants(resource), io);
        } catch (IOException e) {
            LOGGER.warn("Unable to load SkyBlockConstants", e);
        }
        return CompletableFuture.supplyAsync(() -> new SkyBlockConstants(
                Maps.newHashMap(),
                new WarpMessages(Lists.newArrayList(), Maps.newHashMap()),
                Lists.newArrayList(),
                ""));
    }

    protected void apply(SkyBlockConstants object) {
        // TODO commented out because sorting causes unsupported error because list is immutable, maybe fixable?
        /*for (var list : object.menuMatchingMap().values()) {
            LOGGER.info("List: {}", list);
            list.sort(Comparator.comparing(ItemMatchCondition::inventorySlot));
        }*/
        this.skyBlockConstants = object;
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor io, Executor game) {
        return this.prepare(resourceManager, io)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(this::apply, game);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID, "skyblock_constants");
    }
}
