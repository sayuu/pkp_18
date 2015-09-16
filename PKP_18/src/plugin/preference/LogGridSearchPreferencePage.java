package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class LogGridSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public LogGridSearchPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("グリッドサーチのログ出力の設定");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_GRID_SEARCH, "基本情報", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
