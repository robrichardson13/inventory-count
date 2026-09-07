package io.robrichardson.inventorycount;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.awt.Color;
import net.runelite.client.plugins.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Covers InventoryCountInfoBox's per-mode text/tooltip and the free-slots-based text color,
 * added alongside InventoryCountMode (card #1).
 */
@RunWith(MockitoJUnitRunner.class)
public class InventoryCountInfoBoxModeTest
{
	@Mock
	private InventoryCountConfig config;

	@Mock
	private Plugin plugin;

	private InventoryCountInfoBox infoBox;

	@Before
	public void setUp()
	{
		when(config.renderInventoryInfoBox()).thenReturn(true);
		infoBox = new InventoryCountInfoBox(null, plugin, config);
	}

	@Test
	public void freeModeShowsFreeCount()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.FREE);
		infoBox.setCounts(23, 5);

		assertEquals("23", infoBox.getText());
	}

	@Test
	public void usedModeShowsUsedCount()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.USED);
		infoBox.setCounts(23, 5);

		assertEquals("5", infoBox.getText());
	}

	@Test
	public void bothModeShowsFreeCount()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.BOTH);
		infoBox.setCounts(23, 5);

		assertEquals("23", infoBox.getText());
	}

	@Test
	public void freeModeTooltip()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.FREE);
		infoBox.setCounts(23, 5);

		assertEquals("23 free inventory slots", infoBox.getTooltip());
	}

	@Test
	public void usedModeTooltip()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.USED);
		infoBox.setCounts(23, 5);

		assertEquals("5 used inventory slots", infoBox.getTooltip());
	}

	@Test
	public void bothModeTooltipJoinsFreeAndUsed()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.BOTH);
		infoBox.setCounts(23, 5);

		assertEquals("23 free inventory slots</br>5 used inventory slots", infoBox.getTooltip());
	}

	@Test
	public void freeModeTooltipIsSingularWhenOneFreeSlot()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.FREE);
		infoBox.setCounts(1, 27);

		assertEquals("1 free inventory slot", infoBox.getTooltip());
	}

	@Test
	public void usedModeTooltipIsSingularWhenOneUsedSlot()
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.USED);
		infoBox.setCounts(27, 1);

		assertEquals("1 used inventory slot", infoBox.getTooltip());
	}

	@Test
	public void textColorIsRedWhenNoFreeSlots()
	{
		infoBox.setCounts(0, 28);

		assertEquals(Color.RED, infoBox.getTextColor());
	}

	@Test
	public void textColorIsWhiteWhenAnyFreeSlots()
	{
		infoBox.setCounts(1, 27);

		assertEquals(Color.WHITE, infoBox.getTextColor());
	}

	@Test
	public void textColorIsWhiteWhenAllSlotsFree()
	{
		infoBox.setCounts(28, 0);

		assertEquals(Color.WHITE, infoBox.getTextColor());
	}
}
