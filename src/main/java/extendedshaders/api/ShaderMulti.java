package extendedshaders.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;

/** Use this class to create a multi-state shader. **/
public class ShaderMulti extends Shader
{
	/** the different shader states. **/
	public final ShaderData[] states;
	/**
	 * Creates a shader.
	 * @param states the different shader states. must be at least one!
	 */
	public ShaderMulti(ShaderData... states)
	{
		this(0, states);
	}

	/**
	 * Creates a shader.
	 * @param priority the priority of the shader.
	 * @param states the different shader states. must be at least one!
	 */
	public ShaderMulti(int priority, ShaderData... states)
	{
		super(priority);
		if (states.length == 0) throw new IllegalArgumentException("cannot create a shader of 0 states!");
		this.states = states;
		this.onReload(Minecraft.getMinecraft().getResourceManager());
	}

	@Override
	public void onReload(IResourceManager manager)
	{
		for (ShaderData state : states) state.onReload(manager);
	}

	@Override
	public int getNumStates()
	{
		return states.length;
	}

	@Override
	public String getUniformTextVertex(int state)
	{
		return states[state].uniformTextVertex;
	}

	@Override
	public String getShaderTextVertex(int state)
	{
		return states[state].shaderTextVertex;
	}

	@Override
	public String getUniformTextFragment(int state)
	{
		return states[state].uniformTextFragment;
	}

	@Override
	public String getShaderTextFragment(int state)
	{
		return states[state].shaderTextFragment;
	}
}