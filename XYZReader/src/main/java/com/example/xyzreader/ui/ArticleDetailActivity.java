package com.example.xyzreader.ui;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.utilities.GlideApp;
import com.example.xyzreader.utilities.MiscUtils;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;


    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private ImageView mThumbnailImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
        }
        setContentView(R.layout.activity_article_detail);
        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
            supportPostponeEnterTransition();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getLoaderManager().initLoader(0, null, this);

        mThumbnailImageView = findViewById(R.id.detail_thumbnail_iv);
        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        ImageButton floatingActionButton = (FloatingActionButton) findViewById(R.id.share_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText(String.format(getString(R.string.format_text_to_share), mCursor.getString(ArticleLoader.Query.TITLE)))
                        .getIntent(), getString(R.string.action_share)));
            }
        });


        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    ArticleListActivity.currentPosition = position;
                }

                Snackbar.make(mPager, R.string.want_to_read_ebook, Snackbar.LENGTH_LONG)
                        .setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                        .setAction(R.string.yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View aView) {
                                startReadActivity();
                            }
                        }).show();

            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
            }
        }

    }


    private void loadThumbnailImageView() {
        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
            mThumbnailImageView.setTransitionName(getString(R.string.shared_element_image_view));
        }

        GlideApp.with(this)
                .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                .override(600,400)
                .into(new Target<Drawable>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onStop() {
                    }

                    @Override
                    public void onDestroy() {
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mThumbnailImageView.setImageDrawable(resource);
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {
                    }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {
                    }

                    @Override
                    public void setRequest(@Nullable Request request) {
                    }

                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }
                });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void readMore(View view) {
        startReadActivity();
    }

    public void startReadActivity() {
        Intent intent = new Intent(ArticleDetailActivity.this, ReadActivity.class);

        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(ArticleDetailActivity.this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (mCursor != null) {
                mCursor.moveToPosition(position);
                loadThumbnailImageView();
            }
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
