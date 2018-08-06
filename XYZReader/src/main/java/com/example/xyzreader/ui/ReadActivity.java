package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.Window;
import android.widget.ListView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.utilities.MiscUtils;

import java.util.ArrayList;
import java.util.Arrays;


public class ReadActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MiscUtils.LOLLIPOP_AND_HIGHER) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Fade fade = new Fade();
            getWindow().setEnterTransition(fade);
        }
        setContentView(R.layout.activity_read);

        getLoaderManager().initLoader(0, null, this);

    }


    private void prepareBodyText() {
        String body = mCursor.getString(ArticleLoader.Query.BODY);
        String[] result = body.replaceAll("(\\r\\n){2}", "\n").replaceAll("\\r\\n", " ").split("\n");
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(result));
        BodyTextAdapter bodyTextAdapter = new BodyTextAdapter(this, arrayList);
        final ListView listView = findViewById(R.id.read_tv);
        listView.setAdapter(bodyTextAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mCursor.moveToPosition(ArticleListActivity.currentPosition);
        prepareBodyText();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
    }
}
