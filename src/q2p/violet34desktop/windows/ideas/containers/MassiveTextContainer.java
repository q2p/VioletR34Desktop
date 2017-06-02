package q2p.violet34desktop.windows.ideas.containers;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import q2p.violet34desktop.Assist;
import q2p.violet34desktop.Colors;
import q2p.violet34desktop.Font;
import q2p.violet34desktop.Renderer;
import q2p.violet34desktop.windows.components.editfields.DefaultValidator;
import q2p.violet34desktop.windows.components.editfields.MassiveEditField;
import q2p.violet34desktop.windows.ideas.IdeaViewer;

public class MassiveTextContainer extends Container {
	// TODO: при создании сразу перенимать фокус
	private String originalText;
	
	private final MassiveEditField field;

	public MassiveTextContainer(final String title, final BufferedImage button, final IdeaViewer ideaViewer) {
		super(title, button, ideaViewer);
		field = new MassiveEditField(Renderer.C_ICON_OFFSET, 0, 100, Colors.BLACK, "", DefaultValidator.ALLOW_NEWLINE, frame, false);
	}

	public final int calculateSizes(final int width, int y) {
		if(!allocated) {
			height = 0;
			return y;
		}

		field.setWidth(width);
		
		height = Renderer.C_MARGIN + TITLE_OFFSET + field.getHeight();
		
		return y + height;
	}
	
	public int updateSizes(final int width, final int y) {
		return y+height;
	}
	
	public int position(final int y) {
		positionY = y + Renderer.C_MARGIN;
		field.setPosition(Renderer.C_ICON_OFFSET, positionY + TITLE_OFFSET);
		
		return y+height;
	}
	
	public void render() {
		if(!allocated)
			return;
		
		Renderer.drawLine(title, Renderer.C_ICON_OFFSET, positionY, 1, Font.WHITE, frame.graphics());
		field.render();
	}

	public void think() {
		if(!allocated) {
			wasResized = false;
			return;
		}
		
		field.think();
				
		wasResized = field.wasChanged();
	}

	public void beginEditing() {
		field.setEditable(true); // TODO:
	}

	public void endEditingMode() {
		field.setEditable(false);
		// TODO: trim()
		if(field.getText().equals("")) {
			allocated = false;
		}
	}

	public BufferedImage getButton() {
		return allocated ? null : button;
	}

	public boolean changedButton() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onButtonPress() {
		// TODO Auto-generated method stub
		
	}

	public final void load(final DataInputStream dis) throws IOException {
		originalText = Assist.readString(dis);

		restore();
	}

	
	public void restore() {
		allocated = !originalText.equals("");
		field.setEditable(ideaViewer.isInEditingMode());
		field.setText(originalText);
		
		checkIfAllocated();
		
		if(ideaViewer.isInEditingMode()) {
			// TODO Auto-generated method stub
		} else {
			// TODO Auto-generated method stub
		}
	}
	
	private final void checkIfAllocated() {
		if(!allocated) {
			frame.releaseMouseFocusIfHoldingIt(field);
			frame.releaseKeyboardFocusIfHoldingIt(field);
		}
	}
}