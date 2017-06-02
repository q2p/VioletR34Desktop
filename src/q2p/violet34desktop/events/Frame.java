package q2p.violet34desktop.events;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.lowlevel.Tray;

@SuppressWarnings("serial")
final class Frame extends JFrame implements WindowListener, KeyListener, DropTargetListener {
	private final FrameAbstraction frameAbstraction;
	final Panel panel;
	
	public Frame(final FrameAbstraction frameAbstraction, final int minWidth, final int minHeight, final boolean resizable, final String title) {
		super(title);
		this.frameAbstraction = frameAbstraction;
		setContentPane(panel = new Panel(frameAbstraction));
		setResizable(resizable);
		panel.setPreferredSize(new Dimension(minWidth, minHeight));
		panel.setBackground(Colors.BLACK);
		pack();
		setMinimumSize(getSize());

		addWindowListener(this);
		addKeyListener(this);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		setIconImage(Tray.iconImage);
		
		// Отключение потери фокуса при нажатии F10
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F10"), "F10");
		getRootPane().getActionMap().put("F10", new AbstractAction() {public void actionPerformed(ActionEvent e) {}});
		
		setLocationRelativeTo(null);
		
		new DropTarget(this, this);
		
		setVisible(true);
	}
	
	// Listeners
	public final void keyPressed(final KeyEvent e) {
		frameAbstraction.push(e.getKeyCode(), false);
	}

	public final void keyReleased(final KeyEvent e) {
		frameAbstraction.pull(e.getKeyCode(), false);
	}
	
	public final void keyTyped(final KeyEvent e) {
		if(Font.isValid(""+e.getKeyChar()))
			frameAbstraction.type(e.getKeyChar());
	}
	
	public final void windowClosing(final WindowEvent e) {
		frameAbstraction.close();
	}

	public final void windowOpened(final WindowEvent e) {}
	public final void windowClosed(final WindowEvent e) {}
	public final void windowIconified(final WindowEvent e) {}
	public final void windowActivated(final WindowEvent e) {}
	public final void windowDeactivated(final WindowEvent e) {}
	public final void windowDeiconified(final WindowEvent e) {}

	public final void dragEnter(final DropTargetDragEvent dtde) {
		if(!dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			dtde.rejectDrag();
	}
	public final void dragOver(final DropTargetDragEvent dtde) {
		if(!dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			dtde.rejectDrag();
	}
	@SuppressWarnings("unchecked")
	public final void drop(final DropTargetDropEvent dtde) {
		final Transferable transferable = dtde.getTransferable();
		
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		
		if(transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			final LinkedList<File> files;
			try {
				files = (LinkedList<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
			} catch (final UnsupportedFlavorException | IOException e) {
				dtde.rejectDrop();
				return;
			}

			for(final File file : files)
				if(!file.getAbsolutePath().startsWith(Assist.MAIN_FOLDER))
					files.remove(file);
			
			frameAbstraction.putFiles(files);
			
			dtde.dropComplete(true);
		} else
			dtde.rejectDrop();
	}
	
	public final void dragExit(final DropTargetEvent dte) {}
	public final void dropActionChanged(final DropTargetDragEvent dtde) {}
}