package plugin.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class StepWidthPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public StepWidthPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("ローカルサーチ（山登り法）を1ステップ行う時の重みの更新幅の設定");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_0, "特徴1の更新幅", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_1, "特徴2の更新幅", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_2, "特徴3の更新幅", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.LOCAL_STEP_WIDTH_3, "特徴4の更新幅", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
