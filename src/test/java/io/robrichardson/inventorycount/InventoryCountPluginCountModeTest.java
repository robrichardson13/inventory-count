package io.robrichardson.inventorycount;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Covers the free/used slot computation and dynamic-colour behaviour added for
 * InventoryCountMode (card #1): updateOverlays() should push both free and used counts
 * to the overlay and infobox, and dynamic colour should keep deriving from free slots
 * regardless of which mode is configured.
 */
@RunWith(MockitoJUnitRunner.class)
public class InventoryCountPluginCountModeTest
{
	@Mock
	private Client client;

	@Mock
	private ClientThread clientThread;

	@Mock
	private InfoBoxManager infoBoxManager;

	@Mock
	private InventoryCountOverlay overlay;

	@Mock
	private OverlayManager overlayManager;

	@Mock
	private InventoryCountConfig config;

	@Mock
	private ItemContainer itemContainer;

	@InjectMocks
	private InventoryCountPlugin plugin;

	private final List<Runnable> clientThreadQueue = new ArrayList<>();

	@Before
	public void setUp() throws Exception
	{
		doAnswer(inv ->
		{
			clientThreadQueue.add(inv.getArgument(0));
			return null;
		}).when(clientThread).invoke(any(Runnable.class));

		when(config.renderInventoryOverlay()).thenReturn(true);
		when(config.renderInventoryInfoBox()).thenReturn(true);
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.FREE);

		when(client.getItemContainer(net.runelite.api.InventoryID.INVENTORY)).thenReturn(itemContainer);
	}

	private void drainClientThread()
	{
		List<Runnable> pending = new ArrayList<>(clientThreadQueue);
		clientThreadQueue.clear();
		pending.forEach(Runnable::run);
	}

	/** 28-slot inventory with {@code usedCount} real items (id != -1) and the rest empty (-1). */
	private static Item[] inventoryWith(int usedCount)
	{
		Item[] items = new Item[28];
		for (int i = 0; i < 28; i++)
		{
			items[i] = new Item(i < usedCount ? 1 : -1, 1);
		}
		return items;
	}

	/**
	 * startUp() queues toggleOverlayAndInfoBox() -> updateOverlays() onto the client thread,
	 * so draining the queue is enough to trigger a computation; onItemContainerChanged is
	 * private and not reachable from the test, but it calls the same updateOverlays() method.
	 */
	private void triggerUpdate(int usedCount) throws Exception
	{
		when(itemContainer.getItems()).thenReturn(inventoryWith(usedCount));

		plugin.startUp();
		drainClientThread();
	}

	@Test
	public void updateOverlaysPushesFreeAndUsedCountsToOverlay() throws Exception
	{
		triggerUpdate(5);

		verify(overlay).setFreeText("23");
		verify(overlay).setUsedText("5");
	}

	@Test
	public void updateOverlaysPushesFreeAndUsedCountsToInfoBox() throws Exception
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.BOTH);
		triggerUpdate(5);

		InventoryCountInfoBox infoBox = plugin.getInventoryCountInfoBox();
		assertEquals("23 free inventory slots</br>5 used inventory slots", infoBox.getTooltip());
	}

	@Test
	public void dynamicColorInFreeModeIsRedWhenNoFreeSlots() throws Exception
	{
		when(config.dynamicInventoryOverlayColor()).thenReturn(true);
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.FREE);

		triggerUpdate(28); // 0 free slots

		verify(overlay).setColor(Color.RED);
	}

	@Test
	public void dynamicColorInUsedModeStillDerivesFromFreeSlots() throws Exception
	{
		when(config.dynamicInventoryOverlayColor()).thenReturn(true);
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.USED);

		triggerUpdate(28); // 0 free, 28 used -> red despite USED mode
		verify(overlay).setColor(Color.RED);
	}

	@Test
	public void dynamicColorInUsedModeIsGreenWhenAllSlotsFree() throws Exception
	{
		when(config.dynamicInventoryOverlayColor()).thenReturn(true);
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.USED);

		triggerUpdate(0); // 28 free, 0 used -> green
		verify(overlay).setColor(Color.GREEN);
	}

	@Test
	public void dynamicColorInBothModeIsRedWhenNoFreeSlots() throws Exception
	{
		when(config.dynamicInventoryOverlayColor()).thenReturn(true);
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.BOTH);

		triggerUpdate(28);
		verify(overlay).setColor(Color.RED);
	}

	@Test
	public void dynamicColorInBothModeIsGreenWhenAllSlotsFree() throws Exception
	{
		when(config.dynamicInventoryOverlayColor()).thenReturn(true);
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.BOTH);

		triggerUpdate(0);
		verify(overlay).setColor(Color.GREEN);
	}

	@Test
	public void modeIsPushedToOverlay() throws Exception
	{
		when(config.inventoryCountMode()).thenReturn(InventoryCountMode.BOTH);
		triggerUpdate(10);

		verify(overlay).setMode(InventoryCountMode.BOTH);
	}
}
