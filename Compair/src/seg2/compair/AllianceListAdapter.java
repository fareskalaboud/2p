package seg2.compair;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by faresalaboud on 22/11/14.
 */
public class AllianceListAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> allianceList;
    private Context context;

    /**
     * The constructor of the custom ListView adapter, which uses
     * Country objects instead of Strings.
     *
     * @param context The activity in which the ListView is.
     * @param textViewResourceId The textview's resource ID.
     * @param allianceList an ArrayList of Country objects that
     *                    were created from data obtained from
     *                    the World Bank API.
     */
    public AllianceListAdapter(Context context, int textViewResourceId,
                              ArrayList<String> allianceList) {
        super(context, textViewResourceId, allianceList);
        this.context = context;
        this.allianceList = allianceList;
    }

    /**
     * ViewHolder is a singleton class that acts as the view holder for the
     * countries shown on the list, which have a textview and a checkbox.
     */
    private class ViewHolder {
        TextView name;
        ImageView flag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.alliancelistview_row, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.flag = (ImageView) convertView.findViewById(R.id.flag);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String alliance = allianceList.get(position);
        holder.name.setText(alliance);
        int flagId = context.getResources().getIdentifier(alliance.toLowerCase().replace(' ', '_'), "drawable", context.getApplicationContext().getPackageName());
        if (flagId != 0) {
            Log.d("FLAG FINDER", "Flag found: " + alliance);
            holder.flag.setImageResource(flagId);
        } else {
            Log.d("FLAG FINDER", "Flag NOT found: " + alliance);
            holder.flag.setImageDrawable(null);
        }

        return convertView;
    }

    /**
     * Returns the countryList for the filter.
     * @return an ArrayList of Country objects that contains
     * all the countries obtained from the API.
     */
    public ArrayList<String> getAllianceList() {
        return allianceList;
    }
}