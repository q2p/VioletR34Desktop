package q2p.violet34desktop.windows.components.editfields;

import q2p.violet34desktop.Font;

public class DefaultValidator implements Validator {
	final boolean blockNewLine;
	public static final DefaultValidator ALLOW_NEWLINE = new DefaultValidator(false);
	public static final DefaultValidator BLOCK_NEWLINE = new DefaultValidator(true);
	private DefaultValidator(final boolean blockNewLine) {
		this.blockNewLine = blockNewLine;
	}
	
	public TextFieldFrame validate(String prefix, String addition, String suffix, final boolean isManualInput) {
		if(blockNewLine) {
			prefix = Font.fixNewLine(prefix);
			addition = Font.fixNewLine(addition);
			suffix = Font.fixNewLine(suffix);
		}
		return new TextFieldFrame(prefix+addition+suffix, prefix.length()+addition.length());
	}
}