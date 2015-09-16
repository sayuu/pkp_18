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
        				"�ȉ��͊֐���]������e�����̔ԍ�����ѐ����ł��B\n"+
						"1: �؂��\������m�[�h�̐��������قǃ}�C�i�X	\n"+
						"2: �L�[���[�h�Ɉ�v����֐��̃��x���̐��������قǃv���X	\n"+
						"3: �L�[���[�h�Ɉ�v���Ȃ��֐��̃��x���̐��������قǃ}�C�i�X	\n"+
						"4: �R���e�L�X�g�ɋ߂��֐��̐��������قǃv���X	\n"+
						"\n\n"+
						"�I�����C���w�K�Ƃ́A\n" +
						"�uCntl+Space�v�������ăc�[�����N�����A����I����������ɁA\n"+
						"���̗����ɉ����ĉߋ�����̗����𗘗p���čs����w�K�ł��B\n"+
						"\n\n"+
						"�o�b�`�w�K�Ƃ́A\n"+
						"�w�K�r���[�ɂ����đI�����ꂽ�^�X�N�ɑ΂��Ĉꊇ���čs����w�K�ł��B"
						);
        //text = new Text(c, SWT.SINGLE | SWT.BORDER);
        return c;
	}
}
