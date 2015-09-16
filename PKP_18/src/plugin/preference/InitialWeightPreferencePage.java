package plugin.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class InitialWeightPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public InitialWeightPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("�e�����̏d�݂̏����l�̐ݒ�@�i�S�Đ��̐��œ��͂���)");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_0, "����1�̏����l�A-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_1, "����2�̏����l�A+", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_2, "����3�̏����l�A-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_3, "����4�̏����l�A+", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_4, "����5�̏����l�A+", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.CONST_FREQ, "����5�̒萔�A+", getFieldEditorParent()));
		
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
		
}
