package experiment2;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import state.KpRunningState;

public class Experiment2Job extends Job{

	Experiment2 ls;
	int numOfSteps;
	
	public Experiment2Job(String name, Experiment2 ls, int numOfSteps) {
		super(name);
		// TODO Auto-generated constructor stub
		this.ls = ls;
		this.numOfSteps = numOfSteps;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		monitor.beginTask("ローカルサーチ実行中", 10);
		//monitor.subTask("スペルチェック");	//サブタスク名を書ける
		monitor.worked(1);
		//処理
		ls.run(numOfSteps, KpRunningState.LOCAL_SEARCH_BATCH, monitor);
		//キャンセルされたら終了
		if (monitor.isCanceled()) {
		    return Status.CANCEL_STATUS;
		}
		//終了
		monitor.worked(10);
		monitor.done();
	  
		return Status.OK_STATUS;
	}

}
