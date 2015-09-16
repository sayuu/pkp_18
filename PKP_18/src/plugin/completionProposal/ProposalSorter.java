package plugin.completionProposal;

/**
 * 
 * ウィンドウ＞設定＞Java＞エディター＞コンテンツ・アシスト＞
 * ソートおよびフィルター＞プロポーザルをソート
 * にて、
 * キーワードプログラミングを選択する。
 * 
 * そうすると、このソーターがオンになる。
 */

import org.eclipse.jdt.ui.text.java.AbstractProposalSorter;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ProposalSorter extends AbstractProposalSorter {

	public ProposalSorter() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public int compare(ICompletionProposal p1, ICompletionProposal p2) {

		// 評価値順にソート
		double r1 = getRelevance(p1);
		double r2 = getRelevance(p2);
		int relevanceDif = (int)((r2 - r1) * 10000);
		if (relevanceDif != 0) {
		    return relevanceDif;
		}

		// 評価値が等しければアルファベット順にソート
		return getSortKey(p1).compareToIgnoreCase(getSortKey(p2));

	}


	private double getRelevance(ICompletionProposal obj) {
        if (obj instanceof  MyCompletionProposal) {
        	MyCompletionProposal mcp = (MyCompletionProposal) obj;
            return mcp.getEVec();
        }
        //キーワードプログラミング以外のプロポーザル (MyCompletionProposal以外のもの）
        return -9999;
    }

	private String getSortKey(ICompletionProposal p) {
	    return p.getDisplayString();
	}

}
