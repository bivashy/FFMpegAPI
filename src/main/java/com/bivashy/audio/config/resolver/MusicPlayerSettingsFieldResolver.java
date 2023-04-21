package com.bivashy.audio.config.resolver;

import com.ubivashka.configuration.context.ConfigurationFieldResolverContext;
import com.ubivashka.configuration.holder.ConfigurationSectionHolder;
import com.ubivashka.configuration.resolver.field.ConfigurationFieldResolver;
import com.ubivashka.plasmovoice.config.settings.MusicPlayerSettings;

public class MusicPlayerSettingsFieldResolver implements ConfigurationFieldResolver<MusicPlayerSettings> {
    public static final String SLEEP_DELAY_LEY = "sleep-delay";
    public static final String BITRATE_KEY = "bitrate";
    public static final String VOLUME_KEY = "volume";
    public static final String DISTANCE_KEY = "distance";

    @Override
    public MusicPlayerSettings resolveField(ConfigurationFieldResolverContext context) {
        if (!context.isSection())
            return null;
        ConfigurationSectionHolder sectionHolder = context.getSection();
        int sleepDelay = sectionHolder.getInt(20, SLEEP_DELAY_LEY);
        int bitrate = sectionHolder.getInt(-1, BITRATE_KEY);
        int volume = sectionHolder.getInt(100, VOLUME_KEY);
        int distance = sectionHolder.getInt(100, DISTANCE_KEY);
        return new MusicPlayerSettings(sleepDelay, volume, bitrate, distance);
    }
}
