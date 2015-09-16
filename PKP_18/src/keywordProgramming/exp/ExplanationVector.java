package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import keywordProgramming.LongestCommonSubsequence;

import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

import console_test.LevenshteinDistance;

/**
 * ExplanationVector��\���N���X
 * @author sayuu
 *
 */
public class ExplanationVector{
	
	//�֐���]����������̐�
	public static final int FEATURE_NUM = 5;

	private Params para;

	//�L�[���[�h�̌�
	public int keywords_length;
	
	//�L�[���[�h���Ƃ̓�����
	private double[] keyword_p;
	
	//�S�Ă̗v�f(e0��e_arr)�𑫂�������.�召��r�ɂ͂�����g��.	
	private double sum;
	
	//�����x�N�g��
	private double p[] = new double [FEATURE_NUM];	

	public static final double INFINITE_VALUE = 9999.9;
	
	/**
	 * �R���X�g���N�^
	 * @param keywords_length �L�[���[�h��
	 */
	public ExplanationVector(Params para, int keywords_length){
		this.keywords_length = keywords_length;
		this.keyword_p = new double[keywords_length];
		this.para = para;
	}

	/**
	 * �R���X�g���N�^
	 * @param keywords_length �L�[���[�h��
	 * @param sum ExplanationVector�̗v�f�̍��v�l
	 */
	public ExplanationVector(Params para, int keywords_length, double sum){
		this.keywords_length = keywords_length;
		this.keyword_p = new double[keywords_length];
		this.sum = sum;
		this.para = para;
	}

	/**
	 * �R���X�g���N�^
	 * @param e �R�s�[������ExplanationVector
	 */
	public ExplanationVector(Params para, ExplanationVector e){
		this.keywords_length = e.keywords_length;
		this.keyword_p = new double[e.keywords_length];
		this.para = para;
		substitution(e);
	}

	/**
	 * ��r�̂��߂�
	 * ExplanationVector�̊e�v�f�̍��v�l���v�Z����
	 * 
	 */
	public void calcSum(){
		sum = 0.0;
		//�L�[���[�h�A���[����L�[���[�h�̓����ʂ��Z�o�B
		double sum_k = 0.0;
		for(double k: keyword_p)
			sum_k += k;
		p[1] = sum_k;

		//�e�����̍��v�l�B
		for(int i = 0; i < FEATURE_NUM; i++){
			sum += para.w_arr.get(i) * p[i];
		}

	}

	/**
	 * ��r�̂��߂�
	 * ExplanationVector�̊e�v�f�̍��v�l��
	 * �v�Z���A�擾����B
	 * 
	 * @return ExplanationVector�̊e�v�f�̍��v�l
	 */
	public double calcAndGetSum(){
		calcSum();
		return sum;
	}
	
	public double getSum(){
		return sum;
	}

	/**
	 * ExplanationVector�Ɏq�m�[�h�t���������ۂ̒l���v�Z����B
	 * 
	 * @param child�@�t����������q�m�[�h
	 */
	public void add(ExplanationVector child){
		ExplanationVector new_e = ExplanationVector.add(para, this, child);
		this.substitution(new_e);
	}
	
//	public void add(ExplanationVector child){
//		
//		//�L�[���[�h�A���[����L�[���[�h�̓����ʂ��Z�o�B
//		double sum_k = 0.0;
//		for(int i = 0; i < keyword_p.length; i++){
//			
////			keyword_p[i] += child.keyword_p[i];
////			if(keyword_p[i] > 1)
////				keyword_p[i] = 1;
//			keyword_p[i] = Math.max(keyword_p[i], child.keyword_p[i]);
//			
//			sum_k += keyword_p[i];
//		}
//
//		//�e�����ʂ����Z(�P���Ȑe�q�̍��v)
//		for(int i = 0; i < FEATURE_NUM; i++){
//			p[i] += child.p[i];
//		}
//		p[1] = sum_k;
//
//
//		calcSum();
//	}

