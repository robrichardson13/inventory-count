package io.robrichardson.inventorycount;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(InventoryCountConfig.GROUP)
public interface InventoryCountConfig extends Config {
	String GROUP = "InventoryCount";

	@ConfigItem(
			keyName = "renderOnInventory",
			name = "Render on inventory icon",
			description = "Disable for infobox, enable for text overlay on inventory icon"
	)
	default boolean renderOnInventory() {
		return false;
	}

}