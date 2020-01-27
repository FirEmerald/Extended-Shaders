package extendedshaders.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/** Use this class to create a post-render effect, such as blur. **/
public class PostProcessor implements Comparable
{
	/** the shader program of this post-processor **/
	public int program = -1;
	/** the location of the code for this post-processor **/
	public final ResourceLocation shaderFile;
	/** the location of the uniforms/variables/constants for this shader **/
	public final ResourceLocation uniformFile;
	/** the code for this post-processor **/
	public String shaderText;
	/** the uniforms/variables/constants for this post-processor **/
	public String uniformText;
	/** the priority of this post-processor **/
	public final int priority;
	/** locations of set uniforms used to set texture indicies **/
	public int tex0 = -1, tex1 = -1;
	/** locations of set uniforms used to find offset pixels **/
	public int dx = -1, dy = -1;
	/** location of the uniform for setting the current "eye".<br>
	 * the uniform's value (not THIS value) will be -1 for no anaglyph, 0 red, and 1 for cyan. **/
	public int eye = -1;

	/**
	 * Creates a post-processor.
	 * @param shaderFile the location of the post-processor code.
	 */
	public PostProcessor(ResourceLocation shaderFile)
	{
		this(null, shaderFile, 0);
	}

	/**
	 * Creates a post-processor.
	 * @param uniformFile the location of the uniforms/variables/constants.
	 * @param shaderFile the location of the post-processor code.
	 * @param uniforms the uniforms of the post-processor.
	 */
	public PostProcessor(ResourceLocation uniformFile, ResourceLocation shaderFile)
	{
		this(uniformFile, shaderFile, 0);
	}

	/**
	 * Creates a post-processor.
	 * @param shaderFile the location of the post-processor code.
	 * @param priority the priority of the post-processor.
	 */
	public PostProcessor(ResourceLocation shaderFile, int priority)
	{
		this(null, shaderFile, priority);
	}

	/**
	 * Creates a post-processor.
	 * @param uniformFile the location of the uniforms/variables/constants.
	 * @param shaderFile the location of the post-processor code.
	 * @param priority the priority of the post-processor.
	 * @param uniforms the uniforms of the post-processor.
	 */
	public PostProcessor(ResourceLocation uniformFile, ResourceLocation shaderFile, int priority)
	{
		this.shaderFile = shaderFile;
		this.uniformFile = uniformFile;
		this.priority = priority;
		this.onReload(Minecraft.getMinecraft().getResourceManager());
		ReloadListener.addPost(this);
	}

	/** loads the post-processor text **/
	public void onReload(IResourceManager manager)
	{
		shaderText = "";
		uniformText = "";
		if (shaderFile != null) try
		{
			shaderText = GLSLHelper.readFileAsString(shaderFile, manager) + "\n";
		}
		catch (Exception e)
		{
			API.logger.error("Failed to load shader data " + shaderFile.toString(), e);
		}
		if (uniformFile != null) try
		{
			uniformText = GLSLHelper.readFileAsString(uniformFile, manager) + "\n";
		}
		catch (Exception e)
		{
			API.logger.error("Failed to load uniform/function data " + uniformFile.toString(), e);
		}
		if (shaderText.equals("") && uniformText.equals("")) program = 0;
		else Passthrough.instance.loadPostProcessor(this);
	}
	
	/** get the custom uniform locations from this program here. requires use of a superclass, of course.**/
	public void getUniforms(int program) {}

	/** used for sorting purposes **/
	@Override
	public int compareTo(Object o)
	{
		if (o instanceof PostProcessor) return ((PostProcessor) o).priority - priority;
		else return 0;
	}
	
	/** called after the post processor is bound - use this to set uniforms if needed.*/
	public void onBind(float partialTicks) {}
	
	/**
	 * how many times should this post processor run? useful for screen distortion by entities.
	 * @return the number of iterations
	 */
	public int getIterations()
	{
		return 1;
	}
	
	/** called for each iteration, so you can set appropriate uniform values **/
	public void onIteration(int iteration) {}
}