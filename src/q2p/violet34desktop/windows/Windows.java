package q2p.violet34desktop.windows;

import q2p.violet34desktop.Assist;
import q2p.violet34desktop.windows.ideas.Ideas;
import q2p.violet34desktop.windows.main.Main;
import q2p.violet34desktop.windows.updates.Updates;

public class Windows {
	private static final Object lock = new Object();
	private static boolean inputQuiting = false;
	private static boolean quiting = false;

	//TODO: Кнопка README в которой описан метод хранения данных во внешних файлах, способ работы программы в целом и способ чтения данных для использования в других приложениях
		
	public static void think() {
		quiting = false;
		synchronized (lock) {
			if(inputQuiting) {
				quiting = true;
			}
			inputQuiting = false;
		}
		if(quiting) {
			Assist.destroyEverything();
			// TODO: заглушка
			System.exit(0);
		}
		Main.think();
		Updates.think();
		Ideas.think();
	}
	
	public static void render() {
		Main.render();
		Updates.render();
		Ideas.render();
	}

	public static void quiting() {
		synchronized (lock) {
			inputQuiting = true;
		}
	}
}