package com.ubivashka.audio.hook.plasmovoice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.tagtraum.ffsampledsp.FFAudioFileReader;
import com.tagtraum.ffsampledsp.FFFormatConversionProvider;
import com.ubivashka.audio.FFMpegAPI;
import com.ubivashka.plasmovoice.config.settings.MusicPlayerSettings;
import com.ubivashka.plasmovoice.sound.AbstractSoundFactory;
import com.ubivashka.plasmovoice.sound.ISound;
import com.ubivashka.plasmovoice.sound.ISoundFactory;
import com.ubivashka.plasmovoice.sound.ISoundFormat;
import com.ubivashka.plasmovoice.sound.util.VolumeAdjuster;

public class FFmpegSoundFormat implements ISoundFormat {
    private static final FFMpegAPI PLUGIN = FFMpegAPI.getPlugin(FFMpegAPI.class);
    private static final FFAudioFileReader AUDIO_FILE_READER = new FFAudioFileReader();
    private static final FFFormatConversionProvider CONVERSION_PROVIDER = new FFFormatConversionProvider();
    private static final VolumeAdjuster VOLUME_ADJUSTER = new VolumeAdjuster();

    @Override
    public boolean isSupported(InputStream inputStream) {
        try {
            AUDIO_FILE_READER.getAudioInputStream(inputStream);
            return true;
        } catch(UnsupportedAudioFileException | IOException e) {
            return false;
        }
    }

    @Override
    public boolean isSupported(File file, InputStream fileStream) {
        try {
            AUDIO_FILE_READER.getAudioInputStream(file);
            return true;
        } catch(UnsupportedAudioFileException | IOException e) {
            return false;
        }
    }

    @Override
    public MusicPlayerSettings getSettings() {
        return PLUGIN.getPluginConfig().getFfmpegFormatSettings();
    }

    @Override
    public ISoundFactory newSoundFactory() {
        return new FFmpegSoundFactory(this);
    }

    @Override
    public String getName() {
        return "FFMpeg";
    }

    public static class FFmpegSoundFactory extends AbstractSoundFactory {
        public FFmpegSoundFactory(ISoundFormat soundFormat) {
            super(soundFormat);
        }

        @Override
        public ISound createSound(File file, InputStream fileStream) {
            try {
                AudioInputStream audioInputStream = AUDIO_FILE_READER.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
                return createSound(audioInputStream);
            } catch(UnsupportedAudioFileException e) {
                PLUGIN.logWarn("Unsupported audio file format of: " + file.getAbsolutePath());
                return null;
            } catch(IOException e) {
                PLUGIN.logInfo("IOException, probably internet connection problem");
                return null;
            }
        }

        @Override
        protected AudioInputStream createAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {
            AudioInputStream audioInputStream = AUDIO_FILE_READER.getAudioInputStream(inputStream);
            return CONVERSION_PROVIDER.getAudioInputStream(opusCodecHolder.getAudioFormat(), audioInputStream);
        }
    }
}
