package plugin.testSite.exp;

import java.util.Comparator;


public class TestSiteComparator implements Comparator<TestSite>{

	//�ۑ����Ԃ̍~���Ƀ\�[�g����B
	@Override
	public int compare(TestSite t1, TestSite t2) {
		// TODO Auto-generated method stub
		return -(new Long(t1.getSaveTime())).compareTo(new Long(t2.getSaveTime()));
	}

}
