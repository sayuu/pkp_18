package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class GeneralPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public GeneralPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		//addField(new BooleanFieldEditor(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS, "���̓L�[���[�h�̉��ρi�Z�k���j", getFieldEditorParent()));
	
		addField(new BooleanFieldEditor(PreferenceInitializer.SEPARATE_KEYWORDS, "�L�[���[�h����", getFieldEditorParent()));
		
		//addField(new BooleanFieldEditor(PreferenceInitializer.COMMON_SUBSEQUENCE, "�L�[���[�h������v", getFieldEditorParent()));
		RadioGroupFieldEditor editor1 = new RadioGroupFieldEditor(PreferenceInitializer.COMMON_SUBSEQUENCE,
				"�L�[���[�h������v�@�\",4,
                new String[][] {
                { "OFF", PreferenceInitializer.COMMON_SUBSEQUENCE_OFF},
                { "LCS1", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1},
                { "LCS2", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2},
                { "LCS3", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3},
                { "LCS4", PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4},
                { "LD",  PreferenceInitializer.COMMON_SUBSEQUENCE_LD}
                },getFieldEditorParent());
		
		addField(editor1);
		
		addField(new IntegerFieldEditor(PreferenceInitializer.LD_DELETE, "LD �폜�R�X�g", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceInitializer.LD_REPLACE, "LD �u���R�X�g", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceInitializer.LD_ADD, "LD �ǉ��R�X�g", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LD_CONST, "LD �萔C", getFieldEditorParent()));
		
		//addField(new BooleanFieldEditor(PreferenceInitializer.GROUPING_FUNCTIONS, "0�_�֐��̃O���[�s���O", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.INCLUDE_FREQUENCY, "�p�x���v�Z����B", getFieldEditorParent()));
		
//		addField(new BooleanFieldEditor(PreferenceInitializer.STATIC_LABEL_WITHOUT_CLASS_NAME, "static�֐��̓��x���ɃN���X���܂߂Ȃ����̂��ǉ�", getFieldEditorParent()));
//		addField(new BooleanFieldEditor(PreferenceInitializer.CONSTRUCTOR_LABEL_WITH_NEW, "�R���X�g���N�^�̓��x����new���܂߂�", getFieldEditorParent()));
		
		addField(new IntegerFieldEditor(PreferenceInitializer.BEST_R, "BEST_R�̒l�i���I�v��@�̕\�̊e��_�ɕێ�����؂̌��̍ő�l�j", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceInitializer.HEIGHT, "HEIGHT�̒l�i���I�v��@�̕\�̍����̍ő�l�j", getFieldEditorParent()));
		
//		RadioGroupFieldEditor editor1 = new RadioGroupFieldEditor(PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER,
//				"�֐��̈�������̒T�����@",3,
//                new String[][] {
//                { "��s�����Ɠ���(�×~�@)", PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_ORIGINAL},
//                { "(�����̑g�ݍ��킹����臒l�ȉ��̏ꍇ�Ɍ���)�S�T��", PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_EXHAUSTIVE},
//                { "�ŗǗD��T��(臒l�őł��؂�)",  PreferenceInitializer.SEARCH_MODE_FUNCTION_PARAMETER_BEST_FIRST}
//                },getFieldEditorParent());
//		
//		addField(editor1);
//
//		addField(new IntegerFieldEditor(PreferenceInitializer.COMBINATION_SIZE, "�S�T�����s���g�ݍ��킹�̍ő�l", getFieldEditorParent()));
//		addField(new IntegerFieldEditor(PreferenceInitializer.MAX_ARGUMETNT_SIZE, "�S�T�����s���ő������", getFieldEditorParent()));
//		
//		addField(new IntegerFieldEditor(PreferenceInitializer.BEST_FIRST_SIZE, "�ŗǗD��T���̂Ƃ��A�T�����s��", getFieldEditorParent()));
		
		
		
		RadioGroupFieldEditor editor11 = new RadioGroupFieldEditor(PreferenceInitializer.IMPORT_TYPES_FROM,
				"�����f�[�^�x�[�X(java.lang�p�b�P�[�W)�ȊO�̌^�Ɗ֐������C���|�[�g����ꏊ���w��.",3,
                new String[][] {
                { "���ݕҏW���̃N���X�̂�", PreferenceInitializer.IMPORT_TYPES_FROM_MY_CLASS},
                { "���ݕҏW���̃N���X����������p�b�P�[�W����", PreferenceInitializer.IMPORT_TYPES_FROM_MY_PACKAGE},
                { "���ݕҏW���̃N���X����������v���W�F�N�g�S�̂���",  PreferenceInitializer.IMPORT_TYPES_FROM_MY_PROJECT}
                },getFieldEditorParent());
		
		addField(editor11);
		
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
