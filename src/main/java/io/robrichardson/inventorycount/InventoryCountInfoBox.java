package io.robrichardson.inventorycount;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InventoryCountInfoBox extends InfoBox {
    private String _text;
    private final InventoryCountConfig config;

    InventoryCountInfoBox(BufferedImage image, Plugin plugin, InventoryCountConfig config) {
        super(image, plugin);
        this.config = config;
        setTooltip("Number of open inventory spaces");
    }

    @Override
    public String getText() {
        if (config.renderInventoryInfoBox()) {
            return _text;
        }
        return null;
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    public void setText(String text) {
        if (config.renderInventoryInfoBox()) {
            _text = text;
        }
    }
}
