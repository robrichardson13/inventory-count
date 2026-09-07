package io.robrichardson.inventorycount;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InventoryCountInfoBox extends InfoBox {
    private int _freeSlots;
    private int _usedSlots;
    private final InventoryCountConfig config;

    InventoryCountInfoBox(BufferedImage image, Plugin plugin, InventoryCountConfig config) {
        super(image, plugin);
        this.config = config;
    }

    @Override
    public String getText() {
        if (!config.renderInventoryInfoBox()) {
            return null;
        }

        InventoryCountMode mode = config.inventoryCountMode();
        return mode == InventoryCountMode.USED ? String.valueOf(_usedSlots) : String.valueOf(_freeSlots);
    }

    @Override
    public String getTooltip() {
        InventoryCountMode mode = config.inventoryCountMode();

        switch (mode) {
            case USED:
                return usedTooltip();
            case BOTH:
                return freeTooltip() + "</br>" + usedTooltip();
            case FREE:
            default:
                return freeTooltip();
        }
    }

    @Override
    public Color getTextColor() {
        return _freeSlots == 0 ? Color.RED : Color.WHITE;
    }

    public void setCounts(int freeSlots, int usedSlots) {
        if (config.renderInventoryInfoBox()) {
            _freeSlots = freeSlots;
            _usedSlots = usedSlots;
        }
    }

    private String freeTooltip() {
        return _freeSlots + " free inventory " + pluralize(_freeSlots);
    }

    private String usedTooltip() {
        return _usedSlots + " used inventory " + pluralize(_usedSlots);
    }

    private static String pluralize(int count) {
        return count == 1 ? "slot" : "slots";
    }
}
