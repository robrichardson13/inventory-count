package io.robrichardson.inventorycount;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.FontType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class InventoryCountOverlay extends Overlay {

    private final Client client;
    private final InventoryCountConfig config;
    private final FontType infoboxFontType;

    private String _text;

    @Inject
    public InventoryCountOverlay(Client client, InventoryCountPlugin plugin, InventoryCountConfig config, ConfigManager configManager) {
        super(plugin);

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(Overlay.PRIORITY_HIGH);

        this.client = client;
        this.config = config;
        this.infoboxFontType = configManager.getConfiguration("runelite", "infoboxFontType", FontType.class);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.renderOnInventory()) return null;

        Widget inventoryWidget = getInventoryWidget(client);
        if (inventoryWidget == null) return null;

        graphics.setFont(infoboxFontType.getFont());

        TextComponent inventoryOverlayText = getInventoryOverlayText(graphics, inventoryWidget);
        inventoryOverlayText.render(graphics);

        return null;
    }

    public void setText(String text) {
        _text = text;
    }

    private Widget getInventoryWidget(Client client) {
        Widget inventoryWidget = client.getWidget(ComponentID.FIXED_VIEWPORT_INVENTORY_TAB);

        if (!client.isResized()) {
            return inventoryWidget != null && !inventoryWidget.isHidden()
                    ? inventoryWidget
                    : null;
        }

        inventoryWidget = client.getWidget(ComponentID.RESIZABLE_VIEWPORT_INVENTORY_TAB);

        return inventoryWidget != null && !inventoryWidget.isHidden()
                ? inventoryWidget
                : client.getWidget(ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_TAB);
    }

    private TextComponent getInventoryOverlayText(Graphics2D graphics, Widget inventoryWidget) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle bounds = inventoryWidget.getBounds();
        int textWidth = fontMetrics.stringWidth(_text);
        int textHeight = fontMetrics.getHeight();
        int topOffset = 4;
        int x = 0;
        int y = 0;

        TextComponent inventoryOverlayText = new TextComponent();

        inventoryOverlayText.setText(_text);
        inventoryOverlayText.setColor(config.customInventoryOverlayTextColor());
        inventoryOverlayText.setOutline(config.renderInventoryOverlayTextOutline());

        InventoryOverlayTextPositions textPosition = config.inventoryOverlayTextPosition();

        switch (textPosition) {
            case Top:
                x = (int) bounds.getCenterX() - (textWidth / 2);
                y = (int) bounds.getCenterY() - (textHeight / 2) + topOffset;

                inventoryOverlayText.setPosition(new Point(x, y));
                break;
            case Bottom:
                x = (int) bounds.getCenterX() - (textWidth / 2);
                y = (int) bounds.getMaxY();

                inventoryOverlayText.setPosition(new Point(x, y));
                break;
            default:
                x = (int) bounds.getCenterX() - (textWidth / 2);
                y = (int) bounds.getCenterY() + (textHeight / 2);

                inventoryOverlayText.setPosition(new Point(x, y));
                break;
        }

        return inventoryOverlayText;
    }
}
