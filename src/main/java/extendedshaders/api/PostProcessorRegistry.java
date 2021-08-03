package extendedshaders.api;

import java.util.ArrayList;
import java.util.Arrays;

/** used to turn post-processing effects on and off, by adding and removing them from a registry **/
public class PostProcessorRegistry
{
	private static final ArrayList<PostProcessor> postProcessors = new ArrayList<>();
	/** turns the post-processor ON **/
	public static void addPostProcessor(PostProcessor data)
	{
		postProcessors.add(data);
	}
	/** turns the post-processor OFF **/
	public static void removePostProcessor(PostProcessor data)
	{
		postProcessors.remove(data);
	}
	/** gets the active post-processors, sorted by priority **/
	public static PostProcessor[] getPostProcessors()
	{
		PostProcessor[] data = new PostProcessor[postProcessors.size()];
		data = postProcessors.toArray(data);
		Arrays.sort(data);
		return data;
	}
}