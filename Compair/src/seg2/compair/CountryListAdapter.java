package seg2.compair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import model.Country;

import java.util.List;

/**
 * Created by faresalaboud on 22/11/14.
 */
public class CountryListAdapter extends ArrayAdapter<String>{

    Context context;
    int layoutResourceId;
    String data[] = null;

    public CountryListAdapter(Context context, int layoutResourceId, String[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CountryHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CountryHolder();
//            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (CountryHolder)row.getTag();
        }

        String country = data[position];
        System.out.println("SETTING TYPEFACE:");
        holder.txtTitle.setTypeface(Fonts.LATO_LIGHTITALIC);
        holder.txtTitle.setText(country);

//        holder.imgIcon.setImageResource(Country.icon);

        return row;
    }

    static class CountryHolder
    {
//        ImageView imgIcon;
        TextView txtTitle;
    }
}