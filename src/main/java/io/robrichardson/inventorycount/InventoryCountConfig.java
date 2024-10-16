package io.robrichardson.inventorycount;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup(InventoryCountConfig.GROUP)
public interface InventoryCountConfig extends Config {
    String GROUP = "InventoryCount";

    // General Settings
    @ConfigItem(
            keyName = "renderOnInventory",
            name = "Render on inventory icon",
            description = "Disable for infobox, enable for text overlay on inventory icon",
            position = 1
    )
    default boolean renderOnInventory() {
        return false;
    }

    @ConfigSection(
            name = "Text Settings",
            description = "Change your text color, outline and positioning",
            position = 2
    )
    String textSection = "Text Settings";

    @ConfigItem(
            keyName = "renderInventoryOverlayTextOutline",
            name = "Text outline",
            description = "Adds an outline to the inventory overlay text",
            position = 3,
            section = textSection
    )
    default boolean renderInventoryOverlayTextOutline() {
        return false;
    }

    @ConfigItem(
            keyName = "customInventoryOverlayTextColor",
            name = "Text color",
            description = "Customize the color of the inventory overlay text",
            position = 2,
            section = textSection
    )
    default Color customInventoryOverlayTextColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            keyName = "customInventoryOverlayTextPosition",
            name = "Text position",
            description = "Configure the position of the inventory overlay text",
            position = 4,
            section = textSection
    )
    default InventoryOverlayTextPositions inventoryOverlayTextPosition() {
        return InventoryOverlayTextPositions.Bottom;
    }

    @ConfigItem(
            keyName = "dynamicInventoryOverlayColor",
            name = "Dynamic text color",
            description = "Toggle to enable or disable the text color changing based on free inventory slots",
            position = 1,
            section = textSection
    )
    default boolean dynamicInventoryOverlayColor() {
        return true;
    }

    @ConfigSection(
            name = "Custom Font Settings",
            description = "Settings for customizing the font of the inventory counter overlay",
            position = 3
    )
    String fontSection = "Custom Font Settings";

    @ConfigItem(
            keyName = "customInventoryOverlayFontSize",
            name = "Text size",
            description = "Adjust the font size of the inventory overlay text",
            position = 3,
            section = fontSection
    )
    default InventoryOverlayTextFontSizes customInventoryOverlayFontSize() {
        return InventoryOverlayTextFontSizes.SIZE_16;
    }

    @ConfigItem(
            keyName = "customInventoryOverlayFont",
            name = "Custom font",
            description = "Choose a custom font for the inventory counter overlay",
            position = 2,
            section = fontSection
    )
    default InventoryOverlayTextFonts customFont() {
        return InventoryOverlayTextFonts.ARIAL_BOLD;
    }

    @ConfigItem(
            keyName = "customInventoryOverlayFontToggle",
            name = "Use custom font",
            description = "Toggle using a custom font or the default plugin font",
            position = 1,
            section = fontSection
    )
    default boolean useCustomFont() {
        return true;
    }
}
