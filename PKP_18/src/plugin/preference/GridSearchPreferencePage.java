package plugin.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class GridSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public GridSearchPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    setDescription("Grid Searchに関する設定。 特徴2の値は固定し他の3つの特徴を動かす。（全て正の数で入力する)");
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub

		addField(new IntegerFieldEditor(PreferenceInitializer.GRID_BEST_R, "グリッドサーチのBEST_R", getFieldEditorParent()));

		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_WEIGHT_1_CONSTANT, "特徴2の値(固定値)、+", getFieldEditorParent()));

		addField(new IntegerFieldEditor(PreferenceInitializer.GRID_COUNT_FOR_KP, "KPを実行する間隔（指定回数に1回）", getFieldEditorParent()));

		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_START_WEIGHT_0, "特徴1の開始値、-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_START_WEIGHT_2, "特徴3の開始値、-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_START_WEIGHT_3, "特徴4の開始値、+", getFieldEditorParent()));
		
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_END_WEIGHT_0, "特徴1の終了値、-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_END_WEIGHT_2, "特徴3の終了値、-", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_END_WEIGHT_3, "特徴4の終了値、+", getFieldEditorParent()));
		
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_STEP_WIDTH_0, "特徴1の更新幅", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_STEP_WIDTH_2, "特徴3の更新幅", getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceInitializer.GRID_STEP_WIDTH_3, "特徴4の更新幅", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
