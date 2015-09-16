package localSearch;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import state.KpRunningState;

public class LocalSearchJob extends Job{

	LocalSearch ls;
	int numOfSteps;
	
	public LocalSearchJob(String name, LocalSearch ls, int numOfSteps) {
		super(name);
		// TODO Auto-generated constructor stub
		this.ls = ls;
		this.numOfSteps = numOfSteps;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		monitor.beginTask("���[�J���T�[�`���s��", 10);
		//monitor.subTask("�X�y���`�F�b�N");	//�T�u�^�X�N����������
		monitor.worked(1);
		//����
		ls.run(numOfSteps, KpRunningState.LOCAL_SEARCH_BATCH, monitor);
		//�L�����Z�����ꂽ��I��
		if (monitor.isCanceled()) {
		    return Status.CANCEL_STATUS;
		}
		//�I��
		monitor.worked(10);
		monitor.done();
	  
		return Status.OK_STATUS;
	}

}
