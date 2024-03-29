package extendedshaders.core;

import extendedshaders.api.GLSLHelper;
import extendedshaders.api.ShaderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

@SuppressWarnings("deprecation")
public class CoreReloadListener implements IResourceManagerReloadListener
{
	@Override
	public void onResourceManagerReload(IResourceManager manager)
	{
		Shaders.postProcessorFragUniforms = readFile("post_processor_frag_uniforms");
		Shaders.postProcessorVert = 		readFile("post_processor_vert");
		Shaders.postProcessorFrag = 		readFile("post_processor_frag");
		Shaders.shaderUniform = 			readFile("shader_uniform");
		Shaders.shaderFrag = 				readFile("shader_frag");
		Shaders.shaderFragPost = 			readFile("shader_frag_post");
		Shaders.shaderVert = 				readFile("shader_vert");
		Shaders.shaderVertPost = 			readFile("shader_vert_post");
		Shaders.copyFrag = 					readFile("copy_frag");
		Shaders.anaglyphFrag = 				readFile("anaglyph_frag");
		ShaderRegistry.hasChanged = true;
		if (Main.copyShader > 0) GLSLHelper.deleteProgram(Main.copyShader);
		Main.copyShader = Main.createShader(Shaders.postProcessorVert, Shaders.copyFrag);
		if (Main.copyShader <= 0) FMLClientHandler.instance().haltGame("failed to create copy shader!", new Exception());
		Main.copyTex = GLSLHelper.getUniformLocation(Main.copyShader, "tex");
		if (Main.anaglyphShader > 0) GLSLHelper.deleteProgram(Main.anaglyphShader);
		Main.anaglyphShader = Main.createShader(Shaders.postProcessorVert, Shaders.anaglyphFrag);
		if (Main.anaglyphShader <= 0) FMLClientHandler.instance().haltGame("failed to create anaglyph shader!", new Exception());
		Main.anaglyphCyan = GLSLHelper.getUniformLocation(Main.anaglyphShader, "cyan");
		Main.anaglyphRed = GLSLHelper.getUniformLocation(Main.anaglyphShader, "red");
	}

	public static String readFile(String file)
	{
		try
		{
			return GLSLHelper.readFileAsString(new ResourceLocation("extendedshaders", "shaders/" + file + ".txt"), Minecraft.getMinecraft().getResourceManager());
		} catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
}