package keywordProgramming;

import java.util.ArrayList;
import java.util.List;

public class Params {
		//特徴の重みベクトル
		public List<Double> w_arr = new ArrayList<Double>(ExplanationVector.FEATURE_NUM);		
		
		//頻度の定数
		public double const_freq;
		
		//重みの更新幅
		public List<Double> w_step_arr = new ArrayList<Double>(ExplanationVector.FEATURE_NUM);
		
		//キーワード分割ON
		public String separate_keywords;
		//部分一致でもOK
		public String common_subsequence;
		
		public int ld_delete;
		public int ld_replace;
		public int ld_add;
		
		public double ld_const;
		
		public int ld_k;
		
		//キーワード短縮化
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
