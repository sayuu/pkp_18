package keywordProgramming.exp;

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
		
		public void print(){
			for(Double d: w_arr){
				System.out.print(d + ",");
			}
			System.out.println(const_freq);
		}
}
