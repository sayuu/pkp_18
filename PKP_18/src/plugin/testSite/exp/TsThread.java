package plugin.testSite.exp;

import keywordProgramming.ExplanationVector;
import keywordProgramming.exp.Params;
import state.KpRunningState;

public class TsThread extends Thread{

	TsSync sync;
	TestSite ts;
	Params para;
	String projectName;
	long startTime;
	
	public TsThread(TsSync sync, Params para, TestSite ts, String projectName, long startTime){
		this.sync = sync;
		this.ts = ts;
		this.para = para;
		this.projectName = projectName;
		this.startTime = startTime;
	}
	
	public void run(){
		ts.runKeywordProgramming(para, KpRunningState.LOCAL_SEARCH_BATCH);
		sync.printResult(para, ts, projectName, startTime);
		sync.delSync();
	}
	
	
}
