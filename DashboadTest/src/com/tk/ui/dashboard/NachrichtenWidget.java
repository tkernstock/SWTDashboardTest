package com.tk.ui.dashboard;


import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.tk.ui.dashboard.WidgetFactory.DynamicWidgetClass;

public class NachrichtenWidget extends DashboardWidget {
	private Nachrichtenliste liste;
    private Button btnSelectAll;
    private final ApplicationWindow kisWindow;
    private static final int    HEIGHT_HEADER = 40,
                                MARGIN_TABLE = 10;

    public NachrichtenWidget(Composite parent, DynamicWidgetContainer container, FormToolkit toolkit, int style, ApplicationWindow kisWindow) {
        super(parent, container, toolkit, style | SWT.DOUBLE_BUFFERED);
        this.kisWindow = kisWindow;
        this.minimumSize = new Point(500,200);
        this.maximumSize = new Point(0,0);
        this.configurationPrefix = DynamicWidgetClass.NachrichtenWidget.toString();
        this.refreshable = true;
        initializeWidget();
    }
        
    protected void initializeWidget(){
        super.initializeWidget();
        setBackground(toolkit.getColors().getBackground());

        section.setText("Nachrichtenwidget"); //$NON-NLS-1$
        Composite client = toolkit.createComposite(section, SWT.NONE);
        client.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        client.setLayout(new GridLayout(1,false));
        client.setBackground(toolkit.getColors().getBackground());
        client.addListener(SWT.MouseEnter, childEnteredListener);
        toolkit.adapt(client);
        toolkit.paintBordersFor(client);
        
        Composite composite = toolkit.createComposite(client, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        toolkit.adapt(composite);
        toolkit.paintBordersFor(composite);
        
        btnSelectAll = toolkit.createButton(composite, "SelectAll", SWT.CHECK | SWT.TRANSPARENT); //$NON-NLS-1$
        btnSelectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                                     
            }
        });
        btnSelectAll.setBounds(10, 5, 106, 16);
        toolkit.adapt(btnSelectAll, true, true);
        
        Button btnMarkRead = toolkit.createButton(composite, "MarkRead", SWT.NONE); //$NON-NLS-1$
        btnMarkRead.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                setSelectedRead();
            }
        });
        btnMarkRead.setBounds(136, 1, 117, 25);
        toolkit.adapt(btnMarkRead, true, true);
        
        Button btnDelete = toolkit.createButton(composite,"Delete", SWT.NONE); //$NON-NLS-1$
        btnDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                deleteSelected();
            }
        });
        btnDelete.setBounds(275, 1, 117, 25);
        
        liste = new Nachrichtenliste(client, toolkit, kisWindow, SWT.NONE, this);
        liste.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        liste.addListener(SWT.MouseEnter, childEnteredListener);
        
        toolkit.paintBordersFor(liste);
        
        toolkit.adapt(liste);
        toolkit.paintBordersFor(liste);
        section.setClient(client);
        section.setExpanded(true);
    }
    
    public void initialize(){
        liste.initialize();
        setSize(getSize());
        liste.redraw();
    }
    
    private void setSelectedRead(){
        liste.setSelectedRead();
    }
    
    private void deleteSelected(){
        liste.deleteSelected();
    }
    
    @Override
    public void setSize(int x, int y){
        super.setSize(x, y);
    }
    
    public void setSize(Point point){
        super.setSize(point);       
        liste.setSize(new Point(point.x - MARGIN_TABLE*2, point.y - HEIGHT_HEADER - MARGIN_TABLE*2 - 10));
    }
    
    @Override
    public void refresh(){
        super.refresh();
        refreshCount = 0;
        liste.initialize();
    }
}
