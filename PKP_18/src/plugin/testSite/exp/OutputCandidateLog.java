package plugin.testSite.exp;

import keywordProgramming.ExplanationVector;
import keywordProgramming.FunctionTree;

/*
 * �I�����C���w�K�p�ɕۑ������P�̏o�͌�������킷�N���X�B
 * 
 * KP���s�̂��тɕۑ����ꂽ�o�͕�������i�[����B
 * 
 * �i�[������̂́A
 * �P�@�o�͕�����
 * �Q�@�e�����̓�����
 * �R  e_arr (�������͂����L�[���[�h�ɑ΂�����́j
 * 
 */
public class OutputCandidateLog {

	private String completeMethodString;	//�o�͕�����
	private double p[] = new double [ExplanationVector.FEATURE_NUM];	//������
	private double[] keyword_p;		//�L�[���[�h���Ƃ̓����� e_arr
	private double sum;	//�]���l
	
	/*
	 *	line��logFile��1�s��\���B
	 *�@�f�[�^�̕��т́A
	 * [4�̓����̓�����], [e_arr], �o�͕�����
	 * �̏��B
	 *  
	 * ��F�L�[���[�h����3�̂Ƃ��B
	 * 3.0, 3.0, 2.0, 1.0, 1.0, 1.0, 1.0, "new InputStream().read(data)
	 * 
	 * �o�͕�����̐擪�ɂ͖ڈ�Ƃ��� " ���t���B
	 */
	
	public OutputCandidateLog(String line, int keyword_length){
	
		keyword_p = new double [keyword_length];
		String []s_arr = line.split("\"");
		String []s_arr2 = s_arr[0].split(",");
		
		for(int i = 0; i < p.length + keyword_length; i++){
			if(i < p.length){
				p[i] = Double.parseDouble(s_arr2[i]);
			}
			else if(i < keyword_length){
				keyword_p[i] = Double.parseDouble(s_arr2[i]);
			}
		}
		completeMethodString = s_arr[1];
		
//		System.out.println(toString());
		
	}
	
	public OutputCandidateLog(FunctionTree tree){
		for(int i = 0; i < p.length; i++){
			p[i] = tree.e_vec.getFeature(i);
		}
		
		keyword_p = new double [tree.e_vec.keywords_length];
		for(int i = 0; i < tree.e_vec.keywords_length; i++){
			keyword_p[i] = tree.e_vec.getKeywordFeature(i);
		}
		
		completeMethodString = tree.toCompleteMethodString();
	}
	
	/*
	 * �ω������d�� w_arr �ɑ΂���]���l���v�Z����B
	 */
	public void calculateEvaluationValue(){
		double []w_arr = ExplanationVector.getWeights();
		sum = 0.0;
		//�L�[���[�h�A���[����L�[���[�h�̓����ʂ��Z�o�B
		double sum_k = 0.0;
		for(double k: keyword_p)
			sum_k += k;
		p[1] = sum_k;

		//�e�����̍��v�l�B
		for(int i = 0; i < ExplanationVector.FEATURE_NUM; i++){
			sum += w_arr[i] * p[i];
		}
	}
	
	public double getEvaluationValue(){
		return sum;
	}
	
	public String getCompleteMethodString(){
		return completeMethodString;
	}
	
	public String toString(){
		String s = "";
		for(int i = 0; i < p.length; i++){
			s += p[i] + ", ";
		}
		for(int i = 0; i < keyword_p.length; i++){
			s += keyword_p[i] + ", ";
		}
		s += completeMethodString;
		return s;
	}
}
