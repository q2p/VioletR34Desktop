package q2p.violet34desktop.lowlevel;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import q2p.violet34desktop.windows.Windows;

@SuppressWarnings("serial")
class QuitTrayItem extends MenuItem implements ActionListener{
	QuitTrayItem() {
		super("Выход");
		addActionListener(this);
	}

	public void actionPerformed(final ActionEvent e) {
		Windows.quiting();
	}
}