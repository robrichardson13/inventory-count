package io.robrichardson.inventorycount;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.FontType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
public class InventoryCountOverlay extends Overlay {

    private final Client client;
    private final InventoryCountConfig config;
    private final ConfigManager configManager;

    @Inject
    public InventoryCountOverlay(Client client, InventoryCountPlugin plugin, InventoryCountConfig config, ConfigManager configManager) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
        this.client = client;
        this.config = config;
        this.configManager = configManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.renderOnInventory()) return null;

        Widget toDrawOn;
        if (client.isResized()) {
            toDrawOn = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_INVENTORY_TAB);
            if (toDrawOn == null || toDrawOn.isHidden())
                toDrawOn = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_STONES_WIDGET);
        } else {
            toDrawOn = client.getWidget(WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB);
        }
        if (toDrawOn == null || toDrawOn.isHidden()) return null;

        String textToDraw = "15";
        FontType infoboxFontType = configManager.getConfiguration("runelite", "infoboxFontType", FontType.class);
        graphics.setFont(infoboxFontType.getFont()); // make sure we do this before calculating drawLocation

        Rectangle bounds = toDrawOn.getBounds();
        Point drawLocation = new Point((int) bounds.getCenterX() - (graphics.getFontMetrics().stringWidth(textToDraw) / 2), (int) bounds.getMaxY());
        OverlayUtil.renderTextLocation(graphics, drawLocation, textToDraw, Color.WHITE);

        return null;
    }
}