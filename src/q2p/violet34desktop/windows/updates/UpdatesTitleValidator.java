package q2p.violet34desktop.windows.updates;

import q2p.violet34desktop.Font;
import q2p.violet34desktop.windows.components.editfields.TextFieldFrame;
import q2p.violet34desktop.windows.components.editfields.Validator;

public class UpdatesTitleValidator implements Validator {
	public static final UpdatesTitleValidator VALIDATOR = new UpdatesTitleValidator();
	private UpdatesTitleValidator() {}
	
	static final byte MAX_LENGTH = 64;
	
	public TextFieldFrame validate(String prefix, String addition, String suffix, final boolean isManualInput) {
		prefix = Font.fixIfInvalid(prefix);
		suffix = Font.fixIfInvalid(suffix);
		addition = Font.fixIfInvalid(addition);
		addition = addition.substring(0, Math.max(Math.min(MAX_LENGTH - prefix.length() - suffix.length(), addition.length()), 0));
		int cur = Math.min(MAX_LENGTH, prefix.length() + addition.length());
		addition = prefix + addition + suffix;
		addition = addition.substring(0, Math.min(MAX_LENGTH, addition.length()));
		return new TextFieldFrame(addition, cur);
	}
}