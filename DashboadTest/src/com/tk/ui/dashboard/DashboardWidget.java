package com.tk.ui.dashboard;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wb.swt.SWTResourceManager;

public abstract class DashboardWidget extends Composite implements DynamicWidget, MouseListener, MouseMoveListener, IExpansionListener {

	protected final FormToolkit toolkit;
	protected final Composite parent;
	protected final DynamicWidgetContainer container;
	protected Section section;
	private boolean dragged;
	private boolean resized;
	private Rectangle draggableRegion;
	private Rectangle refreshRegion;//, removeRegion;
	private Rectangle resizeHVRegion, resizeHRegion, resizeVRegion;
	protected Point minimumSize, maximumSize;
	protected Point normalSize;
	protected int minimizedHeight = 25;
	protected Point currentLocation;
	protected Rectangle oldBounds;
	protected String configurationPrefix;
	protected Listener childEnteredListener = new Listener() {public void handleEvent(Event event) { setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW)); }};
	protected Image closeImage, refreshImage;
	protected boolean disposed = false;
	protected boolean refreshable;
	protected int refreshCount = 0;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DashboardWidget(Composite parent, DynamicWidgetContainer container, FormToolkit toolkit, int style) {
		super(parent, style);
		this.toolkit = toolkit;
		this.parent = parent;
		this.container = container;
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
	}
	
	protected void initializeWidget(){
		addMouseListener(this);
		section = toolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);
		section.addExpansionListener(this);

		toolkit.paintBordersFor(section);
		
		section.addMouseListener(this);
		this.addMouseListener(this);
		section.addMouseMoveListener(this);
		this.addMouseMoveListener(this);
