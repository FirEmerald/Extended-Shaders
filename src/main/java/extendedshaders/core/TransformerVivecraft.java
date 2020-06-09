package extendedshaders.core;

import java.util.Iterator;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class TransformerVivecraft extends TransformerOptifine
{
	public static final String matrixMode = Shaders.DEOB ? "matrixMode" : "func_179128_n";
	
	@Override
	public byte[] transform(String name, String tName, byte[] basicClass)
	{
		//if (tName.equals("net.minecraftforge.client.ForgeHooksClient")) basicClass = transformForgeHooksClient(basicClass);
		return super.transform(name, tName, basicClass);
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
				boolean crosshairFlag = false;
				AbstractInsnNode prev = null;
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					switch (node.getOpcode())
					{
					case ALOAD:
					{
						if (!crosshairFlag && (i + 1 < size) && ((VarInsnNode) node).var == 0)
						{
							node = m.instructions.get(i + 1);
							if (node.getOpcode() == INVOKEVIRTUAL)
							{
								MethodInsnNode mNode = (MethodInsnNode) node;
								//System.out.println(mNode.owner + "." + mNode.name + mNode.desc);
								if (mNode.owner.equals("net/minecraft/client/renderer/EntityRenderer") && mNode.name.equals("renderCrosshairAtDepth")  && mNode.desc.equals("()V"))
								{
									crosshairFlag = true;
									i++;
									toInject = new InsnList();
									toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "disableEffects", "()V", false));
									toInject.add(new LabelNode());
									i += toInject.size();
									size += toInject.size();
									m.instructions.insert(node, toInject);
								}
							}
						}
						break;
					}
					case INVOKESTATIC:
					{
						MethodInsnNode mNode = (MethodInsnNode) node;
						if (mNode.owner.equals("net/minecraft/client/renderer/GlStateManager") && mNode.desc.equals("(I)V"))
						{
							if (mNode.name.equals(matrixMode))
							{
								if (crosshairFlag)
								{
									toInject = new InsnList();
									toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "reenableEffects", "()V", false));
									toInject.add(new LabelNode());
									toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "disableEntity", "()V", false));
									toInject.add(new LabelNode());
									i += toInject.size();
									size += toInject.size();
									m.instructions.insert(node, toInject);
								}
							}
							else if (mNode.name.equals(clear))
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
						}
						break;
					}
					case IFNE:
					{
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
		//ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		//ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		//ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassWriter writer = new ClassWriter(0);
		classNode.accept(writer);
		Plugin.logger().debug("Patching successful");
		return writer.toByteArray();
	}
}