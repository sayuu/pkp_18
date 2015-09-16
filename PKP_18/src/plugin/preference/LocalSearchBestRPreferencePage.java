package plugin.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class LocalSearchBestRPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public LocalSearchBestRPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	    String s = "ローカルサーチのBEST_Rの設定\n"+
	    			"ローカルサーチを行う前に1度だけ出力候補群を生成する際のBEST_Rの設定。\n"+
	    			"全般で指定するBEST_Rとは別に指定する。\n"+
	    			"足きりが起らないようになるべく大きな値を指定する。";
	    setDescription(s);
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		addField(new IntegerFieldEditor(PreferenceInitializer.LOCAL_BEST_R, "ローカルサーチのBEST_Rの値", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
}
