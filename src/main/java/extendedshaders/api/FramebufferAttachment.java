package extendedshaders.api;

import java.nio.IntBuffer;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

public class FramebufferAttachment
{
	public final int internalformat, format, type;
	public final Consumer<Framebuffer> clear;
	public int bufferIndex, textureIndex;

	public FramebufferAttachment(int internalFormat, int format, int type)
	{
		this(internalFormat, format, type, 0, 0, 0, 1);
	}

	public FramebufferAttachment(int internalFormat, int format, int type, float clearV)
	{
		this(internalFormat, format, type, clearV, clearV, clearV, 1);
	}

	public FramebufferAttachment(int internalFormat, int format, int type, float clearV, float clearA)
	{
		this(internalFormat, format, type, clearV, clearV, clearV, clearA);
	}

	public FramebufferAttachment(int internalFormat, int format, int type, float clearR, float clearG, float clearB)
	{
		this(internalFormat, format, type, clearR, clearG, clearB, 1);
	}

	public FramebufferAttachment(int internalFormat, int format, int type, float clearR, float clearG, float clearB, float clearA)
	{
		this.internalformat = internalFormat;
		this.format = format;
		this.type = type;
		this.clear = (f) -> {
	    	{
	        	IntBuffer buf = BufferUtils.createIntBuffer(1);
	        	buf.put(GL30.GL_COLOR_ATTACHMENT0 + bufferIndex);
	        	buf.flip();
	        	GL20.glDrawBuffers(buf);
	    	}
	    	GlStateManager.clearColor(clearR, clearG, clearB, clearA);
	    	GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
		};
	}

	public FramebufferAttachment(int internalFormat, int format, int type, Consumer<Framebuffer> clear)
	{
		this.internalformat = internalFormat;
		this.format = format;
		this.type = type;
		this.clear = clear;
	}

	@Override
	public String toString()
	{
		return "internal format: " + internalformat + " format: " + format + " type: " + type + " buffer index: " + bufferIndex + " texture index: " + textureIndex;
	}
}