package com.tk.ui.dashboard;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public interface DynamicWidgetContainer {
	public void elementMoved(Point point);
	public void dragStarted(DynamicWidget widget, Point point);
	public void dragEnded();
	public void resizeStarted(DynamicWidget widget, Point point, boolean horizontal, boolean vertical);
	public void elementResized(Point point);
	public void resizeEnded();
	public void updateConfiguration();
	public void reorderWidgets(DynamicWidget activeWidget, Rectangle oldBounds);
	public void removeWidget(DynamicWidget widget);
}
