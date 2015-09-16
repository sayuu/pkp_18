package plugin.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class InitialWeightPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public InitialWeightPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("各特徴の重みの初期値の設定　（全て正の数で入力する)");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_0, "特徴1の初期値、-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_1, "特徴2の初期値、+", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_2, "特徴3の初期値、-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_3, "特徴4の初期値、+", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.INITIAL_WEIGHT_4, "特徴5の初期値、+", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.CONST_FREQ, "特徴5の定数、+", getFieldEditorParent()));
		
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
		
}
