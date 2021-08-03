package extendedshaders.core;

import extendedshaders.api.FramebufferAttachment;
import extendedshaders.api.GLSLHelper;
import extendedshaders.api.Passthrough;
import extendedshaders.api.PostProcessor;
import extendedshaders.api.Shader;
import extendedshaders.api.ShaderRegistry;

public class ShaderPassthrough extends Passthrough
{
	public ShaderPassthrough()
	{
		instance = this;
	}

	@Override
	public void loadPostProcessor(PostProcessor postProcessor)
	{
		if (postProcessor.program > 0)
		{
			GLSLHelper.deleteProgram(postProcessor.program);
			postProcessor.program = 0;
		}
		int vertShader = GLSLHelper.createVertShader();
		if (vertShader <= 0)
		{
			Plugin.logger().error("could not create vertex shader");
			return;
		}
		GLSLHelper.shaderSource(vertShader, Shaders.postProcessorVert);
		GLSLHelper.compileShader(vertShader);
		if (!GLSLHelper.didShaderCompile(vertShader))
		{
			Plugin.logger().error("Failed to compile vertex shader: " + GLSLHelper.getShaderLog(vertShader));
			Plugin.logger().error(Shaders.postProcessorVert);
			GLSLHelper.deleteShader(vertShader);
			return;
		}
		String fragText = Shaders.postProcessorFragUniforms + postProcessor.uniformText + Shaders.postProcessorFrag  + postProcessor.shaderText + "\n}";
		int fragShader = GLSLHelper.createFragShader();
		if (fragShader <= 0)
		{
			Plugin.logger().error("could not create fragment shader");
			GLSLHelper.deleteShader(vertShader);
			return;
		}
		GLSLHelper.shaderSource(fragShader, fragText);
		GLSLHelper.compileShader(fragShader);
		if (!GLSLHelper.didShaderCompile(fragShader))
		{
			Plugin.logger().error("Failed to compile fragment shader: " + GLSLHelper.getShaderLog(fragShader));
			Plugin.logger().error(fragText);
			GLSLHelper.deleteShader(vertShader);
			GLSLHelper.deleteShader(fragShader);
			return;
		}
		postProcessor.program = GLSLHelper.createProgram();
		if (postProcessor.program <= 0)
		{
			Plugin.logger().error("Failed to create shader!");
			GLSLHelper.deleteShader(vertShader);
			GLSLHelper.deleteShader(fragShader);
			return;
		}
		GLSLHelper.linkShader(postProcessor.program, vertShader);
		GLSLHelper.linkShader(postProcessor.program, fragShader);
		GLSLHelper.linkProgram(postProcessor.program);
		if (!GLSLHelper.didProgramLink(postProcessor.program))
		{
			Plugin.logger().error("Failed to link shaders: " + GLSLHelper.getProgramLog(postProcessor.program));
			GLSLHelper.deleteShader(vertShader);
			GLSLHelper.deleteShader(fragShader);
			GLSLHelper.deleteProgram(postProcessor.program);
			postProcessor.program = 0;
			return;
		}
		GLSLHelper.validateProgram(postProcessor.program);
		if (!GLSLHelper.didProgramValidate(postProcessor.program))
		{
			Plugin.logger().error("Failed to validate program: " + GLSLHelper.getProgramLog(postProcessor.program));
			GLSLHelper.deleteProgram(postProcessor.program);
			postProcessor.program = 0;
			return;
		}
		Plugin.logger().debug(Shaders.postProcessorVert);
		postProcessor.tex0 = GLSLHelper.getUniformLocation(postProcessor.program, "tex0");
		postProcessor.tex1 = GLSLHelper.getUniformLocation(postProcessor.program, "tex1");
		postProcessor.dx = GLSLHelper.getUniformLocation(postProcessor.program, "dx");
		postProcessor.dy = GLSLHelper.getUniformLocation(postProcessor.program, "dy");
		postProcessor.eye = GLSLHelper.getUniformLocation(postProcessor.program, "eye");
		postProcessor.getUniforms(postProcessor.program);
		Plugin.logger().debug(fragText);
	}

	@Override
	public void updateShaderStates()
	{
		if (ShaderRegistry.rendering)
		{
			int prevProg = Main.currentShader;
			Shader[] shaders = ShaderRegistry.getShaders();
			int[] prevStates = new int[shaders.length];
			for (int i = 0; i < shaders.length; i++) prevStates[i] = (Main.currentState / Main.OFFSETS.get(shaders[i])) % shaders[i].getNumStates();
			Main.runShader();
			Bypass.set();
			int prog = Main.currentShader;
			for (int i = 0; i < shaders.length; i++) shaders[i].copyUniforms(prevProg, prevStates[i], prog, ShaderRegistry.getShaderState(shaders[i]));
		}
	}

	@Override
	public void pauseShaders()
	{
		Main.unbind();
	}

	@Override
	public void resumeShaders()
	{
		Main.rebind();
	}

	@Override
	public void disableEffects()
	{
		Main.disableEffects();
	}

	@Override
	public void reenableEffects()
	{
		Main.reenableEffects();
	}

	@Override
	public void onAddFramebufferAttachment(FramebufferAttachment attachment)
	{
		Main.onAddFramebufferAttachment(attachment);
	}

	@Override
	public void onRemoveFramebufferAttachment(FramebufferAttachment attachment)
	{
		Main.onRemoveFramebufferAttachment(attachment);
	}
}