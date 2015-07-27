package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import barqsoft.footballscores.service.myFetchService;

public class ScoresWidgetProvider extends AppWidgetProvider {

    private static final String TAG = ScoresWidgetProvider.class.getCanonicalName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        Log.d(TAG, "onUpdate() called");
        context.startService(new Intent(context, ScoresWidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent)  {

        Log.d(TAG, "onReceive() called");
        super.onReceive(context, intent);

        if (myFetchService.ACTION_DATA_UPDATED.equals(intent.getAction()))  {
            context.startService(new Intent(context, ScoresWidgetIntentService.class));
        }

    }
}
