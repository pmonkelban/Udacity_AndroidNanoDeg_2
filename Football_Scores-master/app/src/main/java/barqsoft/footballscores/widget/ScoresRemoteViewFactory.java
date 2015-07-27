package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

public class ScoresRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private final String TAG = ScoresRemoteViewFactory.class.getSimpleName();

    private int appWidgetId;

    private Context mContext;
    private Cursor cursor;

    public static final int IDX_MATCH_TIME = 2;
    public static final int IDX_HOME_TEAM_NAME = 3;
    public static final int IDX_AWAY_TEAM_NAME = 4;
    public static final int IDX_HOME_TEAM_SCORE = 6;
    public static final int IDX_AWAY_TEAM_SCORE = 7;

    public ScoresRemoteViewFactory(Context context, Intent intent) {
        this.mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d(TAG,  "" + appWidgetId);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Entering onDataSetChanged()");

        cursor = mContext.getContentResolver()
                .query(DatabaseContract.BASE_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "Entering onDataSetChanged()");


        if (cursor != null) cursor.close();

        cursor = mContext.getContentResolver()
                .query(DatabaseContract.BASE_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Entering onDestroy()");

        if (cursor != null)
            cursor.close();
    }

    @Override
    public int getCount() {
        Log.d(TAG, "Entering getCount()");

        if (cursor != null)
            return cursor.getCount();
        else
            return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "Entering getViewAt() position:" + position);

        String homeTeamName = "";
        String awayTeamName = "";
        String matchTime = "";
        String matchScore = "";

        if (cursor.moveToPosition(position)) {
            homeTeamName = cursor.getString(IDX_HOME_TEAM_NAME);
            awayTeamName = cursor.getString(IDX_AWAY_TEAM_NAME);
            matchTime = cursor.getString(IDX_MATCH_TIME);
            matchScore =
                    Utilies.getScores(cursor.getInt(IDX_HOME_TEAM_SCORE),
                            cursor.getInt(IDX_AWAY_TEAM_SCORE), mContext);
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        views.setTextViewText(R.id.widget_home_team, homeTeamName);
        views.setTextViewText(R.id.widget_away_team, awayTeamName);
        views.setTextViewText(R.id.widget_final_score, matchScore);
        views.setTextViewText(R.id.widget_date, matchTime);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.d(TAG, "Entering getLoadingView()");

        return null;
    }

    @Override
    public int getViewTypeCount() {
        Log.d(TAG, "Entering getViewTypeCount()");

        return 1;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "Entering getItemId()");

        if (cursor != null)
            return cursor.getLong(cursor.getColumnIndex(DatabaseContract.scores_table.MATCH_ID));
        else
            return 0;
    }

    @Override
    public boolean hasStableIds() {
        Log.d(TAG, "Entering hasStableIds()");

        return true;
    }
}