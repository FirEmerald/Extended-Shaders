package extendedshaders.core;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.*;
import net.minecraftforge.fml.relauncher.Side;

@SortingIndex(value = Integer.MAX_VALUE)
@TransformerExclusions(value = {"extendedshaders.core.Plugin", "extendedshaders.core.Transformer", "extendedshaders.core.Shaders"})
@Name(value = "Extended Shaders")
@MCVersion("1.12.2")
public class Plugin implements IFMLLoadingPlugin, IFMLCallHook
{
    public static final String MC_VERSION = "[1.12.2]";
    public static final String ES_VERSION = "[7.1a]";
    private File location;
    
    public final Logger logger = LogManager.getLogger("Extended Shaders");
    
    public static Logger logger()
    {
    	return instance.logger;
    }
    
    private static Plugin instance;
    
    public static Plugin instance()
    {
    	return instance;
    }
    
	public static final String OPTIFINE_TEST_CLASS = "optifine.OptiFineClassTransformer";
	
	public static boolean isOptifineEnabled()
	{
		try
		{
			return Class.forName(OPTIFINE_TEST_CLASS) != null;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
    
    public Plugin()
    {
    	instance = this;
    }
    
    public File getLocation()
    {
    	return location;
    }
    
	@Override
	public Void call() throws Exception
	{
		return null;
	}

	@Override
	public String[] getASMTransformerClass()
	{
		if (FMLLaunchHandler.side() == Side.CLIENT)	return new String[] {isOptifineEnabled() ? "extendedshaders.core.TransformerOptifine" : "extendedshaders.core.Transformer"};
		else return new String[0];
	}

	@Override
	public String getModContainerClass()
	{
		return "extendedshaders.core.Core";
	}

	@Override
	public String getSetupClass()
	{
		return getClass().getName();
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		location = (File) data.get("coremodLocation");
        if (location == null) location = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile().replace("%20", " "));
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}