package io.robrichardson.inventorycount;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.util.Arrays;

@Slf4j
@PluginDescriptor(
		name = "Inventory Count"
)
public class InventoryCountPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private InventoryCountOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InventoryCountConfig config;

	private static final BufferedImage INVENTORY_IMAGE;

	private static final int INVENTORY_SIZE = 28;

	@Getter
	private InventoryCountInfoBox inventoryCountInfoBox;

	static
	{
		INVENTORY_IMAGE = ImageUtil.loadImageResource(InventoryCountPlugin.class, "inventory_icon.png");
	}

	@Override
	protected void startUp() throws Exception
	{
		if (config.renderOnInventory())  {
			overlayManager.add(overlay);
			updateOverlays();
		} else {
			addInfoBox();
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		if (config.renderOnInventory())  {
			overlayManager.remove(overlay);
		} else {
			removeInfoBox();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!InventoryCountConfig.GROUP.equals(event.getGroup())) return;

		if ("renderOnInventory".equals(event.getKey())) {
			if (config.renderOnInventory()) {
				removeInfoBox();
				overlayManager.add(overlay);
				updateOverlays();
			} else {
				overlayManager.remove(overlay);
				addInfoBox();
			}
		}
	}

	@Provides
	InventoryCountConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryCountConfig.class);
	}

	private void addInfoBox()
	{
		inventoryCountInfoBox = new InventoryCountInfoBox(INVENTORY_IMAGE, this);
		updateOverlays();
		infoBoxManager.addInfoBox(inventoryCountInfoBox);
	}

	private void removeInfoBox()
	{
		infoBoxManager.removeInfoBox(inventoryCountInfoBox);
		inventoryCountInfoBox = null;
	}

	private void updateOverlays()
	{
		String text = String.valueOf(openInventorySpaces());
		if(config.renderOnInventory())
		{
			overlay.setText(text);
		}
		else
		{
			inventoryCountInfoBox.setText(text);
		}
	}

	private int openInventorySpaces()
	{
		ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
		Item[] items = container == null ? new Item[0] : container.getItems();
		int usedSpaces = (int) Arrays.stream(items).filter(p -> p.getId() != -1).count();
		return INVENTORY_SIZE - usedSpaces;
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INVENTORY.getId())
		{
			updateOverlays();
		}
	}
}
