package barqsoft.footballscores.widget;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    final String TAG = DetailWidgetRemoteViewsService.class.getCanonicalName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.d(TAG, "Entering onGetViewFactory()");

        return new RemoteViewsFactory()  {

            private final String TAG = RemoteViewsFactory.class.getCanonicalName();

            private Cursor cursor;

            public static final int IDX_ITEM_ID = 0;
            public static final int IDX_MATCH_DATE = 1;
            public static final int IDX_MATCH_TIME = 2;
            public static final int IDX_HOME_TEAM_NAME = 3;
            public static final int IDX_AWAY_TEAM_NAME = 4;
            public static final int IDX_LEAGUE = 5;
            public static final int IDX_HOME_TEAM_SCORE = 6;
            public static final int IDX_AWAY_TEAM_SCORE = 7;
            public static final int IDX_MATCH_ID = 8;
            public static final int IDX_MATCH_DAY = 9;

            @Override
            public void onCreate() {
                Log.d(TAG, "Entering onDataSetChanged()");

            }

            @Override
            public void onDataSetChanged() {
                Log.d(TAG, "Entering onDataSetChanged()");


                if (cursor != null) cursor.close();

                cursor = getContentResolver()
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

                String matchId = "";
                String homeTeamName = "";
                String awayTeamName = "";
                String matchTime = "";
                String matchScore = "";
                String matchDate = "";
                String prevMatchDate = "";

                // Get Date for previous entry
                if (position > 0)  {
                    if (cursor.moveToPosition(position - 1))  {
                        prevMatchDate = cursor.getString(IDX_MATCH_DATE);
                    }
                }

                if (cursor.moveToPosition(position)) {
                    matchId = cursor.getString(IDX_MATCH_ID);
                    homeTeamName = cursor.getString(IDX_HOME_TEAM_NAME);
                    awayTeamName = cursor.getString(IDX_AWAY_TEAM_NAME);
                    matchTime = cursor.getString(IDX_MATCH_TIME);
                    matchScore =
                            Utilies.getScores(cursor.getInt(IDX_HOME_TEAM_SCORE),
                                    cursor.getInt(IDX_AWAY_TEAM_SCORE), getApplicationContext());
                    matchDate = cursor.getString(IDX_MATCH_DATE);
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                views.setTextViewText(R.id.widget_home_team, homeTeamName);
                views.setTextViewText(R.id.widget_away_team, awayTeamName);
                views.setTextViewText(R.id.widget_final_score, matchScore);
                views.setTextViewText(R.id.widget_date, matchTime);

                if ((matchDate != null) && (!matchDate.equals(prevMatchDate))) {
                    views.setViewVisibility(R.id.widget_match_date_banner, View.VISIBLE);
                    views.setTextViewText(R.id.widget_match_date, matchDate);
                }

                final Intent fillInIntent = new Intent(getApplicationContext(), MainActivity.class);
                fillInIntent.putExtra(MainActivity.SELECTED_MATCH_ID, matchId);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

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
                    return cursor.getLong(
                            cursor.getColumnIndex(DatabaseContract.scores_table.MATCH_ID));
                else
                    return 0;
            }

            @Override
            public boolean hasStableIds() {
                Log.d(TAG, "Entering hasStableIds()");

                return true;
            }
        };
    }


}
