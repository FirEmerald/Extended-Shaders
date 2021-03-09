package extendedshaders.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** used to turn shaders on and off, by adding and removing them from a registry.<br>
 * keep in mind changes do not effect until the beginning of the next render tick, or when {@link Passthrough#forceShaderCompile()} is run. **/
public class ShaderRegistry
{
	private static final Map<Shader, Integer> SHADERS = new HashMap<>();
	public static boolean hasChanged = true;
	/** returns TRUE while shaders are running **/
	public static boolean shadersActive = false;
	/** returns true during the world rendering **/
	public static boolean rendering = false;
	/** turns a shader ON **/
	public static void addShader(Shader data)
	{
		SHADERS.put(data, 0);
		hasChanged = true;
	}
	/** turns a shader OFF **/
	public static void removeShader(Shader data)
	{
		SHADERS.remove(data);
		hasChanged = true;
	}
	/** gets the active shaders, sorted by priority **/
	public static Shader[] getShaders()
	{
		Shader[] data = new Shader[SHADERS.size()];
		data = SHADERS.keySet().toArray(data);
		Arrays.sort(data);
		return data;
	}
	/** attempt to change a multistate shader's state. does nothing if an invalid state. will not update the currently running shader - you must use {@link Passthrough#updateShaderStates()} **/
	public static void setShaderState(Shader data, int state)
	{
		if (state < 0 || state >= data.getNumStates()) API.logger.warn("Tried to set a shader with " + data.getNumStates() + " states to invalid state " + state);
		else SHADERS.put(data, Integer.valueOf(state));
	}
	/** gets a multistate shader's state. **/
	public static int getShaderState(Shader data)
	{
		Integer num = SHADERS.get(data);
		if (num == null) return -1;
		else return num.intValue();
	}
}