package console_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;

import plugin.preference.PreferenceInitializer;

import keywordProgramming.Params;

public class ResultFile implements Comparable<ResultFile>{

	String name;
	String output_string;
	Output output;
	String time;
	String project_name;
	Params para;
	File file;
	
	ResultFile(String result){
		String ss[] = result.split(",");
		this.para = new Params();
		this.para.shortened_input_keywords = ss[0];
		this.para.separate_keywords = ss[1];
		this.para.common_subsequence = ss[2];
		this.para.ld_delete = Integer.parseInt(ss[3]);
		this.para.ld_replace = Integer.parseInt(ss[4]);
		this.para.ld_add = Integer.parseInt(ss[5]);
		this.para.ld_const = Double.parseDouble(ss[6]);
		this.para.ld_k = Integer.parseInt(ss[7]);
		this.output = new Output(null);
		if(ss[8].contains("ŠY“–")){
			return;
		}
		output.total = Integer.parseInt(ss[8]);
		output.sum1 = Integer.parseInt(ss[9]);
		output.sum3 = Integer.parseInt(ss[10]);
		output.sum5 = Integer.parseInt(ss[11]);
		output.sum10 = Integer.parseInt(ss[12]);
		output.sum_all = Integer.parseInt(ss[13]);
	}
	
	ResultFile(File file, boolean flg_read_contents){
		if(file == null)
			return;
		
		this.file = file;
		this.name = file.getName();
		String ss[] = this.name.split(",");

		this.project_name = ss[0];
		this.time = ss[1];
		this.para = new Params();
		
		this.para.shortened_input_keywords = ss[2];
		this.para.separate_keywords = ss[3];
		this.para.common_subsequence = ss[4];
		this.para.ld_delete = Integer.parseInt(ss[5]);
		this.para.ld_replace = Integer.parseInt(ss[6]);
		if(ss.length == 8){
			this.para.ld_add = Integer.parseInt(ss[7].replace(".txt", ""));
			if(this.para.common_subsequence.equals(PreferenceInitializer.COMMON_SUBSEQUENCE_LD))
				this.para.ld_const = 1.0;
			else
				this.para.ld_const = 0.0;
		}else{
			this.para.ld_add = Integer.parseInt(ss[7]);
			this.para.ld_const = Double.parseDouble(ss[8].replace(".txt", ""));
		}
		if(flg_read_contents){
		FileReader fr;
			try {
				fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				//1s–Ú‚¾‚¯‚ðŽæ‚é
				this.output_string = br.readLine();
				this.output = new Output(output_string);
				
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

	public boolean matchParams(Params other){
		if(this.para == null || other == null){
			System.out.println();
		}
		if(!this.para.shortened_input_keywords.equals(other.shortened_input_keywords))
			return false;
		if(!this.para.separate_keywords.equals(other.separate_keywords))
			return false;
		if(!this.para.common_subsequence.equals(other.common_subsequence))
			return false;
		if(this.para.ld_delete != other.ld_delete)
			return false;
		if(this.para.ld_replace != other.ld_replace)
			return false;
		if(this.para.ld_add != other.ld_add)
			return false;
		if(this.para.ld_const != other.ld_const)
			return false;
		
		return true;
	}
	
	@Override
	public int compareTo(ResultFile o) {
		
		if(this.time.compareTo(o.time) > 0)
			return 1;
		else if(this.time.compareTo(o.time) < 0)
			return -1;
		return 0;
	}
	

}
