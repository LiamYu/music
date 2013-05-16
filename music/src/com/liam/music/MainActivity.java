package com.liam.music;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	ViewPager mViewPager;
	SectionsPagerAdapter mSectionsPagerAdapter;
	// private GetAdapter mGetAdapter;
	// //自定义的用于取回歌曲列表适配器的类，返回SimpleCursorAdapter类型
	// private CursorLoader mCursorLoader; // 游标装载器
	private final Uri songlist_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private final Uri album_uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
	private final Uri artist_uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
	/*
	 * private final String[] projection = new String[] {
	 * MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA,
	 * MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
	 * MediaStore.Audio.Media.ALBUM}; //
	 * projection是指要返回的列,CursorLoader必须要有_ID列，否则运行时出错
	 */
	private final String[] projection_songlist = new String[] {
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST }; // SimpleCursorAdapter的projection
	private final String[] projection_album = new String[] {
			MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST }; // SimpleCursorAdapter的projection
	private final String[] projection_artist = new String[] { MediaStore.Audio.Artists.ARTIST }; // SimpleCursorAdapter的projection
	// seleciton 是where子句
	private final String selection_artistsub = "(("
			+ MediaStore.Audio.Albums.ARTIST + "=?))";
	// private final String[] selectionArgs_artistsub = new String[] {};
	private final String sort_order = "TITLE"; // CursorLoader的排列顺序：按歌曲名称排列
	private final int loader_songlist = 0; // 装载器Loader的唯一ID，Activity/Fragment里唯一
	private final int loader_artist = 1;
	private final int loader_album = 2;
	private final int loader_artistsub = 3;
	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
	// The adapter that binds our data to the ListView
	public static SimpleCursorAdapter mSongListAdapter;
	public static SimpleCursorTreeAdapter mArtistListAdapter;
	public static SimpleCursorAdapter mAlbumListAdapter;
	public static String song_title;
	public static String song_path;
	public static String album_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		/*
		 * The Activity (which implements the LoaderCallbacks<Cursor> interface)
		 * is the callbacks object through which we will interact with the
		 * LoaderManager. The LoaderManager uses this object to instantiate the
		 * Loader and to notify the client when data is made
		 * available/unavailable.
		 */
		mCallbacks = this;

		/*
		 * Initialize the adapter. Note that we pass a 'null' Cursor as the
		 * third argument. We will pass the adapter a Cursor only when the data
		 * has finished loading for the first time (i.e. when the LoaderManager
		 * delivers the data to onLoadFinished). Also note that we have passed
		 * the "0" flag as the last argument. This prevents the adapter from
		 * registering a ContentObserver for the Cursor (the CursorLoader will
		 * do this for us!).
		 */
		mSongListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null, projection_songlist,
				new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		mAlbumListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null, projection_album,
				new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		/*
		 * mArtistListAdapter = new SimpleCursorAdapter(this,
		 * android.R.layout.simple_expandable_list_item_1, null,
		 * projection_artist, new int[] { android.R.id.text1 }, 0);
		 */
		mArtistListAdapter = new SimpleCursorTreeAdapter(this, null,
				android.R.layout.simple_expandable_list_item_1,
				android.R.layout.simple_expandable_list_item_2,
				projection_artist, new int[] { android.R.id.text1 },
				loader_album, projection_album, new int[] { android.R.id.text1,
						android.R.id.text2 }) {

			@Override
			protected Cursor getChildrenCursor(Cursor groupCursor) {
				// Given the group, we return a cursor for all the children
				// within that
				// group
				final long idGroup = groupCursor.getLong(groupCursor.getColumnIndex(MediaStore.Audio.Artists._ID));
		        Bundle bundle = new Bundle();
		        bundle.putLong("idGroup", idGroup);
//				int groupPos = groupCursor.getPosition();
				Loader<Cursor> loader = getLoaderManager().getLoader(loader_artistsub);
				if (loader != null && !loader.isReset()) {
					getLoaderManager()
							.restartLoader(loader_artistsub, bundle, mCallbacks);
				} else {
					getLoaderManager().initLoader(loader_artistsub, bundle, mCallbacks);
				}
				return null;
			}
		};
		// mGetAdapter = new GetAdapter();
		// Associate the (now empty) adapter with the ListView.

		/*
		 * Initialize the Loader with id '1' and callbacks 'mCallbacks'. If the
		 * loader doesn't already exist, one is created. Otherwise, the already
		 * created Loader is reused. In either case, the LoaderManager will
		 * manage the Loader across the Activity/Fragment lifecycle, will
		 * receive any new loads once they have completed, and will report this
		 * new data back to the 'mCallbacks' object.
		 */
		LoaderManager lm = getLoaderManager();
		/*
		 * 参数: 1、 第一个参数：0 为Loader的唯一标识ID； 2、 第二个参数： 为Loader的构造器可选参数，这里为null； 3、
		 * 第三个参数：this，这里表示当前Activity对象或者Fragment对象，提供给LoaderManager对象进行数据汇报。
		 */
		lm.initLoader(loader_songlist, null, mCallbacks);
		lm.initLoader(loader_artist, null, mCallbacks);
		lm.initLoader(loader_album, null, mCallbacks);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case loader_songlist:
			// 用以下的查询参数创建一个新的游标加载器CursorLoader
			return new CursorLoader(MainActivity.this, songlist_uri, null,
					null, null, sort_order); // 返回Cursor类型
		case loader_album:
			return new CursorLoader(MainActivity.this, album_uri, null,
					null, null, null);
		case loader_artist:			
			return new CursorLoader(MainActivity.this, artist_uri, null,
					null, null, null);
			
		// artist的子列表，即专辑
		case loader_artistsub:
			long idGroup = bundle.getLong("idGroup");
			String[] selectionArgs_artistsub = new String[] { String
					.valueOf(idGroup) };
			return new CursorLoader(this, album_uri, null, selection_artistsub,
					selectionArgs_artistsub, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		// A switch-case is useful when dealing with multiple Loaders/IDs
		case loader_songlist:
			/*
			 * The asynchronous load is complete and the data is now available
			 * for use. Only now can we associate the queried Cursor with the
			 * SimpleCursorAdapter.
			 */
			mSongListAdapter.swapCursor(cursor);
			break;
		case loader_album:
			mAlbumListAdapter.swapCursor(cursor);
			// cursor.getPosition();
			// mArtistListAdapter.setChildrenCursor(loader.getId(), cursor);
			break;
		case loader_artist:
			mArtistListAdapter.setGroupCursor(cursor);
			break;
		case loader_artistsub:
			mArtistListAdapter.setChildrenCursor(loader_artistsub, cursor);
		}
		// The listview now displays the queried data.
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		/*
		 * For whatever reason, the Loader's data is now unavailable. Remove any
		 * references to the old data by replacing it with a null Cursor.
		 */
		switch (loader.getId()) {
		case loader_songlist:
			mSongListAdapter.swapCursor(null);
			break;
		case loader_album:
			mAlbumListAdapter.swapCursor(null);
//			mArtistListAdapter.setChildrenCursor(loader.getId(), null);
			break;
		case loader_artist:
			mArtistListAdapter.setGroupCursor(null);
			break;
		case loader_artistsub:
			mArtistListAdapter.setChildrenCursor(loader_artistsub, null);
		}
	}

	// 内部类
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			SectionFragment sf = new SectionFragment();
			Bundle args = new Bundle();
			args.putInt(SectionFragment.ARG_SECTION_NUMBER, position + 1);
			sf.setArguments(args);
			return sf;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_songlist);
			case 1:
				return getString(R.string.title_artist);
			case 2:
				return getString(R.string.title_album);
			}
			return null;
		}
	}

	/*
	 * //内部类：展开式列表适配器 public class ExpandableAdapter extends
	 * BaseExpandableListAdapter{
	 * }
	 */

	// 内部类
	public static class SectionFragment extends Fragment {
		private ListView mSongListView;
		private ListView mAlbumListView;
		private ExpandableListView mArtistListView;
		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 1:
				View song_root_view = inflater.inflate(
						R.layout.fragment_song_list, container, false);
				mSongListView = (ListView) song_root_view
						.findViewById(R.id.song_list);
				mSongListView.setAdapter(mSongListAdapter);
				mSongListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Cursor c = (Cursor) mSongListAdapter.getItem(position); // position是Cursor中的，id是MediaStore中的
						int pathColumn = c
								.getColumnIndex(MediaStore.Audio.Media.DATA);
						int titleColumn = c
								.getColumnIndex(MediaStore.Audio.Media.TITLE);
						song_path = c.getString(pathColumn);
						song_title = c.getString(titleColumn);
						Toast.makeText(getActivity(), "即将播放:" + song_title,
								Toast.LENGTH_LONG).show();
						Intent it = new Intent(getActivity(),
								com.liam.music.PlayMySongActivity.class);
						it.putExtra(song_title, song_title);
						it.putExtra(song_path, song_path);
						startActivity(it);
					}
				});
				return song_root_view;
			case 2:
				View artist_root_view = inflater.inflate(
						R.layout.fragment_artist, container, false);
				mArtistListView = (ExpandableListView) artist_root_view
						.findViewById(R.id.artist_list);
				mArtistListView.setAdapter(mArtistListAdapter);
				return artist_root_view;
			case 3:
				View album_root_view = inflater.inflate(
						R.layout.fragment_album, container, false);
				mAlbumListView = (ListView) album_root_view
						.findViewById(R.id.album_list);
				mAlbumListView.setAdapter(mAlbumListAdapter);
				mAlbumListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Cursor c = (Cursor) mAlbumListAdapter.getItem(position); // position是Cursor中的，id是MediaStore中的
						int idColumn = c
								.getColumnIndex(MediaStore.Audio.Albums._ID);
						int albumColumn = c
								.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
						album_id = c.getString(idColumn);
						String album_title = c.getString(albumColumn);
						Toast.makeText(getActivity(), "即将展示专辑:" + album_title,
								Toast.LENGTH_LONG).show();
						Intent it = new Intent(getActivity(),
								com.liam.music.AlbumSongListActivity.class);
						it.putExtra(album_id, album_id);
						startActivity(it);
					}
				});
				return album_root_view;
			}
			return null;
		}

	}

	/*
	 * public SimpleCursorAdapter getAdapter() { return mAdapter; }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_list_actitivy, menu);
		return true;
	}

}
