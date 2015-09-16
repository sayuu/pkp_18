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
	    
	    String s = "学習タスク一括追加機能の設定.\n"+
    			"学習タスクを一括して追加する際に、\n" +
    			"現在のキーワードプログラミングでは正解を出すことが不可能（困難）なタスクを\n" +
    			"除外して追加することができます.\n";
    		
	    setDescription(s);
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_LITERALS, "正解出力に定数が含まれているタスクを除外.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_ANONYMOUS_CLASS_DECLARATION, "匿名クラス定義を除外.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_CONTIGUOUS_UPPER_CASES, "正解出力の関数のラベル中に、連続して大文字が続くものが存在するタスクを除外.", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceInitializer.TASK_IMPORT_EXCLUDE_TYPE_DOWN_CAST, "ロケーションが型キャスト（ダウンキャスト）のタスクを除外.", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
