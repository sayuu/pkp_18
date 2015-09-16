package plugin.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

import plugin.testSite.TestSite;

public class KeywordCellEditingSupport extends EditingSupport{

	private CellEditor editor;
	private ColumnViewer columnViewer;
	
	public KeywordCellEditingSupport(ColumnViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
		viewer.getCellEditors();
		editor = new TextCellEditor((Table)viewer.getControl());
		columnViewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO Auto-generated method stub
		return editor;
	}

	/*
	 * セルが編集可能かどうか。
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
	 */
	@Override
	protected boolean canEdit(Object element) {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * キーワードを返す
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
	 */
	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		TestSite site = (TestSite)element;
		return site.getKeywords();
	}

	/*
	 * キーワードを設定する
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub
		TestSite site = (TestSite)element;
		site.setKeywords((String)value);
		columnViewer.refresh();
		
		//ファイルに保存する
		site.save();
	}

}
