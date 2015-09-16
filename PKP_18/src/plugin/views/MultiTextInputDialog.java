package plugin.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MultiTextInputDialog extends Dialog {
	// ボタンの上に表示するテキストエリア
	private Text text;
	// textに入力された文字が反映されるラベル
	private Label resultContainer;
	// clearボタンのID
	private static final int ERASE_ALL_ID = -1;
	
	private String title;
	
	public MultiTextInputDialog(
			Shell parentShell, String title, Label resultContainer) {
		super(parentShell);
		this.resultContainer = resultContainer;
		this.title = title;
	}
	
	// ダイアログのトップレベル作成
	protected Control createContents(Composite parent) {
		// createDialogAreaメソッドとcreateButtonBarメソッドの呼び出し
		Control control = super.createContents(parent);
		// ウィンドウのタイトル設定
		control.getShell().setText(title);
		
		return control;
	}
	
	// ボタンの上のエリアの作成
	protected Control createDialogArea(Composite parent) {
		text = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER);
		GridData data = new GridData(
				GridData.VERTICAL_ALIGN_FILL | 
				GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = 300;
		data.heightHint = 200;
		text.setLayoutData(data);
		
		return parent;
	}
	
	// ボタンの作成
	protected void createButtonsForButtonBar(Composite parent) {
		// clearボタンの作成
		createButton(parent, ERASE_ALL_ID, "clear", false);
		// OKとCancelボタンの作成
		super.createButtonsForButtonBar(parent);
		
	}
	
	// ボタンが押された際のイベントについてオーバーライド
	protected void buttonPressed(int buttonId) {
		if (ERASE_ALL_ID == buttonId) {
			// cancelボタンが押された場合、textのテキストを削除
			text.setText("");
		} else {
			if (IDialogConstants.OK_ID == buttonId) {
				// OKボタンが押された際、ラベルにテキストを代入
				resultContainer.setText(text.getText());
			}
			// デフォルトのOKもしくはCancelの動作
			super.buttonPressed(buttonId);
		}
	}
}