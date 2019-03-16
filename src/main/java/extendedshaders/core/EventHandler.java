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
		if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isEntity, 1);
	}
	
	@SubscribeEvent
	public void onRenderLivingPost(RenderLivingEvent.Post event)
	{
		if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isEntity, 0);
	}
	
	@SubscribeEvent
	public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
	{
		if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isEntity, 1);
	}
	
	@SubscribeEvent
	public void onRenderPlayerPost(RenderPlayerEvent.Post event)
	{
		if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isEntity, 0);
	}
	
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event)
	{
		if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isEntity, 1);
	}
	/*
	@SubscribeEvent
	public void onRenderSky(ShaderEvent.RenderSky event)
	{
		event.setCanceled(true);
	}
	*/
}