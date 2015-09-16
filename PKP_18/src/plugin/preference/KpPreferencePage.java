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

public class KpPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage  {

	public KpPreferencePage(){
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
        				"以下は関数を評価する各特徴の番号および説明です。\n"+
						"1: 木を構成するノードの数が多いほどマイナス	\n"+
						"2: キーワードに一致する関数のラベルの数が多いほどプラス	\n"+
						"3: キーワードに一致しない関数のラベルの数が多いほどマイナス	\n"+
						"4: コンテキストに近い関数の数が多いほどプラス	\n"+
						"\n\n"+
						"オンライン学習とは、\n" +
						"「Cntl+Space」を押してツールを起動し、候補を選択した直後に、\n"+
						"その履歴に加えて過去数回の履歴を利用して行われる学習です。\n"+
						"\n\n"+
						"バッチ学習とは、\n"+
						"学習ビューにおいて選択されたタスクに対して一括して行われる学習です。"
						);
        //text = new Text(c, SWT.SINGLE | SWT.BORDER);
        return c;
	}
}
