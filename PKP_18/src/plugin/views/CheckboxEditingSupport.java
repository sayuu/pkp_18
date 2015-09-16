package plugin.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

import plugin.testSite.TestSite;

public class CheckboxEditingSupport extends EditingSupport{

	private CellEditor editor;
	private ColumnViewer columnViewer;
	
	public CheckboxEditingSupport(ColumnViewer viewer) {
		super(viewer);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
		editor = new CheckboxCellEditor((Table)viewer.getControl());
		columnViewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		TestSite site = (TestSite)element;
		return site.isSelectedTask();
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		TestSite site = (TestSite)element;
		site.setSelectedTask((Boolean)value);
		columnViewer.refresh();
		
		//�t�@�C���ɕۑ�����
		site.save();
	}

}
