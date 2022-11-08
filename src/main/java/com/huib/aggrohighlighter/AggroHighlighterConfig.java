package com.huib.aggrohighlighter;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("aggrohighlighter")
public interface AggroHighlighterConfig extends Config
{
	@Alpha
	@ConfigItem(
			keyName = "aggroHighlightColor",
			name = "Aggro Highlight Color",
			description = "The color of the outline for aggressive NPCs"
	)
	default Color aggroHighLightColor()
	{
		return new Color(0xFFB31515, true);
	}

	@ConfigItem(
			position = 12,
			keyName = "borderWidth",
			name = "Border Width",
			description = "Width of the highlighted NPC border"
	)
	default double borderWidth()
	{
		return 2;
	}
}
