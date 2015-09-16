package plugin.testSite.exp;

import java.util.Comparator;

public class OutputCanditateLogComparator implements Comparator<OutputCandidateLog>{

	//評価値の降順にソートする。
	@Override
	public int compare(OutputCandidateLog o1, OutputCandidateLog o2) {
		// TODO Auto-generated method stub
		if(o1.getEvaluationValue() < o2.getEvaluationValue())
			return 1;
		else
			return -1;
	}

}
