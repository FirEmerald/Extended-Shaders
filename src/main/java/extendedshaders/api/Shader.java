package extendedshaders.api;

import net.minecraft.client.resources.IResourceManager;

/** Abstract base class for all shaders.<br>
 *  it can have a priority - higher-priority shaders are called FIRST. **/
public abstract class Shader implements Comparable<Shader>
{
	/** the priority of this shader **/
	public final int priority;
	/**
	 * Creates a shader.
	 */
	public Shader()
	{
		this(0);
	}
	/**
	 * Creates a shader.
	 * @param priority the priority of the shader.
	 */
	public Shader(int priority)
	{
		this.priority = priority;
		ReloadListener.addData(this);
	}
	/** get the custom uniform locations from this program here. **/
	public void getUniforms(int program) {}
	/** loads the shader text **/
	public abstract void onReload(IResourceManager manager);
	/** used for sorting purposes **/
	@Override
	public int compareTo(Shader data)
	{
		return data.priority - priority;
	}
	/** gets the uniforms/variables/constants for this vertex shader state **/
	public abstract String getUniformTextVertex(int state);
	/** gets the code for this vertex shader state **/
	public abstract String getShaderTextVertex(int state);
	/** gets the uniforms/variables/constants for this fragment shader state **/
	public abstract String getUniformTextFragment(int state);
	/** gets the code for this fragment shader state **/
	public abstract String getShaderTextFragment(int state);
	/** gets the number of possible states **/
	public abstract int getNumStates();
	/** used to copy uniforms when changing shaders mid-render **/
	public void copyUniforms(int prevProgram, int prevState, int program, int state) {}
	/** reset unform values between renders. **/
	public void onRenderStart(int program, int state) {}
	/** reset uniform values even when this is not active (don't set shader uniforms though) **/
	public void onRenderStartInactive(int program, int state) {}
}