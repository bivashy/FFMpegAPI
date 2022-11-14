package com.ubivashka.audio.hook.plasmovoice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;
import com.tagtraum.ffsampledsp.FFAudioFileReader;
import com.tagtraum.ffsampledsp.FFFormatConversionProvider;
import com.ubivashka.audio.FFMpegAPI;
import com.ubivashka.plasmovoice.config.settings.MusicPlayerSettings;
import com.ubivashka.plasmovoice.sound.AbstractSoundFactory;
import com.ubivashka.plasmovoice.sound.ISound;
import com.ubivashka.plasmovoice.sound.ISoundFactory;
import com.ubivashka.plasmovoice.sound.ISoundFormat;

public class YoutubeSoundFormat implements ISoundFormat {
    private static final YoutubeDownloader YOUTUBE_DOWNLOADER = new YoutubeDownloader();

    private static final FFAudioFileReader AUDIO_FILE_READER = new FFAudioFileReader();
    private static final FFFormatConversionProvider CONVERSION_PROVIDER = new FFFormatConversionProvider();
    private static final Pattern YOUTUBE_VIDEO_ID = Pattern.compile(
            "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))" +
                    "([^&#?\\n]+)",
            Pattern.CASE_INSENSITIVE);
    private static final FFMpegAPI PLUGIN = FFMpegAPI.getPlugin(FFMpegAPI.class);

    @Override
    public boolean isSupported(InputStream inputStream) {
        return false;
    }

    @Override
    public boolean isSupported(URL url, InputStream urlStream) {
        return getVideoId(url) != null;
    }

    @Override
    public MusicPlayerSettings getSettings() {
        return PLUGIN.getPluginConfig().getYoutubeFormatSettings();
    }

    @Override
    public ISoundFactory newSoundFactory() {
        return new YoutubeSoundFactory(this);
    }

    @Override
    public String getName() {
        return "Youtube";
    }

    public static class YoutubeSoundFactory extends AbstractSoundFactory {
        public YoutubeSoundFactory(ISoundFormat format) {
            super(format);
        }

        @Override
        public ISound createSound(File file, InputStream fileStream) {
            throw new UnsupportedOperationException("Cannot create youtube sound from file!");
        }

        @Override
        public ISound createSound(URL url, InputStream urlStream) {
            String videoId = getVideoId(url);
            Response<VideoInfo> response = YOUTUBE_DOWNLOADER.getVideoInfo(new RequestVideoInfo(videoId));
            if (!response.ok()) {
                PLUGIN.logWarn("Error occurred during retrieving information about video: " + url);
                return null;
            }
            VideoInfo videoInfo = response.data();
            Format bestAudioFormat = videoInfo.bestAudioFormat() == null ? videoInfo.bestVideoWithAudioFormat() : videoInfo.bestAudioFormat();
            try {
                System.o
                URL audioUrl = new URL(bestAudioFormat.url());
                InputStream audioStream = new BufferedInputStream(audioUrl.openStream());
                if (bestAudioFormat.extension().isAudio())
                    return createSound(audioStream);
                File temporaryFile = Files.createTempFile("youtube", "." + bestAudioFormat.extension().value()).toFile();
                FileOutputStream fileOutputStream = new FileOutputStream(temporaryFile);
                fileOutputStream.getChannel().transferFrom(Channels.newChannel(audioStream), 0, Long.MAX_VALUE);
                fileOutputStream.close();
                return createSound(
                        CONVERSION_PROVIDER.getAudioInputStream(opusCodecHolder.getAudioFormat(), AUDIO_FILE_READER.getAudioInputStream(temporaryFile)));
            } catch(IOException e) {
                e.printStackTrace();
                PLUGIN.logWarn("Cannot connect to the url " + bestAudioFormat.url() + ", probably you have bad internet connect :(");
                return null;
            } catch(UnsupportedAudioFileException e) {
                e.printStackTrace();
                PLUGIN.logWarn("Cannot process audio file");
                return null;
            }
        }

        @Override
        protected AudioInputStream createAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {
            AudioInputStream audioInputStream = AUDIO_FILE_READER.getAudioInputStream(inputStream);
            return CONVERSION_PROVIDER.getAudioInputStream(opusCodecHolder.getAudioFormat(), audioInputStream);
        }
    }

    private static VideoWithAudioFormat optimalVideoWithAudioFormat(VideoInfo videoInfo) {
        VideoWithAudioFormat best = null;
        for (Format format : videoInfo.formats()) {
            if (!(format instanceof VideoWithAudioFormat))
                continue;
            VideoWithAudioFormat videoWithAudioFormat = (VideoWithAudioFormat) format;
            if(videoWithAudioFormat.audioQuality().compare())
        }
    }

    private static String getVideoId(URL url) {
        Matcher matcher = YOUTUBE_VIDEO_ID.matcher(url.toString());
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}
