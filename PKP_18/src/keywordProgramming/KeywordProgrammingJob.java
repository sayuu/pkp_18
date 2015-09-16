package keywordProgramming;


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
		monitor.beginTask("�L�[���[�h�v���O���~���O���s��", totalWork);
		//monitor.subTask("�X�y���`�F�b�N");	//�T�u�^�X�N����������
		
		for(int i = 0; i < testSites.length; i++){
			//���[�U�[���������L�����Z������
			if(monitor != null && monitor.isCanceled()) {
			    return Status.CANCEL_STATUS;
			}
			if(monitor != null)
				monitor.setTaskName("�^�X�NId= "+ testSites[i].getId() +"(" +(i+1)+ "/" + testSites.length + ") �ɂ��āA�L�[���[�h�v���O���~���O�̃A���S���Y���ɂ��o�͌��Q�𐶐���");
			testSites[i].initKeywordProgramming();
			testSites[i].runKeywordProgramming(KpRunningState.STATE_KP_BATCH);
			monitor.worked(i);
		}
		
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
