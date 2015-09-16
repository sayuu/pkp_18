package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class LogKpPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public LogKpPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("�L�[���[�h�v���O���~���O�̏o�͌��Q�����A���S���Y���Ɋւ��郍�O�o�͂̐ݒ�");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_BASIC, "��{���", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_FUNCTIONS, "���[�J���ɏo�������֐�", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_TYPES, "���[�J���ɏo�������^", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_RESULTS, "�������ꂽ�o�͌��Q", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
