package introduction;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentPagerAdapter;

/**
 * Page adapter for the Welcome Screen
 * Manages the screens in the pageController
 */
public class PageAdapter extends FragmentPagerAdapter{
	
	private List<Fragment> fragments;

	/**
	 * Constructor of the PageAdapter
	 * @param fm the fragment manager
	 * @param fragments the list of fragments
	 */
	public PageAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.fragments.size();
	}
}
