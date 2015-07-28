package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

public class ScoresWidgetIntentService extends IntentService {

    public static final String TAG = ScoresWidgetIntentService.class.getCanonicalName();

    public ScoresWidgetIntentService() {
        super("ScoresWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "in onHandleIntent()");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                DetailWidgetProvider.class));

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.widget_detail);

            Intent listViewIntent = new Intent(getApplicationContext(),
                    DetailWidgetRemoteViewsService.class);

            listViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            listViewIntent.setData(Uri.parse(listViewIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.widget_list, listViewIntent);

            Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, launchIntent, 0);

            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }
}