	/**
	 * �e��ExplanationVector�Ɏq��t���������ۂ̒l���v�Z����
	 * ���̌��ʂ�ێ������V����ExplanationVector��Ԃ��B
	 * 
	 * @param parent�@�e��ExplanationVector
	 * @param child�@�q��ExplanationVector
	 * @return �v�Z���ʂ�ێ������V����ExplanationVector
	 */
	public static ExplanationVector add(Params para, ExplanationVector parent,  ExplanationVector child){
		ExplanationVector new_e = new ExplanationVector(para, parent);
		//�L�[���[�h�A���[����L�[���[�h�̓����ʂ��Z�o�B
		double sum_k = 0.0;
		for(int i = 0; i < new_e.keyword_p.length; i++){
			
			//max �����悤�ɕύX����B(�ގ��x��1�ȏ�s���Ȃ��̂�)
			new_e.keyword_p[i] = Math.max(parent.keyword_p[i], child.keyword_p[i]);
			
			//�����Z�������ƁA1��cap����B
//			new_e.keyword_p[i] = parent.keyword_p[i] + child.keyword_p[i];
//			if(new_e.keyword_p[i] > 1)
//				new_e.keyword_p[i] = 1;
			
			sum_k += new_e.keyword_p[i];
		}

		//�e�����ʂ����Z(�P���Ȑe�q�̍��v)
		for(int i = 0; i < FEATURE_NUM; i++){
			new_e.p[i] = parent.p[i] + child.p[i];
		}
		new_e.p[1] = sum_k;
		new_e.calcSum();
		return new_e;
	}

	/**
	 * ���̃I�u�W�F�N�g�Ɉ����ŗ^����ꂽExplanationVector��
	 * �e�v�f�̒l��S�ăR�s�[����B
	 * 
	 * @param e ���e���R�s�[������ExplanationVector
	 */
	public void substitution(ExplanationVector e){
		sum = e.sum;
		for(int i = 0; i < FEATURE_NUM; i++){
			p[i] = e.p[i];
		}
		try{
		for(int i = 0; i < keyword_p.length; i++){
			keyword_p[i] = e.keyword_p[i];
		}
		}catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
	}

	/**
	 * ���̓L�[���[�h����ExplanationVector���v�Z����B(�ގ��x���B)
	 * 
	 * @param input_words ���̓L�[���[�h��List
	 * @param numOfSameWordsIn �X�̃L�[���[�h�����ꂼ�ꉽ���̓N�G���Ɋ܂܂�Ă��邩��\��HashMap
	 * @param ft ExplanationVector���v�Z������FunctionTree
	 * @return �e�v�f�ɒl�����͂���A�e�v�f�̍��v�l�̌v�Z���Ȃ��ꂽExplanationVector
	 */
	public ExplanationVector calcExplanationVector(List<Word> input_words, HashMap<String, Integer> numOfSameWordsIn, FunctionTree ft){
		
		Function f = ft.getRoot().getFunction();
		
		/*
		 * �]���l�̌v�Z�́A
		 * �؂�root�̕]���l�������v�Z���āA
		 * ���łɋ��܂��Ă���q�̒l�ɑ����΂悢�B
		 */

		//������
		for(int i = 0; i < FEATURE_NUM; i++){
			p[i] = 0;
		}

		//�ؑS�̂̃m�[�h��
		p[0] = 1;	//root1�Ȃ̂ŁA1.

//		if(f.getName().contains("result")){
//			System.out.println();
//		}
		
		//�L�[���[�h�Ɉ�v���郉�x���̐�

		//f.label��input�Ɠ���̃L�[���[�h�����鎞�̌v�Z
		//����P��̏o���������ꂼ��x, y�Ƃ����e_i = max(x/y, 1)�Ƃ���.
		for(int i=0; i < input_words.size(); i++){
			
			Word word = input_words.get(i);
			
			//���������� �@���\�b�h�Ƀ}�b�`@ �ϐ��Ƀ}�b�`-
			if(!judgeWordType(word, f)){
				//���̃��[�h�͎g�p���Ȃ��B
				continue;
			}
			
			for(String flab: f.getLabels()){
				if(word.getWord().equals(flab)){
					keyword_p[i] = (double)(ft.numOfSameWordsFunc.get(flab)) / numOfSameWordsIn.get(flab);

					if(keyword_p[i] > 1)
						keyword_p[i] = 1;
				}
			}
		}


		//�L�[���[�h�Ɉ�v���Ȃ����x���̐�
		for(String flab: f.getLabels()){
			if(numOfSameWordsIn.containsKey(flab) == false){
				p[2]++;
			}
		}

		//����f�����[�J���ϐ��A�����o�ϐ��i�֐��j�Ȃ��e_0��+0.001����(f�̓R���e�L�X�g�ɋ߂������]�܂�������)
		//�i��ŏ����j
		if( f.getParentClass().equals("") || f.getParentClass().equals("this")){
			p[3]++;
		}

		//�p�x�̓����ʂ̌v�Z
		calcFrequencyFeatures(this, f); 
		
		calcSum();
		return this;
	}

