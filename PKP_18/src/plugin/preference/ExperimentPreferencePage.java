package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class ExperimentPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public ExperimentPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		//addField(new BooleanFieldEditor(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS, "入力キーワードの改変（短縮化）", getFieldEditorParent()));
		RadioGroupFieldEditor editor = new RadioGroupFieldEditor(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS,
				"入力キーワードの改変",5,
                new String[][] {
                { "改変無し", PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_OFF},
                { "母音削除", PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL},
                { "先頭３文字", PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3},
                { "ランダム1文字置換",  PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE},
                { "ランダム1文字挿入",  PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD}
                },getFieldEditorParent());
		
		addField(editor);
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
