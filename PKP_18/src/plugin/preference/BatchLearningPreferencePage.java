package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class BatchLearningPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public BatchLearningPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE, "��������܂Ŋw�K���s��", getFieldEditorParent()));
				
		addField(new IntegerFieldEditor(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS, "y�X�e�b�v�������[�J���T�[�`�i�R�o��@�j���s��", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