	/*
	 * �p�x�̓����ʂ̌v�Z���s���B
	 */
	private void calcFrequencyFeatures(ExplanationVector e, Function f) {
		//���P�D�P�� w*f
		//e.p[4] = f.getFrequency();
		
		//���Q�D�p�x�𜓈ӓI��3��5��ŕ����ŁA0, 0.5, 1.0 �̒l������
//		if(f.getFrequency() < 3){
//			e.p[4] = 0;
//		}else if(f.getFrequency() < 5){
//			e.p[4] = 0.5;
//		}else{
//			e.p[4] = 1;
//		}
		
		//���R�D1-exp(-a*f) a = 0.3���x�B
		//�p�x�̉e�������܂�ɂ��傫���Ȃ�A���̓����i���ɃL�[���[�h�Ƃ̈�v�����p�x�̉e���͒Ⴍ�Ȃ��Ăق����j
		//�Ƃ̃o�����X�������Ă��܂��̂�h������
		//exp���g�p���čő�l���P�ŗ}�����֐��B
		e.p[4] = 1 - Math.exp(-para.const_freq * f.getFrequency());
		
	}
	
	/**
	 * ���[�hword�Ɗ֐�f�̃^�C�v���}�b�`���Ă��邩���ׂ�B
	 * @param word
	 * @param f
	 * @return
	 */
	private static boolean judgeWordType(Word word, Function f){
		
		if(word.getType().equals(Word.NORMAL))
			return true;
		
		boolean is_method = word.getType().equals(Word.METHOD_CONSTRUCTOR);
		
		if(f.getFunctionType().equals(Function.METHOD) || f.getFunctionType().equals(Function.CONSTRUCTOR)){
			if(is_method){
				return true;
			}else{
				return false;
			}
		}else{//variable
			if(is_method){
				return false;
			}else{
				return true;
			}
		}
	}
	
	/*
	 * �L�[���[�h�ƃ��x���̃y�A
	 * ��
	 * ���̗ގ��x
	 */
	private static class PairOfKeywordLabel{
		String keyword;
		String label;
		double similarity;
		
		public PairOfKeywordLabel(String keyword, String label, double similarity){
			this.keyword = keyword;
			this.label = label;
			this.similarity = similarity;
		}
	}

