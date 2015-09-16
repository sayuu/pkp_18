package plugin.testSite.exp;

import java.util.Comparator;


public class TestSiteComparator implements Comparator<TestSite>{

	//保存時間の降順にソートする。
	@Override
	public int compare(TestSite t1, TestSite t2) {
		// TODO Auto-generated method stub
		return -(new Long(t1.getSaveTime())).compareTo(new Long(t2.getSaveTime()));
	}

}
