package introduction;

import java.util.List;
import java.util.Vector;

import seg2.compair.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class IntroductionActivity extends FragmentActivity {
	
	private PageAdapter mPageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_layout);
		initialisePaging();
	}

	private void initialisePaging() {
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, Fragment1.class.getName()));
		fragments.add(Fragment.instantiate(this, Fragment2.class.getName()));
		fragments.add(Fragment.instantiate(this, Fragment3.class.getName()));
		mPageAdapter = new PageAdapter(this.getSupportFragmentManager(),fragments);
		ViewPager pager =  (ViewPager)findViewById(R.id.viewpager);
		
		pager.setAdapter(mPageAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
