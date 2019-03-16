package extendedshaders.core;

import java.util.Iterator;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class TransformerOptifine extends Transformer
{
	public static final String NAME = "Extended Shaders";
	
	@Override
	public byte[] transform(String name, String tName, byte[] basicClass)
	{
		if (tName.equals("net.optifine.shaders.Shaders")) basicClass = transformShaders(basicClass);
		else if (tName.equals("extendedshaders.core.Enabled")) basicClass = transformEnabled(basicClass);
		return super.transform(name, tName, basicClass);
	}

	public byte[] transformShaders(byte[] bytes)
	{
		Plugin.logger().debug("Patching net.optifine.shaders.Shaders");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		boolean canSkipSky = true;
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals("listOfShaders"))
			{
				Plugin.logger().debug("Patching listOfShaders");
				InsnList toInject;
				LabelNode asmchangestart = null;
				AbstractInsnNode skyStart = null;
				VarInsnNode ths = null;
				boolean finished = false;
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node.getOpcode() == GETSTATIC)
					{
						FieldInsnNode fNode = (FieldInsnNode) node;
						if (fNode.owner.equals("net/optifine/shaders/Shaders") && fNode.name.equals("shaderPacksDir"))
						{
							toInject = new InsnList();
							toInject.add(new VarInsnNode(ALOAD, 0));
							toInject.add(new LdcInsnNode(NAME));
							toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false));
							toInject.add(new InsnNode(POP));
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
	
	public byte[] transformEnabled(byte[] bytes)
	{
		Plugin.logger().debug("Patching extendedshaders.core.Enabled");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		boolean canSkipSky = true;
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals("isEnabled"))
			{
				Plugin.logger().debug("Replacing isEnabled");
				m.instructions.clear();
				m.localVariables.clear();
				m.visitCode();
				Label l0 = new Label();
				m.visitLabel(l0);
				m.visitLineNumber(7, l0);
				m.visitFieldInsn(GETSTATIC, "net/optifine/shaders/Shaders", "currentShaderName", "Ljava/lang/String;");
				m.visitLdcInsn("Extended Shaders");
				m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
				m.visitInsn(IRETURN);
				Label l1 = new Label();
				m.visitLabel(l1);
				m.visitLocalVariable("this", "Lextendedshaders/core/Enabled;", null, l0, l1, 0);
				m.visitMaxs(2, 1);
				m.visitEnd();
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
	
	@Override
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
				boolean isFirstShadowCheck = true;
				boolean isFirstClear = true;
				AbstractInsnNode prev = null;
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
					case IFNE:
					{
						//getstatic net/optifine/shaders/Shaders.isShadowPass:boolean
						//ifne L48
						if (isFirstShadowCheck && prev != null && prev.getOpcode() == GETSTATIC)
						{
							FieldInsnNode fNode = (FieldInsnNode) prev;
							if (fNode.owner.equals("net/optifine/shaders/Shaders") && fNode.name.equals("isShadowPass"))
							{
								isFirstShadowCheck = false;
								JumpInsnNode jNode = (JumpInsnNode) node;
								LabelNode to = jNode.label;
								toInject = new InsnList();
								toInject.add(new VarInsnNode(FLOAD, 2));
								toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "skipSky", "(F)Z", false));
								toInject.add(new JumpInsnNode(IFNE, to));
								i += toInject.size();
								size += toInject.size();
								m.instructions.insert(node, toInject);
							}
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
					prev = node;
				}
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
}