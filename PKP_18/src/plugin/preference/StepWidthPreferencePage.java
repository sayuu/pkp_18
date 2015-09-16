package plugin.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class StepWidthPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public StepWidthPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("���[�J���T�[�`�i�R�o��@�j��1�X�e�b�v�s�����̏d�݂̍X�V���̐ݒ�");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_0, "����1�̍X�V��", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_1, "����2�̍X�V��", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_2, "����3�̍X�V��", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_3, "����4�̍X�V��", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
