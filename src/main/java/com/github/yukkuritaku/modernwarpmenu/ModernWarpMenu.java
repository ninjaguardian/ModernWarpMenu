package com.github.yukkuritaku.modernwarpmenu;

import com.github.yukkuritaku.modernwarpmenu.client.resources.LayoutManager;
import com.github.yukkuritaku.modernwarpmenu.client.resources.SkyBlockConstantsManager;
import com.github.yukkuritaku.modernwarpmenu.commands.ModernWarpMenuCommand;
import com.github.yukkuritaku.modernwarpmenu.compat.modmenu.UpdateDetection;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.github.yukkuritaku.modernwarpmenu.listeners.ChatListener;
import com.github.yukkuritaku.modernwarpmenu.listeners.SkyBlockJoinListener;
import com.github.yukkuritaku.modernwarpmenu.listeners.WarpMenuListener;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
public class ModernWarpMenu implements ClientModInitializer {

	public static final String MOD_ID = "modernwarpmenu";
	private static ModernWarpMenu instance;
	private final KeyMapping keyOpenWarpMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping("modernwarpmenu.key.openWarpMenu",
			InputConstants.KEY_M, "modernwarpmenu.key.categories.modernWarpMenu"));

	private final SkyBlockConstantsManager skyBlockConstantsManager = new SkyBlockConstantsManager();
	private final LayoutManager layoutManager = new LayoutManager();

	public ModernWarpMenu(){
		instance = this;
	}

	@Override
	public void onInitializeClient() {
		SettingsManager.init();
		ModernWarpMenuCommand.registerCommands();
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(ResourceLocation.fromNamespaceAndPath(MOD_ID, "ultra_wide_layout"),
                modContainer,
                Component.literal("21:9 Ultra wide layout pack"),
                ResourcePackActivationType.NORMAL));
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(this.skyBlockConstantsManager);
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(this.layoutManager);
		new ChatListener().registerEvents();
		new SkyBlockJoinListener().registerEvents();
		new WarpMenuListener().registerEvents();
	}

	public static boolean updateAvailable(){
		if (FabricLoader.getInstance().isModLoaded("modmenu")){
			return new UpdateDetection().hasUpdateAvailable();
		}
		return false;
	}

	public SkyBlockConstantsManager getSkyBlockConstantsManager() {
		return skyBlockConstantsManager;
	}

	/**
	 * Returns the given language key path with the mod ID prepended
	 */
	public static String getFullLanguageKey(String path) {
		return MOD_ID + "." + path;
	}

	public static ModernWarpMenu getInstance() {
		return instance;
	}


	public KeyMapping getKeyOpenWarpMenu() {
		return this.keyOpenWarpMenu;
	}
}
