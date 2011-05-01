package net.peoplero.arg.listener.world;

import net.peoplero.arg.ARG;

import org.bukkit.event.world.WorldListener;

public class ARGWorldListener extends WorldListener 
{
    @SuppressWarnings("unused")
    private final ARG _plugin;
    
    public ARGWorldListener(final ARG plugin)
    {
        _plugin = plugin;
    }

}
