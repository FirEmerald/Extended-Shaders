package extendedshaders.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/** Use this class to create a single-state shader. **/
public class ShaderSingle extends Shader
{
	public final ShaderData state;

	/**
	 * Creates a shader.
	 * @param uniformFileVertex the location of the uniforms/variables/constants for the vertex shader.
	 * @param shaderFileVertex the location of the shader code for the vertex shader.
	 * @param uniformFileFragment the location of the uniforms/variables/constants for the fragment shader.
	 * @param shaderFileFragment the location of the shader code for the fragment shader.
	 * @param priority the priority of the shader.
	 */
	public ShaderSingle(ResourceLocation uniformFileVertex, ResourceLocation shaderFileVertex, ResourceLocation uniformFileFragment, ResourceLocation shaderFileFragment, int priority)
	{
		super(priority);
		this.state = new ShaderData(uniformFileVertex, shaderFileVertex, uniformFileFragment, shaderFileFragment);
		this.onReload(Minecraft.getMinecraft().getResourceManager());
	}
	
	@Override
	public void onReload(IResourceManager manager)
	{
		state.onReload(manager);
	}

	@Override
	public String getUniformTextVertex(int state)
	{
		return this.state.uniformTextVertex;
	}

	@Override
	public String getShaderTextVertex(int state)
	{
		return this.state.shaderTextVertex;
	}

	@Override
	public String getUniformTextFragment(int state)
	{
		return this.state.uniformTextFragment;
	}

	@Override
	public String getShaderTextFragment(int state)
	{
		return this.state.shaderTextFragment;
	}

	@Override
	public int getNumStates()
	{
		return 1;
	}
}