package extendedshaders.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import com.google.common.eventbus.Subscribe;

import extendedshaders.api.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Main
{
	//TODO in-world processors
    private static boolean combineIf(StringBuilder s, boolean cond, boolean flag, String str)
    {
		if (cond)
		{
			if (flag) s.append(", ");
			s.append(str);
			return true;
		}
		return false;
    }
    
    @Subscribe
    public void handleModStateEvent(FMLEvent event)
    {
    	if (event instanceof FMLConstructionEvent)
    	{
        	if (!GLSLHelper.SUPPORTED)
        	{
        		StringBuilder s = new StringBuilder("Lacking required OpenGL features: ");
        		boolean flag = false;
        		flag = combineIf(s, !GLSLHelper.SHADER_SUPPORTED   , flag, "shaders");
        		flag = combineIf(s, !GLSLHelper.FB_SUPPORTED       , flag, "framebuffers");
        		flag = combineIf(s, !GLSLHelper.FB_BLIT_SUPPORTED  , flag, "framebuffer blit");
        		flag = combineIf(s, !GLSLHelper.TEX_FLOAT_SUPPORTED, flag, "floating-point textures");
        		FMLClientHandler.instance().haltGame("Your computer does not support extended shaders! You should uninstall Extended Shaders API", new Exception(s.toString()));
        	}
        	new ShaderPassthrough();
    	}
    	if (event instanceof FMLPreInitializationEvent)
    	{
    		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new CoreReloadListener());
    	}
    	else if (event instanceof FMLPostInitializationEvent)
    	{
        	MinecraftForge.EVENT_BUS.register(new EventHandler());
        	if (!Plugin.isOptifineEnabled()) try
        	{
    			Plugin.logger().info("Optifine not detected, adding shaders option to \"Video Settings\"");
            	EventHandler.shaders = EnumHelper.addEnum(GameSettings.Options.class, "SHADERS", new Class[] {String.class, Boolean.TYPE, Boolean.TYPE}, new Object[] {"Shaders", false, true});
        		for (Field f : GuiVideoSettings.class.getDeclaredFields())
        		{
        			if (f.getName().equals("VIDEO_OPTIONS") || f.getName().equals("field_146502_i"))
        			{
    					Field modfield = Field.class.getDeclaredField("modifiers");
    					modfield.setAccessible(true);
    					modfield.setInt(f, (f.getModifiers() & ~Modifier.FINAL & ~Modifier.PRIVATE) | Modifier.PUBLIC);
    					GameSettings.Options[] options = (GameSettings.Options[]) f.get(null);
    					final GameSettings.Options[] newOptions = new GameSettings.Options[options.length + 1];
    					System.arraycopy(options, 0, newOptions, 0, options.length);
    					newOptions[options.length] = EventHandler.shaders;
    					f.set(null, newOptions);
        			}
        		}
        	}
        	catch (Exception e)
        	{
        		FMLClientHandler.instance().haltGame("Failed to add shader option", e);
        	}
        	//ShaderRegistry.addShader(new ShaderSingle(null, null, new ResourceLocation("extendedshaders", "shaders/xray_define.txt"), new ResourceLocation("extendedshaders", "shaders/xray_code.txt"), 0));
        	//PostProcessorRegistry.addPostProcessor(new PostProcessor(null, new ResourceLocation("extendedshaders", "shaders/cell_shade.txt")));
    	}
    }

    protected static int[] currentShaders = new int[0];
    protected static int currentShader = 0;
    protected static int currentState = 0;
    protected static final HashMap<Shader, Integer> OFFSETS = new HashMap<Shader, Integer>();
    protected static int useNormals = -1;
    protected static int useFog = -1;
    protected static int fogDensity = -1;
    protected static int fogStart = -1;
    protected static int fogEnd = -1;
    protected static int fogScale = -1;
    protected static int fogMode = -1;
    protected static int fogColor = -1;
    protected static int isAlias = -1;
    protected static int isEntity = -1;
    protected static int ignoreEffects = -1;
    protected static int useTex = -1;
    protected static int texGen_s = -1;
    protected static int texGenMode_s = -1;
    protected static int texGen_t = -1;
    protected static int texGenMode_t = -1;
    protected static int texGen_p = -1;
    protected static int texGenMode_p = -1;
    protected static int texGen_q = -1;
    protected static int texGenMode_q = -1;
    protected static int GL_TEXTURE_ENV_MODE = -1;
    protected static int GL_TEXTURE_ENV_COLOR = -1;
    protected static int GL_COMBINE_RGB = -1;
    protected static int GL_COMBINE_ALPHA = -1;
	protected static int GL_RGB_SCALE = -1;
	protected static int GL_ALPHA_SCALE = -1;
	protected static int GL_SRC0_RGB = -1;
	protected static int GL_OPERAND0_RGB = -1;
	protected static int GL_SRC0_ALPHA = -1;
	protected static int GL_OPERAND0_ALPHA = -1;
	protected static int GL_SRC1_RGB = -1;
	protected static int GL_OPERAND1_RGB = -1;
	protected static int GL_SRC1_ALPHA = -1;
	protected static int GL_OPERAND1_ALPHA = -1;
	protected static int GL_SRC2_RGB = -1;
	protected static int GL_OPERAND2_RGB = -1;
	protected static int GL_SRC2_ALPHA = -1;
	protected static int GL_OPERAND2_ALPHA = -1;
    @SideOnly(Side.CLIENT)
    protected static Framebuffer copy;
    @SideOnly(Side.CLIENT)
    protected static Framebuffer cyan;
    @SideOnly(Side.CLIENT)
    protected static Framebuffer red;
    
    public static Framebuffer createIfInvalid(Framebuffer f, int w, int h, boolean useDepth)
    {
    	if (f == null || f.framebufferWidth != w || f.framebufferHeight != h)
    	{
    		if (f != null) f.deleteFramebuffer();
    		return new Framebuffer(w, h, useDepth);
    	}
    	else return f;
    }
    
    public static void clearPos(Framebuffer f)
    {
    	GLSLHelper.bindFramebuffer(GL30.GL_FRAMEBUFFER, f.framebufferObject);
    	IntBuffer buf = BufferUtils.createIntBuffer(1);
    	buf.put(GL30.GL_COLOR_ATTACHMENT1);
    	buf.flip();
    	GL20.glDrawBuffers(buf);
    	int width = f.framebufferWidth;
    	int height = f.framebufferHeight;
    	GlStateManager.depthMask(false);
    	GlStateManager.disableTexture2D();
    	GlStateManager.clearColor(0f, 0f, 0f, 1f);
    	GlStateManager.color(0f, 0f, 0f, 1f);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0, width, 0, height, 1, 3);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Tessellator t = Tessellator.getInstance();
		BufferBuilder b = t.getBuffer();
		b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		b.pos(0, 0, 2).tex(0, 0).endVertex();
		b.pos(width, 0, 2).tex(1, 0).endVertex();
		b.pos(width, height, 2).tex(1, 1).endVertex();
		b.pos(0, height, 2).tex(0, 1).endVertex();
		t.draw();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
    	GlStateManager.depthMask(true);
    	GlStateManager.enableTexture2D();
		buf = BufferUtils.createIntBuffer(2);
		buf.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT);
		buf.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT);
		buf.flip();
		GL20.glDrawBuffers(buf);
    }
    
    public static void runShaders(float partialTicks)
    {
    	ShaderRegistry.rendering = true;
    	if (ShaderRegistry.hasChanged)
    	{
    		createShaders();
    		ShaderRegistry.hasChanged = false;
    	}
    	if (Enabled.isEnabled())
    	{
    		runShader();
    		if (ShaderRegistry.shadersActive)
    		{
        		Bypass.reset();
    			for (Shader data : ShaderRegistry.getShaders())
    			{
    				int state = ShaderRegistry.getShaderState(data);
    				int states = data.getNumStates();
    				for (int i = 0; i < states; i++)
    				{
        				if (i == state) data.onRenderStart(currentShader, i);
        				else data.onRenderStartInactive(currentShader, i);
    				}
    			}
        		if (OpenGlHelper.isFramebufferEnabled())
        		{
         	       	Minecraft mc = Minecraft.getMinecraft();
        			Framebuffer f = mc.getFramebuffer();
         	       	copy = createIfInvalid(copy, f.framebufferWidth, f.framebufferHeight, true);
         	       	cyan = createIfInvalid(cyan, f.framebufferWidth, f.framebufferHeight, true);
         	       	red = createIfInvalid(red, f.framebufferWidth, f.framebufferHeight, true);
            		clearPos(f);
        		}
            	MinecraftForge.EVENT_BUS.post(new ShaderEvent.Start(partialTicks));
     	       	GLSLHelper.checkGLErrors("Pre-render");
    		}
    	}
    }
    
    public static void copyFramebuffers(Framebuffer src, Framebuffer des, int... modes)
    {
		GLSLHelper.bindFramebuffer(GL30.GL_FRAMEBUFFER, src.framebufferObject);
		GLSLHelper.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, des.framebufferObject);
		boolean useDepth = src.useDepth && des.useDepth;
		for (int i = 0; i < modes.length; i++)
		{
			int mode = modes[i];
			GL11.glReadBuffer(mode);
			GL11.glDrawBuffer(mode);
			GLSLHelper.blitFramebuffer(0, 0, src.framebufferWidth, src.framebufferHeight, 0, 0, des.framebufferWidth, des.framebufferHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		}
		if (src.useDepth && des.useDepth) GLSLHelper.blitFramebuffer(0, 0, src.framebufferWidth, src.framebufferHeight, 0, 0, des.framebufferWidth, des.framebufferHeight, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
    }

    public static void stopShaders(float partialTicks)
    {
    	ShaderRegistry.rendering = false;
    	if (ShaderRegistry.shadersActive)
    	{
 	       	GLSLHelper.checkGLErrors("Render");
        	MinecraftForge.EVENT_BUS.post(new ShaderEvent.Stop(partialTicks));
    		GLSLHelper.runProgram(0);
    		ShaderRegistry.shadersActive = false;
 	       	GLSLHelper.checkGLErrors("Post-render");
 	       	//Bypass.printTexState();
			Minecraft mc = Minecraft.getMinecraft();
        	PostProcessor[] posts = PostProcessorRegistry.getPostProcessors();
 	       	Tessellator t = Tessellator.getInstance();
 	       	BufferBuilder b = t.getBuffer();
 	       	int width = mc.displayWidth;
 	       	int height = mc.displayHeight;
 	       	GlStateManager.matrixMode(GL11.GL_PROJECTION);
 	       	GlStateManager.pushMatrix();
 	       	GlStateManager.loadIdentity();
 	       	GlStateManager.ortho(0, width, 0, height, 1000, 3000);
 	       	GlStateManager.matrixMode(GL11.GL_MODELVIEW);
 	       	GlStateManager.pushMatrix();
 	       	GlStateManager.loadIdentity();
 	       	GlStateManager.translate(0, 0, -2000);
 	       	GlStateManager.color(1f, 1f, 1f, 1f);
 	       	GlStateManager.enableBlend();
 	       	Framebuffer f = mc.getFramebuffer();
 	       	GLSLHelper.runProgram(0);
        	if (posts.length > 0 && GLSLHelper.framebuffersEnabled())
        	{
        		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
 	       		for (PostProcessor post : posts)
 	       		{
 	       			if (post.program > 0)
 	       			{
 	       				if (MinecraftForge.EVENT_BUS.post(new PostProcessorEvent.Bind(partialTicks, post))) continue;
 	       				GLSLHelper.runProgram(post.program);
 	       				GLSLHelper.uniform1f(post.dx, 1f / width);
 	       				GLSLHelper.uniform1f(post.dy, 1f / height);
 	       				GLSLHelper.uniform1i(post.eye, mc.gameSettings.anaglyph ? EntityRenderer.anaglyphField : -1);
 	       				post.onBind(partialTicks);
 	       				int iterations = post.getIterations();
 	       				for (int i = 0; i < iterations; i++)
 	       				{
 	 	       				if (MinecraftForge.EVENT_BUS.post(new PostProcessorEvent.Start(partialTicks, post, i))) continue;
 	 	       				GLSLHelper.bindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
 	 	       				copy.framebufferClear();
 	 	        	       	clearPos(copy);
 	 	       				copyFramebuffers(f, copy, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1);
 	 	       				GLSLHelper.bindFramebuffer(GL30.GL_FRAMEBUFFER, f.framebufferObject);
 	 	       				copy.bindFramebufferTexture();
 	 	       				b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
 	 	       				b.pos(0, 0, 1000).tex(0, 0).endVertex();
 	 	       				b.pos(width, 0, 1000).tex(1, 0).endVertex();
 	 	       				b.pos(width, height, 1000).tex(1, 1).endVertex();
 	 	       				b.pos(0, height, 1000).tex(0, 1).endVertex();
 	 	       				t.draw();
 	 	       				MinecraftForge.EVENT_BUS.post(new PostProcessorEvent.Stop(partialTicks, post, i));
 	       				}
 	       			}
 	       		}
 	       		GLSLHelper.runProgram(0);
        		GLSLHelper.checkGLErrors("Post Processing");
        	}
        	if (mc.gameSettings.anaglyph)
        	{
        		copyFramebuffers(f, EntityRenderer.anaglyphField == 0 ? cyan : red, GL30.GL_COLOR_ATTACHMENT0);
        		f.framebufferClear();
        		clearPos(f);
        		GLSLHelper.bindFramebuffer(GL30.GL_FRAMEBUFFER, f.framebufferObject);
        	}
			IntBuffer buf = BufferUtils.createIntBuffer(1);
			buf.put(GL30.GL_COLOR_ATTACHMENT0);
			buf.flip();
			GL20.glDrawBuffers(buf);
			GlStateManager.popMatrix();
 	       	GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.popMatrix();
 	       	GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    	}
    }
    
    public static void unbind()
    {
    	GLSLHelper.runProgram(0);
		ShaderRegistry.shadersActive = false;
    }
    
    public static void rebind()
    {
    	if (Enabled.isEnabled())
    	{
        	int shader = currentShaders[currentState];
        	if (shader > 0) //shader valid, run
        	{
        		GLSLHelper.runProgram(shader);
        		if (shader != currentShader)
        		{
        			currentShader = shader;
            		GLSLHelper.runProgram(currentShaders[currentState] = currentShader);
            		getUniforms(currentShader);
        		}
        		ShaderRegistry.shadersActive = true;
        		Bypass.reset();
        	}
    	}
    }
    
    protected static int anaglyphShader = -1;
    protected static int anaglyphCyan, anaglyphRed;
    
    public static void endRender()
    {
    	Minecraft mc = Minecraft.getMinecraft();
    	if (mc.gameSettings.anaglyph)
    	{
    		Framebuffer f = mc.getFramebuffer();
    		//saveBuffer(f);
    		//saveBuffer(cyan);
    		//saveBuffer(red);
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0, f.framebufferWidth, 0, f.framebufferHeight, -1, 1);
 	       	GlStateManager.matrixMode(GL11.GL_MODELVIEW);
 	       	GlStateManager.pushMatrix();
 	       	GlStateManager.loadIdentity();
    		GLSLHelper.runProgram(anaglyphShader);
    		GLSLHelper.uniform1i(anaglyphCyan, 0);
    		GLSLHelper.uniform1i(anaglyphRed, 1);
    		GlStateManager.bindTexture(cyan.framebufferTexture);
    		OpenGlHelper.setActiveTexture(GL13.GL_TEXTURE1);
    		GlStateManager.bindTexture(red.framebufferTexture);
    		OpenGlHelper.setActiveTexture(GL13.GL_TEXTURE0);
 	       	Tessellator t = Tessellator.getInstance();
 	       	BufferBuilder b = t.getBuffer();
 	       	b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
 	       	b.pos(0, 0, 0).tex(0, 0);
 	       	b.pos(f.framebufferWidth, 0, 0).tex(1, 0);
 	       	b.pos(f.framebufferWidth, f.framebufferHeight, 0).tex(1, 1);
 	       	b.pos(0, f.framebufferHeight, 0).tex(0, 1);
    		t.draw();
    		GLSLHelper.runProgram(0);
    		GlStateManager.popMatrix();
 	       	GlStateManager.matrixMode(GL11.GL_PROJECTION);
    		GlStateManager.popMatrix();
 	       	GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    	}
    }
    /*
    public static void saveBuffer(Framebuffer f)
    {
    	try
    	{
			saveImage(f.framebufferTexture, f.framebufferTextureWidth, f.framebufferTextureHeight);
		} catch (IOException e)
    	{
			e.printStackTrace();
		}
    }

	public static void saveImage(int tex, int w, int h) throws IOException
	{
		int k = w * h;
		IntBuffer pixelBuffer = BufferUtils.createIntBuffer(k);
		int[] pixelValues = new int[k];
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		pixelBuffer.clear();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
		pixelBuffer.get(pixelValues);
		TextureUtil.func_147953_a(pixelValues, w, h);
		BufferedImage bufferedimage = new BufferedImage(w, h, 1);
		for (int i1 = 0; i1 < h; ++i1) for (int j1 = 0; j1 < w; ++j1) bufferedimage.setRGB(j1, i1, pixelValues[i1 * w + j1]);
		File file3 = new File(Minecraft.getMinecraft().mcDataDir, "test" + tex + ".png");
		ImageIO.write(bufferedimage, "png", file3);
	}
    */
    public static boolean skipSky(float partialTicks)
    {
    	return MinecraftForge.EVENT_BUS.post(new ShaderEvent.RenderSky(partialTicks));
    }
    
    public static int deleteFbTex(int tex)
    {
 	   if (tex != -1) TextureUtil.deleteTexture(tex);
 	   return -1;
    }
    
    private static void getUniforms(int program)
    {
		useNormals = GLSLHelper.getUniformLocation(program, "useNormals");
		useFog = GLSLHelper.getUniformLocation(program, "useFog");
		fogDensity = GLSLHelper.getUniformLocation(program, "fogDensity");
		fogStart = GLSLHelper.getUniformLocation(program, "fogStart");
		fogEnd = GLSLHelper.getUniformLocation(program, "fogEnd");
		fogScale = GLSLHelper.getUniformLocation(program, "fogScale");
		fogMode = GLSLHelper.getUniformLocation(program, "fogMode");
		fogColor = GLSLHelper.getUniformLocation(program, "fogColor");
		isAlias = GLSLHelper.getUniformLocation(program, "isAlias");
		isEntity = GLSLHelper.getUniformLocation(program, "isEntity");
		useTex = GLSLHelper.getUniformLocation(program, "useTex");
		{
			int texSpace = GLSLHelper.getUniformLocation(program, "tex");
			GLSLHelper.uniform1(texSpace, (IntBuffer) BufferUtils.createIntBuffer(8).put(new int[] {0, 1, 2, 3, 4, 5, 6, 7}).flip());
		}
		texGen_s = GLSLHelper.getUniformLocation(program, "texGen_s");
		texGenMode_s = GLSLHelper.getUniformLocation(program, "texGenMode_s");
		texGen_t = GLSLHelper.getUniformLocation(program, "texGen_t");
		texGenMode_t = GLSLHelper.getUniformLocation(program, "texGenMode_t");
		texGen_p = GLSLHelper.getUniformLocation(program, "texGen_p");
		texGenMode_p = GLSLHelper.getUniformLocation(program, "texGenMode_p");
		texGen_q = GLSLHelper.getUniformLocation(program, "texGen_q");
		texGenMode_q = GLSLHelper.getUniformLocation(program, "texGenMode_q");
		
	    GL_TEXTURE_ENV_MODE = GLSLHelper.getUniformLocation(program, "OGL_TEXTURE_ENV_MODE");
	    GL_TEXTURE_ENV_COLOR = GLSLHelper.getUniformLocation(program, "OGL_TEXTURE_ENV_COLOR");
	    GL_COMBINE_RGB = GLSLHelper.getUniformLocation(program, "OGL_COMBINE_RGB");
	    GL_COMBINE_ALPHA = GLSLHelper.getUniformLocation(program, "OGL_COMBINE_ALPHA");
		GL_RGB_SCALE = GLSLHelper.getUniformLocation(program, "OGL_RGB_SCALE");
		GL_ALPHA_SCALE = GLSLHelper.getUniformLocation(program, "OGL_ALPHA_SCALE");
		GL_SRC0_RGB = GLSLHelper.getUniformLocation(program, "OGL_SRC0_RGB");
		GL_OPERAND0_RGB = GLSLHelper.getUniformLocation(program, "OGL_OPERAND0_RGB");
		GL_SRC0_ALPHA = GLSLHelper.getUniformLocation(program, "OGL_SRC0_ALPHA");
		GL_OPERAND0_ALPHA = GLSLHelper.getUniformLocation(program, "OGL_OPERAND0_ALPHA");
		GL_SRC1_RGB = GLSLHelper.getUniformLocation(program, "OGL_SRC1_RGB");
		GL_OPERAND1_RGB = GLSLHelper.getUniformLocation(program, "OGL_OPERAND1_RGB");
		GL_SRC1_ALPHA = GLSLHelper.getUniformLocation(program, "OGL_SRC1_ALPHA");
		GL_OPERAND1_ALPHA = GLSLHelper.getUniformLocation(program, "OGL_OPERAND1_ALPHA");
		GL_SRC2_RGB = GLSLHelper.getUniformLocation(program, "OGL_SRC2_RGB");
		GL_OPERAND2_RGB = GLSLHelper.getUniformLocation(program, "OGL_OPERAND2_RGB");
		GL_SRC2_ALPHA = GLSLHelper.getUniformLocation(program, "OGL_SRC2_ALPHA");
		GL_OPERAND2_ALPHA = GLSLHelper.getUniformLocation(program, "OGL_OPERAND2_ALPHA");
		
		for (Shader data : ShaderRegistry.getShaders()) data.getUniforms(program);
		GLSLHelper.checkGLErrors("shader uniforms");
    }
    
    public static void runShader()
    {
    	int newState = 0;
    	for (Map.Entry<Shader, Integer> entry : OFFSETS.entrySet()) newState += ShaderRegistry.getShaderState(entry.getKey()) * entry.getValue().intValue();
    	if (newState != currentState)
    	{
    		Plugin.logger().debug("switching from state " + currentState + " to state " + newState);
    		currentState = newState;
    	}
    	int shader = currentShaders[currentState];
    	
    	if (shader > 0) //shader valid, run
    	{
    		GLSLHelper.runProgram(shader);
    		if (shader != currentShader)
    		{
    			currentShader = shader;
        		GLSLHelper.runProgram(currentShaders[currentState] = currentShader);
        		getUniforms(currentShader);
    		}
    		ShaderRegistry.shadersActive = true;
    	}
    	else if (shader < 0) //shader invalid, turn off shaders
    	{
    		GLSLHelper.runProgram(currentShader = 0);
    		ShaderRegistry.shadersActive = false;
    	}
    	else //shader not created, create and run shader
    	{
        	Shader[] datas = ShaderRegistry.getShaders();
    		String vertTextUniform = Shaders.shaderUniform;
    		String vertText = Shaders.shaderVert;
    		String fragTextUniform = Shaders.shaderUniform;
    		String fragText = Shaders.shaderFrag;
    		for (Shader data : datas)
    		{
    			int state = ShaderRegistry.getShaderState(data);
    			vertTextUniform += data.getUniformTextVertex(state);
    			vertText += data.getShaderTextVertex(state);
    			fragTextUniform += data.getUniformTextFragment(state);
    			fragText += data.getShaderTextFragment(state);
    		}
    		int vertShader = GLSLHelper.createVertShader();
    		if (vertShader <= 0)
    		{
    			Plugin.logger().error("could not create vertex shader");
    			currentShaders[currentState] = -1;
        		ShaderRegistry.shadersActive = false;
        		GLSLHelper.runProgram(currentShader = 0);
    			return;
    		}
    		String vertCode = vertTextUniform + vertText + "}";
    		Plugin.logger().debug("Vertex Shader:\n" + vertCode);
    		GLSLHelper.shaderSource(vertShader, vertCode);
    		GLSLHelper.compileShader(vertShader);
    		if (!GLSLHelper.didShaderCompile(vertShader))
    		{
    			Plugin.logger().error("Failed to compile vertex shader: " + GLSLHelper.getShaderLog(vertShader));
    			Plugin.logger().error(vertCode);
    			GLSLHelper.deleteShader(vertShader);
    			currentShaders[currentState] = -1;
        		ShaderRegistry.shadersActive = false;
        		GLSLHelper.runProgram(currentShader = 0);
    			return;
    		}
    		GLSLHelper.checkGLErrors("vert create");
    		int fragShader = GLSLHelper.createFragShader();
    		if (fragShader <= 0)
    		{
    			Plugin.logger().error("could not create fragment shader");
    			currentShaders[currentState] = -1;
        		ShaderRegistry.shadersActive = false;
        		GLSLHelper.runProgram(currentShader = 0);
    			return;
    		}
    		String fragCode = fragTextUniform + fragText + "}";
    		Plugin.logger().debug("Fragment Shader:\n" + fragCode);
    		GLSLHelper.shaderSource(fragShader, fragCode);
    		GLSLHelper.compileShader(fragShader);
    		if (!GLSLHelper.didShaderCompile(fragShader))
    		{
    			Plugin.logger().error("Failed to compile fragment shader: " + GLSLHelper.getShaderLog(fragShader));
    			Plugin.logger().error(fragCode);
    			GLSLHelper.deleteShader(vertShader);
    			GLSLHelper.deleteShader(fragShader);
    			currentShaders[currentState] = -1;
        		ShaderRegistry.shadersActive = false;
        		GLSLHelper.runProgram(currentShader = 0);
    			return;
    		}
    		GLSLHelper.checkGLErrors("frag create");
        	currentShader = GLSLHelper.createProgram();
    		if (currentShader <= 0)
    		{
    			Plugin.logger().error("Failed to create shader!");
    			GLSLHelper.deleteShader(vertShader);
    			GLSLHelper.deleteShader(fragShader);
    			currentShaders[currentState] = -1;
        		ShaderRegistry.shadersActive = false;
        		GLSLHelper.runProgram(currentShader = 0);
    			return;
    		}
    		GLSLHelper.linkShader(currentShader, vertShader);
    		GLSLHelper.checkGLErrors("vert link");
    		GLSLHelper.linkShader(currentShader, fragShader);
    		GLSLHelper.checkGLErrors("frag link");
    		GLSLHelper.linkProgram(currentShader);
    		if (!GLSLHelper.didProgramLink(currentShader))
    		{
    			Plugin.logger().error("Failed to link shaders: " + GLSLHelper.getProgramLog(currentShader));
    			Plugin.logger().error(vertCode);
    			Plugin.logger().error(fragCode);
    			GLSLHelper.deleteShader(vertShader);
    			GLSLHelper.deleteShader(fragShader);
    			GLSLHelper.deleteProgram(currentShader);
    			currentShaders[currentState] = -1;
        		ShaderRegistry.shadersActive = false;
        		GLSLHelper.runProgram(currentShader = 0);
    			return;
    		}
    		GLSLHelper.checkGLErrors("shader link");
    		GLSLHelper.validateProgram(currentShader);
    		if (!GLSLHelper.didProgramValidate(currentShader))
    		{
    			Plugin.logger().error("Failed to validate program: " + GLSLHelper.getProgramLog(currentShader));
    			Plugin.logger().error(vertCode);
    			Plugin.logger().error(fragCode);
    			GLSLHelper.deleteProgram(currentShader);
    			currentShaders[currentState] = -1;
        		ShaderRegistry.shadersActive = false;
        		GLSLHelper.runProgram(currentShader = 0);
    			return;
    		}
    		else
    		{
        		Plugin.logger().debug("vert shader: \n" + vertCode);
        		Plugin.logger().debug("frag shader: \n" + fragCode);
    		}
    		GLSLHelper.checkGLErrors("shader validate");
    		GLSLHelper.runProgram(currentShaders[currentState] = currentShader);
    		getUniforms(currentShader);
    		ShaderRegistry.shadersActive = true;
    	}
    }
    
    protected static void createShaders()
    {
    	currentShader = 0;
    	if (currentShaders.length > 0) for (int shader : currentShaders) if (shader > 0) GLSLHelper.deleteProgram(shader);
    	OFFSETS.clear();
    	int numShaders = 1;
    	Shader[] datas = ShaderRegistry.getShaders();
    	for (Shader data : datas)
    	{
    		OFFSETS.put(data, Integer.valueOf(numShaders));
    		numShaders *= data.getNumStates();
    	}
    	Plugin.logger().info("Creating shaders for " + numShaders + " different possible shader state(s).");
    	currentShaders = new int[numShaders];
    }
    
    protected static int createShader(String vert, String frag)
    {
		int vertShader = GLSLHelper.createVertShader();
		if (vertShader <= 0)
		{
			Plugin.logger().error("could not create vertex shader");
			return 0;
		}
		GLSLHelper.shaderSource(vertShader, vert);
		GLSLHelper.compileShader(vertShader);
		if (!GLSLHelper.didShaderCompile(vertShader))
		{
			Plugin.logger().error("Failed to compile vertex shader: " + GLSLHelper.getShaderLog(vertShader));
			Plugin.logger().error(vert);
			GLSLHelper.deleteShader(vertShader);
			return 0;
		}
		int fragShader = GLSLHelper.createFragShader();
		if (fragShader <= 0)
		{
			Plugin.logger().error("could not create fragment shader");
			GLSLHelper.deleteShader(vertShader);
			return 0;
		}
		GLSLHelper.shaderSource(fragShader, frag);
		GLSLHelper.compileShader(fragShader);
		if (!GLSLHelper.didShaderCompile(fragShader))
		{
			Plugin.logger().error("Failed to compile fragment shader: " + GLSLHelper.getShaderLog(fragShader));
			Plugin.logger().error(frag);
			GLSLHelper.deleteShader(vertShader);
			GLSLHelper.deleteShader(fragShader);
			return 0;
		}
		int shader = GLSLHelper.createProgram();
		if (shader <= 0)
		{
			Plugin.logger().error("Failed to create shader!");
			GLSLHelper.deleteShader(vertShader);
			GLSLHelper.deleteShader(fragShader);
			return 0;
		}
		GLSLHelper.linkShader(shader, vertShader);
		GLSLHelper.linkShader(shader, fragShader);
		GLSLHelper.linkProgram(shader);
		if (!GLSLHelper.didProgramLink(shader))
		{
			Plugin.logger().error("Failed to link shaders: " + GLSLHelper.getProgramLog(shader));
			Plugin.logger().error(vert);
			Plugin.logger().error(frag);
			GLSLHelper.deleteShader(vertShader);
			GLSLHelper.deleteShader(fragShader);
			GLSLHelper.deleteProgram(shader);
			return 0;
		}
		GLSLHelper.validateProgram(shader);
		if (!GLSLHelper.didProgramValidate(shader))
		{
			Plugin.logger().error("Failed to validate program: " + GLSLHelper.getProgramLog(shader));
			Plugin.logger().error(vert);
			Plugin.logger().error(frag);
			GLSLHelper.deleteShader(vertShader);
			GLSLHelper.deleteShader(fragShader);
			GLSLHelper.deleteProgram(shader);
			return 0;
		}
		return shader;
    }
}