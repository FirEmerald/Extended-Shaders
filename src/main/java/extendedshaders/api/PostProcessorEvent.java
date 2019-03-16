package extendedshaders.api;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PostProcessorEvent extends Event
{
	public final float partialTicks;
	public final PostProcessor processor;
	
	/** ONLY USED BY FORGE!!! **/
	public PostProcessorEvent()
	{
		this.partialTicks = 0;
		this.processor = null;
	}
	
	private PostProcessorEvent(float partialTicks, PostProcessor processor)
	{
		this.partialTicks = partialTicks;
		this.processor = processor;
	}
	/**
	 * This event is fired before a post-processor is bound.<br>
	 * <br>
	 * {@link #partialTicks} is the partial-tick time<br>
	 * {@link #processor} contains the PostProcessor to run.<br>
	 * <br>
	 * This event is {@link Cancelable}.<br>
	 * If the event is canceled, the post-processor will not run.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	@Cancelable
	public static class Bind extends PostProcessorEvent
	{
		/** ONLY USED BY FORGE!!! **/
		public Bind()
		{
			super();
		}
		
		public Bind(float partialTicks, PostProcessor processor)
		{
			super(partialTicks, processor);
		}
	}
	/**
	 * This event is fired before a post-processor is run.<br>
	 * <br>
	 * {@link #partialTicks} is the partial-tick time<br>
	 * {@link #processor} contains the PostProcessor to run.<br>
	 * {@link #iteration} contains the iteration number.<br>
	 * <br>
	 * This event is {@link Cancelable}.<br>
	 * If the event is canceled, the post-processor iteration will not run.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	@Cancelable
	public static class Start extends PostProcessorEvent
	{
		public final int iteration;
		/** ONLY USED BY FORGE!!! **/
		public Start()
		{
			super();
			this.iteration = 0;
		}
		
		public Start(float partialTicks, PostProcessor processor, int iteration)
		{
			super(partialTicks, processor);
			this.iteration = iteration;
		}
	}

	/**
	 * This event is fired after a post-processor is run.<br>
	 * <br>
	 * {@link #partialTicks} is the partial-tick time<br>
	 * {@link #processor} contains the PostProcessor to run.<br>
	 * {@link #iteration} contains the iteration number.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 **/
	public static class Stop extends PostProcessorEvent
	{
		public final int iteration;
		/** ONLY USED BY FORGE!!! **/
		public Stop()
		{
			super();
			this.iteration = 0;
		}
		
		public Stop(float partialTicks, PostProcessor processor, int iteration)
		{
			super(partialTicks, processor);
			this.iteration = iteration;
		}
	}
}