package com.example.xyzreader.ui;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.utilities.MiscUtils;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
//    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
//    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
//    private View mUpButtonContainer;
//    private View mUpButton;

    private ImageView mThumbnailImageView;
    private ImageButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);
        }
        setContentView(R.layout.activity_article_detail);
        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
            supportPostponeEnterTransition();
        }
        getLoaderManager().initLoader(0, null, this);

        mThumbnailImageView = (ImageView) findViewById(R.id.detail_thumbnail_iv);
        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.share_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText(String.format(getString(R.string.format_text_to_share), mCursor.getString(ArticleLoader.Query.TITLE)))
                        .getIntent(), getString(R.string.action_share)));
            }
        });


        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
//                mUpButton.animate()
//                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
//                        .setDuration(300);
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
                mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
//                updateUpButtonPosition();
                Snackbar.make(mPager, R.string.want_to_read_ebook, Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                        .setAction(R.string.yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View aView) {
                                startReadActivity();
                            }
                        }).show();

            }
        });

//        mUpButtonContainer = findViewById(R.id.up_container);
//
//        mUpButton = findViewById(R.id.action_up);
//        mUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSupportNavigateUp();
//            }
//        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
//                @Override
//                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
//                    view.onApplyWindowInsets(windowInsets);
//                    mTopInset = windowInsets.getSystemWindowInsetTop();
//                    mUpButtonContainer.setTranslationY(mTopInset);
//                    updateUpButtonPosition();
//                    return windowInsets;
//                }
//            });
//        }

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }

    }

    private void loadThumbnailImageView() {
        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
            mThumbnailImageView.setTransitionName(getString(R.string.shared_element_image_view));
        }
        ImageLoaderHelper.getInstance(getApplicationContext()).getImageLoader()
                .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap bitmap = imageContainer.getBitmap();
                        if (bitmap != null) {
                            mThumbnailImageView.setImageBitmap(imageContainer.getBitmap());
                            supportStartPostponedEnterTransition();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        supportStartPostponedEnterTransition();
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

//    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
//        if (itemId == mSelectedItemId) {
//            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//            updateUpButtonPosition();
//        }
//    }
//
//    private void updateUpButtonPosition() {
//        int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
//        mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
//    }

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
//            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
//            if (fragment != null) {
//                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//                updateUpButtonPosition();
//            }
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
