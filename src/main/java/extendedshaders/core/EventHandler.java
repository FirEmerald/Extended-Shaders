package extendedshaders.core;

import extendedshaders.api.GLSLHelper;
import extendedshaders.api.ShaderRegistry;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
	public static GameSettings.Options shaders;
	
	@SubscribeEvent
	public void onRenderLivingPre(RenderLivingEvent.Pre event)
	{
		Bypass.setIsEntity(true);
	}
	
	@SubscribeEvent
	public void onRenderLivingPost(RenderLivingEvent.Post event)
	{
		Bypass.setIsEntity(false);
	}
	
	@SubscribeEvent
	public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
	{
		Bypass.setIsEntity(true);
	}
	
	@SubscribeEvent
	public void onRenderPlayerPost(RenderPlayerEvent.Post event)
	{
		Bypass.setIsEntity(false);
	}
	
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event)
	{
		Bypass.setIsEntity(true);
	}
}