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
	    setDescription("キーワードプログラミングの出力候補群生成アルゴリズムに関するログ出力の設定");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_BASIC, "基本情報", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_FUNCTIONS, "ローカルに出現した関数", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_TYPES, "ローカルに出現した型", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_KEYWORD_PROGRAMMING_RESULTS, "生成された出力候補群", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
