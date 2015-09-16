package experiment2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import state.KpRunningState;

import keywordProgramming.ExplanationVector;
import keywordProgramming.FunctionTree;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.Type;
import logging.LogControl;

/*
 * �����^�X�N�I���ł���΁A
 * ���̂����́A
 * ��葽���̃^�X�N�ɂ��Đ������o��悤�ȏd�݂�I��.
 * FullRun()�̏ꍇ�B
 * 
 * ��񂾂����[�J���T�[�`���s���̂͂ǂ����悤��
 * �g�����g��Ȃ����B
 * �w�K�����ǂ��̂����̂ƌ����Ă��ȁB
 * 
 * ��xKP�����āA
 * �������瓾��ꂽ���ʂ����������Ē������Ă������ǁA
 * �����ł͂Ȃ��āA�����KP����ق����ǂ��̂��낤���H
 * GridSearch�ł͂��������̂����B
 * 
 * ���ʂɒ��ڂ���̂��A
 * ���𐔂ɒ��ڂ���̂��A
 * �������𐔂Ȃ�A���ʂ���ɂ��Ă����ق����ǂ��Ƃ��B
 */

public class Experiment2{

	//�]���l�̏��ɕ��ׂ��֐��؂̏���
	private int selected_tree_num;

	//�d�݂̍X�V��
	private double [] step_w;

	//�I��������₪�ŏ�ʂɗ���d�݂̑g�ݍ��킹
	private double [] best_w;

	//�v�Z�������ʏo�����̍ŏ�����
	private int min_order;

	private TestSite[] testSites;
	
	//�������o�������^�X�N���̍ő升�v�l
	//private int max_sum_ans_tasks;
	
	//�X�R�A�̍ő�l
	private double max_score;
	
	//�e�^�X�N�̏o�͌��Q��List
	//private List<FunctionTree[]> functionTreeLists = new ArrayList<FunctionTree[]>();
	
	//�e�^�X�N�̏o�͌��Q�̒��ŁA�������o����������
	private List<Integer> answerOrders = new ArrayList<Integer>();
	
	private boolean isOnline;	//�I�����C���w�K���A�ۂ�
	
	private boolean flg_log_step_by_step;	//1�X�e�b�v���ƂɃ��O���o�����ۂ��̃t���O
	private boolean flg_log_neighbours;		//1�X�e�b�v���ƂɊe�ߖT�̃��O���o�����ۂ��̃t���O
		
	private int times_of_steps;	//�w��X�e�b�v��
	
	private int counter_step;	//�X�e�b�v���J�E���^
	private int counter_neighbour;	//�ߖT�J�E���^
	
	LogControl logControl;	//���O�o�͊Ǘ��I�u�W�F�N�g
	
	//�P��̗�i�p�x���j
	private List<String> term_list = new ArrayList<String>();
	public static final String TERM_FREQUENT_FILE_NAME = "term_total.txt";

	//���͂Ɏg���P�ꐔ�A(�c���P�ꐔ)
	int numOfKeywords;
	
	//�P��̐؂�̂ď��@�p�x�����A�t�����B
	boolean flg_desc;
	
	/*
	 * �R���X�g���N�^
	 */
	public Experiment2(TestSite[] sites, int numOfKeywords, boolean isOnline, boolean flg_desc){
		testSites = sites;
		this.isOnline = isOnline;
		this.numOfKeywords = numOfKeywords;
		this.flg_desc = flg_desc;
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		flg_log_step_by_step = store.getBoolean(PreferenceInitializer.LOG_LOCAL_SEARCH_STEP_BY_STEP);
		flg_log_neighbours = store.getBoolean(PreferenceInitializer.LOG_LOCAL_SEARCH_NEIGHBOURS);
		KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);

		if(isOnline){
			times_of_steps = store.getInt(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_STEPS);
		}else{
			times_of_steps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
		}
		
