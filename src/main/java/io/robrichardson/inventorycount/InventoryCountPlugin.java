package io.robrichardson.inventorycount;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@PluginDescriptor(
		name = "Inventory Count"
)
public class InventoryCountPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private InventoryCountOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InventoryCountConfig config;

//	private static final BufferedImage INVENTORY_IMAGE;

	private boolean active = false;

//	static
//	{
//		INVENTORY_IMAGE = ImageUtil.loadImageResource(InventoryCountPlugin.class, "inventory_icon.png");
//	}

	@Override
	protected void startUp() throws Exception
	{
		active = true;
		if (config.renderOnInventory())
			overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		active = false;
//		lastIdleTicks = -1;
//		removeTimer();
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!InventoryCountConfig.GROUP.equals(event.getGroup())) return;

		if ("renderOnInventory".equals(event.getKey())) {
			if (config.renderOnInventory()) {
				overlayManager.add(overlay);
//				removeTimer();
			} else {
				overlayManager.remove(overlay);

				// emulate infobox timer creation similar to onClientTick does
//				final long durationMillis = (AFK_LOG_TICKS - getIdleTicks()) * AFK_LOG_TIME.toMillis() / AFK_LOG_TICKS + 999;
//				setTimer(Duration.ofMillis(durationMillis));
			}
		}
	}

	@Provides
	InventoryCountConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryCountConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
//		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
//		{
//			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
//		}
	}

}
