package barqsoft.footballscores.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class ScoresRemoteViewService extends RemoteViewsService {

    private final String TAG = ScoresRemoteViewFactory.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "Entering onGetViewFactory()");
        return new ScoresRemoteViewFactory(getApplicationContext(), intent);
    }

}
