package com.nnoboa.istudy.ui.blog.blogUtils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class LoaderBlog extends AsyncTaskLoader<List<BlogItem>> {
    String url;

    /**
     * Constructs a new {@link LoaderBlog}
     *
     * @param context of the activity
     * @param url     to load data from
     * @return
     */
    public LoaderBlog(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public List<BlogItem> loadInBackground() {
        if(url == null) {
            return null;
        }
        return QueryUtils.fetchBlogData(url);
    }
}
