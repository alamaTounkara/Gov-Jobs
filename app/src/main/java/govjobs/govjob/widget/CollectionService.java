package govjobs.govjob.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.HashMap;

import govjobs.govjob.R;

public class CollectionService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new MyWidgetRemoteViewFactory(getApplicationContext(),intent));
    }




    class MyWidgetRemoteViewFactory implements RemoteViewsFactory{
        Context mContext;
        int mAppWidgetId;
        public ArrayList<HashMap<String, String>> mDataSource = new ArrayList<HashMap<String, String>>();


        /**
         * Your custom class that implements the RemoteViewsFactory interface provides the app widget
         * with the data for the items in its collection.
         * To do this, it combines your app widget item XML layout file with a source of data.
         * This source of data could be anything from a database to a simple array.
         * The RemoteViewsFactory functions as an adapter to glue the data to the remote collection view.
         */

        /**
         * The two most important methods you need to implement for your RemoteViewsFactory
         * subclass are onCreate() and getViewAt() .
         */

        MyWidgetRemoteViewFactory(Context context, Intent intent){
            mContext = context;
            mDataSource = (ArrayList<HashMap<String, String>>) FetchDataService.mResult.clone();
        }


        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return  mDataSource.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            /**
             * getViewAt() returns a RemoteViews object corresponding to the data at the specified
             * position in the data set.
             * When your app widget is active, the system accesses these objects using their index
             * position in the array and the text they contain is displayed.
             */

            //construct a remoteView(view)for a single row
            RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.single_row);

            HashMap<String, String> temp = mDataSource.get(position);

            //set data for each row in our Listview
            row.setTextViewText(R.id.titleTxt, temp.get("positionTitle"));
            row.setTextViewText(R.id.companyNameTxt,temp.get("orgName"));
            row.setTextViewText(R.id.cityStateTxt,temp.get("JOB_LOCATIONS"));
            row.setTextViewText(R.id.dateTxt,temp.get("startDate"));

            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;//dont forget to return 1 here. otherwise your list will not show
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
