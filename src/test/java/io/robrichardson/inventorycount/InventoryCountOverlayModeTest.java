package io.robrichardson.inventorycount;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.FontType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Covers InventoryCountOverlay's per-mode text selection and position mapping added
 * for InventoryCountMode (card #1): FREE/USED render their single text at the configured
 * position; BOTH renders free at the configured position and used at the opposite end,
 * with Center resolving to free-Top/used-Bottom.
 *
 * The widget bounds and font metrics are fixed constants below so each expected pixel
 * position can be derived by hand rather than re-deriving overlay internals in the test.
 */
@RunWith(MockitoJUnitRunner.class)
public class InventoryCountOverlayModeTest
{
	private static final Rectangle WIDGET_BOUNDS = new Rectangle(0, 0, 100, 100);
	private static final int FONT_HEIGHT = 20;
	private static final int MAX_DESCENT = 5;
	private static final int TEXT_HEIGHT = FONT_HEIGHT - MAX_DESCENT; // 15
	private static final int STRING_WIDTH = 10;

	// Matches getInventoryOverlayText's layout math for the constants above.
	private static final int EXPECTED_X = (int) WIDGET_BOUNDS.getCenterX() - (STRING_WIDTH / 2); // 45
	private static final int TOP_Y = (int) WIDGET_BOUNDS.getMinY() + TEXT_HEIGHT; // 15
	private static final int BOTTOM_Y = (int) WIDGET_BOUNDS.getMaxY(); // 100

	@Mock
	private Client client;

	@Mock
	private InventoryCountPlugin plugin;

	@Mock
	private InventoryCountConfig config;

	@Mock
	private ConfigManager configManager;

	@Mock
	private Widget inventoryWidget;

	@Mock
	private Graphics2D graphics;

	@Mock
	private FontMetrics fontMetrics;

	private InventoryCountOverlay overlay;

	@Before
	public void setUp()
	{
		when(configManager.getConfiguration("runelite", "infoboxFontType", FontType.class)).thenReturn(FontType.REGULAR);

		overlay = new InventoryCountOverlay(client, plugin, config, configManager);

		when(config.renderInventoryOverlay()).thenReturn(true);
		when(client.getWidget(ComponentID.FIXED_VIEWPORT_INVENTORY_TAB)).thenReturn(inventoryWidget);
		when(inventoryWidget.isHidden()).thenReturn(false);
		when(inventoryWidget.getBounds()).thenReturn(WIDGET_BOUNDS);

		when(graphics.getFontMetrics()).thenReturn(fontMetrics);
		when(fontMetrics.getHeight()).thenReturn(FONT_HEIGHT);
		when(fontMetrics.getMaxDescent()).thenReturn(MAX_DESCENT);
		when(fontMetrics.stringWidth(org.mockito.ArgumentMatchers.anyString())).thenReturn(STRING_WIDTH);

		overlay.setFreeText("23");
		overlay.setUsedText("5");
	}

	private void verifyTextDrawnAt(String text, int y)
	{
		verify(graphics).drawString(eq(text), eq(EXPECTED_X), eq(y));
	}

	private void verifyTextNeverDrawn(String text)
	{
		verify(graphics, never()).drawString(eq(text), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt());
	}

	@Test
	public void freeModeRendersOnlyFreeTextAtConfiguredPosition()
	{
		overlay.setMode(InventoryCountMode.FREE);
		when(config.inventoryOverlayTextPosition()).thenReturn(InventoryOverlayTextPositions.Bottom);

		overlay.render(graphics);

		verifyTextDrawnAt("23", BOTTOM_Y);
		verifyTextNeverDrawn("5");
	}

	@Test
	public void usedModeRendersOnlyUsedTextAtConfiguredPosition()
	{
		overlay.setMode(InventoryCountMode.USED);
		when(config.inventoryOverlayTextPosition()).thenReturn(InventoryOverlayTextPositions.Top);

		overlay.render(graphics);

		verifyTextDrawnAt("5", TOP_Y);
		verifyTextNeverDrawn("23");
	}

	@Test
	public void bothModeAtBottomRendersFreeAtBottomAndUsedAtTop()
	{
		overlay.setMode(InventoryCountMode.BOTH);
		when(config.inventoryOverlayTextPosition()).thenReturn(InventoryOverlayTextPositions.Bottom);

		overlay.render(graphics);

		verifyTextDrawnAt("23", BOTTOM_Y);
		verifyTextDrawnAt("5", TOP_Y);
	}

	@Test
	public void bothModeAtTopRendersFreeAtTopAndUsedAtBottom()
	{
		overlay.setMode(InventoryCountMode.BOTH);
		when(config.inventoryOverlayTextPosition()).thenReturn(InventoryOverlayTextPositions.Top);

		overlay.render(graphics);

		verifyTextDrawnAt("23", TOP_Y);
		verifyTextDrawnAt("5", BOTTOM_Y);
	}

	@Test
	public void bothModeAtCenterRendersFreeAtTopAndUsedAtBottom()
	{
		overlay.setMode(InventoryCountMode.BOTH);
		when(config.inventoryOverlayTextPosition()).thenReturn(InventoryOverlayTextPositions.Center);

		overlay.render(graphics);

		verifyTextDrawnAt("23", TOP_Y);
		verifyTextDrawnAt("5", BOTTOM_Y);
	}
}
