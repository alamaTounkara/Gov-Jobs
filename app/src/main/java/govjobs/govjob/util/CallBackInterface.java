package govjobs.govjob.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alamatounkara on 7/27/15.
 */
public interface CallBackInterface {


    public void onPostExecute(ArrayList<HashMap<String, String>> result);
    public void onPreExecute();
}
