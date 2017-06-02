package q2p.violet34desktop.lowlevel;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import q2p.violet34desktop.Assist;

public final class OpSys {
	private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
	private static final boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
	public static final boolean unknownOperatingSystem = !(isWindows || isLinux);
	public static final boolean canShowInExplorer = isWindows;
	
	public static final void showInExplorer(final String absolutePath) {
		if(!canShowInExplorer || !showInExplorerWin(absolutePath))
			Assist.openFile(absolutePath);
	}
	private static final boolean showInExplorerWin(final String absolutePath) {
		if(isWindows) try {
			Runtime.getRuntime().exec("explorer.exe /select,\""+absolutePath.replace('/','\\')+"\"");
			return true;
		} catch(final Exception e) {}
		return false;
	}

	public static final void copyToClipboard(final String string) {
		final StringSelection selection = new StringSelection(string);
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		} catch(final Exception e) {}
	}
	
	public static final String getFromClipBoard() {
		try {
			final String str = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			if(str != null)
				return str;
		} catch (final Exception e) {}
		return "";
	}
}