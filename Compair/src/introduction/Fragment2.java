package introduction;


import seg2.compair.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;




	
	public class Fragment2 extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			if(container==null)
			{
				return null;
			}
			
			return (RelativeLayout) inflater.inflate(R.layout.fragment2_layout, container,false);
		}
	

}
