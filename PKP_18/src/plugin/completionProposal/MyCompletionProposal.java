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
 *  CompletionProposal�ƂقƂ�Ǔ����B
 *  CompletionProposal��final�N���X�Ȃ̂Ŏd���Ȃ��쐬�B
 *  �������I�΂ꂽ(apply)���ɁA��������B
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

	//�]���l
	private double fExplanationVectorSum;
	//�����ɕۑ�����Ă���o����
	private int fCount;

	//�֐���
	private FunctionTree fFtree;

	//�]���l�̏��ɕ��ׂ��֐��؂̏���
	private int selected_tree_num;
	
	//�L�[���[�h
	private String fKeywords;

	/**
	 *  ����̓_���B
	 *  this�̒���toCompleteMethodString()�̂悤�Ƀ��\�b�h���ĂԂƁA�Ȃ����v���O�������ُ퓮�삷��B
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
	 * �g���̂͂�����
	 *
	 * @param replacementString �u��������
	 * @param replacementOffset �u���J�n�ʒu
	 * @param replacementLength �u������ď�����e�L�X�g��̕�����̒���
	 * @param cursorPosition �u����̃J�[�\���̈ʒu
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
	 * �������͂Ƃ肠�����g��Ȃ��B
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		try {
			//�G�f�B�^�֕������}��
			document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
			System.out.println("selected: " + selected_tree_num + ": " + fReplacementString);
			System.out.println("selected: " + fFtree.toString());
			
			/*
			 * TestSite�̓o�^
			 */
			TestSite site = JavaCompletionProposalComputer.getLogSite();
			//���O�t�@�C���̍쐬
			site.createNewFileAsLog(fReplacementString, fReplacementString.length(), selected_tree_num, fKeywords);
			//���O�����X�g�ɒǉ�
			TestSite.addLogFile(site);
			//�r���[�̃��t���b�V��
			LogSiteView view = (LogSiteView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.logSiteView");
			if(view != null)
				view.refresh();
			
			//�����w�K�͊w�K�����ǂ��̂����́B�B
			//���[�J���T�[�`���s��
			//�X�e�b�v�����w��
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			boolean fOnline = store.getBoolean(PreferenceInitializer.LOCAL_ONLINE_FLAG);//�I�����C���w�K���s���B
			if(fOnline){
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_STEPS);//�X�e�b�v���̎擾
				int numOfLog = store.getInt(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_PAST_LOG);//�g�p���郍�O���̎擾
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return fAdditionalProposalInfo;
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return fContextInformation;
	}

	@Override
	public String getDisplayString() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if (fDisplayString != null)
			return fDisplayString;
		return fReplacementString;
	}

	@Override
	public Image getImage() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return fImage;
	}

	@Override
	public Point getSelection(IDocument document) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
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
