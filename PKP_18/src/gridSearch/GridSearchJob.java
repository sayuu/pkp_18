package gridSearch;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import plugin.testSite.TestSite;

public class GridSearchJob extends Job{

	GridSearch gs;
	int numOfSteps;
	final int totalWork = 10000;
	
	public GridSearchJob(String name, TestSite[] list) {
		super(name);
		// TODO Auto-generated constructor stub
		this.gs = new GridSearch(list, totalWork);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		monitor.beginTask("グリッドサーチ実行中", totalWork);
		//monitor.subTask("スペルチェック");	//サブタスク名を書ける
		monitor.worked(1);
		//処理
		gs.run(monitor);
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
