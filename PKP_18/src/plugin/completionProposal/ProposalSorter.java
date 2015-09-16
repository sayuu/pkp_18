package plugin.completionProposal;

/**
 * 
 * �E�B���h�E���ݒ聄Java���G�f�B�^�[���R���e���c�E�A�V�X�g��
 * �\�[�g����уt�B���^�[���v���|�[�U�����\�[�g
 * �ɂāA
 * �L�[���[�h�v���O���~���O��I������B
 * 
 * ��������ƁA���̃\�[�^�[���I���ɂȂ�B
 */

import org.eclipse.jdt.ui.text.java.AbstractProposalSorter;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ProposalSorter extends AbstractProposalSorter {

	public ProposalSorter() {
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	@Override
	public int compare(ICompletionProposal p1, ICompletionProposal p2) {

		// �]���l���Ƀ\�[�g
		double r1 = getRelevance(p1);
		double r2 = getRelevance(p2);
		int relevanceDif = (int)((r2 - r1) * 10000);
		if (relevanceDif != 0) {
		    return relevanceDif;
		}

		// �]���l����������΃A���t�@�x�b�g���Ƀ\�[�g
		return getSortKey(p1).compareToIgnoreCase(getSortKey(p2));

	}


	private double getRelevance(ICompletionProposal obj) {
        if (obj instanceof  MyCompletionProposal) {
        	MyCompletionProposal mcp = (MyCompletionProposal) obj;
            return mcp.getEVec();
        }
        //�L�[���[�h�v���O���~���O�ȊO�̃v���|�[�U�� (MyCompletionProposal�ȊO�̂��́j
        return -9999;
    }

	private String getSortKey(ICompletionProposal p) {
	    return p.getDisplayString();
	}

}
