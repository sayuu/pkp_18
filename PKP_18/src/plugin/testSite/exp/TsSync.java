package plugin.testSite.exp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import plugin.preference.PreferenceInitializer;
import keywordProgramming.ExplanationVector;
import keywordProgramming.exp.Params;

public class TsSync extends Thread{

	int cnt; // �A�N�e�B�u�X���b�h��
	
	public String output = ""; //�o�͕�����
	
	  public static void main(String args[]) {
		    int num = 3;
		    TsThread ths[];
		    TsSync sync;
		    int i;

		    // �����I�u�W�F�N�g�̍쐬
		    sync = new TsSync();

		    // �q�X���b�h���z��̍쐬
		    ths = new TsThread[num];

		    // �X���b�h�̋N��
		    for (i = 0; i < num; i++) {
		      //ths[i] = new TsThread(sync, (i+1)*1000);
		      ths[i].start();
		    }

		    // �e�X���b�h�̏����i�q�X���b�h�Ɠ������s�j
		    System.out.println("Hello World! at " + Thread.currentThread().getName());

		    // �q�̏I����҂�
		    sync.waitSync();

		    // �e�X���b�h�̏����i�q�X���b�h�I����Ɏ��s�j
		    System.out.println("Hello World! at " + Thread.currentThread().getName());
	  }

	  public TsSync() {
	    // �����I�u�W�F�N�g�̏���������
	    cnt = 0;
	  }

	  public synchronized void addSync() {
	    // �����I�u�W�F�N�g�̒ǉ�
	    cnt++;
	  }

	  public synchronized void delSync() {
	    // �����I�u�W�F�N�g�̍폜
	    cnt--;
	    try {
	      notify();
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }

	  public synchronized void waitSync() {
	    // �����I�u�W�F�N�g�̑҂����킹�i�q�����ׂďI������܂ő҂j
		
	    while (cnt > 0) {
	      try {
	        
	        wait();
	      } catch (Exception ex) {
	        ex.printStackTrace();
	      }
	    }
	  }
	  
	  public synchronized void printResult(Params para, TestSite ts, String projectName, long startTime){
//			for(int i=0;i<ExplanationVector.FEATURE_NUM;i++)
//				System.out.print(para.w_arr.get(i) + ",");
//			System.out.print(para.const_freq + ",");
		  
	  		String output = "";
	  		output += projectName;
			output += ",";
		  		output +=ts.getId();
				output += ",";
				output += para.shortened_input_keywords;
				output += ",";
				output += para.separate_keywords;
				output += ",";
				output += para.common_subsequence;
				output += ",";
				output += para.ld_delete;
				output += ",";
				output += para.ld_replace;
				output += ",";
				output += para.ld_add;
				output += ",";
				output += ts.getAnswerNumber(para, 99999);
			
				System.out.println(output);
			
				this.output += output + "\r\n";
		}
}
