package net.peoplero.arg.listener.server;

import net.peoplero.arg.ARG;

import org.bukkit.event.server.ServerListener;

public class ARGServerListener extends ServerListener 
{
    @SuppressWarnings("unused")
    private final ARG _plugin;

    public ARGServerListener(final ARG plugin)
    {
        _plugin = plugin;
    }
}
