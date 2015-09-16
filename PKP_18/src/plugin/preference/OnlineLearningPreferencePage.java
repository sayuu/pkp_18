package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class OnlineLearningPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public OnlineLearningPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub

		addField(new BooleanFieldEditor(PreferenceInitializer.LOCAL_ONLINE_FLAG, "オンライン学習を行う", getFieldEditorParent()));
				
		addField(new IntegerFieldEditor(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_PAST_LOG, "過去x回の履歴を使用して、", getFieldEditorParent()));
		
		addField(new IntegerFieldEditor(PreferenceInitializer.LOCAL_ONLINE_NUMBER_OF_STEPS, "yステップだけローカルサーチ（山登り法）を行う", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
