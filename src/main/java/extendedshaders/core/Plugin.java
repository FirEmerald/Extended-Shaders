package extendedshaders.core;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import net.minecraftforge.fml.relauncher.Side;

@SortingIndex(value = Integer.MAX_VALUE)
@TransformerExclusions(value = {"extendedshaders.core.Plugin", "extendedshaders.core.Transformer", "extendedshaders.core.Shaders"})
@Name(value = "Extended Shaders")
@MCVersion("1.12.2")
public class Plugin implements IFMLLoadingPlugin, IFMLCallHook
{
    public static final String MC_VERSION = "[1.12.2]";
    public static final String ES_VERSION = "[7.5]";
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

    public static enum Variant
    {
    	VANILLA("extendedshaders.core.Transformer"),
    	OPTIFINE("extendedshaders.core.TransformerOptifine", true),
    	VIVECRAFT("extendedshaders.core.TransformerVivecraft", true);

    	public final String transformerClass;
    	public final boolean isOptifineEnabled;

    	Variant(String transformerClass, boolean isOptifineEnabled)
    	{
    		this.transformerClass = transformerClass;
    		this.isOptifineEnabled = isOptifineEnabled;
    	}

    	Variant(String transformerClass)
    	{
    		this(transformerClass, false);
    	}
    }

	public static final String VIVECRAFT_TEST_CLASS = "org.vivecraft.main.VivecraftMain";
	public static final String OPTIFINE_TEST_CLASS = "optifine.OptiFineClassTransformer";
	private static Variant variant = null;

	private static Variant getVariantOriginal()
	{
		if (FMLLaunchHandler.isDeobfuscatedEnvironment()) return Variant.VANILLA;
		try
		{
			if (Class.forName(VIVECRAFT_TEST_CLASS) != null) return Variant.VIVECRAFT;
		}
		catch (ClassNotFoundException e) {}
		try
		{
			if (Class.forName(OPTIFINE_TEST_CLASS) != null) return Variant.OPTIFINE;
		}
		catch (ClassNotFoundException e) {}
		return Variant.VANILLA;
	}

	public static Variant getVariant()
	{
		if (variant == null) variant = getVariantOriginal();
		return variant;
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
		if (FMLLaunchHandler.side() == Side.CLIENT)
		{
			Variant variant = getVariant();
			String clazz = variant.transformerClass;
			logger.info("Using transformer " + clazz + " for variant " + variant.name());
			return new String[] {clazz};
		}
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