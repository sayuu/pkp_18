package keywordProgramming.exp;

import java.util.ArrayList;
import java.util.List;

public class Params {
		//�����̏d�݃x�N�g��
		public List<Double> w_arr = new ArrayList<Double>(ExplanationVector.FEATURE_NUM);		
		
		//�p�x�̒萔
		public double const_freq;
		
		//�d�݂̍X�V��
		public List<Double> w_step_arr = new ArrayList<Double>(ExplanationVector.FEATURE_NUM);
		
		//�L�[���[�h����ON
		public String separate_keywords;
		//������v�ł�OK
		public String common_subsequence;
		
		public int ld_delete;
		public int ld_replace;
		public int ld_add;
		
		//�L�[���[�h�Z�k��
		public String shortened_input_keywords;
					
		public Params(){
			
		}
		
		public boolean equals(Params p){
			if(!this.w_arr.equals(p.w_arr))
				return false;
			if(!this.w_step_arr.equals(p.w_step_arr))
				return false;
			if(this.const_freq != p.const_freq)
				return false;
			return true;
			
		}
		
		public void print(){
			for(Double d: w_arr){
				System.out.print(d + ",");
			}
			System.out.println(const_freq);
		}
}
