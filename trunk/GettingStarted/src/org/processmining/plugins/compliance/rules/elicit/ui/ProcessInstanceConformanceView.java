/**
 * 
 */
package org.processmining.plugins.compliance.rules.elicit.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.petrinet.visualization.AlignmentConstants;

/**
 * @author aadrians
 * This class is modification of class ProcessInstanceView
 * package org.processmining.plugins.log.ui.logdialog
 * made by Christian W. Guenther (christian@deckfour.org)
 * 
 * The original class need to be re-implemented the core visualization is different
 *  
 */
public class ProcessInstanceConformanceView extends JComponent implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = -3966399549433794592L;
	protected static Color colorAttenuationDark = new Color(0, 0, 0, 160);
	protected static Color colorAttenuationBright = new Color(0, 0, 0, 80);
	protected static Color colorBgInstanceflag = new Color(70, 70, 70, 210);
	protected static Color colorBgEventFlag = new Color(30, 30, 30, 200);

	protected static DecimalFormat format = new DecimalFormat("##0.00%");
	protected static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

	protected static int trackPadding = 120;
	protected static int trackY = 18;
	protected static int trackHeight = 27;
	protected static int elementWidth = 7;
	protected static int elementTriOffset = 6;
	
	public static final int PANEL_HEIGHT = 48;

	protected XLogInfo info;
	protected String traceLabel;
	protected int maxOccurrenceCount;
	protected XTrace instance;
	protected boolean mouseOver = false;
	protected int mouseX;
	protected int mouseY;
	
	// modification
	protected List<Object> nodeInstance = null;
	protected List<StepTypes> stepTypes= null;
	
	protected Map<String, XEventClass> activityMap;
	
	public ProcessInstanceConformanceView(String traceLabel, List<Object> nodeInstance, List<StepTypes> stepTypes, Map<String, XEventClass> map) {
		this.traceLabel = traceLabel;
		this.nodeInstance = nodeInstance;
		this.stepTypes = stepTypes;
		this.activityMap = map; 
		
		addMouseListener(this);
		addMouseMotionListener(this);
		int width = (nodeInstance.size() * elementWidth) + trackPadding + 300;
		setMinimumSize(new Dimension(width, PANEL_HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, PANEL_HEIGHT));
		setPreferredSize(new Dimension(width, PANEL_HEIGHT));
		setDoubleBuffered(true);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Rectangle clip = getVisibleRect();//g.getClipBounds();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// draw background
		g2d.setColor(new Color(30, 30, 30));
		g2d.fillRect(clip.x, clip.y, clip.width, clip.height);
		// determine active event
		int activeEvent = -1;
		if (mouseOver == true) {
			activeEvent = mapEventIndex(mouseX, mouseY);
		}
		// draw events
		int clipRightX = clip.x + clip.width;
		int trackRightX;
		if (nodeInstance != null){
			trackRightX = trackPadding + (nodeInstance.size() * elementWidth);
		} else {
			trackRightX = trackPadding + (instance.size() * elementWidth);
		}
		int startX = clip.x - (clip.x % elementWidth); // shift to left if necessary
		if (startX < trackPadding) {
			startX = trackPadding;
		}
		int eventIndex = (startX - trackPadding) / elementWidth;
		for (int x = startX; ((x < clipRightX) && (x < trackRightX)); x += elementWidth) {
			drawEvent(g2d, eventIndex, (eventIndex == activeEvent), x, trackY, elementWidth, trackHeight);
			eventIndex++;
		}
		if (clip.x <= trackRightX) {
			// draw instance flag
			drawInstanceFlag(g2d, clip.x, trackY, trackHeight);
			// draw event flag
			if (activeEvent >= 0) {
				int eventX = trackPadding + (activeEvent * elementWidth);
				drawEventFlag(g2d, activeEvent, eventX, 7, 8);
			}
		}

	}

	protected int mapEventIndex(int x, int y) {
		if ((y >= trackY) && (y <= (trackY + trackHeight))) {
			// y-coordinate matches, remap x to index
			x -= trackPadding;
			x /= elementWidth;
			if (nodeInstance != null){
				if ((x >= 0) && (x < nodeInstance.size())) {
					return x;
				} else {
					return -1;
				}
			} else {
				if ((x >= 0) && (x < instance.size())) {
					return x;
				} else {
					return -1;
				}
			}
		} else {
			return -1;
		}

	}

	protected void drawInstanceFlag(Graphics2D g2d, int x, int y, int height) {
		
		String size;
		if (nodeInstance != null){
			size = nodeInstance.size() + " events";
		} else {
			size = instance.size() + " events"; 
		}
		// calculate width
		g2d.setFont(g2d.getFont().deriveFont(11f));
		FontMetrics fm = g2d.getFontMetrics();
		int nameWidth = fm.stringWidth(traceLabel);
		int sizeWidth = fm.stringWidth(size);
		int width = (nameWidth > sizeWidth) ? nameWidth + 15 : sizeWidth + 15;
		width = Math.max(width, trackPadding - 10);
		// draw flag shadow
		int shadowOffset = 4;
		int[] xSCoords = new int[] { x, x + width - elementTriOffset + shadowOffset, x + width + shadowOffset,
				x + width - elementTriOffset + shadowOffset, x };
		int[] ySCoords = new int[] { y + shadowOffset, y + shadowOffset, y + (height / 2) + shadowOffset,
				y + height + shadowOffset, y + height + shadowOffset };
		g2d.setColor(new Color(0, 0, 0, 100));
		g2d.fillPolygon(xSCoords, ySCoords, 5);
		// draw flag background
		g2d.setColor(colorBgInstanceflag);
		int[] xCoords = new int[] { x, x + width - elementTriOffset, x + width, x + width - elementTriOffset, x };
		int[] yCoords = new int[] { y, y, y + (height / 2), y + height, y + height };
		g2d.fillPolygon(xCoords, yCoords, 5);
		// draw string
		int fontHeight = fm.getHeight();
		int fontOffset = (height - fontHeight - fontHeight) / 3;
		g2d.setColor(new Color(220, 220, 220));
		g2d.drawString(traceLabel, x + 5, y + fontOffset + fontHeight - 1);
		g2d.setColor(new Color(200, 200, 200));
		g2d.drawString(size, x + 5, y + height - fontOffset - 3);
	}

	protected void drawEventFlag(Graphics2D g2d, int index, int x, int y, int height) throws IndexOutOfBoundsException {
		if (nodeInstance != null){
			String name = nodeInstance.get(index).toString();
			
			// calculate width
			g2d.setFont(g2d.getFont().deriveFont(9f));
			FontMetrics fm = g2d.getFontMetrics();
			int width = fm.stringWidth(name) + 10;
			
			// draw background
			g2d.setColor(colorBgEventFlag);
			g2d.fillRect(x, y, width, height);
			// set color
			Color color = getColor(stepTypes.get(index), getActivityColor(index));
			g2d.setColor(color);
			// draw anchor line
			g2d.drawLine(x, y, x, y + height);
			// draw strings
			int fontHeight = fm.getHeight();
			int fontOffset = (height - fontHeight - fontHeight - fontHeight) / 4;
			g2d.drawString(name, x + 5, y + fontOffset + fontHeight - 2);
		} else {
			XExtendedEvent ate = new XExtendedEvent(instance.get(index));
			XEventClass eventClass = info.getEventClasses().getClassOf(instance.get(index));
			int occurrence = (eventClass != null ? eventClass.size() : 0);
			//		int occurrence = summary.getLogEvents().findLogEvent(ate.getElement(), ate.getType()).getOccurrenceCount();
			double frequency = (maxOccurrenceCount == 0 ? 0.0 : (double) occurrence / (double) maxOccurrenceCount);
	
			String ateName = (ate.getName() != null ? ate.getName() : "<no name>");
			String ateTransition = (ate.getTransition() != null ? ate.getTransition() : "<no transition>");
			String ateResource = (ate.getResource() != null ? ate.getResource() : "<no resource>");
			String name = index + ": " + ateName + " (" + ateTransition + ")";
			String originator = ateResource + "; freq: " + format.format(frequency);
			Date ts = ate.getTimestamp();
			String timestamp;
			if (ts != null) {
				timestamp = dateFormat.format(ate.getTimestamp());
			} else {
				timestamp = "<no timestamp>";
			}
			// calculate width
			g2d.setFont(g2d.getFont().deriveFont(9f));
			FontMetrics fm = g2d.getFontMetrics();
			int widthA = fm.stringWidth(name) + 10;
			int widthB = fm.stringWidth(originator) + 10;
			int widthC = fm.stringWidth(timestamp) + 10;
			int width = (widthA > widthB) ? widthA : widthB;
			if (widthC > width) {
				width = widthC;
			}
			// draw background
			g2d.setColor(colorBgEventFlag);
			g2d.fillRect(x, y, width, height);
			// set color
			Color color = AlignmentConstants.MOVESYNCCOLOR;
			g2d.setColor(color);
			// draw anchor line
			g2d.drawLine(x, y, x, y + height);
			// draw strings
			int fontHeight = fm.getHeight();
			int fontOffset = (height - fontHeight - fontHeight - fontHeight) / 4;
			g2d.drawString(name, x + 5, y + fontOffset + fontHeight - 2);
			g2d.drawString(originator, x + 5, y + fontOffset + fontHeight + fontOffset + fontHeight - 3);
			g2d.drawString(timestamp, x + 5, y + height - fontOffset - 2);
		} 
	}

	private Color getColor(StepTypes stepTypes, Color activityColor) {
		switch (stepTypes){
			case L :
				if (activityColor != null) return activityColor;
				return AlignmentConstants.MOVELOGCOLOR;
			case MINVI : 
				return AlignmentConstants.MOVEMODELINVICOLOR;
			case MREAL : 
				if (activityColor != null) return activityColor;
				return AlignmentConstants.MOVEMODELREALCOLOR;
			case LMNOGOOD : 
				return AlignmentConstants.MOVESYNCVIOLCOLOR;
			case LMGOOD : 
				if (activityColor != null) return activityColor;
				return AlignmentConstants.MOVESYNCCOLOR;
			case LMREPLACED:
				return AlignmentConstants.MOVEREPLACEDCOLOR;
			case LMSWAPPED:
				return AlignmentConstants.MOVESWAPPEDCOLOR;
			default :
				return Color.WHITE; // unknown
		}
	}

	protected void drawEvent(Graphics2D g2d, int index, boolean active, int x, int y, int width, int height) {
		Color color;
		if (nodeInstance != null){
			// set correct color for event
			color = getColorOfIndex(index);
		} else {
			// set correct color for event
			// XEventClass eventClass = info.getEventClasses().getClassOf(instance.get(index));
			// int occurrence = (eventClass != null ? eventClass.size() : 0);
			// double frequency = (maxOccurrenceCount == 0 ? 0.0 : (double) occurrence / (double) maxOccurrenceCount);
			color = AlignmentConstants.MOVESYNCCOLOR;
		}
		
		if (active == false) {
			color = attenuateColor(color);
		}
		g2d.setColor(color);
		// draw triangularish shape
		int midPointBX = x + elementTriOffset;
		int midPointAX = x + width + elementTriOffset;
		int midPointY = y + (height / 2);
		int[] xCoords = new int[] { x, x + width, midPointAX, x + width, x, midPointBX };
		int[] yCoords = new int[] { y, y, midPointY, y + height, y + height, midPointY };
		if (active == true) {
			for (int i = 0; i < xCoords.length; i++) {
				xCoords[i] -= 1;
				yCoords[i] -= 3;
			}
		}
		g2d.fillPolygon(xCoords, yCoords, 6);
		// draw attenuations for 3d-effect
		if (active == true) {
			g2d.setColor(colorAttenuationDark);
			g2d.drawPolyline(new int[] { x - 3, midPointBX - 3, x - 3 }, new int[] { y - 3, midPointY - 3,
					y + height - 3 }, 3);
			g2d.setColor(colorAttenuationBright);
			g2d.drawPolyline(new int[] { x - 2, midPointBX - 2, x - 2 }, new int[] { y - 3, midPointY - 3,
					y + height - 3 }, 3);
		} else {
			g2d.setColor(colorAttenuationDark);
			g2d.drawPolyline(new int[] { x, midPointBX, x }, new int[] { y, midPointY, y + height }, 3);
			g2d.setColor(colorAttenuationBright);
			g2d.drawPolyline(new int[] { x + 1, midPointBX + 1, x + 1 }, new int[] { y, midPointY, y + height }, 3);
		}
	}

	private String getActivityName(int index) {
		Object o = nodeInstance.get(index);
		for (Map.Entry<String, XEventClass> pair : activityMap.entrySet()) {
			if (pair.getValue().toString().equals(o)) {
				return pair.getKey();
			}
		}
		return null;
	}
	
	private Color getActivityColor(int index) {
		String activity = getActivityName(index);
		if (activity != null) return UIUtil.getRGBColorFor(activity);
		return null;
	}
	
	private Color getColorOfIndex(int index) {
		return getColor(stepTypes.get(index), getActivityColor(index));
	}

	protected Color attenuateColor(Color color) {
		int red = (int) (color.getRed() * 0.5);
		int green = (int) (color.getGreen() * 0.5);
		int blue = (int) (color.getBlue() * 0.5);
		return new Color(red, green, blue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	public void mouseDragged(MouseEvent evt) {
		mouseX = evt.getX();
		mouseY = evt.getY();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent evt) {
		mouseX = evt.getX();
		mouseY = evt.getY();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		mouseOver = true;
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		mouseOver = false;
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// ignore
	}
}
