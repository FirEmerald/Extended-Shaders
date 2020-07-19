package extendedshaders.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** used to turn shaders on and off, by adding and removing them from a registry.<br>
 * keep in mind changes do not effect until the beginning of the next render tick, or when {@link Passthrough#forceShaderCompile()} is run. **/
public class FramebufferAttachmentRegistry
{
	private static final List<FramebufferAttachment> ATTACHMENTS = new ArrayList<>(); //TODO ensure not too many attachments
	private static int numAttachments = 0;
	
	public static void addAttachment(FramebufferAttachment data)
	{
		if (!ATTACHMENTS.contains(data))
		{
			int i = 0;
			while (i < ATTACHMENTS.size())
			{
				if (ATTACHMENTS.get(i) == null) break;
				else i++;
			}
			if (i == ATTACHMENTS.size()) ATTACHMENTS.add(data);
			else ATTACHMENTS.set(i, data);
			data.bufferIndex = i + 2;
			numAttachments++;
			Passthrough.instance.onAddFramebufferAttachment(data);
		}
	}
	
	public static void removeAttachment(FramebufferAttachment data)
	{
		if (ATTACHMENTS.contains(data))
		{
			ATTACHMENTS.set(data.bufferIndex - 2, null);
			numAttachments--;
			Passthrough.instance.onRemoveFramebufferAttachment(data);
		}
	}
	
	public static FramebufferAttachment[] getAttachments()
	{
		FramebufferAttachment[] data = new FramebufferAttachment[numAttachments];
		int index = 0;
		for (FramebufferAttachment attachment : ATTACHMENTS) if (attachment != null)
		{
			data[index] = attachment;
			index++;
		}
		return data;
	}
}