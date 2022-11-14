package com.ubivashka.audio.hook;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.tagtraum.ffsampledsp.FFNativeLibraryLoader;
import com.tagtraum.ffsampledsp.FFNativePeerInputStream;
import com.ubivashka.audio.FFMpegAPI;
import com.ubivashka.audio.hook.plasmovoice.FFmpegSoundFormat;
import com.ubivashka.audio.hook.plasmovoice.YoutubeSoundFormat;
import com.ubivashka.plasmovoice.PlasmoVoiceAddon;

public class PlasmoVoiceHook {
    private static final PlasmoVoiceAddon PLASMO_VOICE_ADDON = PlasmoVoiceAddon.getPlugin(PlasmoVoiceAddon.class);
    private final FFMpegAPI plugin;

    public PlasmoVoiceHook(FFMpegAPI plugin) {
        this.plugin = plugin;
    }

    public void hook() {
        if (!plugin.getPluginConfig().isLog()) {
            Logger.getLogger(FFNativeLibraryLoader.class.getName()).setLevel(Level.OFF);
            Logger.getLogger(FFNativePeerInputStream.class.getName()).setLevel(Level.OFF);
        }
        PLASMO_VOICE_ADDON.getSoundFormatHolder().add(0, new YoutubeSoundFormat());
        PLASMO_VOICE_ADDON.getSoundFormatHolder().add(0, new FFmpegSoundFormat());
    }
}
