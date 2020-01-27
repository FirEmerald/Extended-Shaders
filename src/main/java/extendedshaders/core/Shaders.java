package extendedshaders.core;

import net.minecraft.launchwrapper.Launch;

public class Shaders
{
	public static final boolean DEOB = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    
	protected static String postProcessorFragUniforms, postProcessorVert, postProcessorFrag, shaderUniform, shaderFrag, shaderVert, anaglyphFrag, copyFrag;
}	