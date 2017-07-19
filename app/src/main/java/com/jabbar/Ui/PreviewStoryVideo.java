package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.jabbar.API.AddStoryAPI;
import com.jabbar.CameraMaster.internal.ui.view.AspectFrameLayout;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by hardikjani on 7/6/17.
 */

public class PreviewStoryVideo extends BaseActivity {

    private ProgressDialog progressDialog;
    private RelativeLayout rlBack;
    private ImageView conversation_contact_photo;
    private AspectFrameLayout videoPreviewContainer;
    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private EmojiconEditText edit_msg;
    private ImageView img_emoji;
    private RelativeLayout rel_send;
    private ImageView imgSend;
    private EmojIconActions emojIcon;
    private String fileName;

    private int currentPlaybackPosition = 0;
    private boolean isVideoPlaying = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview_story_video);
        Utils.addActivities(this);
        Bundle args = getIntent().getExtras();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        videoPreviewContainer = (AspectFrameLayout) findViewById(R.id.previewAspectFrameLayout);
        surfaceView = (SurfaceView) findViewById(R.id.video_preview);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);

        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
        img_emoji = (ImageView) findViewById(R.id.img_emoji);
        rel_send = (RelativeLayout) findViewById(R.id.rel_send);
        imgSend = (ImageView) findViewById(R.id.img_send);

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(PreviewStoryVideo.this)) {
                    progressDialog.show();
                    Log.print("====fileName====" + fileName);
                    new AddStoryAPI(PreviewStoryVideo.this, fileName, StringEscapeUtils.escapeJava(edit_msg.getText().toString().trim()), new ResponseListener() {
                        @Override
                        public void onResponce(String tag, int result, Object obj) {
                            progressDialog.dismiss();
                            if (tag.equalsIgnoreCase(Config.TAG_ADD_STORY) && result == 0) {
                                setResult(RESULT_OK);
                                finish();

                            } else {
                                new JabbarDialog(PreviewStoryVideo.this, obj.toString()).show();
                            }
                        }
                    }).execute();
                } else {
                    new JabbarDialog(PreviewStoryVideo.this, getString(R.string.no_internet)).show();
                }
            }
        });

        if (!Pref.getValue(this, Config.PREF_AVATAR, "").equalsIgnoreCase(""))
            Utils.setGlideImage(this, Pref.getValue(this, Config.PREF_AVATAR, ""), conversation_contact_photo);

        img_emoji.setOnClickListener(null);
        emojIcon = new EmojIconActions(this, rel_send, edit_msg, img_emoji);
        emojIcon.setUseSystemEmoji(false);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);

        fileName = getIntent().getStringExtra("path");

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mediaController == null) return false;
                if (mediaController.isShowing()) {
                    mediaController.hide();
                } else {
                    mediaController.show();
                }
                return false;
            }
        });

        displayVideo(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaController != null) {
            mediaController.hide();
            mediaController = null;
        }
    }

    private void displayVideo(Bundle savedInstanceState) {

        currentPlaybackPosition = 0;
        isVideoPlaying = true;

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                showVideoPreview(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

        }
    }

    private void showVideoPreview(SurfaceHolder holder) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaController = new MediaController(PreviewStoryVideo.this);
                    mediaController.setAnchorView(surfaceView);
                    mediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
                        @Override
                        public void start() {
                            mediaPlayer.start();
                        }

                        @Override
                        public void pause() {
                            mediaPlayer.pause();
                        }

                        @Override
                        public int getDuration() {
                            return mediaPlayer.getDuration();
                        }

                        @Override
                        public int getCurrentPosition() {
                            return mediaPlayer.getCurrentPosition();
                        }

                        @Override
                        public void seekTo(int pos) {
                            mediaPlayer.seekTo(pos);
                        }

                        @Override
                        public boolean isPlaying() {
                            return mediaPlayer.isPlaying();
                        }

                        @Override
                        public int getBufferPercentage() {
                            return 0;
                        }

                        @Override
                        public boolean canPause() {
                            return true;
                        }

                        @Override
                        public boolean canSeekBackward() {
                            return true;
                        }

                        @Override
                        public boolean canSeekForward() {
                            return true;
                        }

                        @Override
                        public int getAudioSessionId() {
                            return mediaPlayer.getAudioSessionId();
                        }
                    });

                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();

                    videoPreviewContainer.setAspectRatio((double) videoWidth / videoHeight);

                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPlaybackPosition);

                    if (!isVideoPlaying)
                        mediaPlayer.pause();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    finish();
                    return true;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.print("Error media player playing video.");
            finish();
        }
    }

}
