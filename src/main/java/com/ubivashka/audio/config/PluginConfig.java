package com.ubivashka.audio.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import com.ubivashka.audio.config.resolver.MusicPlayerSettingsFieldResolver;
import com.ubivashka.configuration.BukkitConfigurationProcessor;
import com.ubivashka.configuration.ConfigurationProcessor;
import com.ubivashka.configuration.annotation.ConfigField;
import com.ubivashka.plasmovoice.config.settings.MusicPlayerSettings;

public class PluginConfig {
    public static final ConfigurationProcessor CONFIGURATION_PROCESSOR = new BukkitConfigurationProcessor().registerFieldResolver(MusicPlayerSettings.class,
            new MusicPlayerSettingsFieldResolver());

    @ConfigField
    private boolean log;
    @ConfigField("youtube-audio")
    private MusicPlayerSettings youtubeFormatSettings;
    @ConfigField("ffmpeg")
    private MusicPlayerSettings ffmpegFormatSettings;

    private final ConfigurationSection root;

    public PluginConfig(Plugin plugin) {
        plugin.saveDefaultConfig();
        this.root = plugin.getConfig();
        CONFIGURATION_PROCESSOR.resolve(root, this);
    }

    public boolean isLog() {
        return log;
    }

    public MusicPlayerSettings getYoutubeFormatSettings() {
        return youtubeFormatSettings;
    }

    public MusicPlayerSettings getFfmpegFormatSettings() {
        return ffmpegFormatSettings;
    }

    public ConfigurationSection getRoot() {
        return root;
    }
}