		//�P��p�x�̓ǂݍ���
		ReadTermFrequency();
	}

	/*
	 * �P��p�x�̓ǂݍ���
	 */
	public void ReadTermFrequency(){
		//�v���O�C���̒��ɂ���t�@�C���̓ǂݍ���
		
		Path f_path = new Path(TERM_FREQUENT_FILE_NAME);
		URL f_fileURL = FileLocator.find(Activator.bundle, f_path, null);
		InputStream f_in;
		
		try {
			f_in = f_fileURL.openStream();
			BufferedReader f_reader = new BufferedReader(new InputStreamReader(f_in));
			String s;
			while ((s = f_reader.readLine()) != null) {
				term_list.add(s.split(":")[0]);
			}
			f_reader.close();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	/*
	 *  �����^�X�N�I���̂����A
	 *  ��葽���̃^�X�N�ɂ��Đ������o��悤�ȏd�݂�I��.
	 * 
	      �w���numOfSteps
	 * �܂ŁA
	 * ���[�J���T�[�`�i�R�o��@�j���s���B
	 * ������������܂ł�-1.
	 * 
	 */
	public void run(int numOfSteps, String state, IProgressMonitor monitor){
		
		long start = System.currentTimeMillis();

		//�]���l�̍Čv�Z

		/*
		 * �S�Ẵp�����[�^�̑g�ݍ��킹�ɂ��čČv�Z����B
		 * ���������āA���̑g�ݍ��킹�̐��́A
		 * �p�����[�^�̐���n�Ƃ����(���������ł�4�ł���)
		 * �e�p�����[�^�̕ω��̎d����3�ʂ�
		 * �ic���₷[increment]�Ac���炷[decrement]�A�������Ȃ�[zero]�j(c�͒萔.)
		 * �Ȃ̂ŁA3��n��ʂ�ƂȂ�B
		 */

		//�J�n���̓����̏d�݃x�N�g��
		double []start_w = Arrays.copyOf(ExplanationVector.getWeights(), ExplanationVector.getWeights().length);
		this.step_w = ExplanationVector.getSteps();		//�����̏d�݂̍X�V��
		this.best_w = Arrays.copyOf(start_w, start_w.length);	//�I���������ɍŏ��̏��ʂ�^����d�݂̑g

		logControl = new LogControl(LogControl.LOCAL_SEARCH);
//		logControl.printLogState();
//
//		logControl.println(">>> ���[�J���T�[�`�O >>>");
//		logControl.println(" �w��X�e�b�v�� =" + times_of_steps);
//		
//		logControl.println(" >> ���̓^�X�N�ꗗ >> �o�͌`���F [ID, �~�����o��] ");
//
//		for(TestSite ts: testSites){
//			logControl.println("  " + ts.getId()+ ", "+ ts.getSelectedString());
//		}
//		
//		logControl.println(" << ���̓^�X�N�ꗗ << �o�͌`���F [ID, �~�����o��] ");

		/*
		 * �e�^�X�N�ɂ��āA���p�����[�^�ɂ�����o�͌����擾���Ă����B
		 */
		if(isOnline)
			getSelectedStringOrders(testSites, answerOrders);			//�L�[���[�h�v���O���~���O�̕K�v�Ȃ��B
		else{
			//runKeywordProgrammingForAllTasks(monitor, testSites, answerOrders, state);
			runKeywordProgrammingForAllTasksWithSelectedKeywords(monitor, testSites, answerOrders, state);
		}
		if(monitor != null)
			monitor.worked(4);
		
		//�X�R�A�̌v�Z
		max_score = getScoreOfAnswerAppearancedOrder(answerOrders);
		
//		logControl.println(" >> �S���̓^�X�N�ɑ΂��Č��݂̓����̏d�݂̑g���킹�ŃL�[���[�h�v���O���~���O���s�������� >>");
//		
//		logControl.println("  BEST_R = " + KeywordProgramming.BEST_R);
//
//		logControl.println("");
//		logControl.println("");
//		logControl.println(testSites[0].getPackageName());
//		logControl.println(testSites[0].getClassName());
//		
//		logControl.println("  ���v�^�X�N�� = " + testSites.length);
//		
//		logControl.println("  ����(�~�����o��)�o���^�X�N�� = " + getScoreOfAnswerAppearancedTaskNumbar(answerOrders));
		
		List<Result> result_list = new ArrayList<Result>(); 
		
//		logControl.println("  > �����^�X�N�̏��� > �o�͌`���F [ID, �~�����o��, ����], (���ʂ�0�Ԃ��琔����. -1�͌��Q���ɏo�����Ȃ��������Ƃ�\��.)");
		for(int i = 0; i < testSites.length; i++){
//			logControl.println("   " + testSites[i].getId()+ ", "+ testSites[i].getSelectedString() + ", " + answerOrders.get(i));
			String str = testSites[i].getSelectedString();
			int odr = answerOrders.get(i);
//			logControl.println(str + "\t" + odr);//id�폜�B�^�u��؂�ɕύX�B
			int numKey = testSites[i].getNumOfKeywords();
			int numLT = testSites[i].getNumOfLocalTypes();
			int numLF = testSites[i].getNumOfLocalFunctions();
			//generics ��r��
			if(!str.contains("<"))
				result_list.add(new Result(testSites[i].getId(), str, odr, numKey, numLT, numLF));
		}
//		logControl.println("  < �����^�X�N�̏��� < �o�͌`���F [ID, �~�����o��, ����], (���ʂ�0�Ԃ��琔����. -1�͌��Q���ɏo�����Ȃ��������Ƃ�\��.)");
		
		int sum_zero = 0;
		int sum_m_one = 0;
		int sum_within_tree = 0; //���3��
		int sum_within_five = 0; //���5��
		int sum_within_ten = 0; //���10��
		int sum_others = 0;
		int sumKey = 0;
		int sumLT = 0;
		int sumLF = 0;
		
		List<Result> result_list_others = new ArrayList<Result>(); 
		
		List<Result> result_list_incorrect = new ArrayList<Result>();//�ԈႢ�̌��ʂ�ێ�����B
		
        for(Result result: result_list) {
			logControl.println(result.fSelectedString + "\t" + result.fAnswerOrder);
			sumKey += result.fNumOfKeywords; 
			sumLT += result.fNumOfLocalTypes;
			sumLF += result.fNumOfLocalFunctions;
			
			//���3���ȓ��ɐ������o������
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 3)
				sum_within_tree++;//���ʂ�0,1,2�̂Ƃ��B
			//���5���ȓ��ɐ������o������
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 5)
				sum_within_five++;
			//���10���ȓ��ɐ������o������
			if(result.fAnswerOrder != -1 && result.fAnswerOrder < 10)
				sum_within_ten++;
			
			if(result.fAnswerOrder == 0)
				sum_zero++;
			else if(result.fAnswerOrder == -1){
				sum_m_one++;
				result_list_incorrect.add(result);
			}else{
				sum_others++;
				result_list_others.add(result);
			}
					
        }
//		logControl.println("  > �\�[�g���ďd����������폜�����B >");

		logControl.println("BEST_R = " + KeywordProgramming.BEST_R);
		logControl.print("���݂̓����̏d�� = ");		
		for(int i = 0; i < 4; i++){
			logControl.print(String.valueOf(best_w[i]) + ", ");
		}
		logControl.println("");
		logControl.println("");
		
//		logControl.println(testSites[0].getPackageName());
//		logControl.println(testSites[0].getFullyQualifiedClassName());
		
		logControl.println("����\t" + result_list.size());
		logControl.println("�����o����\t\t" + (result_list.size() - sum_m_one));
		logControl.println("�o�����Ȃ�������\t\t\t" + sum_m_one);
		//logControl.println("2�Ԗڈȍ~�ɏo����\t\t\t\t\t" + sum_others);
		
		logControl.println("���1�Ԗڂɏo����\t\t\t\t" + sum_zero);
		logControl.println("���3�Ԗڈȓ��ɐ����o��\t\t\t\t\t" + sum_within_tree);
		logControl.println("���5�Ԗڈȓ��ɐ����o��\t\t\t\t\t\t" + sum_within_five);
		logControl.println("���10�Ԗڈȓ��ɐ����o��\t\t\t\t\t\t\t" + sum_within_ten);
		
//		logControl.println("  > 2�Ԗڈȍ~�ɏo������");
//		for(Result result: result_list_others){
//			logControl.println(result.fSelectedString + "\t" + result.fAnswerOrder);
//		}
//		logControl.println("  > 2�Ԗڈȍ~�ɏo������");

		logControl.println("���σL�[���[�h��\t" + ((double)sumKey/result_list.size()));
		logControl.println("���σ��[�J���^��\t" + ((double)sumLT/result_list.size()));
		logControl.println("���σ��[�J���֐���\t" + ((double)sumLF/result_list.size()));
		logControl.println("���L�[���[�h��\t" + (sumKey));
		logControl.println("�����[�J���^��\t" + (sumLT));
		logControl.println("�����[�J���֐���\t" + (sumLF));
		
		logControl.println("");
		logControl.println("�����̑g�ݍ��킹x�ȉ��őS�T��. x=\t" + KeywordProgramming.COMBINATION_SIZE);

		logControl.println("");
		
		long stop = System.currentTimeMillis();
		
		logControl.println(" �����̎��s�ɂ�����������= " + (stop-start) + " �~���b�BLocalSearch.run");
		
//		logControl.println("  > �o�����Ȃ���������");
//		for(Result result: result_list_incorrect){
//			logControl.println(result.fTestSiteId + "\t" + result.fSelectedString + "\t" + result.fAnswerOrder);
//		}
//		logControl.println("  > �o�����Ȃ���������");
		
//		logControl.println("  �t���X�R�A = " + max_score);
//		
//		logControl.print("  ���݂̓����̏d�� = ");		
//		for(int i = 0; i < 4; i++){
//			logControl.print(String.valueOf(best_w[i]) + ", ");
//		}
//		logControl.println("");
//
//		logControl.print(" �����̏d�݂̍X�V�� = ");
//		for(int i = 0; i < 4; i++){
//			logControl.print(String.valueOf(this.step_w[i]) + ", ");
//		}
//		logControl.println("");
//		
//		logControl.println(" << �S���̓^�X�N�ɑ΂��Č��݂̓����̏d�݂̑g���킹�ŃL�[���[�h�v���O���~���O���s�������� <<");
//
//		logControl.println("<<< ���[�J���T�[�`�O <<<");
//		logControl.println("");
//
//		/*
//		 * �����̐� FEATURE_NUM �񂾂�for�����[�v�����݂���.
//		 * ���ꂼ��̓����ɂ��āA�d�݂̕ω��̕��@�̐� 3�񂾂����[�v
//		 * 0���������Ȃ��A1�����₷�A2�����炷.
//		 */
//
//		double []current_w = Arrays.copyOf(start_w, start_w.length);//���݂̏d��
//		double []step_start_w = Arrays.copyOf(start_w, start_w.length);//�X�e�b�v�J�n���̏d��
//		double []prev_best_w = Arrays.copyOf(start_w, start_w.length);//1�O�̃x�X�g�ȏd��
//		
//		double init_score = max_score;//�����X�R�A
//		double current_score = max_score;//���݂̃X�R�A
//		
//		counter_step = 0;
//
//		//������������܂Ń��[�v
//		while(true){
//			//���[�U�[���������L�����Z������
//			if(monitor != null && monitor.isCanceled()){
//				logControl.close();
//				return;
//			}
//			//�w��񐔂Ŕ�����B
//			if(numOfSteps != -1 && counter_step >= numOfSteps)
//				break;
//			
//			if(monitor != null)
//				monitor.setTaskName("���[�J���T�[�` " + (counter_step) + " �X�e�b�v�ڎ��s��");
//			
//			counter_neighbour = 0;
//			
//			//�I�񂾌��̏��ʂ���ԍ����Ȃ�d�݂̑g�ݍ��킹��I��
//			findBestCombination(4, step_start_w, current_w);
//			
//			boolean flg_reflesh_current_point = false;
//			/*
//			 * ���݂̍ő�X�R�A�����A
//			 * �X�V�������ʂ̃X�R�A�̕����傫���ꍇ�A�X�V����B
//			 */
//			if(max_score > current_score){
//				flg_reflesh_current_point = true;
//				current_score = max_score;
//				current_w = Arrays.copyOf(best_w, best_w.length);
//				step_start_w = Arrays.copyOf(best_w, best_w.length);
//				prev_best_w = Arrays.copyOf(best_w, best_w.length);
//				//�d�݂��X�V
//				ExplanationVector.setWeights(this.best_w);
//			}else if(numOfSteps == -1){
//				//��������܂Ń��[�v�̂Ƃ��B
//				//���ʂ������Ȃ�΃��[�v�𔲂���
//				//min_order��best_w��1�O�̌��ʂɖ߂��Ă����B
//				max_score = current_score;
//				best_w = Arrays.copyOf(prev_best_w, prev_best_w.length);
//				ExplanationVector.setWeights(this.best_w);
//				break;
//			}
//			
//			//�t���O��true�̂Ƃ��A1�X�e�b�v���ƂɃ��O���o��
//			if(flg_log_step_by_step){
//				logControl.println("");
//				logControl.print("�e�X�e�b�v���̏��, �X�e�b�v " + counter_step);
//				logControl.print(", �x�X�g�ȏd�݂̑g = (");
//				for(int i = 0; i < 4; i++){
//					logControl.print(String.valueOf(best_w[i]) + ", ");
//				}
//				logControl.print("), �X�R�A = " + max_score);
//				if(flg_reflesh_current_point){
//					logControl.println(", �X�V����");
//				}else{
//					logControl.println(", �X�V�Ȃ�");
//				}
//				logControl.println("");
//			}
//			
//			counter_step++;
//		}
//		
//		long stop = System.currentTimeMillis();
//
//		//�\��
//		logControl.println("");
//		logControl.println(">>> ���[�J���T�[�`�� >>>");
//		
//		logControl.println(" ���v�^�X�N�� = " + testSites.length);
//		logControl.println(" ����(�~�����o��)�o���^�X�N�� = " + getScoreOfAnswerAppearancedTaskNumbar(answerOrders));
//		
//		logControl.println(" >> �����^�X�N�̏��� >> �o�͌`���F [ID, �~�����o��, ����], (���ʂ�0�Ԃ��琔����. -1�͌��Q���ɏo�����Ȃ��������Ƃ�\��.)");
//		for(int i = 0; i < testSites.length; i++){
//			logControl.println("  " + testSites[i].getId()+ ", "+ testSites[i].getSelectedString() + ", " + answerOrders.get(i));
//		}
//		logControl.println(" << �����^�X�N�̏��� << �o�͌`���F [ID, �~�����o��, ����], (���ʂ�0�Ԃ��琔����. -1�͌��Q���ɏo�����Ȃ��������Ƃ�\��.)");
//		
//		logControl.print(" �t���X�R�A = " + max_score);
//		if(max_score > init_score)
//			logControl.println(", �X�R�A�X�V����I");
//		else
//			logControl.println(", �X�R�A�X�V�Ȃ��B");
//
//		logControl.println(" ���s�ɂ�����������= " + (stop-start) + " �~���b�BLocalSearch.run");
//		
//		logControl.print(" ���݂̓����̏d�� = ");
//		for(int i = 0; i < 4; i++){
//			logControl.print(String.valueOf(this.best_w[i]) + ", ");
//		}
//		logControl.println("");
//		
//		logControl.println("<<< ���[�J���T�[�`�� <<<");
//		logControl.close();
//		
//		//�v���t�@�����X�Œ�߂��l�̃Z�b�g
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		store.setValue(PreferenceInitializer.INITIAL_WEIGHT_0, -this.best_w[0]);
//		store.setValue(PreferenceInitializer.INITIAL_WEIGHT_1, this.best_w[1]);
//		store.setValue(PreferenceInitializer.INITIAL_WEIGHT_2, -this.best_w[2]);
//		store.setValue(PreferenceInitializer.INITIAL_WEIGHT_3, this.best_w[3]);
//		//���������ӂ���B
//		ExplanationVector.setWeight(-store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0), 0);
//		ExplanationVector.setWeight(store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1), 1);
//		ExplanationVector.setWeight(-store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2), 2);
//		ExplanationVector.setWeight(store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3), 3);
	}


	/**
	 * ��ԃX�R�A�������Ȃ�悤�ȓ����̏d�݂̑g���킹��T�����\�b�h
	 * 
	 * 3�i���̂܂܁A�{step, -step��3�j��4��ʂ�=81��
	 * ���݂̓����̏d�݂̑g�ݍ��킹�̋ߖT�ɂ��āA
	 * �X�R�A�����߁A���̒��ōő�̃X�R�A�ƂȂ�g���킹�����߂�B
	 * 
	 * ���ׂĂ̏d�݂̑g�ݍ��킹�������Ă݂邽�߂ɁA
	 * ���̃��\�b�h�͍ċA�I�ɌĂ΂��
	 *  
	 * @param feature_num �����ԍ�
	 * @param start_w	�X�e�b�v�J�n���̏d�݂̑g���킹
	 * @param tmp_w		���݂̏d�݂̑g���킹
	 */
	private void findBestCombination(int feature_num, double[] start_w, double[] tmp_w) {

		//�v�Z
		if(feature_num == 0){

			//�d�݂��X�V
			ExplanationVector.setWeights(tmp_w);
			//�Čv�Z
			List<Integer> tmp_order_list = new ArrayList<Integer>();
			double tmp_score = reCalculateScore(testSites, tmp_w, tmp_order_list);
			
			boolean flg_reflesh_current_point = false;	//�X�V���N�������̃t���O
			/*
			 * ���݂̍ő�X�R�A�����A
			 * �X�V�������ʂ̃X�R�A�̕����傫���ꍇ�A�X�V����B
			 */
			if(tmp_score > max_score){
				flg_reflesh_current_point = true;
				max_score = tmp_score;
				//�z��̃R�s�[
				best_w = Arrays.copyOf(tmp_w, tmp_w.length);
				//���X�g�̃R�s�[
				answerOrders.clear();
				answerOrders.addAll(tmp_order_list);
			}
			//�t���O��true�̂Ƃ��A�e�ߖT�̃��O���o��
			if(flg_log_neighbours){
				logControl.print("�e�ߖT���̏��, �X�e�b�v " + counter_step + ", �ߖT " + counter_neighbour++);
				logControl.print(", �����̏d�� = (");
				for(int i = 0; i < 4; i++){
					logControl.print(String.valueOf(tmp_w[i]) + ", ");
				}
				logControl.print("), �X�R�A = " + tmp_score);
				if(flg_reflesh_current_point){
					logControl.println(", �X�V����");
				}else{
					logControl.println(", �X�V�Ȃ�");
				}
			}
			return;
		}

		//����
		feature_num--;

		//���̂܂�
		tmp_w[feature_num] = start_w[feature_num];
		findBestCombination(feature_num, start_w, tmp_w);

		//���̓��������̓�����
		boolean positive = ExplanationVector.isPositive(feature_num);
		
		//���₷
		tmp_w[feature_num] = start_w[feature_num] + step_w[feature_num];
		
		//�l�K�e�B�u�ȓ�����0�ȏ�ɂȂ邱�Ƃ͂Ȃ��B
		if(positive == false){
			if(tmp_w[feature_num] > 0)
			tmp_w[feature_num] = 0;
		}
			
		findBestCombination(feature_num, start_w, tmp_w);

		//���炷
		tmp_w[feature_num] = start_w[feature_num] - step_w[feature_num];
		
		//�|�W�e�B�u�ȓ�����0�ȉ��ɂȂ邱�Ƃ͂Ȃ��B
		if(positive == true){
			if(tmp_w[feature_num] < 0)
			tmp_w[feature_num] = 0;
		}
			
		findBestCombination(feature_num, start_w, tmp_w);

		return;
	}

	/*
	 * ���ׂĂ�TestSite�ɑ΂���KP���s���A
	 * �o�͌���List���擾����B
	 * 
	 * ������₪�o���������ʂ��ۑ�����B
	 * 
	 */
	public static void runKeywordProgrammingForAllTasks(IProgressMonitor monitor, TestSite[] testSites, List<Integer> answerOrders, String state){
		for(int i = 0; i < testSites.length; i++){
			//���[�U�[���������L�����Z������
			if(monitor != null && monitor.isCanceled()) {
			    return;
			}
			if(monitor != null)
				monitor.setTaskName("�^�X�NId= "+ testSites[i].getId() +"(" +(i+1)+ "/" + testSites.length + ") �ɂ��āA�L�[���[�h�v���O���~���O�̃A���S���Y���ɂ��o�͌��Q�𐶐���");
			testSites[i].initKeywordProgramming();
			testSites[i].runKeywordProgramming(state);
			answerOrders.add(testSites[i].getAnswerNumber(9999));
		}
	}
	

	/*
	 * ���ׂĂ�TestSite�ɑ΂���KP���s���A
	 * �o�͌���List���擾����B
	 * 
	 * ������₪�o���������ʂ��ۑ�����B
	 * 
	 * �L�[���[�h�͕p�x�̍���������w����I��œ��͂���B
	 */
	public void runKeywordProgrammingForAllTasksWithSelectedKeywords(IProgressMonitor monitor, TestSite[] testSites, List<Integer> answerOrders, String state){
		for(int i = 0; i < testSites.length; i++){
			//���[�U�[���������L�����Z������
			if(monitor != null && monitor.isCanceled()) {
			    return;
			}
			if(monitor != null)
				monitor.setTaskName("�^�X�NId= "+ testSites[i].getId() +"(" +(i+1)+ "/" + testSites.length + ") �ɂ��āA�L�[���[�h�v���O���~���O�̃A���S���Y���ɂ��o�͌��Q�𐶐���");
			testSites[i].initKeywordProgramming();
			
			//�L�[���[�h�����
			List<String> keywords = testSites[i].getKeywordList();
			
			//�L�[���[�h��p�x�̏���(�~��)�Ƀ\�[�g���A�w�萔�ȍ~�͍폜����B
			List<String> sorted_keywords = sortKeywordsByFrequency(keywords);
			
			//�ҏW�����L�[���[�h���Z�b�g����
			testSites[i].setKeywordList(sorted_keywords);
			
			testSites[i].runKeywordProgramming(state);
			answerOrders.add(testSites[i].getAnswerNumber(9999));
			
			//�ҏW�����L�[���[�h�����Ƃɖ߂��B
			testSites[i].resetKeyword();
			
		}
	}

	//numOfKeywords �L�[���[�h�̌�
	
	//�o���p�x�̍������́i�Ⴂ���́j���c���B
	private List<String> sortKeywordsByFrequency(List<String> keywords) {
		//treemap�́A�L�[���\�[�g�����B
		
		
		
		TreeMap<Integer, String> key_freq = new TreeMap();
		
		//ExmComparator �~���p�̃R���p���[�^�[
		if(this.flg_desc)
			key_freq = new TreeMap(new ExmComparator());
		
		for(String s: keywords){
			int index = term_list.indexOf(s);
					
			if(index != -1){
				//�݂�B
				key_freq.put(index, s);
			}else{
				//�����Ƃ��B
				key_freq.put(9999999, s);
			}
		}
		
		List<String> result_keywords = new ArrayList<String>();
		Iterator<Integer> it = key_freq.keySet().iterator();
		int count = 0;
		while (it.hasNext()) {
			
			if(count >= this.numOfKeywords)
				break;
			else
				count++;
			
			Integer key = it.next();
			result_keywords.add(key_freq.get(key));
//		    System.out.println(key + "/" + key_freq.get(key));
		}
		return result_keywords;		
	}
	
	/*
	 * �I�����̏��ʂ��擾����B
	 */
	public static void getSelectedStringOrders(TestSite[] testSites, List<Integer> answerOrders){
		for(int i = 0; i < testSites.length; i++){
			answerOrders.add(testSites[i].getSelectedStringOrder());
		}
	}
	
	/*
	 * �������o�������^�X�N�̐����X�R�A�Ƃ���
	 * �X�R�A��Ԃ��B
	 */
	public static int getScoreOfAnswerAppearancedTaskNumbar(List<Integer> order_list){
		int score = 0;
		for(Integer i: order_list){
			if(i != -1)
				score++;
		}
		return score;
	}
	
	
	/*
	 * �������o���������ʂ̋t�����e�^�X�N���v�������̂��X�R�A�Ƃ���
	 * �X�R�A��Ԃ��B
	 */
	public static double getScoreOfAnswerAppearancedOrder(List<Integer> order_list){
		double score = 0.0;
		for(Integer i: order_list){
			if(i != -1)	//�o�����Ȃ���΃X�R�A��0
				score += 1.0 / (i + 1);
		}
		return score;
	}
	
	/*
	 * 
	 * ��x�ڂɍs����KP�ɂ���Đ������ꂽ�o�͌��Q��p����
	 * �]���l�̍Čv�Z���s��
	 * 
	 * �t���X�R�A���o�͂���B
	 * 
	 * tmp_w �����̏d��
	 */
	public static double reCalculateScore(TestSite[] testSites, double[] tmp_w, List<Integer> tmp_order_list){
		
		for(int i = 0; i < testSites.length; i++){
			//�eTree�Ɋւ���tmp_w�̂Ƃ��̃X�R�A���v�Z�B
			testSites[i].reCalculateEvaluationValue();
			//�\�[�g����.
			testSites[i].sortFunctionTrees();
			//�������ʂ𓾂�B
//			answerOrders.add(testSites[i].getAnswerNumber(200));
			tmp_order_list.add(testSites[i].getAnswerNumber(200));
		}
		
		//�t���X�R�A���o�͂���B
		return getScoreOfAnswerAppearancedOrder(tmp_order_list);
	}
	
	//�~���ɕ��ׂ邽�߂̃R���p���[�^
	public class ExmComparator implements java.util.Comparator{
	    public int compare( Object object1, Object object2 ){
	        return ( (Comparable)object1 ).compareTo( object2 ) * -1;
	    }
	}
}
