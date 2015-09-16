package plugin.views;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import plugin.testSite.TestSite;

public class OffsetLabelProvider extends CellLabelProvider{

	@Override
	public void update(ViewerCell cell) {
		// TODO Auto-generated method stub
		TestSite site = (TestSite)cell.getElement();
		cell.setText(String.valueOf(site.getOffset()));
	}

}
