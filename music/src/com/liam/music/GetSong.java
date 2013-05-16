package com.liam.music;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.ContentResolver;
import android.content.Context;

public class GetSong {
	// private static final String tag = "GetSong";
	private Uri uri;
	// private String[] projection; // SimpleCursorAdapter��projection
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
		ctx = context; // ����ʹ�ô��˵�context��������ֿ�ָ���쳣
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
						// �����ǰ�ǵ�һ�ף��α��ƶ������һ�׸�
						cursor.moveToLast();
					}
					mPreviousSongData[0] = cursor.getString(titleColumn); // ���ظ�������SONG_TITLE
					mPreviousSongData[1] = cursor.getString(dataColumn); // ���ظ�����ַSONG_PATH
					break; // �Ѿ��ҵ���һ�׸裬����whileѭ��
				}
			} while (cursor.moveToPrevious());
		}
		return mPreviousSongData;
	}
	
	public String[] getPreviousAlbumSongData(String SONG_TITLE, String album_id) {
		// c = context.getContentResolver().query(URI, PROJECTION, null, null,
		// null);
		String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
		String[] selectionArgs = {album_id};
		ContentResolver contentResolver = ctx.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);
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
						// �����ǰ�ǵ�һ�ף��α��ƶ������һ�׸�
						cursor.moveToLast();
					}
					mPreviousSongData[0] = cursor.getString(titleColumn); // ���ظ�������SONG_TITLE
					mPreviousSongData[1] = cursor.getString(dataColumn); // ���ظ�����ַSONG_PATH
					break; // �Ѿ��ҵ���һ�׸裬����whileѭ��
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
						// �����ǰ�����һ�ף��α��ƶ�����һ�׸�
						cursor.moveToFirst();
					}
					mNextSongData[0] = cursor.getString(titleColumn); // ���ظ�������SONG_TITLE
					mNextSongData[1] = cursor.getString(dataColumn); // ���ظ�����ַSONG_PATH
					break; // �Ѿ��ҵ���һ�׸裬����whileѭ��
				}
			} while (cursor.moveToNext());
		}
		return mNextSongData;
	}
	
	public String[] getNextAlbumSongData(String SONG_TITLE, String album_id) {
		// c = context.getContentResolver().query(URI, PROJECTION, null, null,
		// null);
		String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
		String[] selectionArgs = {album_id};
		ContentResolver contentResolver = ctx.getContentResolver();
		Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);
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
						// �����ǰ�����һ�ף��α��ƶ�����һ�׸�
						cursor.moveToFirst();
					}
					mNextSongData[0] = cursor.getString(titleColumn); // ���ظ�������SONG_TITLE
					mNextSongData[1] = cursor.getString(dataColumn); // ���ظ�����ַSONG_PATH
					break; // �Ѿ��ҵ���һ�׸裬����whileѭ��
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