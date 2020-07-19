package extendedshaders.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/** helper functions mainly for working with GLSL,
 * 	but also some other misc functions you may find usefull.
 * @author FirEmerald
 */
public class GLSLHelper
{
	/** are shaders supported? **/
	public static final boolean SHADER_SUPPORTED;
	/** are framebuffers supported? **/
	public static final boolean FB_SUPPORTED;
	/** is framebuffer blitting supported? **/
	public static final boolean FB_BLIT_SUPPORTED;
	/** are floating-point textures supported? **/
	public static final boolean TEX_FLOAT_SUPPORTED;
	/** TRUE if using the compatibility profile **/
	public static final boolean ARBSHADERS;
	/** 0 for GL30, 1 for ARB framebuffers, 2 for extension framebuffers, or -1 for unsupported **/
	public static final int FBCOMPAT;
	/** is Extended Shaders supported? **/
	public static final boolean SUPPORTED;
	static
	{
		ContextCapabilities caps = GLContext.getCapabilities();
		SHADER_SUPPORTED = (ARBSHADERS = caps.OpenGL21) || (caps.GL_ARB_vertex_shader && caps.GL_ARB_fragment_shader && caps.GL_ARB_shader_objects);
		FB_SUPPORTED = caps.OpenGL30 || caps.GL_ARB_framebuffer_object || caps.GL_EXT_framebuffer_object;
		FB_BLIT_SUPPORTED = caps.OpenGL30 || caps.GL_ARB_framebuffer_object || caps.GL_EXT_framebuffer_blit;
		TEX_FLOAT_SUPPORTED = caps.OpenGL30 || caps.GL_ARB_texture_float || caps.GL_ATI_texture_float;
		FBCOMPAT = FB_SUPPORTED ? caps.OpenGL30 ? 0 : caps.GL_ARB_framebuffer_object ? 1 : 2 : -1;
		SUPPORTED = SHADER_SUPPORTED && FB_SUPPORTED && FB_BLIT_SUPPORTED && TEX_FLOAT_SUPPORTED;
	}
	
