package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class LocalSearchBestRPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public LocalSearchBestRPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    String s = "���[�J���T�[�`��BEST_R�̐ݒ�\n"+
	    			"���[�J���T�[�`���s���O��1�x�����o�͌��Q�𐶐�����ۂ�BEST_R�̐ݒ�B\n"+
	    			"�S�ʂŎw�肷��BEST_R�Ƃ͕ʂɎw�肷��B\n"+
	    			"�����肪�N��Ȃ��悤�ɂȂ�ׂ��傫�Ȓl���w�肷��B";
	    setDescription(s);
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new IntegerFieldEditor(PreferenceInitializer.LOCAL_BEST_R, "���[�J���T�[�`��BEST_R�̒l", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
