package extendedshaders.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ShaderData
{
	/** the code for this vertex shader **/
	public String shaderTextVertex;
	/** the uniforms/variables/constants for this vertex shader **/
	public String uniformTextVertex;
	/** the code for this fragment shader **/
	public String shaderTextFragment;
	/** the uniforms/variables/constants for this fragment shader **/
	public String uniformTextFragment;
	/** the location of the code for this vertex shader **/
	public final ResourceLocation shaderFileVertex;
	/** the location of the uniforms/variables/constants for this vertex shader **/
	public final ResourceLocation uniformFileVertex;
	/** the location of the code for this fragment shader **/
	public final ResourceLocation shaderFileFragment;
	/** the location of the uniforms/variables/constants for this fragment shader **/
	public final ResourceLocation uniformFileFragment;

	/**
	 * Creates a shader.
	 * @param uniformFile the location of the uniforms/variables/constants.
	 * @param shaderFile the location of the shader code.
	 * @param priority the priority of the shader.
	 */
	public ShaderData(ResourceLocation uniformFileVertex, ResourceLocation shaderFileVertex, ResourceLocation uniformFileFragment, ResourceLocation shaderFileFragment)
	{
		this.uniformFileVertex = uniformFileVertex;
		this.shaderFileVertex = shaderFileVertex;
		this.uniformFileFragment = uniformFileFragment;
		this.shaderFileFragment = shaderFileFragment;
		this.onReload(Minecraft.getMinecraft().getResourceManager());
	}

	/** loads the shader text **/
	public void onReload(IResourceManager manager)
	{
		shaderTextVertex = uniformTextVertex = shaderTextFragment = uniformTextFragment = "";
		if (uniformFileVertex != null) try
		{
			uniformTextVertex = GLSLHelper.readFileAsString(uniformFileVertex, manager) + "\n";
		}
		catch (Exception e)
		{
			API.logger.error("Failed to load uniform/function data " + uniformTextVertex.toString(), e);
		}
		if (shaderFileVertex != null) try
		{
			shaderTextVertex = GLSLHelper.readFileAsString(shaderFileVertex, manager) + "\n";
		}
		catch (Exception e)
		{
			API.logger.error("Failed to load shader data " + shaderFileVertex.toString(), e);
		}
		if (uniformFileFragment != null) try
		{
			uniformTextFragment = GLSLHelper.readFileAsString(uniformFileFragment, manager) + "\n";
		}
		catch (Exception e)
		{
			API.logger.error("Failed to load uniform/function data " + uniformFileFragment.toString(), e);
		}
		if (shaderFileFragment != null) try
		{
			shaderTextFragment = GLSLHelper.readFileAsString(shaderFileFragment, manager) + "\n";
		}
		catch (Exception e)
		{
			API.logger.error("Failed to load shader data " + shaderFileFragment.toString(), e);
		}
	}
}