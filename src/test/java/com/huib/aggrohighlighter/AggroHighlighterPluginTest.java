package com.huib.aggrohighlighter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AggroHighlighterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AggroHighlighterPlugin.class);
		RuneLite.main(args);
	}
}