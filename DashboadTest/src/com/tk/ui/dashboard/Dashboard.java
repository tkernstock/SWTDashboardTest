package com.tk.ui.dashboard;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.internal.win32.WINDOWPOS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import com.tk.ui.dashboard.WidgetFactory.DynamicWidgetClass;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class Dashboard extends Composite implements DynamicWidgetContainer {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final FormToolkit widgetToolkit = new FormToolkit(Display.getCurrent());
	private ArrayList<DynamicWidget> widgets = new ArrayList<DynamicWidget>();
	private DynamicWidget activeElement;
	private ApplicationWindow kisWindow;
	private Menu contextMenu;
	private MenuItem miNachrichtenWidget;
	
	private Point offset, originalPosition, originalSize;
	private final static int 	COLUMN_WIDTH = 300,
								ROW_HEIGHT = 35,
								MARGIN_HORIZONTAL = 10,
								MARGIN_VERTICAL = 10;
	private boolean allowHResize, allowVResize;


	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Dashboard(Composite parent, ApplicationWindow kisWindow, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		this.kisWindow = kisWindow;
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		this.setLayout(null);
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
        setBackground(SWTResourceManager.getColor(1, 73, 132));
 
		widgetToolkit.setBackground(SWTResourceManager.getColor(255, 255, 255));
		
	}

	private void createMenu() {
		
 		contextMenu = new Menu(this);
		miNachrichtenWidget = new MenuItem(contextMenu, SWT.NONE);
		miNachrichtenWidget.setText("Dashboad-NachrichtenWidget");
		miNachrichtenWidget.setEnabled(false);
		miNachrichtenWidget.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addWidget(DynamicWidgetClass.NachrichtenWidget);
			}			
		});
		setMenu(contextMenu);
	}

	private void addWidget(DynamicWidgetClass widgetName) {		
		
		DynamicWidget w = WidgetFactory.createWidget(widgetName, this, this, widgetToolkit, kisWindow);
		Point pos = Display.getCurrent().getCursorLocation();
		pos.x -= getLocation().x;
		pos.y -= getLocation().y;
		widgets.add(w);
		w.setLocation(pos);
		w.initialize();
	    reorderWidgets(w, new Rectangle(0, 0, w.getSize().x, w.getSize().y));
//		createMenu();
//		for(DynamicWidget dw : widgets){
//			reorderWidgets(dw, dw.getBounds());
//		}
	}

	public void initializeWidgets() {
		readWidgetConfiguration();
		for(DynamicWidget w : widgets){
			w.initialize();
//		    reorderWidgets(w, w.getBounds());
		}
		createMenu();
	}

	@Override
	public void dragStarted(DynamicWidget element, Point point) {
		activeElement = element;
		offset = point;
		originalPosition = element.getLocation();
		this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZEALL));
	}

	@Override
	public void elementMoved(Point point) {
		int x = activeElement.getLocation().x + (point.x - offset.x);
		int y = activeElement.getLocation().y + (point.y - offset.y);
//		activeElement.setLocation(normalizeLocation(activeElement, x, y));
		activeElement.setLocation(new Point(x, y));
		
	}
	
	@Override
	public void dragEnded() {
		if(!originalPosition.equals(activeElement.getLocation())){
			activeElement.setLocation(normalizeLocation(activeElement, activeElement.getLocation().x, activeElement.getLocation().y));
			reorderWidgets(activeElement, new Rectangle(originalPosition.x, originalPosition.y, activeElement.getSize().x, activeElement.getSize().y));
		}
		activeElement = null;
		this.setCursor(new Cursor(getDisplay(),SWT.CURSOR_ARROW));
		updateConfiguration();
	}

	@Override
	public void resizeStarted(DynamicWidget element, Point point, boolean horizontal, boolean vertical) {
		originalSize = element.getSize();
		allowHResize = horizontal;
		allowVResize = vertical;
		if(horizontal && vertical){
			setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZENW));
		}else if(horizontal){
			setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZEWE));
		}else{
			setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZENS));
		}
		activeElement = element;
		offset = point;
	}

	@Override
	public void elementResized(Point point) {
		Point oldSize = activeElement.getSize();
		int sizeX, sizeY;
		if(allowHResize){
			sizeX = point.x;
		}else{
			sizeX = oldSize.x;
		}
		if(allowVResize){
			sizeY = point.y;
		}else{
			sizeY = oldSize.y;
		}
		activeElement.setSize(new Point(sizeX, sizeY));
		
		if(!oldSize.equals(activeElement.getSize())){
			reorderWidgets(activeElement, new Rectangle(activeElement.getLocation().x, activeElement.getLocation().y, oldSize.x, oldSize.y));
		}
	}

	@Override
	public void resizeEnded() {
		boolean roundUpWidth = activeElement.getSize().x > originalSize.x;
		boolean roundUpHeight = activeElement.getSize().y > originalSize.y;
		
		activeElement.setSize(normalizeSize(activeElement, activeElement.getSize().x, activeElement.getSize().y, roundUpWidth, roundUpHeight)); 
		if(roundUpWidth || roundUpHeight)
			activeElement.refresh();
		activeElement = null;
		this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
		updateConfiguration();
	}
	
	public void reorderWidgets(DynamicWidget active, Rectangle oldBounds){
		Rectangle newBounds = active.getBounds();
		for(DynamicWidget widget : widgets){
			if(widget.equals(active))
				continue;
			
			int wx = widget.getLocation().x;		
			int wy = widget.getLocation().y;
			int wwidth = widget.getSize().x;
			int wheight = widget.getSize().y;
					
			boolean horizontalOverlap = false;
			boolean verticalOverlap = false;
			if(newBounds.x >= wx && newBounds.x <= wx + wwidth)
				horizontalOverlap = true;
			if(wx >= newBounds.x && wx <= newBounds.x + newBounds.width)
				horizontalOverlap = true;
			if(newBounds.y >= wy && newBounds.y <= wy + wheight)
				verticalOverlap = true;
			if(wy >= newBounds.y && wy <= newBounds.y + newBounds.height)
				verticalOverlap = true;
			
			if(horizontalOverlap && verticalOverlap){
				widget.setLocation(new Point(widget.getLocation().x, newBounds.y + newBounds.height + ROW_HEIGHT));
				reorderWidgets(widget, new Rectangle(wx, wy, wwidth, wheight));
			}
			
			if(!sendToGrid(widget)){
				if(widget.getLocation().y - newBounds.height - 3*(ROW_HEIGHT -1) > 0){
					active.setLocation(new Point(newBounds.x, widget.getLocation().y - newBounds.height - 3*(ROW_HEIGHT +1)));
					sendToGrid(active);
				}else{
					active.setLocation(new Point(oldBounds.x, oldBounds.y));
					active.setSize(new Point(oldBounds.width, oldBounds.height));
					sendToGrid(active);
					widget.setLocation(new Point(wx, wy));
					widget.setSize(new Point(wwidth, wheight));
					sendToGrid(widget);
				}
			}
		}
	}
	
	private boolean sendToGrid(DynamicWidget widget){
		Rectangle b = widget.getBounds();		
		Point loc = normalizeLocation(widget, b.x, b.y);
		if(loc.x != widget.getLocation().x || loc.y != widget.getLocation().y){
			widget.setLocation(loc);
		}
		Point size = normalizeSize(widget, b.width, b.height, false, false);
		if(size.x != widget.getSize().x || size.y != widget.getSize().y){
			widget.setSize(size);	
		}		
		
		if(widget.getLocation().y + (ROW_HEIGHT-1) < b.y  || widget.getLocation().x + (COLUMN_WIDTH -1) < b.x)
			return false;
		
//		if(widget.getLocation().y + widget.getSize().y > this.getSize().y || widget.getLocation().x + widget.getSize().x > this.getSize().x)
//			return false;
		
		return true;
	}
	
	private Point normalizeLocation(DynamicWidget widget, int x, int y){
		x = Math.max(0, Math.min(x, getSize().x - widget.getSize().x));
		x = (x / COLUMN_WIDTH) * COLUMN_WIDTH;

		y = Math.max(0, Math.min(y, getSize().y - widget.getSize().y));
		y = (y / ROW_HEIGHT) * ROW_HEIGHT;
		return new Point(x,y);
	}
	
	private Point normalizeSize(DynamicWidget widget, int x, int y, boolean roundUpWidth, boolean roundUpHeight){
		Point minSize = widget.getMinimumSize();
		Point maxSize = widget.getMaximumSize();
		x = (((x + MARGIN_HORIZONTAL) / COLUMN_WIDTH) + (roundUpWidth ? 1 : 0)) * COLUMN_WIDTH - MARGIN_HORIZONTAL;
		y = (((y + MARGIN_HORIZONTAL) / ROW_HEIGHT) + (roundUpHeight ? 1 : 0)) * ROW_HEIGHT - MARGIN_VERTICAL;
		x = Math.max(minSize.x, maxSize.x == 0 ? x : Math.min(x, maxSize.x));
		x = Math.min(getClientArea().width - widget.getLocation().x, x);
		y = Math.max(minSize.y, maxSize.y == 0 ? y :  Math.min(y, maxSize.y));
		y = Math.min(getClientArea().height - widget.getLocation().y, y);
		return new Point(x, y);
	}
	
	public void updateConfiguration() {
	    try {
	        Properties props = new Properties();
	        for(DynamicWidget widget : widgets){
	        	props = widget.storeConfiguration(props);
	        }

	    }
	    catch (Exception e ) {
	        e.printStackTrace();
	    }
	}
	
	private void readWidgetConfiguration() {
   
       widgets.add(WidgetFactory.createWidget(DynamicWidgetClass.NachrichtenWidget, this, this, widgetToolkit, kisWindow));
   
	    
	    for(DynamicWidget w : widgets){
	
	    	sendToGrid(w);
	//	    reorderWidgets(w, w.getBounds());
	    }

	}
	
	@Override
	public void removeWidget(DynamicWidget widget) {
		widget.dispose();
		widgets.remove(widget);
		updateConfiguration();
//		createMenu();
	}
	

}