package gridSearch;

import java.util.ArrayList;
import java.util.List;

import keywordProgramming.ExplanationVector;
import keywordProgramming.KeywordProgramming;

import localSearch.LocalSearch;
import logging.LogControl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import state.KpRunningState;

/*
 * GridSearch�Ɋւ���N���X
 * 
 * �Œ肷��v�f���w��ł���
 * CSV���o�͂ł���
 * 
 *
 * 
 */
public class GridSearch {

	private TestSite[] testSites;
	
	//�e�^�X�N�̏o�͌��Q�̒��ŁA�������o����������
	private List<Integer> answerOrders = new ArrayList<Integer>();
	
	//�����Q�̒l
	private double f2_const;//��

	//�e�����̊J�n�ƏI���̒l (�����Q�ȊO�j
	private double f1_start;//��
	private double f3_start;//��
	private double f4_start;//��
	
	private double f1_end;
	private double f3_end;
	private double f4_end;

	private double f1_step;
	private double f3_step;
	private double f4_step;
	
	private int one_work;
	private int totalwork;
	
	private int numOfKpExec;//�����1��KP�����s���邩�B
	/*
	 * �R���X�g���N�^
	 */
	public GridSearch(TestSite[] sites, int totalwork){
		testSites = sites;
		this.totalwork = totalwork;
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		numOfKpExec = store.getInt(PreferenceInitializer.GRID_COUNT_FOR_KP);
		
		f2_const = store.getDouble(PreferenceInitializer.GRID_WEIGHT_1_CONSTANT);
		
		f1_start = -store.getDouble(PreferenceInitializer.GRID_START_WEIGHT_0);
		f3_start = -store.getDouble(PreferenceInitializer.GRID_START_WEIGHT_2);
		f4_start = store.getDouble(PreferenceInitializer.GRID_START_WEIGHT_3);
		f1_end = -store.getDouble(PreferenceInitializer.GRID_END_WEIGHT_0);
		f3_end = -store.getDouble(PreferenceInitializer.GRID_END_WEIGHT_2);
		f4_end = store.getDouble(PreferenceInitializer.GRID_END_WEIGHT_3);
		
		//��Βl�̑傫������end�ɂ���B
		if(f1_start < f1_end){
			double tmp = f1_start;
			f1_start = f1_end;
			f1_end = tmp;
		}
		if(f3_start < f3_end){
			double tmp = f3_start;
			f3_start = f3_end;
			f3_end = tmp;
		}	
		if(f4_start > f4_end){
			double tmp = f4_start;
			f4_start = f4_end;
			f4_end = tmp;
		}
		
		f1_step = store.getDouble(PreferenceInitializer.GRID_STEP_WIDTH_0);
		f3_step = store.getDouble(PreferenceInitializer.GRID_STEP_WIDTH_2);
		f4_step = store.getDouble(PreferenceInitializer.GRID_STEP_WIDTH_3);
		
		one_work = (int) Math.abs( (f1_start - f1_end)/f1_step * (f3_start - f3_end)/f3_step * (f4_start - f4_end)/f4_step / totalwork);
		
		//BEST_R�̐ݒ�
		KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.GRID_BEST_R);
	}

	public void run(IProgressMonitor monitor){
		
		LogControl logControl = new LogControl(LogControl.GRID_SEARCH);
		logControl.printLogState();

		logControl.println(">>> �O���b�h�T�[�` >>>");
		
		logControl.println(" ����1 start= " + f1_start + ", end= " + f1_end + ", ���ݕ�= " + f1_step);
		logControl.println(" ����2 �l= " + f2_const + " �ŌŒ�");
		logControl.println(" ����3 start= " + f3_start + ", end= " + f3_end + ", ���ݕ�= " + f3_step);
		logControl.println(" ����4 start= " + f4_start + ", end= " + f4_end + ", ���ݕ�= " + f4_step);
		logControl.println(" KP���s����: " + numOfKpExec + " ���1��s��.");
		logControl.println(" ���̓^�X�N��: " + testSites.length );
		
		logControl.println(" >> ���̓^�X�N�ꗗ >> �o�͌`���F [ID, �~�����o��] ");

		for(TestSite ts: testSites){
			logControl.println("  " + ts.getId()+ ", "+ ts.getSelectedString());
		}
		
		logControl.println(" << ���̓^�X�N�ꗗ << �o�͌`���F [ID, �~�����o��] ");
		
		logControl.println("[�J�E���^, ������(4�����ɕ\��), ���v�^�X�N��, ���������݂����^�X�N�̐�, �t���X�R�A, �����^�X�N�̏��ʂ̔z��(���ʂ�0�Ԏn�܂�. �o�����Ȃ��ꍇ��-1.�@�z��̏��Ԃ̓^�X�N�ꗗ�r���[�̏ォ�珇), (KP���sor���ԒZ�k)]");
		
		int worked = 1; //���j�^�p
		int counter_for_kp = 0;
		int counter_for_grobal = 0;
		 
		//�l��double������for���ł͂Ȃ���while�����g��
		/*
		 * �L�[���[�h�ƃ��x���̈�v�̓����Q��1.0�ŌŒ肷��B
		 */
		double f1_now = f1_start; 
		while(f1_now > f1_end){
			double f3_now = f3_start; 
			while(f3_now > f3_end){
				double f4_now = f4_start; 
				while(f4_now < f4_end){
					//�L�����Z�����ꂽ��I��
					if (monitor.isCanceled()) {
						logControl.println("<<< �O���b�h�T�[�` <<<");
						logControl.close();
					    return;
					}

					
					//�d�݂��X�V
					double[] tmp_w = {f1_now, f2_const, f3_now, f4_now};	//2�Ԗڂ̏d�݂��w��l�ŌŒ�.
					ExplanationVector.setWeights(tmp_w);
					
					
					double score;
					
					boolean flg_kp = false;//KP�����s����񂩔ۂ��B
					String flg_kp_string = "���ԒZ�k";
					if(counter_for_kp % numOfKpExec == 0){
						flg_kp = true;
						flg_kp_string = "KP���s";
					}
					
					/*
					 * �w��񐔂��Ƃ�KP���s���B
					 */
					if(flg_kp){
						LocalSearch.runKeywordProgrammingForAllTasks(monitor, testSites, answerOrders, KpRunningState.GRID_SEARCH);
						//�X�R�A�̌v�Z
						score = LocalSearch.getScoreOfAnswerAppearancedOrder(answerOrders);
					}else{
						if(monitor != null)
							monitor.setTaskName("���ԒZ�k�̕��@�ɂ���ăX�R�A���v�Z�B");
						//KP�����s���Ȃ��Ƃ��B
						//�Čv�Z
						score = LocalSearch.reCalculateScore(testSites, tmp_w, answerOrders);
					}
					
					
					//���������݂����^�X�N��
					int ans_num = LocalSearch.getScoreOfAnswerAppearancedTaskNumbar(answerOrders);
					/*
					 * �\��
					 * 
					 * �J�E���^�A�����ʁA���v�^�X�N���A���������݂����^�X�N���A�t���X�R�A�A�����^�X�N�̏���
					 */
					logControl.println(counter_for_grobal + ", " + f1_now + ", " + f2_const + ", " + f3_now + ", " + f4_now + ", " + 
					 testSites.length + ", " + ans_num + ", " + score + ", " + answerOrders + ", " + flg_kp_string);
					
					//���X�g�̃N���A
					answerOrders.clear();
					//���j�^�𓮂���
					worked += one_work;
					monitor.worked(worked);
					
					counter_for_grobal++;
					counter_for_kp++;
					f4_now += f4_step;	//�v���X����B
				}
				f3_now -= f3_step;	//�}�C�i�X����B
			}
			f1_now -= f1_step;	//�}�C�i�X����B
		}
		
		logControl.close();
	}

	/*
	 * �Ђ�����Ԃ��B
	 */
	private void change(double f1, double f2){
		double tmp = f1;
		f1 = f2;
		f2 = tmp;
	}
}
