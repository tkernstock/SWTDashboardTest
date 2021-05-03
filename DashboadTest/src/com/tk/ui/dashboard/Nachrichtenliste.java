/*
 * Created on 04.07.2005
 *
 */
package com.tk.ui.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;

/**
 * @author ER
 *
 */
public class Nachrichtenliste extends Composite {
	
    public Nachrichtenliste(Composite container, FormToolkit toolkit, ApplicationWindow kisWindow, int style, NachrichtenWidget parent) {
        super(container, style);
        this.toolkit = toolkit;
        this.createContents(container);
    }

    private Table table;
    private TableViewer tableViewer;
    private final FormToolkit toolkit;
    private List<String> list=new ArrayList<>();
	private NachrichtenlisteModel model;


    protected Control createContents(Composite parent) {
        initGUI(parent);
        return parent;
    }

    protected void initGUI(Composite container) {
        setLayout(new GridLayout(1, false));
        this.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        table = toolkit.createTable(this, SWT.CHECK | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        tableViewer = new TableViewer(table);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_table.heightHint = 410;
        table.setLayoutData(gd_table);

        model = new NachrichtenlisteModel(tableViewer);
        tableViewer.setLabelProvider(model);
        tableViewer.setContentProvider(model);
 
    }

    /**
     *
     */
    public void initialize() {
        list.add("Nachricht 1");
        list.add("Nachricht 2");
        list.add("Nachricht 3");
        list.add("Nachricht 4");
        tableViewer.setInput(list);

    }

    public void toggleAll(boolean checked) {

        for(TableItem item : table.getItems()){
            item.setChecked(checked);
        }

        tableViewer.refresh();
    }

    public void setSelectedRead() {
    }

    public void deleteSelected() {
    }


}
