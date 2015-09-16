package plugin.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import plugin.activator.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	//�L�[���[�h����ON
	public static String SEPARATE_KEYWORDS = "SEPARATE_KEYWORDS";
	
	//�L�[���[�h�Z�k��
	public static String SHORTENED_INPUT_KEYWORDS = "SHORTENED_INPUT_KEYWORDS";
	public static String SHORTENED_INPUT_KEYWORDS_OFF = "SHORTENED_INPUT_KEYWORDS_OFF";
	public static String SHORTENED_INPUT_KEYWORDS_BOIN_DEL = "SHORTENED_INPUT_KEYWORDS_BOIN_DEL";
	public static String SHORTENED_INPUT_KEYWORDS_FIRST_3 = "SHORTENED_INPUT_KEYWORDS_FIRST_3";
	public static String SHORTENED_INPUT_KEYWORDS_REPLACE = "SHORTENED_INPUT_KEYWORDS_REPLACE";
	public static String SHORTENED_INPUT_KEYWORDS_ADD = "SHORTENED_INPUT_KEYWORDS_ADD";
		
	//������v�ł�OK
	public static String COMMON_SUBSEQUENCE = "COMMON_SUBSEQUENCE";
	public static String COMMON_SUBSEQUENCE_OFF = "COMMON_SUBSEQUENCE_OFF";
	public static String COMMON_SUBSEQUENCE_LCS1 = "COMMON_SUBSEQUENCE_LCS1";
	public static String COMMON_SUBSEQUENCE_LCS2 = "COMMON_SUBSEQUENCE_LCS2";
	public static String COMMON_SUBSEQUENCE_LCS3 = "COMMON_SUBSEQUENCE_LCS3";
	public static String COMMON_SUBSEQUENCE_LCS4 = "COMMON_SUBSEQUENCE_LCS4";
	public static String COMMON_SUBSEQUENCE_LD = "COMMON_SUBSEQUENCE_LD";
	
	public static String LD_REPLACE = "LD_REPLACE";
	public static String LD_DELETE = "LD_DELETE";
	public static String LD_ADD = "LD_ADD";
	public static String LD_CONST = "LD_CONST";
	
	//�p�x���܂߂�
	public static String INCLUDE_FREQUENCY = "INCLUDE_FREQUENCY";
	
	//�L�[���[�h�ɂ�链�_���O�ŁA�����̌^���������֐��͂��ꂼ��P�ɂ܂Ƃ߂�
	public static String GROUPING_FUNCTIONS = "GROUPING_FUNCTIONS";
				
	// static�̊֐��̃��x���ɃN���X�����OVer��������
	public static String STATIC_LABEL_WITHOUT_CLASS_NAME = "STATIC_LABEL_WITHOUT_CLASS_NAME";
	
	// �R���X�g���N�^�ŁA���x���� new ���܂߂�B
	public static String CONSTRUCTOR_LABEL_WITH_NEW = "CONSTRUCTOR_LABEL_WITH_NEW";
		
	public static String BEST_R = "BEST_R";
	public static String HEIGHT = "HEIGHT";

	//����������@
	public static String SEARCH_MODE_FUNCTION_PARAMETER = "SEARCH_MODE_FUNCTION_PARAMETER";
	//�I���W�i��
	public static String SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL = "SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL";
	//�����̑g�ݍ��킹����臒l�ȉ��Ȃ�S�T��
	public static String SEARCH_MODE_FUNCTION_PARAMETER_EXHAUSTIVE = "SEARCH_MODE_FUNCTION_PARAMETER_EXHAUSTIVE";
	//�ŗǗD��T��
	public static String SEARCH_MODE_FUNCTION_PARAMETER_BEST_FIRST = "SEARCH_MODE_FUNCTION_PARAMETER_BEST_FIRST";
	
	//�S�T���̑g�ݍ��킹����臒l
	public static String COMBINATION_SIZE = "COMBINATION_SIZE";
	
	//�S�T��������ő�������B
	public static String MAX_ARGUMETNT_SIZE = "MAX_ARGUMETNT_SIZE";
		
	//�ŗǗD��T���̑ł��؂�臒l
	public static String BEST_FIRST_SIZE = "BEST_FIRST_SIZE";

	
	
	//����DB�ȊO�Ƀv���W�F�N�g���Ɋ܂܂��N���X���C���|�[�g���邩�ǂ���
	public static String IMPORT_TYPES_FROM = "IMPORT_TYPES_FROM";
		//�����̃N���X�̂݃C���|�[�g
	public static String IMPORT_TYPES_FROM_MY_CLASS = "IMPORT_TYPES_FROM_MY_CLASS";
		//�����̏�������p�b�P�[�W����C���|�[�g
	public static String IMPORT_TYPES_FROM_MY_PACKAGE = "IMPORT_TYPES_FROM_MY_PACKAGE";
		//�����̏�������v���W�F�N�g����C���|�[�g
	public static String IMPORT_TYPES_FROM_MY_PROJECT = "IMPORT_TYPES_FROM_MY_PROJECT";
	
	public static String INITIAL_WEIGHT_0 = "INITIAL_WEIGHT_0";
	public static String INITIAL_WEIGHT_1 = "INITIAL_WEIGHT_1";
	public static String INITIAL_WEIGHT_2 = "INITIAL_WEIGHT_2";
	public static String INITIAL_WEIGHT_3 = "INITIAL_WEIGHT_3";
	public static String INITIAL_WEIGHT_4 = "INITIAL_WEIGHT_4";
	
	public static String CONST_FREQ = "CONST_FREQ";
	
	//���[�J���T�[�`�֘A
	public static String LOCAL_BEST_R = "LOCAL_BEST_R";	//���[�J���T�[�`����BEST_R
	public static String LOCAL_STEP_WIDTH_0 = "LOCAL_STEP_WIDTH_0";
	public static String LOCAL_STEP_WIDTH_1 = "LOCAL_STEP_WIDTH_1";
	public static String LOCAL_STEP_WIDTH_2 = "LOCAL_STEP_WIDTH_2";
	public static String LOCAL_STEP_WIDTH_3 = "LOCAL_STEP_WIDTH_3";
		//���[�J���T�[�`�@�I�����C���w�K�֘A
	public static String LOCAL_ONLINE_FLAG = "LOCAL_ONLINE_FLAG";
	public static String LOCAL_ONLINE_NUMBER_OF_PAST_LOG = "LOCAL_ONLINE_NUMBER_OF_PAST_LOG";
	public static String LOCAL_ONLINE_NUMBER_OF_STEPS = "LOCAL_ONLINE_NUMBER_OF_STEPS";
		//���[�J���T�[�`�@�o�b�`�w�K�֘A
	public static String LOCAL_BATCH_FLAG_CONVERGENCE = "LOCAL_BATCH_FLAG_CONVERGENCE";	//��������܂Ŏ��s���邩
	public static String LOCAL_BATCH_NUMBER_OF_STEPS = "LOCAL_BATCH_NUMBER_OF_STEPS";

	//Log�o�͊֘A
	public static String LOG_KEYWORD_PROGRAMMING = "LOG_KEYWORD_PROGRAMMING";
	public static String LOG_KEYWORD_PROGRAMMING_BASIC = "LOG_KEYWORD_PROGRAMMING_BASIC";
	public static String LOG_KEYWORD_PROGRAMMING_TIME = "LOG_KEYWORD_PROGRAMMING_TIME";
	public static String LOG_KEYWORD_PROGRAMMING_FUNCTIONS = "LOG_KEYWORD_PROGRAMMING_FUNCTIONS";
	public static String LOG_KEYWORD_PROGRAMMING_TYPES = "LOG_KEYWORD_PROGRAMMING_TYPES";
	public static String LOG_KEYWORD_PROGRAMMING_RESULTS = "LOG_KEYWORD_PROGRAMMING_RESULTS";
	
	public static String LOG_LOCAL_SEARCH = "LOG_LOCAL_SEARCH";
	public static String LOG_LOCAL_SEARCH_STEP_BY_STEP = "LOG_LOCAL_SEARCH_STEP_BY_STEP";
	public static String LOG_LOCAL_SEARCH_NEIGHBOURS = "LOG_LOCAL_SEARCH_NEIGHBOURS";
	
	public static String LOG_GRID_SEARCH = "LOG_GRID_SEARCH";
	
	public static String LOG_EXPORT_FLAG = "LOG_EXPORT_FLAG";
	public static String LOG_EXPORT_FILE = "LOG_EXPORT_FILE";
	public static final String LogFileFolder = "./KeywordProgramming/logFile/";

	//�������ʃt�H���_
	public static final String extFileFolder = "./KeywordProgramming/extFile/";

	//�O���b�h�T�[�`�֘A
	public static String GRID_BEST_R = "GRID_BEST_R";	//�O���b�h�T�[�`����BEST_R

	public static String GRID_COUNT_FOR_KP = "GRID_COUNT_FOR_KP";
		//����1�̂݌Œ�l�B�w��ł���B
	public static String GRID_WEIGHT_1_CONSTANT = "GRID_WEIGHT_1_CONSTANT";

	public static String GRID_START_WEIGHT_0 = "GRID_START_WEIGHT_0";
	public static String GRID_START_WEIGHT_1 = "GRID_START_WEIGHT_1";
	public static String GRID_START_WEIGHT_2 = "GRID_START_WEIGHT_2";
	public static String GRID_START_WEIGHT_3 = "GRID_START_WEIGHT_3";
	
	public static String GRID_END_WEIGHT_0 = "GRID_END_WEIGHT_0";
	public static String GRID_END_WEIGHT_1 = "GRID_END_WEIGHT_1";
	public static String GRID_END_WEIGHT_2 = "GRID_END_WEIGHT_2";
	public static String GRID_END_WEIGHT_3 = "GRID_END_WEIGHT_3";
	
	public static String GRID_STEP_WIDTH_0 = "GRID_STEP_WIDTH_0";
	public static String GRID_STEP_WIDTH_1 = "GRID_STEP_WIDTH_1";
	public static String GRID_STEP_WIDTH_2 = "GRID_STEP_WIDTH_2";
	public static String GRID_STEP_WIDTH_3 = "GRID_STEP_WIDTH_3";
	
	//�^�X�N�ǉ��֘A
	//���݂̃L�[���[�h�v���O���~���O�ł͐������o�����Ƃ��ł��Ȃ��^�X�N��r�����Ĉꊇ�ǉ�����B
	
	//�����N���X��` �r�� AnonymousClassDeclaration
	public static String TASK_IMPORT_EXCLUDE_ANONYMOUS_CLASS_DECLARATION = "EXCLUDE_ANONYMOUS_CLASS_DECLARATION";
	//�o�͊֐��؂̈����ɒ萔���܂܂�Ă���@LiteralsInArguments
	public static String TASK_IMPORT_EXCLUDE_LITERALS = "TASK_IMPORT_EXCLUDE_LITERALS";
	//�֐��̃��x���ɂ��āA�A�����đ啶�����������̂����݂���Ƃ��BUpperCaseContiguous
	public static String TASK_IMPORT_EXCLUDE_CONTIGUOUS_UPPER_CASES = "TASK_IMPORT_EXCLUDE_CONTIGUOUS_UPPER_CASES";
	//���P�[�V�������^�L���X�g�i�_�E���L���X�g�j�̃^�X�N�����O.
	public static String TASK_IMPORT_EXCLUDE_TYPE_DOWN_CAST = "TASK_IMPORT_EXCLUDE_TYPE_DOWN_CAST";
	
	@Override
	public void initializeDefaultPreferences() {
		// TODO Auto-generated method stub
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		store.setDefault(SEPARATE_KEYWORDS, false);
		store.setDefault(SHORTENED_INPUT_KEYWORDS, SHORTENED_INPUT_KEYWORDS_OFF);
		
		//store.setDefault(COMMON_SUBSEQUENCE, false);
		store.setDefault(COMMON_SUBSEQUENCE, COMMON_SUBSEQUENCE_OFF);
		
		store.setDefault(LD_DELETE, 1);
		store.setDefault(LD_REPLACE, 1);
		store.setDefault(LD_ADD, 1);
		store.setDefault(LD_CONST, 1.5);
		
		store.setDefault(INCLUDE_FREQUENCY, false);
		store.setDefault(GROUPING_FUNCTIONS, false);
		
		
		store.setDefault(STATIC_LABEL_WITHOUT_CLASS_NAME, true);
		store.setDefault(CONSTRUCTOR_LABEL_WITH_NEW, false);
		
		store.setDefault(BEST_R, 50);
		store.setDefault(HEIGHT, 3);
		store.setDefault(COMBINATION_SIZE, 5);
		store.setDefault(MAX_ARGUMETNT_SIZE, 5);
		store.setDefault(BEST_FIRST_SIZE, 5);
		
		store.setDefault(SEARCH_MODE_FUNCTION_PARAMETER, SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL);
		
		store.setDefault(IMPORT_TYPES_FROM, IMPORT_TYPES_FROM_MY_CLASS);
//		store.setDefault(IMPORT_TYPES_FROM_MY_CLASS, );
//		store.setDefault(IMPORT_TYPES_FROM_MY_PACKAGE, false);
//		store.setDefault(IMPORT_TYPES_FROM_MY_PROJECT, false);

		//"�i���͗�F\"-0.05, 1, -0.01, 0.001\"�j\n"+
		
		store.setDefault(INITIAL_WEIGHT_0, 0.05);
		store.setDefault(INITIAL_WEIGHT_1, 1.0);
		store.setDefault(INITIAL_WEIGHT_2, 0.01);
		store.setDefault(INITIAL_WEIGHT_3, 0.001);
		store.setDefault(INITIAL_WEIGHT_4, 0.001);
		store.setDefault(CONST_FREQ, 0.001);
		
		//"�i���͗�F\"0.04, 0.2, 0.02, 0.02\"�j\n"+

		store.setDefault(LOCAL_BEST_R, 10000);

		store.setDefault(LOCAL_STEP_WIDTH_0, 0.04);
		store.setDefault(LOCAL_STEP_WIDTH_1, 0.2);
		store.setDefault(LOCAL_STEP_WIDTH_2, 0.02);
		store.setDefault(LOCAL_STEP_WIDTH_3, 0.02);
		
		store.setDefault(LOCAL_ONLINE_FLAG, true);
		store.setDefault(LOCAL_ONLINE_NUMBER_OF_PAST_LOG, 3);
		store.setDefault(LOCAL_ONLINE_NUMBER_OF_STEPS, 1);
		
		store.setDefault(LOCAL_BATCH_FLAG_CONVERGENCE, false);
		store.setDefault(LOCAL_BATCH_NUMBER_OF_STEPS, 10);
		
		store.setDefault(LOG_KEYWORD_PROGRAMMING, false);
		store.setDefault(LOG_KEYWORD_PROGRAMMING_BASIC, true);
		store.setDefault(LOG_KEYWORD_PROGRAMMING_TIME, false);
		store.setDefault(LOG_KEYWORD_PROGRAMMING_FUNCTIONS, false);
		store.setDefault(LOG_KEYWORD_PROGRAMMING_RESULTS, false);
		store.setDefault(LOG_KEYWORD_PROGRAMMING_TYPES, false);
		
		store.setDefault(LOG_LOCAL_SEARCH, true);
		store.setDefault(LOG_LOCAL_SEARCH_STEP_BY_STEP, false);
		store.setDefault(LOG_LOCAL_SEARCH_NEIGHBOURS, false);
		
		store.setDefault(LOG_GRID_SEARCH, true);
		
		store.setDefault(LOG_EXPORT_FLAG, false);
		store.setDefault(LOG_EXPORT_FILE, LogFileFolder);
		
		store.setDefault(GRID_BEST_R, 10000);

		store.setDefault(GRID_WEIGHT_1_CONSTANT, 1.0);
		
		store.setDefault(GRID_START_WEIGHT_0, 5.0);
		store.setDefault(GRID_START_WEIGHT_1, 1.0);
		store.setDefault(GRID_START_WEIGHT_2, 5.0);
		store.setDefault(GRID_START_WEIGHT_3, 0.0);
		
		store.setDefault(GRID_END_WEIGHT_0, 0.0);
		store.setDefault(GRID_END_WEIGHT_1, 1.0);
		store.setDefault(GRID_END_WEIGHT_2, 0.0);
		store.setDefault(GRID_END_WEIGHT_3, 0.5);
		
		//"�i���͗�F\"0.04, 0.2, 0.02, 0.02\"�j\n"+
		
		store.setDefault(GRID_STEP_WIDTH_0, 0.1);
		store.setDefault(GRID_STEP_WIDTH_1, 0.1);
		store.setDefault(GRID_STEP_WIDTH_2, 0.1);
		store.setDefault(GRID_STEP_WIDTH_3, 0.1);
		
		store.setDefault(GRID_COUNT_FOR_KP, 100);
		
		store.setDefault(TASK_IMPORT_EXCLUDE_ANONYMOUS_CLASS_DECLARATION, true);
		store.setDefault(TASK_IMPORT_EXCLUDE_CONTIGUOUS_UPPER_CASES, true);
		store.setDefault(TASK_IMPORT_EXCLUDE_LITERALS, true);
		store.setDefault(TASK_IMPORT_EXCLUDE_TYPE_DOWN_CAST, true);

	}

}
