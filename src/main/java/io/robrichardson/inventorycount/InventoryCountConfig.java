package io.robrichardson.inventorycount;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup(InventoryCountConfig.GROUP)
public interface InventoryCountConfig extends Config {
    String GROUP = "InventoryCount";

    @ConfigItem(
            keyName = "renderOnInventory",
            name = "Render on inventory icon",
            description = "Disable for infobox, enable for text overlay on inventory icon",
            position = 1
    )
    default boolean renderOnInventory() {
        return false;
    }

    @ConfigItem(
            keyName = "renderInventoryOverlayTextOutline",
            name = "Text outline",
            description = "Adds an outline to the inventory overlay text",
            position = 2
    )
    default boolean renderInventoryOverlayTextOutline() {
        return false;
    }

    @ConfigItem(
            keyName = "customInventoryOverlayTextColor",
            name = "Text color",
            description = "Customize the color of the inventory overlay text",
            position = 3
    )
    default Color customInventoryOverlayTextColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            keyName = "customInventoryOverlayTextPosition",
            name = "Text position",
            description = "Configure the position of the inventory overlay text",
            position = 4
    )
    default InventoryOverlayTextPositions inventoryOverlayTextPosition() {
        return InventoryOverlayTextPositions.Bottom;
    }
}
