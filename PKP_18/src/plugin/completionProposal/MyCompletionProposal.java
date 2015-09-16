package plugin.completionProposal;

import keywordProgramming.FunctionTree;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

import org.eclipse.core.runtime.Assert;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import plugin.views.LogSiteView;
import plugin.views.TestSiteView;
import state.KpRunningState;

import localSearch.LocalSearch;

/**
 *  CompletionProposalとほとんど同じ。
 *  CompletionProposalがfinalクラスなので仕方なく作成。
 *  自分が選ばれた(apply)時に、何かする。
 * @author sayuu
 *
 */
public class MyCompletionProposal implements ICompletionProposal{

	/** The string to be displayed in the completion proposal popup. */
	private String fDisplayString;
	/** The replacement string. */
	private String fReplacementString;
	/** The replacement offset. */
	private int fReplacementOffset;
	/** The replacement length. */
	private int fReplacementLength;
	/** The cursor position after this proposal has been applied. */
	private int fCursorPosition;
	/** The image to be displayed in the completion proposal popup. */
	private Image fImage;
	/** The context information of this proposal. */
	private IContextInformation fContextInformation;
	/** The additional info of this proposal. */
	private String fAdditionalProposalInfo;

	//評価値
	private double fExplanationVectorSum;
	//履歴に保存されている出現回数
	private int fCount;

	//関数木
	private FunctionTree fFtree;

	//評価値の順に並べた関数木の順位
	private int selected_tree_num;
	
	//キーワード
	private String fKeywords;

	/**
	 *  これはダメ。
	 *  thisの中でtoCompleteMethodString()のようにメソッドを呼ぶと、なぜかプログラムが異常動作する。
	 *
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @param keywords
	 * @param eVec
	 */
//	public MyCompletionProposal(int replacementOffset, int cursorPosition, String keywords, String retType, FunctionTree ft) {
//		this(ft.toCompleteMethodString(), replacementOffset, ft.toCompleteMethodString().length(), cursorPosition, null, null, null, null);
//		fKeywords = keywords;
//		fReturnType = retType;
//		fExplanationVectorSum = ft.getRoot().getExplanationVector().getSum();
//		fFtree = ft;
//	}

	/**
	 * 使うのはこっち
	 *
	 * @param replacementString 置換文字列
	 * @param replacementOffset 置換開始位置
	 * @param replacementLength 置換されて消えるテキスト上の文字列の長さ
	 * @param cursorPosition 置換後のカーソルの位置
	 * @param keywords
	 * @param eVec
	 */
	public MyCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, String keywords, String retType, int ft_number, FunctionTree ftree) {
		this(replacementString, replacementOffset, replacementLength, cursorPosition, null, null, null, null);
		selected_tree_num = ft_number;
		fFtree = ftree;
		fExplanationVectorSum = fFtree.getEvaluationValue();
		fKeywords = keywords;
	}

	/**
	 * こっちはとりあえず使わない。
	 *
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @param image
	 * @param displayString
	 * @param contextInformation
	 * @param additionalProposalInfo
	 */
	public MyCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo) {
		Assert.isNotNull(replacementString);
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);
		Assert.isTrue(cursorPosition >= 0);

		fReplacementString= replacementString;
		fReplacementOffset= replacementOffset;
		fReplacementLength= replacementLength;
		fCursorPosition= cursorPosition;
		fImage= image;
		fDisplayString= displayString;
		fContextInformation= contextInformation;
		fAdditionalProposalInfo= additionalProposalInfo;
	}

	@Override
	public void apply(IDocument document) {
		// TODO 自動生成されたメソッド・スタブ
		try {
			//エディタへ文字列を挿入
			document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
			System.out.println("selected: " + selected_tree_num + ": " + fReplacementString);
			System.out.println("selected: " + fFtree.toString());
			
			/*
			 * TestSiteの登録
			 */
			TestSite site = JavaCompletionProposalComputer.getLogSite();
			//ログファイルの作成
			site.createNewFileAsLog(fReplacementString, fReplacementString.length(), selected_tree_num, fKeywords);
			//ログをリストに追加
			TestSite.addLogFile(site);
			//ビューのリフレッシュ
			LogSiteView view = (LogSiteView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.logSiteView");
			if(view != null)
				view.refresh();
			
			//逐次学習は学習率がどうのこうの。。
			//ローカルサーチを行う
			//ステップ数を指定
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			boolean fOnline = store.getBoolean(PreferenceInitializer.LOCAL_ONLINE_FLAG);//オンライン学習を行う。
			if(fOnline){
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_STEPS);//ステップ数の取得
				int numOfLog = store.getInt(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_PAST_LOG);//使用するログ数の取得
				TestSite []sites = TestSite.getLogFiles(numOfLog);
				LocalSearch ls = new LocalSearch(sites, true);
				ls.run(numOfSteps, KpRunningState.LOCAL_SEARCH_ONLINE, null);
			}
		} catch (BadLocationException x) {
			// ignore
		}
	}

	@Override
	public String getAdditionalProposalInfo() {
		// TODO 自動生成されたメソッド・スタブ
		return fAdditionalProposalInfo;
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO 自動生成されたメソッド・スタブ
		return fContextInformation;
	}

	@Override
	public String getDisplayString() {
		// TODO 自動生成されたメソッド・スタブ
		if (fDisplayString != null)
			return fDisplayString;
		return fReplacementString;
	}

	@Override
	public Image getImage() {
		// TODO 自動生成されたメソッド・スタブ
		return fImage;
	}

	@Override
	public Point getSelection(IDocument document) {
		// TODO 自動生成されたメソッド・スタブ
		return new Point(fReplacementOffset + fCursorPosition, 0);
	}

	public void setCount(int count){
		this.fCount = count;
	}

	public int getCount(){
		return fCount;
	}

	public double getEVec(){
		return fExplanationVectorSum;
	}

	public void plusEVec(double x){
		fExplanationVectorSum += x;
	}

}
