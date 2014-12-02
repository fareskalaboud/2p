package introduction;


import seg2.compair.CountrySelectActivity;
import seg2.compair.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;


public class Fragment3 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(container==null)
		{
			return null;
		}
		
		//We get the view, so we can edit the button on the final screen to do the necessary task.

		RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.fragment3_layout, container,false);
		
		Button endtut = (Button)v.findViewById(R.id.endtut);
		//We add an action listener here for the button. 
		endtut.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				/*
				 * When the button is pressed, we store in sharedpref that the tutorial has been done on this device,
				 * we also get the bundled username from the main activity, and pass it on when we make the new rates intent. 
				 */
				
				//We write into sharedpreferences that the user has already seen the intro.
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("com.SEG2.skipintro","skipintro");
				editor.apply();
				
				Intent intent = new Intent(getActivity().getApplicationContext(), CountrySelectActivity.class);
			
				startActivity(intent);
				
				
				
			}
			
		});
		
		return v;
	}


}
