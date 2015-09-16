package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class LogLocalSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public LogLocalSearchPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("���[�J���T�[�`�̃��O�o�͂̐ݒ�");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_LOCAL_SEARCH, "��{���", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_LOCAL_SEARCH_STEP_BY_STEP, "1�X�e�b�v���̏��", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_LOCAL_SEARCH_NEIGHBOURS, "�X�e�b�v�̊e�ߖT(80��)�̏��", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
