package com.liam.music.activity;

import com.liam.music.R;
import com.liam.music.entity.GetSong;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayMySongActivity extends Activity {
	private TextView mTextView;
	private static MediaPlayer mMediaPlayer = new MediaPlayer(); // 必须设置为静态并初始化，以防止创建多个实例
	// private boolean pause; // 标记，是否之前已暂停过
	private int position; // onPause之前的歌曲播放位置
	private SeekBar music_seekbar; // 进度条
	private String song_title;
	private String song_path;
	private String album_id;
	private ImageButton mPauseButton;
	private TextView progressText;
	private TextView durationText;
	private GetSong mGetSong;
	private String[] mPreviousSongData = new String[2];
	// private ArrayList<String> mNextSongData;
	private String[] mNextSongData = new String[2];
	private ImageView mAlbumCover;
	private byte[] img = new byte[10];

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_my_song);
		mGetSong = new GetSong(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		Intent it = getIntent();
		mAlbumCover = (ImageView) findViewById(R.id.album_cover);		
		if (it.getStringExtra("album_id") != null) {
			album_id = it.getStringExtra("album_id");
			song_title = it.getStringExtra("song_title");
			song_path = it.getStringExtra("song_path");
		} else {
			song_path = it.getStringExtra("song_path");
			song_title = it.getStringExtra("song_title");
		}
		mTextView = (TextView) findViewById(R.id.song_title);		
		mTextView.setText(song_title);
		progressText = (TextView) findViewById(R.id.song_progress);
		durationText = (TextView) findViewById(R.id.song_duration);
		if (song_path != null) {
			setAlbumCover();
		}
		play(0);
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
		music_seekbar = (SeekBar) this.findViewById(R.id.music_seekbar);
		music_seekbar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser == true) {
							mMediaPlayer.seekTo(progress);
							handler.post(updateThread);
						}
					}
				});
		mMediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						handler.removeCallbacks(updateThread);
						music_seekbar.setProgress(0);
						progressText.setText("0:00");
						mPauseButton = (ImageButton) findViewById(R.id.pause);
						mPauseButton
								.setImageResource(android.R.drawable.ic_media_play);
					}
				});
	}

	Handler handler = new Handler();
	Runnable updateThread = new Runnable() {

		@Override
		public void run() {
			position = mMediaPlayer.getCurrentPosition();
			music_seekbar.setProgress(position); // 获得歌曲当前的播放位置并设置成播放进度条的值
			progressText.setText(getTime(position));
			handler.postDelayed(updateThread, 100);
		}

	};

	private final class MyPhoneListener extends PhoneStateListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.telephony.PhoneStateListener#onCallStateChanged(int,
		 * java.lang.String)
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // 来电
				if (mMediaPlayer.isPlaying()) {
					position = mMediaPlayer.getCurrentPosition();
					mMediaPlayer.stop();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (position > 0 && song_path != null) {
					play(position);
					position = 0;
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_my_song, menu);
		return true;
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: // This ID represents the
	 * Home or Up button. In the case of this // activity, the Up button is
	 * shown. Use NavUtils to allow users // to navigate up one level in the
	 * application structure. For // more details, see the Navigation pattern on
	 * Android Design: // //
	 * http://developer.android.com/design/patterns/navigation.html#up-vs-back
	 * // NavUtils.navigateUpFromSameTask(this); return true; } return
	 * super.onOptionsItemSelected(item); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 * 
	 * @Override protected void onPause() { if(mediaplayer.isPlaying()) {
	 * position = mediaplayer.getCurrentPosition(); mediaplayer.stop(); }
	 * super.onPause(); }
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 * 
	 * @Override protected void onResume() { if(position > 0 && path != null) {
	 * play(); mediaplayer.seekTo(position); position = 0; } super.onResume(); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 * 
	 * @Override protected void onDestroy() { mMediaPlayer.release();
	 * mMediaPlayer = null; super.onDestroy(); }
	 */

	public void mediaplay(View v) {
		switch (v.getId()) {
		// case R.id.playbutton:
		// play(0);
		// break;
		case R.id.pause:
			handler.post(updateThread);
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause(); // 暂停
				// pause = true;
				((ImageButton) v)
						.setImageResource(android.R.drawable.ic_media_play);
			} else {
				mMediaPlayer.start(); // 继续播放
				// pause = false;
				((ImageButton) v)
						.setImageResource(android.R.drawable.ic_media_pause);
			}
			break;
		case R.id.previous:
			if (!mMediaPlayer.isPlaying()) {
				mPauseButton = (ImageButton) (this.findViewById(R.id.pause));
				mPauseButton
						.setImageResource(android.R.drawable.ic_media_pause);
				// pause = false;
			} // 如果不处于播放状态，并将暂停按钮状态设置为点击暂停
			music_seekbar.setProgress(0); // 不管怎样，进度条始终回到0
			if (mMediaPlayer.getCurrentPosition() > 5000) {
				mMediaPlayer.seekTo(0); // 歌曲从头播放
			} else {
				if (album_id != null) {
					mPreviousSongData = mGetSong.getPreviousAlbumSongData(song_title, album_id);
				} else {
					mPreviousSongData = mGetSong.getPreviousSongData(song_title); // 取回下首歌的title和path,下标分别为0, 1
				}				
				song_title = mPreviousSongData[0];
				song_path = mPreviousSongData[1];
				mTextView.setText(song_title);
				mMediaPlayer.stop();
			}
			if (song_path != null) {
				setAlbumCover();
			}
			play(0);
			/*
			 * if (music_seekbar.getProgress() > 5000) { mMediaPlayer.seekTo(0);
			 * // 歌曲从头播放 if (!mMediaPlayer.isPlaying()) { mPauseButton =
			 * (ImageButton) (this.findViewById(R.id.pause));
			 * mPauseButton.setImageResource(android.R.drawable.ic_media_pause);
			 * play(0); // pause = false; } //如果不处于播放状态，启用播放，并将暂停按钮状态设置为点击暂停 }
			 * else { if (!mMediaPlayer.isPlaying()) { mPauseButton =
			 * (ImageButton) (this.findViewById(R.id.pause));
			 * mPauseButton.setImageResource(android.R.drawable.ic_media_pause);
			 * // pause = false; } //如果不处于播放状态，将暂停按钮状态设置为点击暂停，停止播放后切换到上一首
			 * mPreviousSongData = mGetSong.getPreviousSongData(SONG_TITLE);
			 * //取回下首歌的title和path,下标分别为0, 1 SONG_TITLE = mPreviousSongData[0];
			 * SONG_PATH = mPreviousSongData[1]; mTextView.setText(SONG_TITLE);
			 * mMediaPlayer.stop(); // 停止播放 play(0); }
			 */
			break;
		case R.id.next:
			if (album_id != null) {
				mNextSongData = mGetSong.getNextAlbumSongData(song_title, album_id); 
			} else {
				mNextSongData = mGetSong.getNextSongData(song_title); // 取回下首歌的title和path,下标分别为0, 1
			}
			song_title = mNextSongData[0];
			song_path = mNextSongData[1];
			mTextView.setText(song_title);
			mMediaPlayer.stop(); // 停止播放
			music_seekbar.setProgress(0);
			if (!mMediaPlayer.isPlaying()) {
				mPauseButton = (ImageButton) (this.findViewById(R.id.pause));
				mPauseButton
						.setImageResource(android.R.drawable.ic_media_pause);
			}
			if (song_path != null) {
				setAlbumCover();
			}
			play(0);
			/*
			 * Toast.makeText(getApplicationContext(), "即将播放:" + SONG_TITLE ,
			 * Toast.LENGTH_LONG) .show();
			 */
			break;
		}
	}

	/*
	 * public void viewSonglist(View v) { Intent it = new Intent(v.getContext(),
	 * com.liam.music_list.SongListActivity.class); startActivity(it); }
	 */
	private void setAlbumCover() {
		img = mGetSong.getAlbumCover(song_path);
		if (img != null) {
			mAlbumCover.setImageBitmap(BitmapFactory.decodeByteArray(img, 0,
					img.length)); // 设置专辑图片
		} else {
			mAlbumCover.setImageResource(R.drawable.default_cover);
		}
	}

	private String getTime(int t) {
		String mTime;
		int time[] = new int[2];
		time[0] = (int) (t / 60000); // 分
		time[1] = (int) (t / 1000 - time[0] * 60); // 秒
		if (time[1] < 10) {
			mTime = Integer.toString(time[0]) + ":0"
					+ Integer.toString(time[1]);
		} else {
			mTime = Integer.toString(time[0]) + ":" + Integer.toString(time[1]);
		}
		return mTime;
	}

	private void play(int position) {
		try {
			mMediaPlayer.reset(); // 把各项参数恢复初始状态
			mMediaPlayer.setDataSource(song_path);
			mMediaPlayer.prepare(); // 进行缓冲
			mMediaPlayer.setOnPreparedListener(new PrepareListener(position));
			progressText.setText(getTime(position));
			durationText.setText(getTime(mMediaPlayer.getDuration()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private final class PrepareListener implements OnPreparedListener {
		private int position;

		public PrepareListener(int position) {
			this.position = position;
		}

		@Override
		public void onPrepared(MediaPlayer arg0) {
			mMediaPlayer.start(); // 开始播放
			music_seekbar.setMax(mMediaPlayer.getDuration()); // 获取歌曲的长度并设置成播放进度条的最大值
			handler.post(updateThread);
			if (position > 0) {
				mMediaPlayer.seekTo(position);
			}
		}
	}

}
