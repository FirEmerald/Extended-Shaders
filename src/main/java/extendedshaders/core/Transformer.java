package extendedshaders.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer, Opcodes
{
	public static final String renderWorld = 				Shaders.DEOB ? "renderWorld" : 				"func_78471_a";
	public static final String renderWorldPass = 			Shaders.DEOB ? "renderWorldPass" : 			"func_175068_a";
	public static final String clear = 						Shaders.DEOB ? "clear" :					"func_179086_m";
	public static final String renderEntities = 			Shaders.DEOB ? "renderEntities" : 			"func_180446_a";
	public static final String framebufferClear = 			Shaders.DEOB ? "framebufferClear" :			"func_147614_f";
	public static final String bindFramebuffer = 			Shaders.DEOB ? "bindFramebuffer" :			"func_147610_a";
	public static final String setOptionValue = 			Shaders.DEOB ? "setOptionValue" : 			"func_74306_a";
	public static final String getOptionOrdinalValue =	 	Shaders.DEOB ? "getOptionOrdinalValue" : 	"func_74308_b";
	public static final String loadOptions = 				Shaders.DEOB ? "loadOptions" : 				"func_74300_a";
	public static final String saveOptions = 				Shaders.DEOB ? "saveOptions" : 				"func_74303_b";
	public static final String deleteFramebuffer = 			Shaders.DEOB ? "deleteFramebuffer" : 		"func_147608_a";
	public static final String unbindFramebuffer = 			Shaders.DEOB ? "unbindFramebuffer" : 		"func_147609_e";
	public static final String createFramebuffer = 			Shaders.DEOB ? "createFramebuffer" : 		"func_147605_b";
	public static final String framebufferTexture = 		Shaders.DEOB ? "framebufferTexture" : 		"field_147617_g";
	public static final String glGenTextures = 				Shaders.DEOB ? "glGenTextures" : 			"func_110996_a";
	public static final String framebufferTextureWidth =  	Shaders.DEOB ? "framebufferTextureWidth" : 	"field_147622_a";
	public static final String framebufferTextureHeight = 	Shaders.DEOB ? "framebufferTextureHeight" : "field_147620_b";
	public static final String setFramebufferFilter = 		Shaders.DEOB ? "setFramebufferFilter" : 	"func_147607_a";
	public static final String bindFramebufferTexture = 	Shaders.DEOB ? "bindFramebufferTexture" : 	"func_147612_c";
	public static final String setActiveTexture = 			Shaders.DEOB ? "setActiveTexture" : 		"func_77473_a";
	public static final String unbindFramebufferTexture = 	Shaders.DEOB ? "unbindFramebufferTexture" : "func_147606_d";
	public static final String glFramebufferTexture2D = 	Shaders.DEOB ? "glFramebufferTexture2D" : 	"func_153188_a";
	public static final String bindTexture = 				Shaders.DEOB ? "bindTexture" : 				"func_179144_i";
	public static final String glTexImage2D = 				Shaders.DEOB ? "glTexImage2D" : 			"func_187419_a";
	public static final String glTexParameteri = 			Shaders.DEOB ? "glTexParameteri" : 			"func_187421_b";
	public static final String bindTexture2 = 				Shaders.DEOB ? "bindTexture" : 				"func_110577_a";
	public static final String renderItemInFirstPerson = 	Shaders.DEOB ? "renderItemInFirstPerson" : 	"renderItemInFirstPerson";
	
	@Override
	public byte[] transform(String name, String tName, byte[] basicClass)
	{
		if (tName.equals("net.minecraft.client.renderer.EntityRenderer")) basicClass = transformEntityRenderer(basicClass);
		else if (tName.equals("net.minecraft.client.renderer.RenderGlobal")) basicClass = transformRenderGlobal(basicClass);
		else if (tName.equals("net.minecraft.client.settings.GameSettings")) basicClass = transformGameSettings(basicClass);
		else if (tName.equals("net.minecraft.client.shader.Framebuffer")) basicClass = transformFramebuffer(basicClass);
		else if (tName.equals("net.minecraft.client.renderer.ItemRenderer")) basicClass = transformItemRenderer(basicClass);
		else if (tName.equals("extendedshaders.core.FramebufferUtil")) basicClass = transformFramebufferUtil(basicClass);
		if (!tName.equals("extendedshaders.core.Bypass")) basicClass = bypass(name, basicClass);
		//if (tName.equals("net.minecraft.client.shader.Framebuffer")) saveClass(tName, basicClass);
		//if (tName.equals("net.minecraft.client.renderer.EntityRenderer")) saveClass(tName, basicClass);
		//else if (tName.equals("net.minecraft.client.renderer.RenderGlobal")) saveClass(tName, basicClass);
		//else if (tName.equals("net.minecraft.client.settings.GameSettings")) saveClass(tName, basicClass);
		//else if (tName.equals("net.minecraft.client.shader.Framebuffer")) saveClass(tName, basicClass);
		//else if (tName.equals("net.minecraft.client.renderer.ItemRenderer")) saveClass(tName, basicClass);
		//saveClass(tName, basicClass);
		return basicClass;
	}
	
	/** /
	public void saveClass(String name, byte[] basicClass)
	{
		File toWrite = new File("classes/" + name.replaceAll("\\.", "/") + ".class");
		if (toWrite.getParentFile() != null) toWrite.getParentFile().mkdirs();
		try
		{
			toWrite.createNewFile();
			FileOutputStream out = new FileOutputStream(toWrite);
			out.write(basicClass);
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	/**/
	
	public byte[] transformEntityRenderer(byte[] bytes)
	{
		Plugin.logger().debug("Patching deobfuscated net.minecraft.client.render.EntityRenderer");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		boolean canSkipSky = true;
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(renderWorld))
			{
				Plugin.logger().debug("Patching renderWorld");
				InsnList toInject;
				LabelNode asmchangestart = null;
				AbstractInsnNode skyStart = null;
				VarInsnNode ths = null;
				boolean finished = false;
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node.getOpcode() == RETURN)
					{
						toInject = new InsnList();
						toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "endRender", "()V", false));
						i += toInject.size();
						size += toInject.size();
						m.instructions.insertBefore(node, toInject);
						break;
					}
				}
			}
			else if (m.name.equals(renderWorldPass))
			{
				Plugin.logger().debug("Patching renderWorldPass");
				InsnList toInject;
				boolean isFirstICMPLT = true;
				boolean isFirstClear = true;
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					switch (node.getOpcode())
					{
					case INVOKESTATIC:
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.owner.equals("net/minecraft/client/renderer/GlStateManager") && mNode.name.equals(clear) && mNode.desc.equals("(I)V"))
						{
							if (isFirstClear)
							{
								isFirstClear = false;
								toInject = new InsnList();
								toInject.add(new LabelNode());
								toInject.add(new VarInsnNode(FLOAD, 2));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "runShaders", "(F)V", false));
								toInject.add(new LabelNode());
								i += toInject.size();
								size += toInject.size();
								m.instructions.insert(node, toInject);
							}
						}
						break;
					}
					case IF_ICMPLT:
					{
						if (isFirstICMPLT)
						{
							JumpInsnNode jNode = (JumpInsnNode) node;
							LabelNode to = jNode.label;
							toInject = new InsnList();
							toInject.add(new VarInsnNode(FLOAD, 2));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "skipSky", "(F)Z", false));
							toInject.add(new JumpInsnNode(IFNE, to));
							i += toInject.size();
							size += toInject.size();
							m.instructions.insert(node, toInject);
							isFirstICMPLT = false;
						}
						break;
					}
					case RETURN:
					{
						toInject = new InsnList();
						toInject.add(new VarInsnNode(FLOAD, 2));
						toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "stopShaders", "(F)V", false));
						i += toInject.size();
						size += toInject.size();
						m.instructions.insertBefore(node, toInject);
						break;
					}
					}
				}
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
	
	public byte[] transformRenderGlobal(byte[] bytes)
	{
		Plugin.logger().debug("Patching net.minecraft.client.render.RenderGlobal");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		boolean canSkipSky = true;
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(renderEntities))
			{
				Plugin.logger().debug("Patching renderEntities");
				InsnList toInject;
				int num = 0;
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					//after FrameBuffer.framebufferClear() un-bind
					//after second FrameBuffer.bindFramebuffer(boolean) re-bind
					if (node.getOpcode() == INVOKEVIRTUAL)
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.owner.equals("net/minecraft/client/shader/Framebuffer"))
						{
							if (mNode.name.equals(framebufferClear))
							{
								if (mNode.desc.equals("()V"))
								{
									toInject = new InsnList();
									toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "unbind", "()V", false));
									i += toInject.size();
									size += toInject.size();
									m.instructions.insert(node, toInject);
								}
							}
							else if (mNode.name.equals(bindFramebuffer))
							{
								if (mNode.desc.equals("(Z)V"))
								{
									if (++num == 2)
									{
										toInject = new InsnList();
										toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "rebind", "()V", false));
										i += toInject.size();
										size += toInject.size();
										m.instructions.insert(node, toInject);
									}
								}
							}
						}
					}
				}
			}
		}
		ClassWriter writer = new ClassWriter(0);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
	
	public byte[] transformGameSettings(byte[] basicClass)
	{
		Plugin.logger().debug("Patching net.minecraft.client.settings.GameSettings");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(setOptionValue) && m.desc.equals("(Lnet/minecraft/client/settings/GameSettings$Options;I)V"))
			{
				Plugin.logger().debug("Patching setOptionValue");
				LabelNode toJump1 = new LabelNode();
				LabelNode toJump2 = new LabelNode();
				LabelNode toJump3 = new LabelNode();
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(ALOAD, 1));
				toInject.add(new FieldInsnNode(GETSTATIC, "extendedshaders/core/EventHandler", "shaders", "Lnet/minecraft/client/settings/GameSettings$Options;"));
				toInject.add(new JumpInsnNode(IF_ACMPNE, toJump1));
				toInject.add(new FieldInsnNode(GETSTATIC, "extendedshaders/core/Enabled", "shadersEnabled", "Z"));
				toInject.add(new JumpInsnNode(IFNE, toJump2));
				toInject.add(new InsnNode(ICONST_1));
				toInject.add(new JumpInsnNode(GOTO, toJump3));
				toInject.add(toJump2);
				toInject.add(new InsnNode(ICONST_0));
				toInject.add(toJump3);
				toInject.add(new FieldInsnNode(PUTSTATIC, "extendedshaders/core/Enabled", "shadersEnabled", "Z"));
				toInject.add(toJump1);
				m.instructions.insert(toInject);
			}
			else if (m.name.equals(getOptionOrdinalValue) && m.desc.equals("(Lnet/minecraft/client/settings/GameSettings$Options;)Z"))
			{
				Plugin.logger().debug("Patching getOptionOrdinalValue");
				LabelNode toJump = new LabelNode();
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(ALOAD, 1));
				toInject.add(new FieldInsnNode(GETSTATIC, "extendedshaders/core/EventHandler", "shaders", "Lnet/minecraft/client/settings/GameSettings$Options;"));
				toInject.add(new JumpInsnNode(IF_ACMPNE, toJump));
				toInject.add(new FieldInsnNode(GETSTATIC, "extendedshaders/core/Enabled", "shadersEnabled", "Z"));
				toInject.add(new InsnNode(IRETURN));
				toInject.add(toJump);
				m.instructions.insert(toInject);
			}
			else if (m.name.equals(loadOptions) && m.desc.equals("()V"))
			{
				Plugin.logger().debug("Patching loadOptions");
				for (int i = 0; i < m.instructions.size(); i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof LdcInsnNode && ((LdcInsnNode) node).cst.equals("mouseSensitivity"))
					{
						LabelNode toJump = new LabelNode();
						InsnList toInject = new InsnList();
						toInject.add(new LabelNode());
						toInject.add(new LdcInsnNode("shaders"));
						toInject.add(new VarInsnNode(ALOAD, 4));
						toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
						toInject.add(new JumpInsnNode(IFEQ, toJump));
						toInject.add(new LabelNode());
						toInject.add(new LdcInsnNode("true"));
						toInject.add(new VarInsnNode(ALOAD, 5));
						toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
						toInject.add(new FieldInsnNode(PUTSTATIC, "extendedshaders/core/Enabled", "shadersEnabled", "Z"));
						toInject.add(toJump);
						i += toInject.size();
						m.instructions.insert(node, toInject);
						break;
					}
				}
			}
			else if (m.name.equals(saveOptions) && m.desc.equals("()V"))
			{
				Plugin.logger().debug("Patching saveOptions");
				AbstractInsnNode prevNode = null;
				for (int i = 0; i < m.instructions.size(); i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof VarInsnNode && node.getOpcode() == ASTORE && ((VarInsnNode) node).var == 1 && prevNode instanceof MethodInsnNode && prevNode.getOpcode() == INVOKESPECIAL)
					{
						MethodInsnNode mNode = (MethodInsnNode) prevNode;
						if (mNode.owner.equals("java/io/PrintWriter") && mNode.name.equals("<init>") && mNode.desc.equals("(Ljava/io/Writer;)V"))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode());
							toInject.add(new VarInsnNode(ALOAD, 1));
							toInject.add(new TypeInsnNode(NEW, "java/lang/StringBuilder"));
							toInject.add(new InsnNode(DUP));
							toInject.add(new LdcInsnNode("shaders:"));
							toInject.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false));
							toInject.add(new FieldInsnNode(GETSTATIC, "extendedshaders/core/Enabled", "shadersEnabled", "Z"));
							toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false));
							toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));
							toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintWriter", "println", "(Ljava/lang/String;)V", false));
							m.instructions.insert(node, toInject);
							break;
						}
					}
					prevNode = node;
				}
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}

	public byte[] transformFramebuffer(byte[] bytes)
	{
		Plugin.logger().debug("Patching net.minecraft.client.shader.Framebuffer");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		classNode.visitField(ACC_PUBLIC, "framebufferTexturePos", "I", null, new Integer(-1)); //public int framebufferTexturePos = -1;
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(deleteFramebuffer))
			{
				Plugin.logger().debug("Patching deleteFramebuffer");
				for (int i = 0; i < m.instructions.size(); i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof MethodInsnNode)
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.getOpcode() == INVOKEVIRTUAL && mNode.owner.equals("net/minecraft/client/shader/Framebuffer") && mNode.name.equals(unbindFramebuffer))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode()); //this.framebufferTexturePos = Main.deleteFbTex(this.framebufferTexturePos)
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "deleteFbTex", "(I)I", false));
							toInject.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferDelete", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));
							
							m.instructions.insert(mNode, toInject);
							break;
						}
					}
				}
			}
			else if (m.name.equals(createFramebuffer) && m.desc.equals("(II)V"))
			{
				Plugin.logger().debug("Patching createFramebuffer");
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof FieldInsnNode)
					{
						FieldInsnNode fNode = (FieldInsnNode) node;
						if (fNode.getOpcode() == PUTFIELD && fNode.owner.equals("net/minecraft/client/shader/Framebuffer") && fNode.name.equals(framebufferTexture))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode()); //this.framebufferTexturePos = GL11.glGenTextures()
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/texture/TextureUtil", glGenTextures, "()I", false));
							toInject.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferCreateGenTextures", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));
							
							i += toInject.size();
							size += toInject.size();
							m.instructions.insert(fNode, toInject);
						}
					}
					else if (node instanceof MethodInsnNode)
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.getOpcode() == INVOKESTATIC && mNode.owner.equals("net/minecraft/client/renderer/GlStateManager") && mNode.name.equals(glTexImage2D) && mNode.desc.equals("(IIIIIIIILjava/nio/IntBuffer;)V"))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode()); //GL11.bindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexturePos)
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", bindTexture, "(I)V", false));
							
							toInject.add(new LabelNode()); //GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_FLOAT, null)
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
							toInject.add(new InsnNode(ICONST_0));
							toInject.add(new LdcInsnNode(new Integer(GL30.GL_RGBA32F)));
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", framebufferTextureWidth, "I"));
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", framebufferTextureHeight, "I"));
							toInject.add(new InsnNode(ICONST_0));
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_RGBA)));
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_FLOAT)));
							toInject.add(new InsnNode(ACONST_NULL));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", glTexImage2D, "(IIIIIIIILjava/nio/IntBuffer;)V", false));

							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferCreateSetTextures", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));

							i += toInject.size();
							size += toInject.size();
							m.instructions.insert(mNode, toInject);
						}
						else if (mNode.getOpcode() == INVOKESTATIC && mNode.owner.equals("net/minecraft/client/renderer/OpenGlHelper") && mNode.name.equals(glFramebufferTexture2D) && mNode.desc.equals("(IIIII)V"))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode()); //GLSLHelper.framebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, this.framebufferTexturePos, 0)
							toInject.add(new LdcInsnNode(new Integer(GL30.GL_FRAMEBUFFER)));
							toInject.add(new LdcInsnNode(new Integer(GL30.GL_COLOR_ATTACHMENT1)));
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							toInject.add(new InsnNode(ICONST_0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/api/GLSLHelper", "framebufferTexture2D", "(IIIII)V", false));

							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferCreateBindTextures", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));

							i += toInject.size();
							size += toInject.size();
							m.instructions.insert(mNode, toInject);
						}
					}
				}
			}
			else if (m.name.equals("createFramebuffer") && m.desc.equals("(III)V"))
			{
				Plugin.logger().debug("Patching vivecraft's createFramebuffer");
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof FieldInsnNode)
					{
						FieldInsnNode fNode = (FieldInsnNode) node;
						if (fNode.getOpcode() == PUTFIELD && fNode.owner.equals("net/minecraft/client/shader/Framebuffer") && fNode.name.equals(framebufferTexture))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode()); //this.framebufferTexturePos = GL11.glGenTextures()
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/texture/TextureUtil", glGenTextures, "()I", false));
							toInject.add(new FieldInsnNode(PUTFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferCreateGenTextures", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));
							
							i += toInject.size();
							size += toInject.size();
							m.instructions.insert(fNode, toInject);
						}
					}
					else if (node instanceof MethodInsnNode)
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.getOpcode() == INVOKESTATIC && mNode.owner.equals("net/minecraft/client/renderer/GlStateManager") && mNode.name.equals(glTexImage2D) && mNode.desc.equals("(IIIIIIIILjava/nio/IntBuffer;)V"))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode()); //GL11.bindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexturePos)
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", bindTexture, "(I)V", false));
							
							toInject.add(new LabelNode()); //GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_RGBA, GL30.GL_FLOAT, null)
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
							toInject.add(new InsnNode(ICONST_0));
							toInject.add(new LdcInsnNode(new Integer(GL30.GL_RGBA32F)));
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", framebufferTextureWidth, "I"));
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", framebufferTextureHeight, "I"));
							toInject.add(new InsnNode(ICONST_0));
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_RGBA)));
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_FLOAT)));
							toInject.add(new InsnNode(ACONST_NULL));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", glTexImage2D, "(IIIIIIIILjava/nio/IntBuffer;)V", false));

							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferCreateSetTextures", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));

							i += toInject.size();
							size += toInject.size();
							m.instructions.insert(mNode, toInject);
						}
						else if (mNode.getOpcode() == INVOKESTATIC && mNode.owner.equals("net/minecraft/client/renderer/OpenGlHelper") && mNode.name.equals(glFramebufferTexture2D) && mNode.desc.equals("(IIIII)V"))
						{
							InsnList toInject = new InsnList();
							toInject.add(new LabelNode()); //GLSLHelper.framebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, this.framebufferTexturePos, 0)
							toInject.add(new LdcInsnNode(new Integer(GL30.GL_FRAMEBUFFER)));
							toInject.add(new LdcInsnNode(new Integer(GL30.GL_COLOR_ATTACHMENT1)));
							toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
							toInject.add(new InsnNode(ICONST_0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/api/GLSLHelper", "framebufferTexture2D", "(IIIII)V", false));

							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferCreateBindTextures", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));

							i += toInject.size();
							size += toInject.size();
							m.instructions.insert(mNode, toInject);
						}
					}
				}
			}
			else if (m.name.equals(setFramebufferFilter))
			{
				int j = 0;
				Plugin.logger().debug("Patching setFramebufferFilter");
				for (int i = 0; i < m.instructions.size(); i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof MethodInsnNode)
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.getOpcode() == INVOKESTATIC && mNode.owner.equals("net/minecraft/client/renderer/GlStateManager") && mNode.name.equals(bindTexture) && mNode.desc.equals("(I)V"))
						{
							j++;
							if (j == 2)
							{
								InsnList toInject = new InsnList();
								
								toInject.add(new LabelNode()); //GL11.bindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexturePos)
								toInject.add(new VarInsnNode(ALOAD, 0));
								toInject.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I"));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", bindTexture, "(I)V", false));
								toInject.add(new LabelNode()); //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_MIN_FILTER)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_NEAREST)));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", glTexParameteri, "(III)V", false));
								toInject.add(new LabelNode()); //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_MAG_FILTER)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_NEAREST)));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", glTexParameteri, "(III)V", false));
								toInject.add(new LabelNode()); //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP)
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_WRAP_S)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_CLAMP)));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", glTexParameteri, "(III)V", false));
								toInject.add(new LabelNode()); //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP)
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_2D)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_TEXTURE_WRAP_T)));
								toInject.add(new LdcInsnNode(new Integer(GL11.GL_CLAMP)));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", glTexParameteri, "(III)V", false));
								
								toInject.add(new VarInsnNode(ALOAD, 0));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "onFramebufferSetFilter", "(Lnet/minecraft/client/shader/Framebuffer;)V", false));
								
								m.instructions.insert(mNode, toInject);
								break;
							}
						}
					}
				}
			}
		}
		ClassWriter writer = new ClassWriter(0);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
	
	public byte[] transformItemRenderer(byte[] bytes)
	{
		Plugin.logger().debug("Patching net.minecraft.client.render.ItemRenderer");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		boolean canSkipSky = true;
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(renderItemInFirstPerson))
			{
				Plugin.logger().debug("Patching renderItemInFirstPerson");
				InsnList toInject;
				LabelNode asmchangestart = null;
				AbstractInsnNode skyStart = null;
				VarInsnNode ths = null;
				boolean finished = false;
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node.getOpcode() == RETURN)
					{
						toInject = new InsnList();
						toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "disableEntity", "()V", false));
						i += toInject.size();
						size += toInject.size();
						m.instructions.insertBefore(node, toInject);
						break;
					}
				}
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
	
	public byte[] transformFramebufferUtil(byte[] bytes)
	{
		Plugin.logger().debug("Patching extendedshaders.core.FramebufferUtil");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		boolean canSkipSky = true;
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals("getFBPosTex"))
			{
				Plugin.logger().debug("Replacing getFBPosTex");
				m.instructions.clear();
				m.localVariables.clear();
				m.visitCode();
				Label l0 = new Label();
				m.visitLabel(l0);
				m.visitLineNumber(19, l0);
				m.visitVarInsn(ALOAD, 0);
				m.visitFieldInsn(GETFIELD, "net/minecraft/client/shader/Framebuffer", "framebufferTexturePos", "I");
				m.visitInsn(IRETURN);
				Label l1 = new Label();
				m.visitLabel(l1);
				m.visitLocalVariable("buf", "Lnet/minecraft/client/shader/Framebuffer;", null, l0, l1, 0);
				m.visitMaxs(1, 1);
				m.visitEnd();
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
	
	public byte[] bypass(String name, byte[] bytes)
	{
		int bypasses = 0;
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, 0);
			Iterator<MethodNode> methods = classNode.methods.iterator();
			while(methods.hasNext())
			{
				MethodNode m = methods.next();
				for (int i = 0; i < m.instructions.size() - 1; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof MethodInsnNode)
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.owner.equals("org/lwjgl/opengl/GL11") && (
								mNode.name.equals("glEnable") || 
								mNode.name.equals("glDisable") || 
								mNode.name.equals("glTexGeni") || 
								mNode.name.equals("glFogi") || 
								mNode.name.equals("glFogf") || 
								mNode.name.equals("glFog") || 
								mNode.name.equals("glColorMask") ||
								mNode.name.equals("glTexEnv") ||
								mNode.name.equals("glTexEnvi") ||
								mNode.name.equals("glTexEnvf")))
						{
							mNode.owner = "extendedshaders/core/Bypass";
							bypasses++;
						}
						else if (mNode.owner.equals("net/minecraft/client/renderer/texture/TextureManager") && mNode.name.equals(bindTexture2) && mNode.getOpcode() == INVOKEVIRTUAL)
						{
							mNode.setOpcode(INVOKESTATIC);
							mNode.owner = "extendedshaders/core/Bypass";
							mNode.name = "bindTexture";
							mNode.desc = "(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/util/ResourceLocation;)V";
							bypasses++;
						}
					}
				}
			}
			ClassWriter writer = new ClassWriter(0);
			classNode.accept(writer);
			if (bypasses > 0) Plugin.logger().debug("successfully bypassed " + bypasses + " call(s) in " + name);
			return writer.toByteArray();
		}
		catch (Exception e)
		{
			if (bypasses > 0)
			{
				if (bypasses > 0) Plugin.logger().debug("failed to bypass " + bypasses + " call(s) in " + name);
				e.printStackTrace();
			}
			return bytes;
		}
	}
}