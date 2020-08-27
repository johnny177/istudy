package com.nnoboa.istudy.ui.alarm.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;

public class AlarmRingTone {
    public static MediaPlayer mediaPlayer;
    public static boolean isplayingAudio = false;
    private static SoundPool soundPool;

    /** Handles playback of all the sound files */
    /**
     * Handles audio focus when playing a sound file
     */
    private static AudioManager mAudioManager;

    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private static AudioManager.OnAudioFocusChangeListener
            mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                        // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                        // our app is allowed to continue playing sound but at a lower volume. We'll treat
                        // both cases the same way because our app is playing short sound files.

                        // Pause playback and reset player to the start of the file. That way, we can
                        // play the word from the beginning when we resume playback.
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                        mediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                        // Stop playback and clean up resources
                        releaseMediaPlayer();
                    }
                }
            };

    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */
    private static MediaPlayer.OnCompletionListener
            mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // Now that the sound file has finished playing, release the media player resources.
                    releaseMediaPlayer();
                }
            };


    public static void playAudio(Context c, Uri uri) {
        mAudioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);

        releaseMediaPlayer();

        // Request audio focus so in order to play the audio file. The app needs to play a
        // short audio file, so we will request audio focus with a short amount of time
        // with AUDIOFOCUS_GAIN_TRANSIENT.
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = MediaPlayer.create(c, uri);
//            soundPool = new SoundPool(4, AudioManager.STREAM_ALARM, 6000);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.setVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM), mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM));
                isplayingAudio = true;
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnCompletionListener(mCompletionListener);
            }
        }
    }

    public static void stopAudio() {
        isplayingAudio = false;
        mediaPlayer.stop();
        releaseMediaPlayer();
    }

    private static void releaseMediaPlayer() {
        // if the media player is not null then its currently playing a sound
        if (mediaPlayer != null) {
            //releases the resource regardless of its state because we no longer need it.
            mediaPlayer.release();
// Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
//            audioManager.abandonAudioFocus(afChangeListener);
            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);

        }
    }

}
