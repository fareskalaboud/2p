package seg2.compair;

import android.widget.Filter;
import model.Country;

import java.util.ArrayList;

/**
 * Created by faresalaboud on 01/12/14.
 */
public class CountryFilter extends Filter
{

    private ArrayList<Country> originalList;
    private ArrayList<Country> filteredResults;
    private CountryListAdapter adapter;

    public CountryFilter(CountryListAdapter adapter) {
        this.adapter = adapter;
        originalList = adapter.getCountryList();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        constraint = constraint.toString().toLowerCase();
        FilterResults result = new FilterResults();
        if(constraint != null && constraint.toString().length() > 0)
        {
            ArrayList<Country> filteredItems = new ArrayList<Country>();

            for(int i = 0, l = originalList.size(); i < l; i++)
            {
                Country country = originalList.get(i);
                if(country.toString().toLowerCase().contains(constraint))
                    filteredItems.add(country);
            }
            result.count = filteredItems.size();
            result.values = filteredItems;
        }
        else
        {
            synchronized(this)
            {
                result.values = originalList;
                result.count = originalList.size();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint,
                                  FilterResults results) {

        filteredResults = (ArrayList<Country>)results.values;
        adapter.notifyDataSetChanged();
        adapter.clear();
        for(int i = 0; i < filteredResults.size(); i++)
            adapter.add(filteredResults.get(i));
        adapter.notifyDataSetInvalidated();
    }
}