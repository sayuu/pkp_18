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
	    String s = "コンソール・ビューへのログ出力の設定\n"+
	    			"キーワードプログラミング、ローカルサーチ、グリッドサーチに関するログの細かい設定を行うことができます";
	    		
	    setDescription(s);
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.LOG_EXPORT_FLAG, "コンソールに出力されたのと同じログをテキストファイルにも出力する", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
