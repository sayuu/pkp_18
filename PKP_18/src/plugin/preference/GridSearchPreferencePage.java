package plugin.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class GridSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public GridSearchPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Grid Search�Ɋւ���ݒ�B ����2�̒l�͌Œ肵����3�̓����𓮂����B�i�S�Đ��̐��œ��͂���)");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub

		addField(new IntegerFieldEditor(PreferenceInitializer.GRID_BEST_R, "�O���b�h�T�[�`��BEST_R", getFieldEditorParent()));

		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_WEIGHT_1_CONSTANT, "����2�̒l(�Œ�l)�A+", getFieldEditorParent()));

		addField(new IntegerFieldEditor(PreferenceInitializer.GRID_COUNT_FOR_KP, "KP�����s����Ԋu�i�w��񐔂�1��j", getFieldEditorParent()));

		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_START_WEIGHT_0, "����1�̊J�n�l�A-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_START_WEIGHT_2, "����3�̊J�n�l�A-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_START_WEIGHT_3, "����4�̊J�n�l�A+", getFieldEditorParent()));
		
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_END_WEIGHT_0, "����1�̏I���l�A-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_END_WEIGHT_2, "����3�̏I���l�A-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_END_WEIGHT_3, "����4�̏I���l�A+", getFieldEditorParent()));
		
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_STEP_WIDTH_0, "����1�̍X�V��", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_STEP_WIDTH_2, "����3�̍X�V��", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_STEP_WIDTH_3, "����4�̍X�V��", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
