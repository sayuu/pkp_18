package plugin.testSite;

import java.util.Comparator;

public class OutputCanditateLogComparator implements Comparator<OutputCandidateLog>{

	//�]���l�̍~���Ƀ\�[�g����B
	@Override
	public int compare(OutputCandidateLog o1, OutputCandidateLog o2) {
		// TODO Auto-generated method stub
		if(o1.getEvaluationValue() < o2.getEvaluationValue())
			return 1;
		else
			return -1;
	}

}
