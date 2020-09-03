package com.nnoboa.istudy.ui.blog;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nnoboa.istudy.adapters.BlogAdapter;
import com.nnoboa.istudy.ui.blog.blogUtils.BlogItem;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.blog.blogUtils.LoaderBlog;

import java.util.ArrayList;
import java.util.List;


public class BlogFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<BlogItem>> {

    private static final String BLOG_REQUEST_URL = "https://www.googleapis.com/blogger/v3/blogs/" +
            "5733303841599055017/posts?key=AIzaSyAnVV5Yd1rUk9aQYsR7YfuQu1R6qEHZXfM";
    private static final int BLOG_LOADER_ID = 1;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    boolean isConnected;
    Context context;
    View emptyView;
    ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BlogAdapter blogAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_glance_blog, container, false);

        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        listView = root.findViewById(R.id.blog_list);
        emptyView = root.findViewById(R.id.blog_empty_view);
        swipeRefreshLayout = root.findViewById(R.id.refresh_view);
        blogAdapter = new BlogAdapter(context, new ArrayList<BlogItem>());
        listView.setAdapter(blogAdapter);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Uri baseUri = Uri.parse(BLOG_REQUEST_URL);
                Uri.Builder builder = baseUri.buildUpon();

                new LoaderBlog(getActivity(), builder.toString());
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlogItem currentBlog = blogAdapter.getItem(position);

                Uri blogUri = Uri.parse(currentBlog.getmUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, blogUri);

                startActivity(websiteIntent);
            }
        });
        getLoaderManager().initLoader(0,null,this);
        return root;
    }

    @NonNull
    @Override
    public Loader<List<BlogItem>> onCreateLoader(int id, @Nullable Bundle args) {
        Uri baseUri = Uri.parse(BLOG_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        return new LoaderBlog(getContext(),builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<BlogItem>> loader, List<BlogItem> data) {
        blogAdapter.clear();
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        if (data != null && !data.isEmpty()) {
            blogAdapter.addAll(data);
            swipeRefreshLayout.setRefreshing(false);
        } else if (!isConnected) {
            listView.setEmptyView(emptyView);
            swipeRefreshLayout.setRefreshing(true);
        } else if (networkInfo.isConnected()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onLoaderReset(@NonNull Loader<List<BlogItem>> loader) {
        blogAdapter.clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}