//		closeImage = SWTResourceManager.getImage(DashboardWidget.class, "/isg/kis/client/icons/delete.gif");
		refreshImage = SWTResourceManager.getImage(DashboardWidget.class, "/isg/kis/client/icons/update.gif");
		
		section.addPaintListener(new PaintListener(){
			
			@Override
			public void paintControl(PaintEvent e) {
//				e.gc.drawImage(closeImage, getSize().x - 25, 5);
				if(refreshable){
					e.gc.drawImage(refreshImage, getSize().x-25, 5);
					if(refreshCount > 0){
						e.gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
						e.gc.setFont(SWTResourceManager.getFont("Verdana", 10, SWT.BOLD));
						e.gc.drawString("[" + refreshCount + "]", getSize().x - (40 + String.valueOf(refreshCount).length() * 10), 4, true);
					}
				}
			}
			
		});
		
	}
	
	@Override
	public void mouseDown(MouseEvent event){
		Point position = new Point(event.x, event.y);
		if(draggableRegion.contains(event.x, event.y)){
			container.dragStarted(this, new Point(event.x, event.y));
			dragged = true;
		}else if(resizeHVRegion.contains(event.x, event.y)){
			resized = true;
			container.resizeStarted(this, position, true, true);
		}else if(resizeHRegion.contains(event.x, event.y)){
			resized = true;
			container.resizeStarted(this, position, true, false);			
		}else if(resizeVRegion.contains(event.x, event.y)){
			resized = true;
			container.resizeStarted(this, position, false, true);
//		}else if(removeRegion.contains(event.x, event.y)){
//			container.removeWidget(this);
		}else if(refreshRegion.contains(event.x, event.y)){
			refresh();
		}

	}
	
	@Override
	public void refresh() {	}

	@Override
	public void mouseUp(MouseEvent event){
		uiInteractionEnded();
	}
	
	public void mouseDoubleClick(MouseEvent event){
		uiInteractionEnded();
	}
	
	private void uiInteractionEnded(){
		if(dragged)
			container.dragEnded();
		if(resized)
			container.resizeEnded();
		resized = false;
		dragged = false;		
	}
	
	@Override
	public void mouseMove(MouseEvent event) {
		if(dragged){
			container.elementMoved(new Point(event.x, event.y));
		}else if (resized){
			container.elementResized(new Point(event.x, event.y));
		}else if(draggableRegion.contains(event.x, event.y)){
			this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZEALL));
		}else if(resizeHVRegion.contains(event.x, event.y)){
			this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZENW));	
		}else if(resizeHRegion.contains(event.x, event.y)){
			this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZEWE));			
		}else if(resizeVRegion.contains(event.x, event.y)){
			this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZENS));
		}else if(refreshRegion.contains(event.x, event.y)){ //removeRegion.contains(event.x, event.y) || 
			this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
		}else{
			this.setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
		}
	}
	
	public Point getMinimumSize() {
		return minimumSize;
	}
	
	public Point getMaximumSize(){
		return maximumSize;
	}
	
	public void setSize(Point size){
		normalSize = new Point(size.x, size.y);
		Point actualSize = size;
		if(!section.isExpanded()){
			actualSize.y = minimizedHeight;
		}
		super.setSize(actualSize);
		if(section.isExpanded()){
			section.setSize(new Point(actualSize.x, actualSize.y - 8));
		}else{
			section.setSize(new Point(actualSize.x, actualSize.y));			
		}
		
		if(refreshable){
//			refreshRegion = new Rectangle(size.x-50, 5, size.x-35, 21);
			refreshRegion = new Rectangle(size.x-25, 5, size.x-9, 21);
			draggableRegion = new Rectangle(0,0,size.x-50,minimizedHeight);
		}else{
			refreshRegion = new Rectangle(0,0,0,0);
			draggableRegion = new Rectangle(0,0,size.x-25,minimizedHeight);
		}
//		removeRegion = new Rectangle(size.x-25, 5, size.x-9, 21);
		resizeHVRegion = new Rectangle(size.x-10, Math.max(minimizedHeight, size.y-10), size.x, size.y);
		resizeHRegion = new Rectangle(size.x-10, minimizedHeight, size.x, size.y-10);
		resizeVRegion = new Rectangle(0, Math.max(minimizedHeight, size.y-100), size.x-10, size.y);
			}
	
	public void setLocation(Point point){
		super.setLocation(point);
		currentLocation = point;
	}
	
	public void setLocation(int x, int y){
		super.setLocation(x, y);
	}
	
	public void setBounds(int x, int y, int width, int height){
		
	}
	
	public void setBounds(Rectangle rect){
		
	}
	
	@Override
	public void expansionStateChanged(ExpansionEvent event) {
		setSize(normalSize);
		setLocation(currentLocation.x, currentLocation.y);
		container.reorderWidgets(this, oldBounds);
		container.updateConfiguration();
	}

	@Override
	public void expansionStateChanging(ExpansionEvent event) {
		oldBounds = this.getBounds();
	}
	
	public Properties storeConfiguration(Properties prop){
		prop.setProperty(configurationPrefix, disposed ? "false" : "true");
		prop.setProperty(configurationPrefix + ".expanded", String.valueOf(section.isExpanded()));
		prop.setProperty(configurationPrefix + ".x", String.valueOf(this.getLocation().x));
		prop.setProperty(configurationPrefix + ".y", String.valueOf(this.getLocation().y));
		prop.setProperty(configurationPrefix + ".width", String.valueOf(normalSize.x));
		prop.setProperty(configurationPrefix + ".height", String.valueOf(normalSize.y));
		return prop;
	}
	
	public void applyConfiguration(Properties prop){
		int x = safeIntRetrieval(prop, configurationPrefix + ".x", 0);
		int y = safeIntRetrieval(prop, configurationPrefix + ".y", 0);
		int width = safeIntRetrieval(prop, configurationPrefix + ".width", 0);
		int height = safeIntRetrieval(prop, configurationPrefix + ".height", 0);
		boolean expanded = safeBoolRetrieval(prop, configurationPrefix + ".expanded", true);
				
		this.setLocation(x, y);
		this.setSize(width, height);
		section.setExpanded(expanded);
	}
	
	protected int safeIntRetrieval(Properties prop, String key, int defaultValue){
		if(!prop.containsKey(key))
			return defaultValue;
		try{
			return Integer.valueOf(prop.getProperty(key));
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	protected boolean safeBoolRetrieval(Properties prop, String key, boolean defaultValue){
		if(!prop.containsKey(key))
			return defaultValue;
		try{
			return Boolean.valueOf(prop.getProperty(key));
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	public void minimize(){
		section.setExpanded(false);
	}
	
	public void restore(){
		section.setExpanded(true);
	}
	
	public void dispose(){
		super.dispose();
		this.disposed = true;
	}
	
	public void setRefreshCount(int count){
		this.refreshCount = count;
		Display.getDefault().asyncExec(new Runnable() {
            public void run() {
            	redraw();
            }
        });
	}
}

