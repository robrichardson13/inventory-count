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

    private String _text = "";
    private Color _color = Color.WHITE; // Default color

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
        if (!config.renderInventoryOverlay()) {
            return null;
        }

        Widget inventoryWidget = getInventoryWidget(client);
        if (inventoryWidget == null) return null;

        Font infoboxFont = infoboxFontType.getFont();

        if (config.useCustomFont()) {
            InventoryOverlayTextFonts selectedFontEnum = config.customFont();
            int fontSize = config.customInventoryOverlayFontSize().getSize();

            infoboxFont = new Font(selectedFontEnum.getFontName(), selectedFontEnum.getFontStyle(), fontSize);
        }

        graphics.setFont(infoboxFont);

        TextComponent inventoryOverlayText = getInventoryOverlayText(graphics, inventoryWidget);
        inventoryOverlayText.render(graphics);

        return null;
    }

    public void setText(String text) {
        _text = text;
    }

    public void setColor(Color color) {
        _color = color;
    }

    private Widget getInventoryWidget(Client client) {
        Widget inventoryWidget = client.getWidget(ComponentID.FIXED_VIEWPORT_INVENTORY_TAB);

        if (isInventoryWidgetVisible(inventoryWidget)) {
            return inventoryWidget;
        }

        inventoryWidget = getResizableInventoryWidget(client);

        return isInventoryWidgetVisible(inventoryWidget) ? inventoryWidget : null;
    }

    private Widget getResizableInventoryWidget(Client client) {
        Widget inventoryWidget = client.getWidget(ComponentID.RESIZABLE_VIEWPORT_INVENTORY_TAB);

        if (isInventoryWidgetVisible(inventoryWidget)) {
            return inventoryWidget;
        }

        return client.getWidget(ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_TAB);
    }

    private boolean isInventoryWidgetVisible(Widget inventoryWidget) {
        return inventoryWidget != null && !inventoryWidget.isHidden();
    }

    private TextComponent getInventoryOverlayText(Graphics2D graphics, Widget inventoryWidget) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle bounds = inventoryWidget.getBounds();
        InventoryOverlayTextPositions textPosition = config.inventoryOverlayTextPosition();
        TextComponent inventoryOverlayText = new TextComponent();

        inventoryOverlayText.setText(_text);
        inventoryOverlayText.setColor(_color);
        inventoryOverlayText.setOutline(config.renderInventoryOverlayTextOutline());

        int textWidth = fontMetrics.stringWidth(_text);
        int textHeight = fontMetrics.getHeight() - fontMetrics.getMaxDescent();
        int x = (int) bounds.getCenterX() - (textWidth / 2);
        int y = (int) bounds.getCenterY() + (textHeight / 2);

        switch (textPosition) {
            case Top:
                y = (int) bounds.getMinY() + textHeight;
                break;
            case Bottom:
                y = (int) bounds.getMaxY();
                break;
        }

        inventoryOverlayText.setPosition(new Point(x, y));

        return inventoryOverlayText;
    }
}
