package extendedshaders.api;

import java.nio.IntBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;

public class FramebufferAttachment
{
	public final int internalformat, format, type;
	public final Consumer<Framebuffer> clear;
	public int bufferIndex, textureIndex;
	
	public FramebufferAttachment(int internalFormat, int format, int type, float[] clearColor)
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
	    	float cr = clearColor[0];
	    	float cg = clearColor[1];
	    	float cb = clearColor[2];
	    	float ca = clearColor[3];
	    	/*
	    	int width = f.framebufferWidth;
	    	int height = f.framebufferHeight;
	    	GlStateManager.depthMask(false);
	    	GlStateManager.disableTexture2D();
	    	*/
	    	GlStateManager.clearColor(cr, cg, cb, ca);
	    	GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
	    	/*
	    	GlStateManager.color(cr, cg, cb, ca);
			Tessellator t = Tessellator.getInstance();
			BufferBuilder b = t.getBuffer();
			b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			b.pos(0, 0, 2).tex(0, 0).endVertex();
			b.pos(width, 0, 2).tex(1, 0).endVertex();
			b.pos(width, height, 2).tex(1, 1).endVertex();
			b.pos(0, height, 2).tex(0, 1).endVertex();
			t.draw();
			*/
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