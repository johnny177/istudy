package com.nnoboa.istudy.ui.blog.blogUtils;

import android.content.AsyncTaskLoader;
import android.content.Context;

import androidx.annotation.Nullable;

import java.util.List;

public class BlogLoader extends AsyncTaskLoader<List<BlogItems>> {

    private static final String LOG_TAG = BlogLoader.class.getSimpleName();


    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link BlogLoader}
     *
     * @param context of the activity
     * @param url     to load data from
     * @return
     */
    public BlogLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<BlogItems> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        return QueryUtils.fetchBlogData(mUrl);
    }


}
