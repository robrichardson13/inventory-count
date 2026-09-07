package io.robrichardson.inventorycount;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Reproduces https://github.com/robrichardson13/inventory-count/issues/13.
 *
 * RuneLite starts and stops plugins on the Swing EDT, but the plugin queues its
 * "add overlay" work onto the client thread. If the plugin is disabled before that
 * queued work drains, the overlay is added after shutDown() and nothing ever removes it.
 */
@RunWith(MockitoJUnitRunner.class)
public class InventoryCountPluginShutDownTest
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

	@InjectMocks
	private InventoryCountPlugin plugin;

	/** Tasks queued to the fake client thread, drained manually by the test. */
	private final List<Runnable> clientThreadQueue = new ArrayList<>();

	/** Fake OverlayManager / InfoBoxManager state so we can assert what is actually on screen. */
	private final Set<Overlay> visibleOverlays = new HashSet<>();
	private final Set<InfoBox> visibleInfoBoxes = new HashSet<>();

	@Before
	public void setUp()
	{
		doAnswer(inv ->
		{
			clientThreadQueue.add(inv.getArgument(0));
			return null;
		}).when(clientThread).invoke(any(Runnable.class));

		doAnswer(inv -> visibleOverlays.add(inv.getArgument(0))).when(overlayManager).add(any(Overlay.class));
		doAnswer(inv -> visibleOverlays.remove(inv.getArgument(0))).when(overlayManager).remove(any(Overlay.class));

		doAnswer(inv ->
		{
			visibleInfoBoxes.add(inv.getArgument(0));
			return null;
		}).when(infoBoxManager).addInfoBox(any(InfoBox.class));
		doAnswer(inv ->
		{
			visibleInfoBoxes.remove(inv.getArgument(0));
			return null;
		}).when(infoBoxManager).removeInfoBox(any(InfoBox.class));

		when(config.renderInventoryOverlay()).thenReturn(true);
		when(config.renderInventoryInfoBox()).thenReturn(true);
	}

	private void drainClientThread()
	{
		List<Runnable> pending = new ArrayList<>(clientThreadQueue);
		clientThreadQueue.clear();
		pending.forEach(Runnable::run);
	}

	@Test
	public void startUpShowsOverlayAndInfoBoxOnceClientThreadRuns() throws Exception
	{
		plugin.startUp();
		drainClientThread();

		assertTrue("overlay should be shown after startUp", visibleOverlays.contains(overlay));
		assertFalse("infobox should be shown after startUp", visibleInfoBoxes.isEmpty());
	}

	@Test
	public void disablingPluginBeforeClientThreadRunsLeavesNothingOnScreen() throws Exception
	{
		plugin.startUp();
		// Plugin gets disabled before the client thread has processed the queued add.
		plugin.shutDown();
		drainClientThread();

		assertFalse("overlay must not persist after plugin is disabled", visibleOverlays.contains(overlay));
		assertTrue("infobox must not persist after plugin is disabled", visibleInfoBoxes.isEmpty());
	}

	@Test
	public void disablingPluginRightAfterTogglingCounterOnLeavesNothingOnScreen() throws Exception
	{
		// Issue #13 steps: plugin enabled, "Toggle Inventory Counter" turned on, plugin disabled.
		plugin.startUp();
		drainClientThread();

		net.runelite.client.events.ConfigChanged event = new net.runelite.client.events.ConfigChanged();
		event.setGroup(InventoryCountConfig.GROUP);
		event.setKey("renderInventoryOverlay");
		event.setNewValue("true");
		plugin.onConfigChanged(event);

		plugin.shutDown();
		drainClientThread();

		assertFalse("overlay must not persist after plugin is disabled", visibleOverlays.contains(overlay));
		assertTrue("infobox must not persist after plugin is disabled", visibleInfoBoxes.isEmpty());
	}
}
