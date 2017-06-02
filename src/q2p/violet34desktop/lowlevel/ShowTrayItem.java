package q2p.violet34desktop.lowlevel;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import q2p.violet34desktop.windows.main.Main;

@SuppressWarnings("serial")
class ShowTrayItem extends MenuItem implements ActionListener {
	ShowTrayItem() {
		super("Показать");
		addActionListener(this);
	}

	public void actionPerformed(final ActionEvent e) {
		Main.openWindow();
	}
}