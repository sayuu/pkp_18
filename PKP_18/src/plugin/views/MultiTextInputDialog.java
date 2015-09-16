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
	// �{�^���̏�ɕ\������e�L�X�g�G���A
	private Text text;
	// text�ɓ��͂��ꂽ���������f����郉�x��
	private Label resultContainer;
	// clear�{�^����ID
	private static final int ERASE_ALL_ID = -1;
	
	private String title;
	
	public MultiTextInputDialog(
			Shell parentShell, String title, Label resultContainer) {
		super(parentShell);
		this.resultContainer = resultContainer;
		this.title = title;
	}
	
	// �_�C�A���O�̃g�b�v���x���쐬
	protected Control createContents(Composite parent) {
		// createDialogArea���\�b�h��createButtonBar���\�b�h�̌Ăяo��
		Control control = super.createContents(parent);
		// �E�B���h�E�̃^�C�g���ݒ�
		control.getShell().setText(title);
		
		return control;
	}
	
	// �{�^���̏�̃G���A�̍쐬
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
	
	// �{�^���̍쐬
	protected void createButtonsForButtonBar(Composite parent) {
		// clear�{�^���̍쐬
		createButton(parent, ERASE_ALL_ID, "clear", false);
		// OK��Cancel�{�^���̍쐬
		super.createButtonsForButtonBar(parent);
		
	}
	
	// �{�^���������ꂽ�ۂ̃C�x���g�ɂ��ăI�[�o�[���C�h
	protected void buttonPressed(int buttonId) {
		if (ERASE_ALL_ID == buttonId) {
			// cancel�{�^���������ꂽ�ꍇ�Atext�̃e�L�X�g���폜
			text.setText("");
		} else {
			if (IDialogConstants.OK_ID == buttonId) {
				// OK�{�^���������ꂽ�ہA���x���Ƀe�L�X�g����
				resultContainer.setText(text.getText());
			}
			// �f�t�H���g��OK��������Cancel�̓���
			super.buttonPressed(buttonId);
		}
	}
}