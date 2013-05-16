package com.liam.music;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.content.ContentResolver;
import android.content.Context;

public class GetSong {
	// private static final String tag = "GetSong";
	private Uri uri;
	// private String[] projection; // SimpleCursorAdapter的projection
	// private String SELECTION;
	// private String[] SELECTION_ARGS;
	private String order;
	private static Context ctx;
	private String[] mPreviousSongData;
	private String[] mNextSongData;
	private MediaMetadataRetriever mMMR;

	public GetSong(Context context) {
		uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		// projection = new String[] { MediaStore.Audio.Media._ID,
		// MediaStore.Audio.Media.TITLE,
		// MediaStore.Audio.Media.ARTIST,
		// MediaStore.Audio.Media.DATA};
		order = android.provider.MediaStore.Audio.Media.TITLE;
		mPreviousSongData = new String[2];
		mNextSongData = new String[2];
		ctx = context; // 必须使用传人的context，否则出现空指针异常
		// c = context.getContentResolver().query(URI, PROJECTION, null, null,
		// null);
		// c =
		// getApplicationContext().getContentResolver().query(getApplicationContext(),
		// URI, PROJECTION, null, null, null);
		// SELECTION = MediaStore.Audio.Media.TITLE;
		// SELECTION_ARGS = new String[] { SONG_TITLE };
	}

	public String[] getPreviousSongData(String SONG_TITLE) {
		// c = context.getContentResolver().query(URI, PROJECTION, null, null,
		// null);
		ContentResolver contentResolver = ctx.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, null, null, order);
		if (cursor == null) {
			// query failed, handle error.
			// Log.i(tag, "null Cursor");
		} else if (!cursor.moveToLast()) {
			// no media on the device
		} else {
			int titleColumn = cursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int dataColumn = cursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			do {
				if (cursor.getString(titleColumn) != null
						&& cursor.getString(titleColumn).toString()
								.equals(SONG_TITLE)) {
					if (!cursor.moveToPrevious()) {
						// 如果当前是第一首，游标移动到最后一首歌
						cursor.moveToLast();
					}
					mPreviousSongData[0] = cursor.getString(titleColumn); // 返回歌曲名称SONG_TITLE
					mPreviousSongData[1] = cursor.getString(dataColumn); // 返回歌曲地址SONG_PATH
					break; // 已经找到上一首歌，跳出while循环
				}
			} while (cursor.moveToPrevious());
		}
		return mPreviousSongData;
	}

	public String[] getNextSongData(String SONG_TITLE) {
		// c = context.getContentResolver().query(URI, PROJECTION, null, null,
		// null);
		ContentResolver contentResolver = ctx.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, null, null, order);
		if (cursor == null) {
			// query failed, handle error.
			// Log.i(tag, "null Cursor");
		} else if (!cursor.moveToFirst()) {
			// no media on the device
		} else {
			int titleColumn = cursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int dataColumn = cursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			do {
				if (cursor.getString(titleColumn) != null
						&& cursor.getString(titleColumn).toString()
								.equals(SONG_TITLE)) {
					if (!cursor.moveToNext()) {
						// 如果当前是最后一首，游标移动到第一首歌
						cursor.moveToFirst();
					}
					mNextSongData[0] = cursor.getString(titleColumn); // 返回歌曲名称SONG_TITLE
					mNextSongData[1] = cursor.getString(dataColumn); // 返回歌曲地址SONG_PATH
					break; // 已经找到下一首歌，跳出while循环
				}
			} while (cursor.moveToNext());
		}
		return mNextSongData;
	}

	public byte[] getAlbumCover(String path) {
		mMMR = new MediaMetadataRetriever();
		mMMR.setDataSource(path);
		return mMMR.getEmbeddedPicture();
	}

}