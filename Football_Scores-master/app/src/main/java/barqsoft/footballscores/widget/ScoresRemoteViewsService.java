package barqsoft.footballscores.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class ScoresRemoteViewsService extends RemoteViewsService {

    private final String TAG = ScoresRemoteViewsFactory.class.getCanonicalName();

    public ScoresRemoteViewsService()  {
        Log.d(TAG, getClass().getSimpleName() + " Created!");

    }


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "Entering onGetViewFactory()");
        return new ScoresRemoteViewsFactory(getApplicationContext(), intent);
    }


}
