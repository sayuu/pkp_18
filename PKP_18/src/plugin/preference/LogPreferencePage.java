package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class LogPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public LogPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    String s = "�R���\�[���E�r���[�ւ̃��O�o�͂̐ݒ�\n"+
	    			"�L�[���[�h�v���O���~���O�A���[�J���T�[�`�A�O���b�h�T�[�`�Ɋւ��郍�O�ׂ̍����ݒ���s�����Ƃ��ł��܂�";
	    		
	    setDescription(s);
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_EXPORT_FLAG, "�R���\�[���ɏo�͂��ꂽ�̂Ɠ������O���e�L�X�g�t�@�C���ɂ��o�͂���", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