	/**
	 * ���̓L�[���[�h����ExplanationVector���v�Z����B
	 * 
	 * @param input_words ���̓L�[���[�h��List
	 * @param numOfSameWordsIn �X�̃L�[���[�h�����ꂼ�ꉽ���̓N�G���Ɋ܂܂�Ă��邩��\��HashMap
	 * @param ft ExplanationVector���v�Z������FunctionTree
	 * @return �e�v�f�ɒl�����͂���A�e�v�f�̍��v�l�̌v�Z���Ȃ��ꂽExplanationVector
	 */
	public ExplanationVector calcExplanationVector2(List<Word> input_words, HashMap<String, Integer> numOfSameWordsIn, FunctionTree ft){
		Function f = ft.getRoot().getFunction();
		
		/*
		 * �]���l�̌v�Z�́A
		 * �؂�root�̕]���l�������v�Z���āA
		 * ���łɋ��܂��Ă���q�̒l�ɑ����΂悢�B
		 */

		//������
		for(int i = 0; i < FEATURE_NUM; i++){
			p[i] = 0;
		}

		//�ؑS�̂̃m�[�h��
		p[0] = 1;	//root1�Ȃ̂ŁA1.

		//�L�[���[�h�Ɉ�v���郉�x���̐�
		List<Word> word_list = new ArrayList<Word>();
		for(int i=0; i < input_words.size(); i++){
			word_list.add(input_words.get(i));
		}
		
		//f.label��input�Ɠ���̃L�[���[�h�����鎞�̌v�Z
		//����P��̏o���������ꂼ��x, y�Ƃ����e_i = max(x/y, 1)�Ƃ���.
		
		// �L�[���[�h x in K(���̓N�G���Ɋ܂܂��L�[���[�h�̏W��),
		// ���x�� y in L(���x���̏W��)
		// �ގ��x f(x,y) 
		//�S�Ă�(x,y)�ɂ��ėގ��xf���v�Z����B
		List<PairOfKeywordLabel> similarity_list = new ArrayList<PairOfKeywordLabel>();
		
		for(int i=0; i < input_words.size(); i++){
			Word word = input_words.get(i);
			
			//���[�h�Ɗ֐��̃^�C�v�����������̂ݎ��s (�������Ȃ����[�h�͎g�p���Ȃ�)
			if(judgeWordType(word, f))
				for(String flabel: f.getLabels()){
					String key = word.getWord();
					double s = similarity(this.para, key, flabel);
					similarity_list.add(new PairOfKeywordLabel(key, flabel, s));
				}
		}
		
		Collections.sort(similarity_list, new Comparator<PairOfKeywordLabel>() {
			public int compare( PairOfKeywordLabel a, PairOfKeywordLabel b ) {
				// ����1�ƈ���2�͂��ꂼ�ꃊ�X�g���̒l
				
				// compare ���\�b�h�ŁA���̐��A0�A���̐� (int) ��Ԃ����Ƃɂ���āA���̑召���`���܂��B 
				// ���������������Ƃ���ꍇ�͕��̐��B�傫���Ƃ���ꍇ�͐��̐���Ԃ��� OK �ł��B
				if(a.similarity == b.similarity)
					return 0;
				else
					return  a.similarity > b.similarity ? -1 : 1;
			}
		} );
		
		
		//�ǂ̃L�[���[�h�Ƃǂ̃��x�����Ή����邩�̑Ή��\
		List<PairOfKeywordLabel> kl_map = new ArrayList<PairOfKeywordLabel>();
		//���łɊm�ۂ��ꂽ���x���̃��X�g
		List<String> keeped_label_list = new ArrayList<String>();
		//���łɊm�ۂ��ꂽ�L�[���[�h
		List<String> keeped_word_list = new ArrayList<String>();

		for(PairOfKeywordLabel p: similarity_list){
			//���łɃ��[�h�ƃ��x���̗��������肳��Ă��Ȃ��Ƃ��̂݁A
			//�g�ݍ��킹��ǉ�����B
			if(!keeped_word_list.contains(p.keyword) && !keeped_label_list.contains(p.label)){
				//�}�b�v�ɒǉ�����B
				kl_map.add(p);
				//����ς݃L�[���[�h�ƃ��x����ێ�����B
				keeped_word_list.add(p.keyword);
				keeped_label_list.add(p.label);
			}
		}
	
		//�v�Z���ꂽ�e���_���Ae_p�ɑ������B
		for(int i=0; i < input_words.size(); i++){
			for(PairOfKeywordLabel kl: kl_map){
				if(input_words.get(i).equals(kl.keyword)){
					keyword_p[i] = kl.similarity;
					if(keyword_p[i] > 1)
						keyword_p[i] = 1;
				}
			}
		}
		
//		if(f.getName().contains("addResultListener")){
//			System.out.println("==========");
//			for(PairOfKeywordLabel p: similarity_list){
//				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
//			}
//			System.out.println("==========");
//			for(PairOfKeywordLabel p: kl_map){
//				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
//			}
//			System.out.println("==========");
//			for(int i=0; i < input_words.size(); i++){
//				System.out.println(input_words.get(i) + ", " + e.keyword_p[i]);
//			}
//			System.out.println("==========");
//		}
		//{(pnl,new,0.3), (lbl,j,0), (img,frame,0.2)}
		// 0.3 + 0.0 + 0.2 = 0.5 
		//���ꂪ�Anew JFrame �� pnl ad img lbl �ƁA�ǂ̒��x�ގ����Ă��邩���������_�ƂȂ�B 
		
		
		
		//�L�[���[�h�Ɉ�v���Ȃ����x���̐�
		p[2] = f.getLabels().size() - keeped_label_list.size();
//		for(String flab: f.getLabels()){
//			if(numOfSameWordsIn.containsKey(flab) == false){
//				e.p[2]++;
//			}
//		}

		//����f�����[�J���ϐ��A�����o�ϐ��i�֐��j�Ȃ��e_0��+0.001����(f�̓R���e�L�X�g�ɋ߂������]�܂�������)
		//�i��ŏ����j
		if( f.getParentClass().equals("") || f.getParentClass().equals("this")){
			p[3]++;
		}

		//�p�x�̓����ʂ̌v�Z
		calcFrequencyFeatures(this, f); 
		
		calcSum();
		
		return this;
	}
	
//	public static ExplanationVector calcExplanationVector2(List<String> input_words, HashMap<String, Integer> numOfSameWordsIn, FunctionTree ft){
//		ExplanationVector e = new ExplanationVector(input_words.size());
//		Function f = ft.getRoot().getFunction();
//		
//		/*
//		 * �]���l�̌v�Z�́A
//		 * �؂�root�̕]���l�������v�Z���āA
//		 * ���łɋ��܂��Ă���q�̒l�ɑ����΂悢�B
//		 */
//
//		//������
//		for(int i = 0; i < FEATURE_NUM; i++){
//			e.p[i] = 0;
//		}
//
//		//�ؑS�̂̃m�[�h��
//		e.p[0] = 1;	//root1�Ȃ̂ŁA1.
//
//		//�L�[���[�h�Ɉ�v���郉�x���̐�
//
//		//f.label��input�Ɠ���̃L�[���[�h�����鎞�̌v�Z
//		//����P��̏o���������ꂼ��x, y�Ƃ����e_i = max(x/y, 1)�Ƃ���.
//		
//		// �L�[���[�h x in K(���̓N�G���Ɋ܂܂��L�[���[�h�̏W��),
//		// ���x�� y in L(���x���̏W��)
//		// �ގ��x f(x,y) 
//		//�S�Ă�(x,y)�ɂ��ėގ��xf���v�Z����B
//		List<PairOfKeywordLabel> similarity_list = new ArrayList<PairOfKeywordLabel>();
//		
//		for(int i=0; i < input_words.size(); i++){
//			for(String flabel: f.getLabels()){
//				String key = input_words.get(i);
//				double s = similarity(key, flabel);
//				similarity_list.add(new PairOfKeywordLabel(key, flabel, s));
//			}
//		}
//		
//		Collections.sort(similarity_list, new Comparator<PairOfKeywordLabel>() {
//			public int compare( PairOfKeywordLabel a, PairOfKeywordLabel b ) {
//				// ����1�ƈ���2�͂��ꂼ�ꃊ�X�g���̒l
//				
//				// compare ���\�b�h�ŁA���̐��A0�A���̐� (int) ��Ԃ����Ƃɂ���āA���̑召���`���܂��B 
//				// ���������������Ƃ���ꍇ�͕��̐��B�傫���Ƃ���ꍇ�͐��̐���Ԃ��� OK �ł��B
//				return  a.similarity > b.similarity ? -1 : 1;
//			}
//		} );
//		
//		
//		//�ǂ̃L�[���[�h�Ƃǂ̃��x�����Ή����邩�̑Ή��\
//		List<PairOfKeywordLabel> kl_map = new ArrayList<PairOfKeywordLabel>();
//		//���łɊm�ۂ��ꂽ���x���̃��X�g
//		List<String> keeped_label_list = new ArrayList<String>();
//		//���łɊm�ۂ��ꂽ�L�[���[�h
//		List<String> keeped_word_list = new ArrayList<String>();
//
//		for(PairOfKeywordLabel p: similarity_list){
//			//���łɃ��[�h�ƃ��x���̗��������肳��Ă��Ȃ��Ƃ��̂݁A
//			//�g�ݍ��킹��ǉ�����B
//			if(!keeped_word_list.contains(p.keyword) && !keeped_label_list.contains(p.label)){
//				//�}�b�v�ɒǉ�����B
//				kl_map.add(p);
//				//����ς݃L�[���[�h�ƃ��x����ێ�����B
//				keeped_word_list.add(p.keyword);
//				keeped_label_list.add(p.label);
//			}
//		}
//	
//		//�v�Z���ꂽ�e���_���Ae_p�ɑ������B
//		for(int i=0; i < input_words.size(); i++){
//			for(PairOfKeywordLabel kl: kl_map){
//				if(input_words.get(i).equals(kl.keyword)){
//					e.keyword_p[i] = kl.similarity;
//					if(e.keyword_p[i] > 1)
//						e.keyword_p[i] = 1;
//				}
//			}
//		}
//		
////		if(f.getName().contains("addResultListener")){
////			System.out.println("==========");
////			for(PairOfKeywordLabel p: similarity_list){
////				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
////			}
////			System.out.println("==========");
////			for(PairOfKeywordLabel p: kl_map){
////				System.out.println(p.label + ", " + p.keyword + ", " + p.similarity);
////			}
////			System.out.println("==========");
////			for(int i=0; i < input_words.size(); i++){
////				System.out.println(input_words.get(i) + ", " + e.keyword_p[i]);
////			}
////			System.out.println("==========");
////		}
//		//{(pnl,new,0.3), (lbl,j,0), (img,frame,0.2)}
//		// 0.3 + 0.0 + 0.2 = 0.5 
//		//���ꂪ�Anew JFrame �� pnl ad img lbl �ƁA�ǂ̒��x�ގ����Ă��邩���������_�ƂȂ�B 
//		
//		
//		
//		//�L�[���[�h�Ɉ�v���Ȃ����x���̐�
//		e.p[2] = f.getLabels().size() - keeped_label_list.size();
////		for(String flab: f.getLabels()){
////			if(numOfSameWordsIn.containsKey(flab) == false){
////				e.p[2]++;
////			}
////		}
//
//		//����f�����[�J���ϐ��A�����o�ϐ��i�֐��j�Ȃ��e_0��+0.001����(f�̓R���e�L�X�g�ɋ߂������]�܂�������)
//		//�i��ŏ����j
//		if( f.getParentClass().equals("") || f.getParentClass().equals("this")){
//			e.p[3]++;
//		}
//
//		//�p�x
//		e.p[4] = f.getFrequency();
//				
//		e.calcSum();
//		return e;
//	}
//	
	
