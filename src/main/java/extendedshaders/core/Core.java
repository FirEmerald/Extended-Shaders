package extendedshaders.core;

import java.io.File;
import java.util.ArrayList;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;

public class Core extends DummyModContainer
{
	public static final ModMetadata METADATA = new ModMetadata();
	static
	{
		METADATA.authorList = new ArrayList<String>();
    	METADATA.authorList.add("FirEmerald");
    	METADATA.credits = "FirEmerald";
    	METADATA.description = "Shader support for mods.";
    	METADATA.modId = "extended shaders";
    	METADATA.name = "Extended Shaders";
    	METADATA.version = Plugin.ES_VERSION;
	}
	
    public Core()
    {
        super(METADATA);
    }
    
    @Override
    public File getSource()
    {
        return Plugin.instance().getLocation();
    }
    
    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }
    
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	if (FMLCommonHandler.instance().getSide().isClient())
    	{
            bus.register(new Main());
            return true;
    	}
    	else return false;
    }
}