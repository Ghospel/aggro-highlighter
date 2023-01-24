package com.huib.aggrohighlighter;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@PluginDescriptor(
	name = "Aggro Highlighter"
)
public class AggroHighlighterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private AggroHighlighterConfig config;

	@Inject
	private NpcOverlayService npcOverlayService;

	@Getter(AccessLevel.PACKAGE)
	private final Map<NPC, HighlightedNpc> highlightedNpcs = new HashMap<>();
	private final Map<NPC, HighlightedNpc> notHighlightedNpcs = new HashMap<>();

	private final Map<Actor, NPC> actorNPCMap = new HashMap<>();

	private final Function<NPC, HighlightedNpc> isHighlighted = highlightedNpcs::get;

	private Player localPlayer;

	@Override
	protected void startUp() throws Exception
	{
		npcOverlayService.registerHighlighter(isHighlighted);
		log.info("Aggro Highlighter started");
		localPlayer = client.getLocalPlayer();
	}

	@Override
	protected void shutDown() throws Exception
	{
		highlightedNpcs.clear();
		npcOverlayService.unregisterHighlighter(isHighlighted);
		log.info("Aggro Highlighter stopped");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ||
				gameStateChanged.getGameState() == GameState.HOPPING)
		{
			highlightedNpcs.clear();
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e)
	{
		if(isInteractingWithLocalPlayer(e.getActor())){
			NPC npc = actorNPCMap.get(e.getActor());
			if(hasAttackOption(npc))
			{
				log.info(e.getActor().getName() + " is attacking you");
				log.info("Highlighting npc: " + npc.getName());
				highlightedNpcs.put(npc, highlightedNpc(npc));
				log.info("highlightedNPC count: " + highlightedNpcs.size());
				npcOverlayService.registerHighlighter(isHighlighted);
			}
		}
	}

	private boolean isInteractingWithLocalPlayer(Actor e){
		return e.getInteracting().getName().equals(client.getLocalPlayer().getName());
	}

	private boolean hasAttackOption(NPC npc)
	{
		return ArrayUtils.contains(npc.getTransformedComposition().getActions(), "Attack");
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null)
		{
			return;
		}

		log.info("NPC Spawned: " + npc.getName());
		actorNPCMap.put(npcSpawned.getActor(), npcSpawned.getNpc());
	}
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();
		if(highlightedNpcs.containsKey(npc)){
			highlightedNpcs.remove(npc);
		}
	}

	@Provides
	AggroHighlighterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AggroHighlighterConfig.class);
	}

	private HighlightedNpc highlightedNpc(NPC npc)
	{
		return HighlightedNpc.builder()
				.npc(npc)
				.highlightColor(config.aggroHighLightColor())
				.outline(true)
				.borderWidth((float) config.borderWidth())
				.render(this::render)
				.build();
	}


	private boolean render(NPC n)
	{
		return true;
	}
	
//	private HighlightedNpc highlightedNpc(NPC npc)
//	{
//		return HighlightedNpc.builder()
//				.npc(npc)
//				.highlightColor(config.aggroHighLightColor())
//				.borderWidth((float)config.borderWidth())
//				.build();
//	}
}
