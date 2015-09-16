package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class TaskImportPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public TaskImportPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    
	    String s = "�w�K�^�X�N�ꊇ�ǉ��@�\�̐ݒ�.\n"+
    			"�w�K�^�X�N���ꊇ���Ēǉ�����ۂɁA\n" +
    			"���݂̃L�[���[�h�v���O���~���O�ł͐������o�����Ƃ��s�\�i����j�ȃ^�X�N��\n" +
    			"���O���Ēǉ����邱�Ƃ��ł��܂�.\n";
    		
	    setDescription(s);
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_LITERALS, "�����o�͂ɒ萔���܂܂�Ă���^�X�N�����O.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_ANONYMOUS_CLASS_DECLARATION, "�����N���X��`�����O.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_CONTIGUOUS_UPPER_CASES, "�����o�͂̊֐��̃��x�����ɁA�A�����đ啶�����������̂����݂���^�X�N�����O.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_TYPE_DOWN_CAST, "���P�[�V�������^�L���X�g�i�_�E���L���X�g�j�̃^�X�N�����O.", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
