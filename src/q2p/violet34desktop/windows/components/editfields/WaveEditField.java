package q2p.violet34desktop.windows.components.editfields;

import java.awt.Color;
import q2p.violet34desktop.events.FrameAbstraction;

public class WaveEditField extends LineEditField {
	//TODO: когда нет выделения, должен передвигаться в стороны
	public WaveEditField(final int x, final int y, final int width, final int height, final Color background, final String text, final Validator validator, final FrameAbstraction frame, final boolean editable) {
		super(x, y, width, height, background, text, validator, frame, editable);
	}
}
