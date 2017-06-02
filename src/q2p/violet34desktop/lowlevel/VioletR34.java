package q2p.violet34desktop.lowlevel;

import q2p.violet34desktop.windows.Windows;
import q2p.violet34desktop.windows.main.Main;

public final class VioletR34 {
	public static final String TITLE = "VioletR34";

	/*
	TODO: Фичи
	спрашивать перед сохранением изменений или удалением
	Хранилище ссылок
	Рейтинг лучших изображений
	// Desktop.getDesktop().open(file); // для открытия файлов
	автоматическое создание бекапов с разумным названием и периодом времени / кол-вом изменений
	Продвинутая система тэгов, группировка, аргументы, плавающее значение, зависимости
	Перемещать идею в списке
	Фокус мыши
	Создание бэкапов
	Тэги:
		Ориентация(альбомная, портретная, квадратная)
		разрешение(hi-res, 4k, fullHD)
		сортировка по дате
		поиск по дате(2014г/ 2014.03/6 месяцев назад)
	*/

	public static void main(final String[] args) {
		/*if(OpSys.unknownOperatingSystem) {
			// TODO: рендерить в менюшке эту информацию
		}*/
		
		Tray.initilize();
		Main.openWindow();
		Timing.init();
		while(true) {
			Windows.think();
			Windows.render();
			Timing.sleep();
		}
	}
}