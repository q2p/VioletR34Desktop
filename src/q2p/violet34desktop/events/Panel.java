package q2p.violet34desktop.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
class Panel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private final FrameAbstraction frameAbstraction;
	
	Panel(final FrameAbstraction frameAbstraction) {
		super(false);
		this.frameAbstraction = frameAbstraction;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public void mousePressed(MouseEvent e) {
		frameAbstraction.push(e.getButton(), true);
	}
	public void mouseReleased(MouseEvent e) {
		frameAbstraction.pull(e.getButton(), true);
	}
	public void mouseClicked(MouseEvent e) {
		frameAbstraction.pull(e.getButton(), true);
	}

	public void mouseMoved(MouseEvent e) {
		frameAbstraction.mouseMoved(e.getX(), e.getY());
	}
	public void mouseDragged(MouseEvent e) {
		frameAbstraction.mouseMoved(e.getX(), e.getY());
	}
	public void mouseEntered(MouseEvent e) {
		frameAbstraction.mouseMoved(e.getX(), e.getY());
	}
	
	public void mouseExited(MouseEvent e) {
		frameAbstraction.mouseExited();
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		frameAbstraction.wheel(e.getWheelRotation());
	}
}