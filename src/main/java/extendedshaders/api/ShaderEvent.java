package extendedshaders.api;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class ShaderEvent extends Event
{
	public final float partialTicks;
	/** ONLY USED BY FORGE!!! **/
	public ShaderEvent()
	{
		partialTicks = 0;
	}
	
	private ShaderEvent(float partialTicks)
	{
		this.partialTicks = partialTicks;
	}

	/**
	 * This event is fired before the sky is rendered.<br>
	 * <br>
	 * {@link #partialTicks} is the partial-tick time.<br>
	 * <br>
	 * This event is {@link Cancelable}.<br>
	 * If the event is canceled, the sky will not be rendered.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	@Cancelable
	public static class RenderSky extends ShaderEvent
	{
		/** ONLY USED BY FORGE!!! **/
		public RenderSky()
		{
			super();
		}
		
		public RenderSky(float partialTicks)
		{
			super(partialTicks);
		}
	}

	/**
	 * This event is fired before shaders are run<br>
	 * <br>
	 * {@link #partialTicks} is the partial-tick time<br>
	 * {@link #renderPass} is the render pass, always 1.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	public static class Start extends ShaderEvent
	{
		/** ONLY USED BY FORGE!!! **/
		public Start()
		{
			super();
		}
		
		public Start(float partialTicks)
		{
			super(partialTicks);
		}
	}

	/**
	 * This event is fired when shaders are stopped.<br>
	 * <br>
	 * {@link #partialTicks} is the partial-tick time<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	public static class Stop extends ShaderEvent
	{
		/** ONLY USED BY FORGE!!! **/
		public Stop()
		{
			super();
		}
		
		public Stop(float partialTicks)
		{
			super(partialTicks);
		}
	}
}