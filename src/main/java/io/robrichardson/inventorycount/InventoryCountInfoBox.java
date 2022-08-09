package io.robrichardson.inventorycount;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InventoryCountInfoBox extends InfoBox
{
    private String _text;

    InventoryCountInfoBox(BufferedImage image, Plugin plugin)
    {
        super(image, plugin);
        setTooltip("Number of open inventory spaces");
    }

    @Override
    public String getText() {
        return _text;
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    public void setText(String text)
    {
        _text = text;
    }
}
