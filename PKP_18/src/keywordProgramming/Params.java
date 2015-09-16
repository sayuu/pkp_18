package keywordProgramming;

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
		
		public double ld_const;
		
		public int ld_k;
		
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
		
		public String printString(){
			String ret = "";
			ret += (this.shortened_input_keywords);
			ret +=(",");
			ret +=(this.separate_keywords);
			ret +=(",");
			ret +=(this.common_subsequence);
			ret +=(",");
			ret +=(this.ld_delete + "," + this.ld_replace + "," + this.ld_add + "," + this.ld_const + "," + this.ld_k);
			return ret;
		}
		
		public void print(){
			System.out.print(this.printString());
		}
		
		public void println(){
			this.print();
			System.out.println();
		}
}
