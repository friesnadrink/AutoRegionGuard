package net.peoplero.arg.listener.vehicle;

import net.peoplero.arg.ARG;

import org.bukkit.event.vehicle.VehicleListener;

public class ARGVehicleListener extends VehicleListener 
{
    @SuppressWarnings("unused")
    private final ARG _plugin;
    
    public ARGVehicleListener(final ARG plugin)
    {
        _plugin = plugin;
    }    

}
