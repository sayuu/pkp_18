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
		monitor.beginTask("�O���b�h�T�[�`���s��", totalWork);
		//monitor.subTask("�X�y���`�F�b�N");	//�T�u�^�X�N����������
		monitor.worked(1);
		//����
		gs.run(monitor);
		//�L�����Z�����ꂽ��I��
		if (monitor.isCanceled()) {
		    return Status.CANCEL_STATUS;
		}
		//�I��
		monitor.worked(totalWork);
		monitor.done();
	  
		return Status.OK_STATUS;
	}

}
