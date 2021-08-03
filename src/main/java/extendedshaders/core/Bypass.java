package extendedshaders.core;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import extendedshaders.api.GLSLHelper;
import extendedshaders.api.ShaderRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class Bypass
{
	public static boolean isAlias = false;
	public static boolean isEntity = false;
	public static float[] fogColor = {0, 0, 0, 1};
	public static IntBuffer useTex = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_TEXTURE_ENV_MODE = BufferUtils.createIntBuffer(8);
	public static FloatBuffer GL_TEXTURE_ENV_COLOR = BufferUtils.createFloatBuffer(32);
	public static IntBuffer GL_COMBINE_RGB = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_COMBINE_ALPHA = BufferUtils.createIntBuffer(8);
	public static FloatBuffer GL_RGB_SCALE = BufferUtils.createFloatBuffer(8);
	public static FloatBuffer GL_ALPHA_SCALE = BufferUtils.createFloatBuffer(8);
	public static IntBuffer GL_SRC0_RGB = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_OPERAND0_RGB = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_SRC0_ALPHA = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_OPERAND0_ALPHA = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_SRC1_RGB = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_OPERAND1_RGB = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_SRC1_ALPHA = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_OPERAND1_ALPHA = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_SRC2_RGB = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_OPERAND2_RGB = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_SRC2_ALPHA = BufferUtils.createIntBuffer(8);
	public static IntBuffer GL_OPERAND2_ALPHA = BufferUtils.createIntBuffer(8);
	public static boolean disableEffects = false;
	/*
	public static void printTexState()
	{
		for (int i = 0; i < 8; i++) if (GlStateManager.textureState[i].texture2DState.currentState)
		{
			System.out.println("Texture " + i);
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + i);
			//System.out.println("Enabled: " + useTex.get(i));
			System.out.println("Env mode: " + GL_TEXTURE_ENV_MODE.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE));
			System.out.println("Env color: " + GL_TEXTURE_ENV_COLOR.get(i * 4) + ", " + GL_TEXTURE_ENV_COLOR.get(i * 4 + 1) + ", " + GL_TEXTURE_ENV_COLOR.get(i * 4 + 2) + ", " + GL_TEXTURE_ENV_COLOR.get(i * 4 + 3));
			System.out.println("Combine RGB: " + GL_COMBINE_RGB.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB));
			System.out.println("Combine alpha: " + GL_COMBINE_ALPHA.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA));
			System.out.println("RGB scale: " + GL_RGB_SCALE.get(i) + " : " + GL11.glGetTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE));
			System.out.println("Alpha scale: " + GL_ALPHA_SCALE.get(i) + " : " + GL11.glGetTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE));
			System.out.println("Source 0 RGB: " + GL_SRC0_RGB.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB));
			System.out.println("Operand 0 RGB: " + GL_OPERAND0_RGB.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB));
			System.out.println("Source 0 alpha: " + GL_SRC0_ALPHA.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA));
			System.out.println("Operand 0 alpha: " + GL_OPERAND0_ALPHA.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA));
			System.out.println("Source 1 RGB: " + GL_SRC1_RGB.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB));
			System.out.println("Operand 1 RGB: " + GL_OPERAND1_RGB.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB));
			System.out.println("Source 1 alpha: " + GL_SRC1_ALPHA.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_ALPHA));
			System.out.println("Operand 1 alpha: " + GL_OPERAND1_ALPHA.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA));
			System.out.println("Source 2 RGB: " + GL_SRC2_RGB.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB));
			System.out.println("Operand 2 RGB: " + GL_OPERAND2_RGB.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB));
			System.out.println("Source 2 alpha: " + GL_SRC2_ALPHA.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_ALPHA));
			System.out.println("Operand 2 alpha: " + GL_OPERAND2_ALPHA.get(i) + " : " + GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_ALPHA));
		}
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
	}
	*/
	static
	{
		for (int i = 0; i < 8; i++)
		{
			useTex.put(i, 0);
			GL_TEXTURE_ENV_MODE.put(i, GL13.GL_COMBINE);
			GL_TEXTURE_ENV_COLOR.put(i * 4, 0);
			GL_TEXTURE_ENV_COLOR.put(i * 4 + 1, 0);
			GL_TEXTURE_ENV_COLOR.put(i * 4 + 2, 0);
			GL_TEXTURE_ENV_COLOR.put(i * 4 + 3, 0);
			GL_COMBINE_RGB.put(i, GL11.GL_MODULATE);
			GL_COMBINE_ALPHA.put(i, GL11.GL_MODULATE);
			GL_RGB_SCALE.put(i, 1f);
			GL_ALPHA_SCALE.put(i, 1f);
			GL_SRC0_RGB.put(i, GL11.GL_TEXTURE);
			GL_OPERAND0_RGB.put(i, GL11.GL_SRC_COLOR);
			GL_SRC0_ALPHA.put(i, GL11.GL_TEXTURE);
			GL_OPERAND0_ALPHA.put(i, GL11.GL_SRC_ALPHA);
			GL_SRC1_RGB.put(i, GL13.GL_PREVIOUS);
			GL_OPERAND1_RGB.put(i, GL11.GL_SRC_COLOR);
			GL_SRC1_ALPHA.put(i, GL13.GL_PREVIOUS);
			GL_OPERAND1_ALPHA.put(i, GL11.GL_SRC_ALPHA);
			GL_SRC2_RGB.put(i, GL13.GL_CONSTANT);
			GL_OPERAND2_RGB.put(i, GL11.GL_SRC_COLOR);
			GL_SRC2_ALPHA.put(i, GL13.GL_CONSTANT);
			GL_OPERAND2_ALPHA.put(i, GL11.GL_SRC_ALPHA);
		}
	}

	public static void reset()
	{
		isAlias = false;
		isEntity = false;
		disableEffects = false;
		for (int i = 0; i < 8; i++)
		{
			useTex.put(i, GlStateManager.textureState[i].texture2DState.currentState ? 1 : 0);
			GL_TEXTURE_ENV_MODE.put(i, GL13.GL_COMBINE);
			GL_TEXTURE_ENV_COLOR.put(i * 4, 0);
			GL_TEXTURE_ENV_COLOR.put(i * 4 + 1, 0);
			GL_TEXTURE_ENV_COLOR.put(i * 4 + 2, 0);
			GL_TEXTURE_ENV_COLOR.put(i * 4 + 3, 0);
			GL_COMBINE_RGB.put(i, GL11.GL_MODULATE);
			GL_COMBINE_ALPHA.put(i, GL11.GL_MODULATE);
			GL_RGB_SCALE.put(i, 1f);
			GL_ALPHA_SCALE.put(i, 1f);
			GL_SRC0_RGB.put(i, GL11.GL_TEXTURE);
			GL_OPERAND0_RGB.put(i, GL11.GL_SRC_COLOR);
			GL_SRC0_ALPHA.put(i, GL11.GL_TEXTURE);
			GL_OPERAND0_ALPHA.put(i, GL11.GL_SRC_ALPHA);
			GL_SRC1_RGB.put(i, GL13.GL_PREVIOUS);
			GL_OPERAND1_RGB.put(i, GL11.GL_SRC_COLOR);
			GL_SRC1_ALPHA.put(i, GL13.GL_PREVIOUS);
			GL_OPERAND1_ALPHA.put(i, GL11.GL_SRC_ALPHA);
			GL_SRC2_RGB.put(i, GL13.GL_CONSTANT);
			GL_OPERAND2_RGB.put(i, GL11.GL_SRC_COLOR);
			GL_SRC2_ALPHA.put(i, GL13.GL_CONSTANT);
			GL_OPERAND2_ALPHA.put(i, GL11.GL_SRC_ALPHA);
		}
		GLSLHelper.uniform1i(Main.useNormals, GlStateManager.lightingState.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.useFog, GlStateManager.fogState.fog.currentState ? 1 : 0);
		GLSLHelper.uniform1f(Main.fogDensity, GlStateManager.fogState.density);
		GLSLHelper.uniform1f(Main.fogStart, GlStateManager.fogState.start);
		GLSLHelper.uniform1f(Main.fogEnd, GlStateManager.fogState.end);
		float dif = GlStateManager.fogState.end - GlStateManager.fogState.start;
		GLSLHelper.uniform1f(Main.fogScale, dif == 0 ? 0 : 1f / dif);
		GLSLHelper.uniform1i(Main.fogMode, GlStateManager.fogState.mode);
		GLSLHelper.uniform4f(Main.fogColor, fogColor[0] = 0, fogColor[1] = 0, fogColor[2] = 0, fogColor[3] = 1);
		GLSLHelper.uniform1i(Main.isAlias, 0);
		GLSLHelper.uniform1i(Main.isEntity, 0);
		GLSLHelper.uniform1(Main.useTex, useTex);
		GLSLHelper.uniform1i(Main.texGen_s, GlStateManager.texGenState.s.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_s, GlStateManager.texGenState.s.param);
		GLSLHelper.uniform1i(Main.texGen_t, GlStateManager.texGenState.t.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_t, GlStateManager.texGenState.t.param);
		GLSLHelper.uniform1i(Main.texGen_p, GlStateManager.texGenState.r.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_p, GlStateManager.texGenState.r.param);
		GLSLHelper.uniform1i(Main.texGen_q, GlStateManager.texGenState.q.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_q, GlStateManager.texGenState.q.param);
		GLSLHelper.uniform1(Main.GL_TEXTURE_ENV_MODE, GL_TEXTURE_ENV_MODE);
		GLSLHelper.uniform4(Main.GL_TEXTURE_ENV_COLOR, GL_TEXTURE_ENV_COLOR);
		GLSLHelper.uniform1(Main.GL_COMBINE_RGB, GL_COMBINE_RGB);
		GLSLHelper.uniform1(Main.GL_COMBINE_ALPHA, GL_COMBINE_ALPHA);
		GLSLHelper.uniform1(Main.GL_RGB_SCALE, GL_RGB_SCALE);
		GLSLHelper.uniform1(Main.GL_ALPHA_SCALE, GL_ALPHA_SCALE);
		GLSLHelper.uniform1(Main.GL_SRC0_RGB, GL_SRC0_RGB);
		GLSLHelper.uniform1(Main.GL_OPERAND0_RGB, GL_OPERAND0_RGB);
		GLSLHelper.uniform1(Main.GL_SRC0_ALPHA, GL_SRC0_ALPHA);
		GLSLHelper.uniform1(Main.GL_OPERAND0_ALPHA, GL_OPERAND0_ALPHA);
		GLSLHelper.uniform1(Main.GL_SRC1_RGB, GL_SRC1_RGB);
		GLSLHelper.uniform1(Main.GL_OPERAND1_RGB, GL_OPERAND1_RGB);
		GLSLHelper.uniform1(Main.GL_SRC1_ALPHA, GL_SRC1_ALPHA);
		GLSLHelper.uniform1(Main.GL_OPERAND1_ALPHA, GL_OPERAND1_ALPHA);
		GLSLHelper.uniform1(Main.GL_SRC2_RGB, GL_SRC2_RGB);
		GLSLHelper.uniform1(Main.GL_OPERAND2_RGB, GL_OPERAND2_RGB);
		GLSLHelper.uniform1(Main.GL_SRC2_ALPHA, GL_SRC2_ALPHA);
		GLSLHelper.uniform1(Main.GL_OPERAND2_ALPHA, GL_OPERAND2_ALPHA);
		GLSLHelper.uniform1i(Main.disableEffects, 0);
	}

	public static void set()
	{
		for (int i = 0; i < 8; i++) useTex.put(i, GlStateManager.textureState[i].texture2DState.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.useNormals, GlStateManager.lightingState.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.useFog, GlStateManager.fogState.fog.currentState ? 1 : 0);
		GLSLHelper.uniform1f(Main.fogDensity, GlStateManager.fogState.density);
		GLSLHelper.uniform1f(Main.fogStart, GlStateManager.fogState.start);
		GLSLHelper.uniform1f(Main.fogEnd, GlStateManager.fogState.end);
		float dif = GlStateManager.fogState.end - GlStateManager.fogState.start;
		GLSLHelper.uniform1f(Main.fogScale, dif == 0 ? 0 : 1f / dif);
		GLSLHelper.uniform1i(Main.fogMode, GlStateManager.fogState.mode);
		GLSLHelper.uniform4f(Main.fogColor, fogColor[0], fogColor[1], fogColor[2], fogColor[3]);
		GLSLHelper.uniform1i(Main.isAlias, isAlias ? 1 : 0);
		GLSLHelper.uniform1i(Main.isEntity, isEntity ? 1 : 0);
		GLSLHelper.uniform1(Main.useTex, useTex);
		GLSLHelper.uniform1i(Main.texGen_s, GlStateManager.texGenState.s.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_s, GlStateManager.texGenState.s.param);
		GLSLHelper.uniform1i(Main.texGen_t, GlStateManager.texGenState.t.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_t, GlStateManager.texGenState.t.param);
		GLSLHelper.uniform1i(Main.texGen_p, GlStateManager.texGenState.r.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_p, GlStateManager.texGenState.r.param);
		GLSLHelper.uniform1i(Main.texGen_q, GlStateManager.texGenState.q.textureGen.currentState ? 1 : 0);
		GLSLHelper.uniform1i(Main.texGenMode_q, GlStateManager.texGenState.q.param);
		GLSLHelper.uniform1(Main.GL_TEXTURE_ENV_MODE, GL_TEXTURE_ENV_MODE);
		GLSLHelper.uniform4(Main.GL_TEXTURE_ENV_COLOR, GL_TEXTURE_ENV_COLOR);
		GLSLHelper.uniform1(Main.GL_COMBINE_RGB, GL_COMBINE_RGB);
		GLSLHelper.uniform1(Main.GL_COMBINE_ALPHA, GL_COMBINE_ALPHA);
		GLSLHelper.uniform1(Main.GL_RGB_SCALE, GL_RGB_SCALE);
		GLSLHelper.uniform1(Main.GL_ALPHA_SCALE, GL_ALPHA_SCALE);
		GLSLHelper.uniform1(Main.GL_SRC0_RGB, GL_SRC0_RGB);
		GLSLHelper.uniform1(Main.GL_OPERAND0_RGB, GL_OPERAND0_RGB);
		GLSLHelper.uniform1(Main.GL_SRC0_ALPHA, GL_SRC0_ALPHA);
		GLSLHelper.uniform1(Main.GL_OPERAND0_ALPHA, GL_OPERAND0_ALPHA);
		GLSLHelper.uniform1(Main.GL_SRC1_RGB, GL_SRC1_RGB);
		GLSLHelper.uniform1(Main.GL_OPERAND1_RGB, GL_OPERAND1_RGB);
		GLSLHelper.uniform1(Main.GL_SRC1_ALPHA, GL_SRC1_ALPHA);
		GLSLHelper.uniform1(Main.GL_OPERAND1_ALPHA, GL_OPERAND1_ALPHA);
		GLSLHelper.uniform1(Main.GL_SRC2_RGB, GL_SRC2_RGB);
		GLSLHelper.uniform1(Main.GL_OPERAND2_RGB, GL_OPERAND2_RGB);
		GLSLHelper.uniform1(Main.GL_SRC2_ALPHA, GL_SRC2_ALPHA);
		GLSLHelper.uniform1(Main.GL_OPERAND2_ALPHA, GL_OPERAND2_ALPHA);
		GLSLHelper.uniform1i(Main.disableEffects, disableEffects ? 1 : 0);
	}

	public static int getTexSpace()
	{
		return GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) - GL13.GL_TEXTURE0;
	}

	public static boolean isTexture(int param)
	{
		return param == GL11.GL_TEXTURE || (param >= GL13.GL_TEXTURE0 && param <= GL13.GL_TEXTURE7);
	}

	public static void glEnable(int cap)
	{
		if (cap == GL11.GL_TEXTURE_2D)
		{
			int texSpace = getTexSpace();
			if (texSpace >= 0 && texSpace < 8)
			{
				useTex.put(texSpace, 1);
				if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.useTex, useTex);
			}
		}
		else if (ShaderRegistry.shadersActive)
		{
			if (cap == GL11.GL_LIGHTING)
			{
				GLSLHelper.uniform1i(Main.useNormals, 1);
			}
			else if (cap == GL11.GL_FOG)
			{
				GLSLHelper.uniform1i(Main.useFog, 1);
			}
			else if (cap == GL11.GL_TEXTURE_GEN_S)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_s, 1);
				}
			}
			else if (cap == GL11.GL_TEXTURE_GEN_T)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_t, 1);
				}
			}
			else if (cap == GL11.GL_TEXTURE_GEN_R)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_p, 1);
				}
			}
			else if (cap == GL11.GL_TEXTURE_GEN_Q)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_q, 1);
				}
			}
		}
		GL11.glEnable(cap);
	}

	public static void glDisable(int cap)
	{
		if (cap == GL11.GL_TEXTURE_2D)
		{
			int texSpace = getTexSpace();
			if (texSpace >= 0 && texSpace < 8)
			{
				useTex.put(texSpace, 0);
				if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.useTex, useTex);
			}
		}
		else if (ShaderRegistry.shadersActive)
		{
			if (cap == GL11.GL_LIGHTING)
			{
				GLSLHelper.uniform1i(Main.useNormals, 0);
			}
			else if (cap == GL11.GL_FOG)
			{
				GLSLHelper.uniform1i(Main.useFog, 0);
			}
			else if (cap == GL11.GL_TEXTURE_GEN_S)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_s, 0);
				}
			}
			else if (cap == GL11.GL_TEXTURE_GEN_T)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_t, 0);
				}
			}
			else if (cap == GL11.GL_TEXTURE_GEN_R)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_p, 0);
				}
			}
			else if (cap == GL11.GL_TEXTURE_GEN_Q)
			{
				if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
				{
					GLSLHelper.uniform1i(Main.texGen_q, 0);
				}
			}
		}
		GL11.glDisable(cap);
	}

	public static void bindTexture(TextureManager manager, ResourceLocation tex)
	{
		if (tex.equals(TextureMap.LOCATION_BLOCKS_TEXTURE))
    	{
			if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isAlias, 1);
    		isAlias = true;
    	}
    	else if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
    	{
    		if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isAlias, 0);
    		isAlias = false;
    	}
		manager.bindTexture(tex);
	}

	public static void glTexGeni(int coord, int pname, int param)
	{
		GL11.glTexGeni(coord, pname, param);
		if (ShaderRegistry.shadersActive && pname == GL11.GL_TEXTURE_GEN_MODE)
		{
			if (GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE) == GL13.GL_TEXTURE0)
			{
				if (coord == GL11.GL_S)      GLSLHelper.uniform1i(Main.texGenMode_s, param);
				else if (coord == GL11.GL_T) GLSLHelper.uniform1i(Main.texGenMode_t, param);
				else if (coord == GL11.GL_R) GLSLHelper.uniform1i(Main.texGenMode_p, param);
				else if (coord == GL11.GL_Q) GLSLHelper.uniform1i(Main.texGenMode_q, param);
				else return;
			}
		}
	}

	public static void glFogi(int pname, int param)
	{
		GL11.glFogi(pname, param);
		if (ShaderRegistry.shadersActive && pname == GL11.GL_FOG_MODE) GLSLHelper.uniform1i(Main.fogMode, param);
	}

	public static void glFogf(int pname, float param)
	{
		GL11.glFogf(pname, param);
		if (ShaderRegistry.shadersActive)
		{
			if (pname == GL11.GL_FOG_DENSITY) GLSLHelper.uniform1f(Main.fogDensity, param);
			else if (pname == GL11.GL_FOG_START)
			{
				GLSLHelper.uniform1f(Main.fogStart, param);
				float dif = GlStateManager.fogState.end - param;
				GLSLHelper.uniform1f(Main.fogScale, (dif == 0 ? 0 : 1f / dif));
			}
			else if (pname == GL11.GL_FOG_END)
			{
				GLSLHelper.uniform1f(Main.fogEnd, param);
				float dif = param - GlStateManager.fogState.start;
				GLSLHelper.uniform1f(Main.fogScale, (dif == 0 ? 0 : 1f / dif));
			}
		}
	}

	public static void glFog(int pname, FloatBuffer param)
	{
		GL11.glFog(pname, param);
		if (pname == GL11.GL_FOG_COLOR)
		{
			fogColor[0] = param.get(0);
			fogColor[1] = param.get(1);
			fogColor[2] = param.get(2);
			fogColor[3] = param.get(3);
			if (ShaderRegistry.shadersActive) GLSLHelper.uniform4f(Main.fogColor, fogColor[0], fogColor[1], fogColor[2], fogColor[3]);
		}
	}

	public static void glColorMask(boolean r, boolean g, boolean b, boolean a) //TODO
	{
		if (!(ShaderRegistry.shadersActive && (r ^ (g && b)))) GL11.glColorMask(r, g, b, a);
		else GL11.glColorMask(true, true, true, a);
	}

	public static void glTexEnv(int target, int pname, IntBuffer params)
	{
		GL11.glTexEnv(target, pname, params);
	}

	public static void glTexEnv(int target, int pname, FloatBuffer params)
	{
		GL11.glTexEnv(target, pname, params);
		if (target == GL11.GL_TEXTURE_ENV)
		{
			if (pname == GL11.GL_TEXTURE_ENV_COLOR)
			{
				int texSpace = getTexSpace();
				if (texSpace >= 0 && texSpace < 8)
				{
					GL_TEXTURE_ENV_COLOR.put(texSpace * 4, params.get(0));
					GL_TEXTURE_ENV_COLOR.put(texSpace * 4 + 1, params.get(1));
					GL_TEXTURE_ENV_COLOR.put(texSpace * 4 + 2, params.get(2));
					GL_TEXTURE_ENV_COLOR.put(texSpace * 4 + 3, params.get(3));
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform4(Main.GL_TEXTURE_ENV_COLOR, GL_TEXTURE_ENV_COLOR);
				}
			}
		}
	}

	public static void glTexEnvi(int target, int pname, int param)
	{
		GL11.glTexEnvi(target, pname, param);
		if (target == GL11.GL_TEXTURE_ENV)
		{
			int texSpace = getTexSpace();
			if (texSpace >= 0 && texSpace < 8)
			{
				if (isTexture(param)) param = GL11.GL_TEXTURE;
				switch (pname)
				{
				case GL11.GL_TEXTURE_ENV_MODE:
				{
					GL_TEXTURE_ENV_MODE.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_TEXTURE_ENV_MODE, GL_TEXTURE_ENV_MODE);
					break;
				}
				case GL13.GL_COMBINE_RGB:
				{
					GL_COMBINE_RGB.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_COMBINE_RGB, GL_COMBINE_RGB);
					break;
				}
				case GL13.GL_COMBINE_ALPHA:
				{
					GL_COMBINE_ALPHA.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_COMBINE_ALPHA, GL_COMBINE_ALPHA);
					break;
				}
				case GL13.GL_SOURCE0_RGB:
				{
					GL_SRC0_RGB.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_SRC0_RGB, GL_SRC0_RGB);
					break;
				}
				case GL13.GL_OPERAND0_RGB:
				{
					GL_OPERAND0_RGB.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_OPERAND0_RGB, GL_OPERAND0_RGB);
					break;
				}
				case GL13.GL_SOURCE0_ALPHA:
				{
					GL_SRC0_ALPHA.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_SRC0_ALPHA, GL_SRC0_ALPHA);
					break;
				}
				case GL13.GL_OPERAND0_ALPHA:
				{
					GL_OPERAND0_ALPHA.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_OPERAND0_ALPHA, GL_OPERAND0_ALPHA);
					break;
				}
				case GL13.GL_SOURCE1_RGB:
				{
					GL_SRC1_RGB.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_SRC1_RGB, GL_SRC1_RGB);
					break;
				}
				case GL13.GL_OPERAND1_RGB:
				{
					GL_OPERAND1_RGB.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_OPERAND1_RGB, GL_OPERAND1_RGB);
					break;
				}
				case GL13.GL_SOURCE1_ALPHA:
				{
					GL_SRC1_ALPHA.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_SRC1_ALPHA, GL_SRC1_ALPHA);
					break;
				}
				case GL13.GL_OPERAND1_ALPHA:
				{
					GL_OPERAND1_ALPHA.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_OPERAND1_ALPHA, GL_OPERAND1_ALPHA);
					break;
				}
				case GL13.GL_SOURCE2_RGB:
				{
					GL_SRC2_RGB.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_SRC2_RGB, GL_SRC2_RGB);
					break;
				}
				case GL13.GL_OPERAND2_RGB:
				{
					GL_OPERAND2_RGB.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_OPERAND2_RGB, GL_OPERAND2_RGB);
					break;
				}
				case GL13.GL_SOURCE2_ALPHA:
				{
					GL_SRC2_ALPHA.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_SRC2_ALPHA, GL_SRC2_ALPHA);
					break;
				}
				case GL13.GL_OPERAND2_ALPHA:
				{
					GL_OPERAND2_ALPHA.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_OPERAND2_ALPHA, GL_OPERAND2_ALPHA);
					break;
				}
				}
			}
		}
	}

	public static void glTexEnvf(int target, int pname, float param)
	{
		GL11.glTexEnvf(target, pname, param);
		if (target == GL11.GL_TEXTURE_ENV)
		{
			int texSpace = getTexSpace();
			if (texSpace >= 0 && texSpace < 8)
			{
				switch (pname)
				{
				case GL13.GL_RGB_SCALE:
				{
					GL_RGB_SCALE.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_RGB_SCALE, GL_RGB_SCALE);
					break;
				}
				case GL11.GL_ALPHA_SCALE:
				{
					GL_ALPHA_SCALE.put(texSpace, param);
					if (ShaderRegistry.shadersActive) GLSLHelper.uniform1(Main.GL_ALPHA_SCALE, GL_ALPHA_SCALE);
					break;
				}
				}
			}
		}
	}

	public static void setIsEntity(boolean flag)
	{
		if (isEntity ^ flag)
		{
			isEntity = flag;
			if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.isEntity, flag ? 1 : 0);
		}
	}

	public static void setDisableEffects(boolean flag)
	{
		if (disableEffects ^ flag)
		{
			disableEffects = flag;
			if (ShaderRegistry.shadersActive) GLSLHelper.uniform1i(Main.disableEffects, flag ? 1 : 0);
		}
	}
}