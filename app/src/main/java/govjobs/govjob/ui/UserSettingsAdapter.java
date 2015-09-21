package govjobs.govjob.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import govjobs.govjob.util.AccountManger;
import govjobs.govjob.R;


/**
 * Created by alamatounkara on 8/1/15.
 */
public class UserSettingsAdapter extends BaseAdapter {

    //we use an arraylist mainly in order to get the size of the 3 views array at once
    ArrayList<HashMap<Integer,String>> dataSource = new ArrayList<HashMap<Integer,String>>();
    private Context mContext;
    private TextView jobTotalTxt;
    private AccountManger mAccountManger;
    private long mUserId;

    public UserSettingsAdapter(Context c, ArrayList<HashMap<Integer,String>> data, TextView textView,
                                AccountManger manager, long userid) {
        this.mContext = c;
        this.dataSource = data;
        jobTotalTxt = textView; //the job total textview on the dashborad that we want to update
        mAccountManger = manager;
        mUserId = userid;

    }

    @Override
    public int getCount() {
        //we use an arraylist mainly in order to get the size of the 3 views array at once
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
            /*
             *@return object(item) at index position
             */
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
            /*
             *this method is mostly used in databases to populate data.
             *
            */
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView; //convertView will be null for the creation of row
        MyViewHolder mMyViewHolder = null; //our view holder class
        if (row == null) {//if yes, the row is not created yet, we need to do all first creations
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.usersettings, parent, false); //parent represent our mListView here
            mMyViewHolder = new MyViewHolder(row);
            row.setTag(mMyViewHolder); //save mMyViewHolder for future reference
        } else {
            mMyViewHolder = (MyViewHolder) row.getTag();
        }
        HashMap<Integer, String> temp = dataSource.get(position);
        Iterator<Map.Entry<Integer, String>> jobs = temp.entrySet().iterator();
        final Map.Entry<Integer,String> entry = jobs.next();

        try {
            JSONObject obj = new JSONObject(entry.getValue());
            String title = obj.getString("JobTitle");
            String location = obj.getString("Locations");
            mMyViewHolder.titleTxt.setText(title + " - " + location);
            mMyViewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   dataSource.remove(position);
                    notifyDataSetChanged();
                    jobTotalTxt.setText("You have " + dataSource.size() + " job(s) saved");
                    boolean c = mAccountManger.deleteJob(mUserId,entry.getKey());
                    //mAccountManger.deleteJob(mUserId, position);
                }
            });

        } catch (Throwable t) {
            Log.e("json conversion", "" + t.fillInStackTrace());

            Log.e("json conversion", "" + t);
        }

        return row;
    }

    //view holder pattern class for optimezation purpose only (make the listview(175% faster)
    private class MyViewHolder {
        TextView titleTxt;
        ImageView image;
        public MyViewHolder(View v) {
            image  = (ImageView) v.findViewById(R.id.deleteJobImageView);
            titleTxt = (TextView) v.findViewById(R.id.jobNameTxt);
        }
    }


}