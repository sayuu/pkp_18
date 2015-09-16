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
		//addField(new BooleanFieldEditor(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS, "���̓L�[���[�h�̉��ρi�Z�k���j", getFieldEditorParent()));
		RadioGroupFieldEditor editor = new RadioGroupFieldEditor(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS,
				"���̓L�[���[�h�̉���",5,
                new String[][] {
                { "���ϖ���", PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_OFF},
                { "�ꉹ�폜", PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL},
                { "�擪�R����", PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3},
                { "�����_��1�����u��",  PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE},
                { "�����_��1�����}��",  PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD}
                },getFieldEditorParent());
		
		addField(editor);
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
