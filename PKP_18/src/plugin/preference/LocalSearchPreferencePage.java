package plugin.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.activator.Activator;

public class LocalSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public LocalSearchPreferencePage(){
		super(GRID);
	    setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new GridLayout(1, true));
        new Label(c, SWT.NONE).setText(
        				"ローカルサーチに関する以下の設定ができます。\n"+
						"・各特徴の重みの更新幅の設定	\n"+
						"・オンライン学習に関する設定　\n"+
						"・バッチ学習に関する設定　\n"+
						"・BEST_Rの設定　\n"
						);
        //text = new Text(c, SWT.SINGLE | SWT.BORDER);
        return c;
	}
}
