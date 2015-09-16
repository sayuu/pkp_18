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
		// TODO 自動生成されたコンストラクター・スタブ
		editor = new CheckboxCellEditor((Table)viewer.getControl());
		columnViewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO 自動生成されたメソッド・スタブ
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		// TODO 自動生成されたメソッド・スタブ
		TestSite site = (TestSite)element;
		return site.isSelectedTask();
	}

	@Override
	protected void setValue(Object element, Object value) {
		// TODO 自動生成されたメソッド・スタブ
		TestSite site = (TestSite)element;
		site.setSelectedTask((Boolean)value);
		columnViewer.refresh();
		
		//ファイルに保存する
		site.save();
	}

}
