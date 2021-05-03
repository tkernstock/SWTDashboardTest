package com.tk.ui.dashboard;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

public class NachrichtenlisteModel extends LabelProvider implements ITableLabelProvider, IColorProvider, IStructuredContentProvider{

    public final static int COL_BEMERKUNG=0;

    private final String[] header ={"Bemerkung"};
    private final int[] colW={350};

	private final static Color COLOR_OFFEN = SWTResourceManager.getColor(SWT.COLOR_BLACK);
	private final static Color COLOR_GELESEN = SWTResourceManager.getColor(SWT.COLOR_GRAY);
    
	TableViewer viewer;

    
	Color rowColour=null;

	public NachrichtenlisteModel(TableViewer tv){
		viewer = tv;
		for (int i = 0; i < getNoOfCols(); i++) {
			TableColumn tc = new TableColumn(viewer.getTable(), SWT.NULL);
			tc.setText(header[i]);
			tc.setWidth(colW[i]);
		}
		
	}
	
	public int getNoOfCols() {
		return header.length;
	}


	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		try{
			switch (columnIndex) {
			case COL_BEMERKUNG :
				return  element.toString();
			}
		} catch (NullPointerException e){}

		return ""; //$NON-NLS-1$
	}

	/*
	 *
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */

	@SuppressWarnings("rawtypes")
	public Object[] getElements(Object input) {
		return ((ArrayList)input).toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object arg1, Object arg2) {
		viewer=(TableViewer)viewer;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
     */
    public Color getForeground(Object element) {
       rowColour=COLOR_OFFEN;

        return rowColour;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
     */
    public Color getBackground(Object element) {
         return null;
         
    }
}