	public List<Double> getWeights(){
		return para.w_arr;
	}
	
	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * �̌`���̕������Ԃ��B
	 */
	public String getWeightString(){
		String str = "";
		for(int i = 0; i < para.w_arr.size(); i++){
			str += String.valueOf(para.w_arr.get(i));
			if(i < para.w_arr.size()-1)
				str += ", ";
		}
		return str;
	}

	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * �̌`���̕������ݒ肷��B
	 */
	public void setWeightString(String input){
		String[] s_arr = input.split(",");
		for(int i = 0; i < para.w_arr.size(); i++){
			para.w_arr.set(i, Double.parseDouble(s_arr[i].trim()));
		}
	}
	
	/**
	 * 1�̓����̏d�݂̐ݒ�
	 * i��0�n�܂�
	 * @param w
	 * @param i
	 */
	public void setWeight(double w, int i){
		para.w_arr.set(i, w);
	}

	
	/*
	 * 1�̓����̏d�݂̐ݒ�
	 */
	public void setConstFreq(double c){
		para.const_freq = c;
	}
	
	public double getConstFreq(){
		return para.const_freq;
	}
	
	public void setWeights(List<Double> w){
		para.w_arr = w;
	}

	public List<Double> getSteps(){
		return para.w_step_arr;
	}
	
	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * �̌`���̕������Ԃ��B
	 */
	public String getStepString(){
		String str = "";
		for(int i = 0; i < para.w_step_arr.size(); i++){
			str += String.valueOf(para.w_step_arr.get(i));
			if(i < para.w_step_arr.size()-1)
				str += ", ";
		}
		return str;
	}
	
	/*
	 * "-0.05, 1, -0.01, 0.001"
	 * �̌`���̕������ݒ肷��B
	 */
	public void setStepString(String input){
		String[] s_arr = input.split(",");
		for(int i = 0; i < para.w_step_arr.size(); i++){
			para.w_step_arr.set(i, Double.parseDouble(s_arr[i].trim()));
		}
	}
	
