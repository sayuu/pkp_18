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
	    setDescription("ローカルサーチのログ出力の設定");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_LOCAL_SEARCH, "基本情報", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_LOCAL_SEARCH_STEP_BY_STEP, "1ステップ毎の情報", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_LOCAL_SEARCH_NEIGHBOURS, "ステップの各近傍(80個)の情報", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
