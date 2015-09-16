package keywordProgramming;

import java.util.ArrayList;
import java.util.List;

import ast.AstUtil;

public class Frequency {
		public int apiFreq;
		public String parent_class;
		public String name;
		public String return_type;
		public List<String> parameters;
		
		/**
		 * db_str �͐擪��apiFreq�@��
		 * ��́AFunction��DBString�Ɠ��������B
		 * @param db_str
		 */
		public Frequency(String db_str){
			String s[] = db_str.split(",");
			
			//�f�[�^�s��
			if(s.length < 8)
				return;//�j��
			
			this.apiFreq = Integer.valueOf(s[0]);
			parent_class = s[1];
			return_type = AstUtil.formatTypeName(s[6]);
			name = s[7];
			
			parameters = new ArrayList<String>();
			
			if(s.length <= 9)//�p�����[�^�Ȃ��B
				return;
			
			for(int i = 9; i < s.length; i++){
				String param = s[i].trim();
				if(!param.equals(""))
					parameters.add(AstUtil.formatTypeName(param));
			}
		}
		
//		public Frequency(String db_str){
//			String s[] = db_str.split(",");
//			this.apiFreq = Integer.valueOf(s[0]);
//			this.parent_class = s[1];
//			this.name = s[2];		
//			this.return_type = s[3];
//			parameters = new ArrayList<String>();
//			
//			if(s.length == 4)
//				return;
//			
//			for(int i = 4; i < s.length; i++){
//				parameters.add(s[i]);
//			}
//			
//		}
		
		public Frequency(int apiFreq, String parent_class, String name, String ret_type, List<String> params){
			this.apiFreq = apiFreq;
			this.parent_class = parent_class;
			this.name = name;		
			this.return_type = ret_type;
			this.parameters = params;
		}
		
		public void setFrequency(Function f){
			
		}
}