	/*
	 * 1�̓����̏d�݂̍X�V��(�X�e�b�v��)�̐ݒ�
	 */
	public void setStep(double w, int i){
		para.w_step_arr.set(i, w);
	}

	public double getFeature(int i){
		return p[i];
	}

	public double getKeywordFeature(int i){
		return keyword_p[i];
	}

	//this��sum���傫�����1�A���������-1.
	public int compareTo(ExplanationVector o) {
		if(o.getSum() != -INFINITE_VALUE)
			o.calcSum();
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if(this.sum > o.getSum())
			return 1;
		else if(this.sum < o.getSum())
			return -1;
		return 0;
	}


	//�e��������(1)�̓������A��(-1)�̓��������ׂ郁�\�b�h
	/*
	 * �e�����́A
	 * 0: �؂��\������m�[�h�̐�					 Negative
	 * 1: �L�[���[�h�Ɉ�v���郉�x���̐��i�������͊֐��̐��j Positive
	 * 2: �L�[���[�h�Ɉ�v���Ȃ����x���̐� 			 Negative
	 * 3: �R���e�L�X�g�ɋ߂��֐��̐� 				 Positive
	 */
	public static boolean isPositive(int i){
		if(i == 0)
			return false;
		else if(i == 1)
			return true;
		else if(i == 2)
			return false;
		else 
			return true;
	}
	