	/** creates a new shader program **/
	public static int createProgram()
	{
        return ARBSHADERS ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
	}
	/** gets the location of a uniform in the shader program **/
	public static int getUniformLocation(int program, CharSequence name)
	{
        return ARBSHADERS ? ARBShaderObjects.glGetUniformLocationARB(program, name) : GL20.glGetUniformLocation(program, name);
	}
	/** stores the value of the INTEGER uniform to the buffer. **/
	public static void getUniform(int program, int uniform, IntBuffer value)
	{
		if (ARBSHADERS) ARBShaderObjects.glGetUniformARB(program, uniform, value);
		else GL20.glGetUniform(program, uniform, value);
	}
	/** stores the value of the FLOAT uniform to the buffer. **/
	public static void getUniform(int program, int uniform, FloatBuffer value)
	{
		if (ARBSHADERS) ARBShaderObjects.glGetUniformARB(program, uniform, value);
		else GL20.glGetUniform(program, uniform, value);
	}
	/** creates a vertex shader **/
	public static int createVertShader()
	{
        return ARBSHADERS ? ARBShaderObjects.glCreateShaderObjectARB(GL20.GL_VERTEX_SHADER) : GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
	}
	/** creates a fragment shader **/
	public static int createFragShader()
	{
        return ARBSHADERS ? ARBShaderObjects.glCreateShaderObjectARB(GL20.GL_FRAGMENT_SHADER) : GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
	}
	/** load the string into the shader **/
	public static void shaderSource(int shader, CharSequence string)
	{
		if (ARBSHADERS) ARBShaderObjects.glShaderSourceARB(shader, string);
		else GL20.glShaderSource(shader, string);
	}
	/** compiles the shader **/
	public static void compileShader(int shader)
	{
        if (ARBSHADERS) ARBShaderObjects.glCompileShaderARB(shader);
        else GL20.glCompileShader(shader);
	}
	/** returns TRUE if the shader DID compile successfully **/
	public static boolean didShaderCompile(int shader)
	{
		return (ARBSHADERS ? ARBShaderObjects.glGetObjectParameteriARB(shader, GL20.GL_COMPILE_STATUS) : GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS)) == GL11.GL_TRUE;
	}
	/** deletes a shader **/
	public static void deleteShader(int shader)
	{
        if (ARBSHADERS) ARBShaderObjects.glDeleteObjectARB(shader);
        else GL20.glDeleteShader(shader);
	}
	/** gets the log for the shader **/
    public static String getShaderLog(int shader)
    {
    	return ARBSHADERS ? ARBShaderObjects.glGetInfoLogARB(shader, ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)) : GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
    }
	/** attaches the shader to the program **/
    public static void linkShader(int program, int shader)
    {
        if (ARBSHADERS) ARBShaderObjects.glAttachObjectARB(program, shader);
        else GL20.glAttachShader(program, shader);
    }
    /** finalizes the program after linking shaders **/
    public static void linkProgram(int program)
    {
        if (ARBSHADERS) ARBShaderObjects.glLinkProgramARB(program);
        else GL20.glLinkProgram(program);
    }
    /** returns TRUE if the shaders linked to the program successfully **/
	public static boolean didProgramLink(int program)
	{
        return (ARBSHADERS ? ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_LINK_STATUS) : GL20.glGetProgrami(program, GL20.GL_LINK_STATUS)) == GL11.GL_TRUE;
	}
	/** runs a validation check on the program to ensure nothing goes wrong **/
	public static void validateProgram(int program)
	{
		if (ARBSHADERS) ARBShaderObjects.glValidateProgramARB(program);
		else GL20.glValidateProgram(program);
	}
	/** returns TRUE if the program validated. **/
	public static boolean didProgramValidate(int program)
	{
		return (ARBSHADERS ? ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) : GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS)) == GL11.GL_TRUE;
	}
	/** gets the program log. **/
    public static String getProgramLog(int program)
    {
    	return ARBSHADERS ? ARBShaderObjects.glGetInfoLogARB(program, ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)) : GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH));
    }
    /** sets the active shader program **/
	public static void runProgram(int program)
	{
        if (ARBSHADERS) ARBShaderObjects.glUseProgramObjectARB(program);
        else GL20.glUseProgram(program);
	}
	/** deletes a program **/
	public static void deleteProgram(int program)
	{
        if (ARBSHADERS) ARBShaderObjects.glDeleteObjectARB(program);
        else GL20.glDeleteProgram(program);
	}
	/** sets a uniform integer type **/
	public static void uniform1i(int location, int val)
	{
    	if (ARBSHADERS) ARBShaderObjects.glUniform1iARB(location, val);
    	else GL20.glUniform1i(location, val);
	}
	/** sets a uniform float type **/
	public static void uniform1f(int location, float val)
	{
    	if (ARBSHADERS) ARBShaderObjects.glUniform1fARB(location, val);
    	else GL20.glUniform1f(location, val);
	}
	/** sets a uniform 2-int type **/
	public static void uniform2i(int location, int val1, int val2)
	{
    	if (ARBSHADERS) ARBShaderObjects.glUniform2iARB(location, val1, val2);
    	else GL20.glUniform2i(location, val1, val2);
	}
	/** sets a uniform 2-float type **/
	public static void uniform2f(int location, float val1, float val2)
	{
    	if (ARBSHADERS) ARBShaderObjects.glUniform2fARB(location, val1, val2);
    	else GL20.glUniform2f(location, val1, val2);
	}
	/** sets a uniform 3-int type **/
	public static void uniform3i(int location, int val1, int val2, int val3)
	{
    	if (ARBSHADERS) ARBShaderObjects.glUniform3iARB(location, val1, val2, val3);
    	else GL20.glUniform3i(location, val1, val2, val3);
	}
	/** sets a uniform 3-float type **/
	public static void uniform3f(int location, float val1, float val2, float val3)
	{
		if (ARBSHADERS) ARBShaderObjects.glUniform3fARB(location, val1, val2, val3);
		else GL20.glUniform3f(location, val1, val2, val3);
	}
	/** sets a uniform 4-int type **/
	public static void uniform4i(int location, int val1, int val2, int val3, int val4)
	{
    	if (ARBSHADERS) ARBShaderObjects.glUniform4iARB(location, val1, val2, val3, val4);
    	else GL20.glUniform4i(location, val1, val2, val3, val4);
	}
	/** sets a uniform 4-float type **/
	public static void uniform4f(int location, float val1, float val2, float val3, float val4)
	{
		if (ARBSHADERS) ARBShaderObjects.glUniform4fARB(location, val1, val2, val3, val4);
		else GL20.glUniform4f(location, val1, val2, val3, val4);
	}
	/** sets a uniform integer type array **/
	public static void uniform1(int location, IntBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform1ARB(location, val);
        else GL20.glUniform1(location, val);
	}
	/** sets a uniform float type array **/
	public static void uniform1(int location, FloatBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform1ARB(location, val);
        else GL20.glUniform1(location, val);
	}
	/** sets a uniform 2-integer type array **/
	public static void uniform2(int location, IntBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform2ARB(location, val);
        else GL20.glUniform2(location, val);
	}
	/** sets a uniform 2-float type array **/
	public static void uniform2(int location, FloatBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform2ARB(location, val);
        else GL20.glUniform2(location, val);
	}
	/** sets a uniform 3-integer type array **/
	public static void uniform3(int location, IntBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform3ARB(location, val);
        else GL20.glUniform3(location, val);
	}
	/** sets a uniform 3-float type array **/
	public static void uniform3(int location, FloatBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform3ARB(location, val);
        else GL20.glUniform3(location, val);
	}
	/** sets a uniform 4-integer type array **/
	public static void uniform4(int location, IntBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform4ARB(location, val);
        else GL20.glUniform4(location, val);
	}
	/** sets a uniform 4-float type array **/
	public static void uniform4(int location, FloatBuffer val)
	{
        if (ARBSHADERS) ARBShaderObjects.glUniform4ARB(location, val);
        else GL20.glUniform4(location, val);
	}
	/** sets a uniform mat2 **/
    public static void uniformMat2(int location, boolean transpose, FloatBuffer val)
    {
        if (ARBSHADERS) ARBShaderObjects.glUniformMatrix2ARB(location, transpose, val);
        else GL20.glUniformMatrix2(location, transpose, val);
    }
	/** sets a uniform mat3 **/
    public static void uniformMat3(int location, boolean transpose, FloatBuffer val)
    {
        if (ARBSHADERS) ARBShaderObjects.glUniformMatrix3ARB(location, transpose, val);
        else GL20.glUniformMatrix3(location, transpose, val);
    }
	/** sets a uniform mat4 **/
    public static void uniformMat4(int location, boolean transpose, FloatBuffer val)
    {
        if (ARBSHADERS) ARBShaderObjects.glUniformMatrix4ARB(location, transpose, val);
        else GL20.glUniformMatrix4(location, transpose, val);
    }
    /** binds a framebuffer **/
    public static void bindFramebuffer(int mode, int fb)
    {
        switch (FBCOMPAT)
        {
        case 0:
            GL30.glBindFramebuffer(mode, fb);
            break;
        case 1:
            ARBFramebufferObject.glBindFramebuffer(mode, fb);
            break;
        case 2:
            EXTFramebufferObject.glBindFramebufferEXT(mode, fb);
        }
    }
    /** blits the read framebuffer to the draw framebuffer **/
    public static void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter)
    {
        switch (FBCOMPAT)
        {
            case 0:
            	GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
                break;
            case 1:
            	ARBFramebufferObject.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
                break;
            case 2:
                EXTFramebufferBlit.glBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
        }
    }
    /**attaches a texture to a framebuffer target **/
    public static void framebufferTexture2D(int target, int attachment, int texTarget, int texture, int level)
    {
        switch (FBCOMPAT)
        {
            case 0:
                GL30.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
                break;
            case 1:
                ARBFramebufferObject.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
                break;
            case 2:
                EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, texTarget, texture, level);
        }
    }
    /** are framebuffers enabled? **/
    public static boolean framebuffersEnabled()
    {
    	return FB_SUPPORTED && OpenGlHelper.isFramebufferEnabled();
    }
    /** reads a text file as a single string of text **/
    public static String readFileAsString(ResourceLocation loc, IResourceManager manager) throws Exception
    {
    	InputStream in = manager.getResource(loc).getInputStream();
        StringBuilder source = new StringBuilder();
        Exception exception = null;
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            Exception innerExc= null;
            try
            {
                String line;
                while((line = reader.readLine()) != null) source.append(line).append('\n');
            }
            catch(Exception exc)
            {
                exception = exc;
            }
            finally
            {
                try
                {
                    reader.close();
                }
                catch(Exception exc)
                {
                    if(innerExc == null) innerExc = exc;
                    else exc.printStackTrace();
                }
            }
            if(innerExc != null) throw innerExc;
        }
        catch(Exception exc)
        {
            exception = exc;
        }
        finally
        {
            try
            {
                in.close();
            }
            catch(Exception exc)
            {
                if (exception == null) exception = exc;
                else exc.printStackTrace();
            }
            if (exception != null) throw exception;
        }
        return source.toString();
    }
    /** checks for OpenGL errors and prints them to the log **/
    public static void checkGLErrors(String location)
    {
        int i = GL11.glGetError();
        if (i != 0)
        {
            String s1 = GLU.gluErrorString(i);
            API.logger.error("########## GL ERROR ##########");
            API.logger.error("@ " + location);
            API.logger.error(i + ": " + s1);
            while ((i = GL11.glGetError()) != 0) API.logger.error(i + ": " + s1);
            //Thread.dumpStack();
        }
    }
}