package com.github.yukkuritaku.modernwarpmenu.client.gui.screens;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.client.gui.components.IslandButton;
import com.github.yukkuritaku.modernwarpmenu.client.gui.components.WarpButton;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Layout;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.Menu;
import com.github.yukkuritaku.modernwarpmenu.utils.GameCheckUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class FastTravelScreen extends ModernWarpScreen{

    public FastTravelScreen(ChestMenu menu, Inventory playerInventory, Layout layout) {
        super(Menu.FAST_TRAVEL, menu, playerInventory, layout);
        this.lastSlotIndexToCheck = ModernWarpMenu.getInstance().getSkyBlockConstantsManager()
                .getSkyBlockConstants()
                .getLastMatchConditionInventorySlotIndex(this.warpMenu);
    }

    @Override
    protected void warpButtonHandler(WarpButton button) {
        if (Util.getMillis() > this.warpFailCoolDownExpiryTime) {
            String warpCommand = button.getWarpCommand();
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.connection.sendCommand(warpCommand.substring(1));
        }
    }

    @Override
    protected void islandButtonHandler(IslandButton button) {
        if (Util.getMillis() > this.warpFailCoolDownExpiryTime) {
            String warpCommand = button.island.warpList.getFirst().getWarpCommand();
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.connection.sendCommand(warpCommand.substring(1));
        }
    }

    @Override
    protected void updateButtonStates() {
        GameCheckUtils.checkSeason();
        super.updateButtonStates();
    }
}
