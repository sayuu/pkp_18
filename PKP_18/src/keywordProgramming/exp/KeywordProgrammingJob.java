package keywordProgramming.exp;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import plugin.testSite.TestSite;
import state.KpRunningState;

public class KeywordProgrammingJob extends Job{

	private int totalWork;
	private TestSite []testSites;
			
	public KeywordProgrammingJob(String name, TestSite[] list) {
		super(name);
		// TODO Auto-generated constructor stub
		this.totalWork = list.length;
		this.testSites = list;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		monitor.beginTask("キーワードプログラミング実行中", totalWork);
		//monitor.subTask("スペルチェック");	//サブタスク名を書ける
		
		for(int i = 0; i < testSites.length; i++){
			//ユーザーが処理をキャンセルした
			if(monitor != null && monitor.isCanceled()) {
			    return Status.CANCEL_STATUS;
			}
			if(monitor != null)
				monitor.setTaskName("タスクId= "+ testSites[i].getId() +"(" +(i+1)+ "/" + testSites.length + ") について、キーワードプログラミングのアルゴリズムにより出力候補群を生成中");
			testSites[i].initKeywordProgramming();
			testSites[i].runKeywordProgramming(KpRunningState.STATE_KP_BATCH);
			monitor.worked(i);
		}
		
		//キャンセルされたら終了
		if (monitor.isCanceled()) {
		    return Status.CANCEL_STATUS;
		}
		//終了
		monitor.worked(totalWork);
		monitor.done();
	  
		return Status.OK_STATUS;
	}

}
