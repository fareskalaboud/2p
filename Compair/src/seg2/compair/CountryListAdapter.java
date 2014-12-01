package seg2.compair;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import model.Country;

import java.util.ArrayList;

/**
 * Created by faresalaboud on 22/11/14.
 */
public class CountryListAdapter extends ArrayAdapter<Country> implements Filterable {

    private ArrayList<Country> countryList;
    private Context context;
    private Filter filter;

    /**
     * The constructor of the custom ListView adapter, which uses
     * Country objects instead of Strings.
     *
     * @param context The activity in which the ListView is.
     * @param textViewResourceId The textview's resource ID.
     * @param countryList an ArrayList of Country objects that
     *                    were created from data obtained from
     *                    the World Bank API.
     */
    public CountryListAdapter(Context context, int textViewResourceId,
                              ArrayList<Country> countryList) {
        super(context, textViewResourceId, countryList);
        this.context = context;
        this.countryList = new ArrayList<Country>();
        this.countryList.addAll(countryList);
    }

    /**
     * ViewHolder is a singleton class that acts as the view holder for the
     * countries shown on the list, which have a textview and a checkbox.
     */
    private class ViewHolder {
        TextView code;
        CheckBox name;
        ImageView flag;
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new CountryFilter(this);
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.countrylistview_row, null);

            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.flag = (ImageView) convertView.findViewById(R.id.flag);

            convertView.setTag(holder);

            holder.name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Country country = (Country) cb.getTag();
                    country.setSelected(cb.isChecked());
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Country country = countryList.get(position);
        holder.code.setText("(" + country.getId() + ")");
        holder.name.setText(country.getName() + " ");
        holder.name.setChecked(country.isSelected());
        holder.name.setTag(country);
        int flagId = context.getResources().getIdentifier(country.getName().toLowerCase().replace(' ', '_'), "drawable", context.getApplicationContext().getPackageName());
        if (flagId != 0) {
            Log.d("FLAG FINDER", "Flag found: " + country.getName());
            holder.flag.setImageResource(flagId);
        } else {
            Log.d("FLAG FINDER", "Flag NOT found: " + country.getName());
            holder.flag.setImageDrawable(null);
        }

        return convertView;
    }

    /**
     * Returns the countryList for the filter.
     * @return an ArrayList of Country objects that contains
     * all the countries obtained from the API.
     */
    public ArrayList<Country> getCountryList() {
        return countryList;
    }
}