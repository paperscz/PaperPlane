package com.marktony.zhihudaily.data.source.local;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.marktony.zhihudaily.data.DoubanMomentNewsPosts;
import com.marktony.zhihudaily.data.source.datasource.DoubanMomentNewsDataSource;
import com.marktony.zhihudaily.database.AppDatabase;
import com.marktony.zhihudaily.database.DatabaseCreator;

import java.util.List;

/**
 * Created by lizhaotailang on 2017/5/21.
 */

public class DoubanMomentNewsLocalDataSource implements DoubanMomentNewsDataSource {

    @Nullable
    private static DoubanMomentNewsLocalDataSource INSTANCE = null;

    @Nullable
    private AppDatabase mDb = null;

    private DoubanMomentNewsLocalDataSource(@NonNull Context context) {
        DatabaseCreator creator = DatabaseCreator.getInstance();
        if (!creator.isDatabaseCreated()) {
            creator.createDb(context);
        }
        mDb = creator.getDatabase();
    }

    public static DoubanMomentNewsLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DoubanMomentNewsLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getDoubanMomentNews(boolean forceUpdate, boolean clearCache, long date, @NonNull LoadDoubanMomentDailyCallback callback) {
        if (mDb == null) {
            callback.onDataNotAvailable();
            return;
        }

        new AsyncTask<Void, Void, List<DoubanMomentNewsPosts>>() {

            @Override
            protected List<DoubanMomentNewsPosts> doInBackground(Void... voids) {
                return mDb.doubanMomentNewsDao().queryAll();
            }

            @Override
            protected void onPostExecute(List<DoubanMomentNewsPosts> list) {
                super.onPostExecute(list);
                if (list == null) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onNewsLoaded(list);
                }
            }
        }.execute();
    }

    @Override
    public void getFavorites(@NonNull LoadDoubanMomentDailyCallback callback) {
        if (mDb == null) {
            callback.onDataNotAvailable();
            return;
        }

        new AsyncTask<Void, Void, List<DoubanMomentNewsPosts>>() {

            @Override
            protected List<DoubanMomentNewsPosts> doInBackground(Void... voids) {
                return mDb.doubanMomentNewsDao().queryAllFavorites();
            }

            @Override
            protected void onPostExecute(List<DoubanMomentNewsPosts> list) {
                super.onPostExecute(list);
                if (list == null) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onNewsLoaded(list);
                }
            }
        }.execute();
    }

    @Override
    public void getItem(int id, @NonNull GetNewsItemCallback callback) {
        if (mDb == null) {
            callback.onDataNotAvailable();
            return;
        }

        new AsyncTask<Void, Void, DoubanMomentNewsPosts>() {

            @Override
            protected DoubanMomentNewsPosts doInBackground(Void... voids) {
                return mDb.doubanMomentNewsDao().queryItemById(id);
            }

            @Override
            protected void onPostExecute(DoubanMomentNewsPosts item) {
                super.onPostExecute(item);
                if (item == null) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onItemLoaded(item);
                }
            }

        }.execute();
    }

    @Override
    public void favoriteItem(int itemId, boolean favorited) {

    }

    @Override
    public void outdateItem(int itemId) {

    }

    @Override
    public void saveAll(@NonNull List<DoubanMomentNewsPosts> list) {
        if (mDb != null) {
            mDb.beginTransaction();
            try {
                mDb.doubanMomentNewsDao().insertAll(list);
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }
        }
    }

}