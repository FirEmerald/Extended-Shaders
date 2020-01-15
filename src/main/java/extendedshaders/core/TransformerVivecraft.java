package extendedshaders.core;

import java.util.Iterator;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class TransformerVivecraft extends TransformerOptifine
{
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
			else if (m.name.equals("renderGuiLayer"))
			{
				if (m.desc.equals("(F)V"))
				{
					Plugin.logger().debug("patching renderGuiLayer(float)");
					int size = m.instructions.size();
					for (int i = 0; i < size; i++)
					{
						AbstractInsnNode node = m.instructions.get(i);
						if (node.getOpcode() == RETURN)
						{
							InsnList toInject = new InsnList();
							/*
							toInject.add(new FieldInsnNode(GETSTATIC, "extendedshaders/api/Passthrough", "instance", "Lextendedshaders/api/Passthrough;"));
							toInject.add(new InsnNode(ICONST_0));
							toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "extendedshaders/api/Passthrough", "setIgnoreEffects", "(Z)V", false));
							*/
							toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "rebind", "()V", false));
							toInject.add(new LabelNode());
							i += toInject.size();
							size += toInject.size();
							m.instructions.insertBefore(node, toInject);
						}
					}
					InsnList toInject = new InsnList();
					/*
					toInject.add(new FieldInsnNode(GETSTATIC, "extendedshaders/api/Passthrough", "instance", "Lextendedshaders/api/Passthrough;"));
					toInject.add(new InsnNode(ICONST_1));
					toInject.add(new MethodInsnNode(INVOKEVIRTUAL, "extendedshaders/api/Passthrough", "setIgnoreEffects", "(Z)V", false));
					*/
					toInject.add(new MethodInsnNode(INVOKESTATIC, "extendedshaders/core/Main", "unbind", "()V", false));
					toInject.add(new LabelNode());
					m.instructions.insert(toInject);
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