	public static double similarity(Params para, String keyword, String label){
		String flg = para.common_subsequence;
		if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1)){
			return similarityWithLCS(keyword, label, 1);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2)){
			return similarityWithLCS(keyword, label, 2);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3)){
			return similarityWithLCS(keyword, label, 3);
		}else if(flg.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4)){
			return similarityWithLCS(keyword, label, 4);
		}else{
			return similarityWithLD(para, keyword, label);
		}
	}
	
	/**
	 * �L�[���[�h�ƃ��x���̈�v�x��Ԃ�
	 * 
	 * ���Ԃ�ۂ����܂܂P���������ׂāA
	 * ��v���������̌�/���x����
	 * �����_�ƂȂ�B
	 * @param keyword
	 * @param label
	 * @param div_flg = true �Ȃ�ALCS2, false �Ȃ� LCS1
	 * @return
	 */
	public static double similarityWithLCS(String keyword, String label, int number){
		
		//�Ō�Ƀ��x���̕����񒷂Ŋ���. �Œ����ʕ����n��/���x����
		double ret_score = 0;
		
		try{
			switch(number){
			case 1:
				ret_score = LCS1(keyword, label);
				break;
			case 2:
				ret_score = LCS2(keyword, label);
				break;
			case 3:
				ret_score = Math.pow(LCS1(keyword, label), 2);
				break;
			case 4:
				ret_score = Math.pow(LCS2(keyword, label), 2);
				break;
			default:
				throw new Exception();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret_score;
	}
	
	public static double LCS1(String keyword, String label){
		return (double)LongestCommonSubsequence.LCS_Length(keyword, label) / (label.length());//LCS1
	}
	
	public static double LCS2(String keyword, String label){
		return (double)LongestCommonSubsequence.LCS_Length(keyword, label) / (keyword.length() + label.length());//LCS2
	}
	/**
	 * �L�[���[�h�ƃ��x���̈�v�x��Ԃ�
	 * 
	 * �ގ��x  =  1/(�ҏW���� + 1)
	 * �ގ��x  =  1/�ҏW����  �i���ꂾ�ƁA����=0�̂Ƃ����U���Ă��܂��j
	 * 
	 * 
	 * @param keyword
	 * @param label
	 * @return
	 */
	public static double similarityWithLD(Params para, String keyword, String label){
		double ret_score = 0;
		
		int del = para.ld_delete;
		int rep = para.ld_replace;
		int add = para.ld_add;
		
		try{
			LevenshteinDistance ld = new LevenshteinDistance(del, rep, add);
			ret_score = 1 / (double)(ld.edit(keyword, label) + 1.0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret_score;
	}
	
//	public static double similarity(String keyword, String label){
//		int match_ch_count = 0;//�}�b�`����������
//		String sub_label = label;	//���x���̕���������
//		for(int i = 0; i < keyword.length(); i++){
//			int idx = sub_label.indexOf(keyword.charAt(i));
//			if(idx != -1){
//				match_ch_count++;
//				sub_label = sub_label.substring(idx + 1);//�������ꂽ�����̎��̕�����擪�����Ƃ��镶����
//				if(sub_label.length() == 0){//���x���̏I�[�ɓ��B�����B
//					break;
//				}
//			}else{
//				//1�����ł�label�ɑ��݂��Ȃ��Ƃ��͂����Ƀ��[�v�𔲂���B
//				break;
//			}
//			
//		}
//		double ret_score = (double)match_ch_count / label.length();	//�Ō�Ƀ��x���̕����񒷂Ŋ���
//		return ret_score;
//	}
	
	public static void main(String[] args){
//		System.out.println(similarityWithLCS("usg", "usage", true));
//		System.out.println(similarityWithLCS("usg", "anchor", true));
//		System.out.println(similarityWithLCS("usg", "arg", true));
//		System.out.println(similarityWithLCS("usg", "background", true));
//		System.out.println(similarityWithLCS("usg", "in", true));
//		System.out.println("========");
//		System.out.println(similarityWithLCS("usg", "usage", false));
//		System.out.println(similarityWithLCS("usg", "anchor", false));
//		System.out.println(similarityWithLCS("usg", "arg", false));
//		System.out.println(similarityWithLCS("usg", "background", false));
//		System.out.println(similarityWithLCS("usg", "in", false));
//		System.out.println("========");
//		System.out.println(similarityWithLD("usg", "usage"));
//		System.out.println(similarityWithLD("usg", "anchor"));
//		System.out.println(similarityWithLD("usg", "arg"));
//		System.out.println(similarityWithLD("usg", "background"));
//		System.out.println(similarityWithLD("usg", "in"));
//		System.out.println("========");
//		
////		LevenshteinDistance.w_delete = 1;
////		LevenshteinDistance.w_replace = 2;
////		LevenshteinDistance.w_insert = 2;
//		
//		System.out.println(similarityWithLD("usg", "usage"));
//		System.out.println(similarityWithLD("usg", "anchor"));
//		System.out.println(similarityWithLD("usg", "arg"));
//		System.out.println(similarityWithLD("usg", "background"));
//		System.out.println(similarityWithLD("usg", "in"));
		
	}
}
