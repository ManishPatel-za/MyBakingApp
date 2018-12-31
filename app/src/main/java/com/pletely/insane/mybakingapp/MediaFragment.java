package com.pletely.insane.mybakingapp;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;


import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pletely.insane.mybakingapp.Utils.*;

public class MediaFragment extends Fragment {

    @BindView(R.id.media_viewer)
    SimpleExoPlayerView exoPlayerView;

    private SimpleExoPlayer exoPlayer;

    private String videoUrl;
    private String thumbnailUrl;
    private int currentPosition;
    private Handler handler;
    private BandwidthMeter bandwidthMeter;
    private long currentVideoPos = 0;
    private boolean playWhenReady = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_viewer, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            currentVideoPos = savedInstanceState.getLong(KEY_SAVED_VIDEO_POSITION,0);
            playWhenReady = savedInstanceState.getBoolean(KEY_SAVED_VIDEO_STATE, false);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {

            handler = new Handler();
            bandwidthMeter = new DefaultBandwidthMeter();

            videoUrl = bundle.getString(KEY_VIDEO_URI, "");
            currentPosition = bundle.getInt(KEY_NEW_POSITION, 0);
            thumbnailUrl = bundle.getString(KEY_THUMBNAIL_URI);

            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            if (thumbnailUrl != null && thumbnailUrl.length() > 0) {
                try {
                    Bitmap defaultArtwork = Picasso.get().load(Uri.parse(thumbnailUrl)).get();
                    exoPlayerView.setDefaultArtwork(defaultArtwork);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (videoUrl.length() > 0) {
                getInitialisePlayer(Uri.parse(videoUrl));
            }

        }

        return rootView;
    }

    private void getInitialisePlayer(Uri videoUri) {
        if (exoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelection.Factory trackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            exoPlayerView.setPlayer(exoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "MyBakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(playWhenReady);
            if (currentVideoPos>0) {
                exoPlayer.seekTo(currentVideoPos);
            }
        }
    }

    public void releaseVideo(boolean completelyDestroy) {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();

            if (completelyDestroy) {
                exoPlayer = null;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseVideo(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseVideo(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseVideo(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(exoPlayer!=null){
            //save current position
            long currentPos = exoPlayer.getCurrentPosition();
            outState.putLong(KEY_SAVED_VIDEO_POSITION, currentPos);

            //save state of player
            boolean isPlayWhenReady = exoPlayer.getPlayWhenReady();
            outState.putBoolean(KEY_SAVED_VIDEO_STATE, isPlayWhenReady);
        }
    }
}
