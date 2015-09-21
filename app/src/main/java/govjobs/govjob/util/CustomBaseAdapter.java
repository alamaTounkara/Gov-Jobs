package govjobs.govjob.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import govjobs.govjob.R;

/**
 * Created by alamatounkara on 7/29/15.
 */
public class CustomBaseAdapter extends BaseAdapter {

    //we use an arraylist mainly in order to get the size of the 3 views array at once
    ArrayList<HashMap<String, String>> dataSource = new ArrayList<HashMap<String, String>>();
    private Context mContext;

    public CustomBaseAdapter(Context c, ArrayList<HashMap<String, String>> data){
        this.mContext = c;
        this.dataSource = data;
    }


    //view holder pattern class for optimezation purpose only (make the listview(175% faster)
    private class MyViewHolder {
        TextView titleTxt,statusTxt,companyTxt,stateTxt,goTxt,dateTxt;

        public MyViewHolder(View v){
            //let access the myImage in RelativeLayout in our single_row.xml
            //img = (ImageView) v.findViewById(R.id.imageView);

            titleTxt= (TextView) v.findViewById(R.id.titleTxt);
           // statusTxt= (TextView) v.findViewById(R.id.jobStatusTxt);
            companyTxt= (TextView) v.findViewById(R.id.companyNameTxt);
            stateTxt= (TextView) v.findViewById(R.id.cityStateTxt);
            dateTxt= (TextView) v.findViewById(R.id.dateTxt);
            goTxt= (TextView) v.findViewById(R.id.goTxt);
        }
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView; //convertView will be null for the creation of row
        MyViewHolder mMyViewHolder = null ; //our view holder class
        if (row == null){//if yes, the row is not created yet, we need to do all first creations
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_listrow, parent, false ); //parent represent our mListView here
            mMyViewHolder = new MyViewHolder(row);
            row.setTag(mMyViewHolder); //save mMyViewHolder for future reference
        }else{
            mMyViewHolder = (MyViewHolder) row.getTag();
        }

        HashMap<String, String> temp = dataSource.get(position);

//        private static final String JOB_ID = "id";
//        private static final String JOB_POSITION_TITLE = "position_title";
//        private static final String JOB_ORGANISATION_NAME = "organization_name";
//        private static final String JOB_STATE_CODE = "rate_interval_code";
//        private static final String JOB_MINIMUM_SALARY = "minimum";
//        private static final String JOB_MAXIMUM_SALARY = "maximum";
//        private static final String JOB_START_DATE = "start_date";
//        private static final String JOB_END_DATE = "end_date";
//        private static final String JOB_LOCATIONS = "locations";
//        private static final String JOB_URL = "url";

        String jj = temp.get("positionTitle");
//        {id=ng:ohio:1205617, startDate=2015-07-29, actualLocation=Athens County, OH, minimum=24.9,
//         maximum=34.83, positionTitle=Psychiatric/MR Nurse, stateCode=PH, endDate=2015-08-05,
//        orgName=State of Ohio, url=http://agency.governmentjobs.com/ohio/default.cfm?action=viewjob&jobid=1205617},
//               // Toast.makeText(this, ""+jj, Toast.LENGTH_LONG).show();

        mMyViewHolder.titleTxt.setText(temp.get("positionTitle"));
        mMyViewHolder.stateTxt.setText(temp.get("JOB_LOCATIONS"));
       // mMyViewHolder.statusTxt.setText(temp.get("startDate"));
        mMyViewHolder.companyTxt.setText(temp.get("orgName"));
        //mMyViewHolder.goTxt.setText(temp.get("phone"));
        mMyViewHolder.dateTxt.setText(temp.get("startDate"));
//        mMyViewHolder.lic_noTxt.setText(temp.get("lic_no"));
//        mMyViewHolder.typeTxt.setText(temp.get("type"));
//        mMyViewHolder.addressTxt.setText(temp.get("_addresse"));


        return row;
    }


}