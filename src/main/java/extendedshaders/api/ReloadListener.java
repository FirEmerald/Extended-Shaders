package extendedshaders.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

/** DO NOT TOUCH THESE METHODS. they are used to ensure shaders and post-processors reload when resources are reloaded, even if Extended Shaders is not installed **/
public class ReloadListener implements IResourceManagerReloadListener
{
	private static ArrayList<WeakReference<Shader>> data = new ArrayList<WeakReference<Shader>>();
	private static ArrayList<WeakReference<PostProcessor>> post = new ArrayList<WeakReference<PostProcessor>>();
	static
	{
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ReloadListener());
	}
	
	protected static void addData(Shader shaderData)
	{
		data.add(new WeakReference<Shader>(shaderData));
	}
	
	protected static void addPost(PostProcessor postProcessor)
	{
		post.add(new WeakReference<PostProcessor>(postProcessor));
	}

	@Override
	public void onResourceManagerReload(IResourceManager manager)
	{
		ShaderRegistry.hasChanged = true;
		int size = data.size();
		for (int i = 0; i < size; i++)
		{
			WeakReference<Shader> reference = data.get(i);
			if (reference.get() == null)
			{
				data.remove(i);
				i--;
				size--;
			}
			else reference.get().onReload(manager);
		}
		size = post.size();
		for (int i = 0; i < size; i++)
		{
			WeakReference<PostProcessor> reference = post.get(i);
			if (reference.get() == null)
			{
				post.remove(i);
				i--;
				size--;
			}
			else reference.get().onReload(manager);
		}
	}
}