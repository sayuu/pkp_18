package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

/**
 * 1��FunctionTree�̃m�[�h��\���N���X
 * @author sayuu
 *
 */
public class FunctionTree implements Comparable<FunctionTree>{
	
	//�֐��{��(root�̎����Ă���e��Node�P�̂̕]���l)
	private FunctionNode root;
	
	//���̊֐��؂́u�q�v�֐��؁B�֐��̈����ɑ�������B�����̏��Ԃ�ۂ��߁A�v�f��null�������Ă��邱�Ƃ�����B
	private ArrayList<FunctionTree> children = new ArrayList<FunctionTree>();
	
	//�؂��\������m�[�h�̐�
	private int length;
	
	//���̊֐��؂����[�U�ɑI�����ꂽ����\���t���O
	private boolean flg_selected = false;

	//�f�o�b�O�p
	private String completeMethodString;	
	
	//���̖؂́u���v�֐��m�[�h�̃��x���̃��X�g
	HashMap<String, Integer> numOfSameWordsFunc = new HashMap<String, Integer>();

	//�]���x�N�g��
	public ExplanationVector e_vec;

	private Params para;
	/**
	 * �R���X�g���N�^
	 * 1�̃m�[�h�ɕK�v�Ȃ��̂́A�؂̃��[�g�ƂȂ�FunctionRoot1�B
	 * 
	 * @param f �֐�
	 * @param input_words ���̓L�[���[�h��List
	 * @param numOfSameWordsIn�@�X�̃L�[���[�h�̓��̓N�G�����̏o����
	 */
	public FunctionTree(Params para, Function f, List<Word> input_words, HashMap<String, Integer> numOfSameWordsIn){
		this.para = para;
		root = new FunctionNode(f);
		length = 1;
		//label_list.addAll(Arrays.asList(f.getLabels()));

		//Function��Label�Ɋ܂܂�铯��̒P�ꂲ�Ƃɂ��̌����J�E���g����
		for(String flab: root.getFunction().getLabels()){

			if(numOfSameWordsFunc.containsKey(flab) == false){
				//�L�[��������ΐV�����L�[��������
				numOfSameWordsFunc.put(flab, 1);
			}else{
				//�L�[�����łɂ���Ƃ��A�����P���₷
				int num = numOfSameWordsFunc.get(flab);
				num++;
				numOfSameWordsFunc.put(flab, num);
			}
		}
		//numOfSameWordsTree.putAll(numOfSameWordsFunc);
		
		String flg_common = para.common_subsequence;
		if(flg_common.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_OFF))
			e_vec = new ExplanationVector(para, input_words.size()).calcExplanationVector(input_words, numOfSameWordsIn, this);
		else
			//�L�[���[�h�ƃ��x���̕�����v�ł��n�j.
			e_vec = new ExplanationVector(para, input_words.size()).calcExplanationVector2(input_words, numOfSameWordsIn, this);
	}

	public FunctionNode getRoot(){
		return root;
	}

	public int getLength(){
		return length;
	}

	public boolean getSelectedFlg(){
		return flg_selected;
	}

	public void setSelectedFlg(){
		flg_selected = true;
	}

	/**
	 *  �u�q�v�֐��؂�t��������(�u�e�v�֐��؂̈����̏��Ԓʂ�Ɏg�p����)
	 *  
	 * @param child �u�q�v�֐���
	 */
	public void addChild(FunctionTree child){
		//���Ԃ�ۂ��߂ɁAchild��null�ł����Ă������B
		this.children.add(child);
		//e�̒l�͒P���ɑ����Z�B
		if(child != null){
			this.length += child.length;
			this.e_vec.add(child.e_vec);
		}
	}

	/*
	 * �q��e�ɈӖ��͂Ȃ��B
	 * �i�e��e�Ɍォ��h���h��������ē����ł̑召��r�Ɏg�p���Ă��邾���j
	 */
	public String toString(){
		String s = "FunctionTree[cs=" + toCompleteMethodString() + ", sum="+ e_vec.getSum() +", len= "+ this.length + ", p= ";
		for(int i = 0; i < this.e_vec.FEATURE_NUM; i++){
			s += e_vec.getFeature(i) + ", ";
		}
		s += "e_arr= ";
		for(int i = 0; i < this.e_vec.keywords_length; i++){
			s += this.e_vec.getKeywordFeature(i) + ", ";
		}

		s += "root= " + this.root.toString();

		if(this.children.size() > 0){
			s += "\n";
			for(FunctionTree child: this.children){
				if(child != null){
					//���Ԃ�ۂ��߁Achild�ɂ�null�������Ă��邱�Ƃ����肤��B
					s += "\t" + child.toString();
				}
			}
			s += "\n";
		}
		s += "]";
		return s;

	}

	/*
	 * ���O�ۑ��p�̕�����
	 * �����ʁ{�o�͕�����
	 */
	public String toLogDBString(){
		String s = "";
		for(int i = 0; i < this.e_vec.FEATURE_NUM; i++){
			s += e_vec.getFeature(i) + ", ";
		}
		for(int i = 0; i < this.e_vec.keywords_length; i++){
			s += this.e_vec.getKeywordFeature(i) + ", ";
		}
		s += "\"" + toCompleteMethodString() + "\n";
		return s;
	}
	
	/*
	 * �]���l���o�͂���
	 */
	public String toEvalString(){
		String s = getEvaluationValue() + ", p= ";
		for(int i = 0; i < this.e_vec.FEATURE_NUM; i++){
			s += e_vec.getFeature(i) + ", ";
		}
		s += "e_arr= ";
		for(int i = 0; i < this.e_vec.keywords_length; i++){
			s += this.e_vec.getKeywordFeature(i) + ", ";
		}
		return s;

	}

	/**
	 * ���̊֐��ؑS�̂�Java�̃��\�b�h�̕�����Ƃ��Đ����A�Ԃ�
	 * @return Java�̃��\�b�h�̕�����
	 */
	public String toCompleteMethodString(){
		String s = "";

		boolean isInThisClass = root.getFunction().isInThisClass();
		boolean isStatic = root.getFunction().isStatic();
		String funcType = root.getFunction().getFunctionType();

		if(isStatic == true && funcType.equals("method")){
			//static method�̏ꍇ�A
			//�N���X��.���\�b�h���ƂȂ�B
			//root�̖��O
			if(isInThisClass == false)
				s += getParentClassName(root.getFunction().getParentClass()) + "." + root.getFunction().getName();
			else
				s += root.getFunction().getName();//this�̂Ƃ��B
			s += "(";
			//�����̖��O
			for(int i = 0; i < this.children.size(); i++){
				if(i != 0)
					s += ", ";
				if(children.get(i) != null)
					s += children.get(i).toCompleteMethodString();
			}
			s += ")";
		}else if(isStatic == false && funcType.equals("method")){
			//Instance method�̏ꍇ�A
			//1�ڂ̃p�����[�^(i==0)�̓��V�[�o�ł��邩��A
			//root�̑O�ɏ���
			if(isInThisClass == false)//(�e��this�����B
			if(children.size() > 0 && children.get(0) != null){
				s += children.get(0).toCompleteMethodString() + ".";
			}
			//root�̖��O
			s += root.getFunction().getName();
			s += "(";
			//�����̖��O
			for(int i = 1; i < this.children.size(); i++){
				if(i != 1)
					s += ", ";
				if(children.get(i) != null)
					s += children.get(i).toCompleteMethodString();
			}
			s += ")";
		}else if((isStatic == false && funcType.equals("field")) || funcType.equals("localvariable")){
			//Instance field �̏ꍇ (�e��this���������܂�) �܂��́A���[�J���ϐ��̏ꍇ�B
			//root�̖��O
			s += root.getFunction().getName();
		}else if(isStatic == true && funcType.equals("field")){
			//static field �̏ꍇ
			
			//�e��this�܂��́A�e�������i��{�f�[�^�^�j�ȊO�̎��A
			if(!(isInThisClass == true || root.getFunction().getParentClass().equals("")))
				//root�̖��O(�N���X��.�t�B�[���h��)
				s += getParentClassName(root.getFunction().getParentClass()) + "." + root.getFunction().getName();
			else
				//�uthis.�v����n�܂�^�X�N�͂قƂ�ǂȂ��B�F���B�Ƃ������A�����Ă����O���Ď�������B
				s += root.getFunction().getName();//�e��this
				
		}else if(funcType.equals("constructor")){
			//�R���X�g���N�^�̏ꍇ
			//new + root�̖��O
			s += "new " + root.getFunction().getName();
			s += "(";
			//�����̖��O
				for(int i = 0; i < this.children.size(); i++){
					if(i != 0)
						s += ", ";
					if(children.get(i) != null)
						s += children.get(i).toCompleteMethodString();
				}
			s += ")";
		}else{
			//�s��
			s += "???";
		}
		
		completeMethodString = s;

		return s;
	}

	/**
	 * �e�N���X�̖��O��Ԃ�
	 * @param parent �e�N���X�� or this or ��������""�B
	 * @return
	 */
	private String getParentClassName(String parent){
		if(parent.equals("this") || parent.equals("")){
			return parent;
		}else{
			return parent.substring(parent.lastIndexOf(".")+1);
		}
	}

	public double getEvaluationValue(){
		return e_vec.getSum();
	}
	
	/*
	 *	�\�[�g�̂Ƃ��Ɏg���B
	 */

	@Override
	public int compareTo(FunctionTree arg0) {
		if(arg0 == null)
			throw new NullPointerException();
		
		if(this.getEvaluationValue() < arg0.getEvaluationValue()){
			return 1;
		}else if(this.getEvaluationValue() == arg0.getEvaluationValue()){
			return this.toCompleteMethodString().compareTo(arg0.toCompleteMethodString());
		}else
			return -1;
	}



	/*
	 *	contains�̂Ƃ��Ɏg���B
	 */

	@Override
	public boolean equals(Object obj){
		if (this == obj)
	        return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FunctionTree o = (FunctionTree) obj;
		return this.toCompleteMethodString().equals(o.toCompleteMethodString());
	}



	/*
	 *	contains�̂Ƃ��Ɏg���B
	 */

	@Override
	public int hashCode(){
		return this.toCompleteMethodString().hashCode();
	}

}
