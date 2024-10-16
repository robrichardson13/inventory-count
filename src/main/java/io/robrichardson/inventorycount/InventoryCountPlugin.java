package io.robrichardson.inventorycount;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

@Slf4j
@PluginDescriptor(
        name = "Inventory Count"
)
public class InventoryCountPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

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

    static {
        INVENTORY_IMAGE = ImageUtil.loadImageResource(InventoryCountPlugin.class, "inventory_icon.png");
    }

    @Override
    protected void startUp() throws Exception {
        toggleOverlayAndInfoBox();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        removeInfoBox();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!InventoryCountConfig.GROUP.equals(event.getGroup())) return;

        toggleOverlayAndInfoBox();
    }

    @Provides
    InventoryCountConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(InventoryCountConfig.class);
    }

    private void toggleOverlayAndInfoBox() {
        clientThread.invoke(() -> {
            if (config.renderInventoryOverlay()) {
                overlayManager.add(overlay);
            } else {
                overlayManager.remove(overlay);
            }

            if (config.renderInventoryInfoBox()) {
                addInfoBox();
            } else {
                removeInfoBox();
            }

            updateOverlays();
        });
    }

    private void addInfoBox() {
        if (inventoryCountInfoBox == null) {
            inventoryCountInfoBox = new InventoryCountInfoBox(INVENTORY_IMAGE, this, config); // Pass config
            infoBoxManager.addInfoBox(inventoryCountInfoBox);
        }
    }

    private void removeInfoBox() {
        if (inventoryCountInfoBox != null) {
            infoBoxManager.removeInfoBox(inventoryCountInfoBox);
            inventoryCountInfoBox = null;
        }
    }

    private void updateOverlays() {
        String text = String.valueOf(openInventorySpaces());
        Color textColor = getInventoryTextColor();

        overlay.setText(text);
        overlay.setColor(textColor);

        if (inventoryCountInfoBox != null) {
            inventoryCountInfoBox.setText(text);
        }
    }

    public int openInventorySpaces() {
        ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
        Item[] items = container == null ? new Item[0] : container.getItems();
        int usedSpaces = (int) Arrays.stream(items).filter(p -> p.getId() != -1).count();
        return INVENTORY_SIZE - usedSpaces;
    }

    public Color getInventoryTextColor() {
        int freeSlots = openInventorySpaces();

        if (config.dynamicInventoryOverlayColor()) {
            if (freeSlots > InventoryOverlaySlotSizes.HIGH) {
                return Color.GREEN;
            } else if (freeSlots > InventoryOverlaySlotSizes.MEDIUM) {
                return Color.YELLOW;
            } else if (freeSlots > InventoryOverlaySlotSizes.LOW) {
                return new Color(255, 165, 0); // Light orange
            } else {
                return Color.RED;
            }
        }
        return config.customInventoryOverlayTextColor();
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            updateOverlays();
        }
    }
}
