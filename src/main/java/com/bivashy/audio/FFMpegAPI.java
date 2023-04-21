package com.bivashy.audio;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bivashy.audio.config.PluginConfig;
import com.bivashy.audio.hook.PlasmoVoiceHook;

public class FFMpegAPI extends JavaPlugin {
    public static final String PLASMO_VOICE_NAME = "PlasmoVoiceAddon";
    private PluginConfig config;

    @Override
    public void onEnable() {
        this.config = new PluginConfig(this);
        if (Bukkit.getPluginManager().isPluginEnabled(PLASMO_VOICE_NAME)) {
            new PlasmoVoiceHook(this).hook();
            logInfo("PlasmoVoiceAddon hooked, added youtube, ffmpeg sound formats support");
        } else {
            logWarn("PlasmoVoiceAddon not hooked!");
        }
    }

    public void logInfo(String log) {
        if (!config.isLog())
            return;
        getLogger().log(Level.INFO, log);
    }

    public void logWarn(String log) {
        if (!config.isLog())
            return;
        getLogger().log(Level.WARNING, log);
    }

    public PluginConfig getPluginConfig() {
        return config;
    }
}
