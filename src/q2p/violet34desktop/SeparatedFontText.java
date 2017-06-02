package q2p.violet34desktop;

import java.awt.Graphics;
import java.util.LinkedList;
import q2p.violet34desktop.windows.components.Component;

public final class SeparatedFontText {
	// TODO: за inline'ить функции, провести чистку кода, и оптимизировать 
	private int maxWidth;
	public final int getMaxWidth() {
		return maxWidth;
	}
	private short[][][] fontNewLines;
	private short[][][] words;
	private int[][] fontNewLinesHeightAndWidth;
	public short[][] fontRender; 
	public final int getHeight() {
		return fontRender.length*Font.FONT_HEIGHT;
	}

	public SeparatedFontText(final String string, final int maxWidth) {
		this.maxWidth = maxWidth;
		setString(string);
	}
	
	public final void setString(final String string) {
		final LinkedList<String> lines = new LinkedList<String>();
		
		int pointer = 0;
		for(int i = 0; (i = string.indexOf("\n", pointer)) != -1; ) {
			lines.addLast(string.substring(pointer, i));
			pointer = i+1;
		}
		lines.addLast(string.substring(pointer));

		words = new short[lines.size()][][];
		fontNewLines = new short[words.length][][];
		fontNewLinesHeightAndWidth = new int[words.length][2];
		
		for(int i = lines.size()-1; i != -1; i--) {
			fontNewLinesHeightAndWidth[i][1] = lines.getLast().length();
			words[i] = splitLineToWords(lines.removeLast());
		}

		setMaxWidth(maxWidth);
	}
	
	private final short[][] splitLineToWords(final String line) {
		short[] fontLine = Font.toArray(line);
		
		if(line.length() < 2)
			return new short[][] {fontLine};

		final LinkedList<short[]> words = new LinkedList<short[]>();
		
		int start = 0;
		for(int i = 1; i != fontLine.length; i++) {
			if(fontLine[i] != Font.SPACE && fontLine[i-1] == Font.SPACE) {
				words.add(cut(fontLine, start, i-start));
				start = i;
			}
		}
		words.add(cut(fontLine, start, fontLine.length-start));
		
		short[][] ret = new short[words.size()][];
		for(int i = ret.length - 1; i != -1; i--)
			ret[i] = words.removeLast();
		return ret;
	}

	public final void setMaxWidth(final int maxWidth) {
		this.maxWidth = maxWidth;
		
		final LinkedList<short[][]> fontLines = new LinkedList<short[][]>();

		int totalLength = 0;
		
		for(int i = 0; i != words.length; i++) {
			final LinkedList<short[]> lines = new LinkedList<short[]>();
			final LinkedList<short[]> oneLine = new LinkedList<short[]>();

			LinkedList<short[]> wordsList = new LinkedList<short[]>();
			for(int j = words[i].length-1; j != -1; j--)
				wordsList.addFirst(words[i][j]);
			
			while(!wordsList.isEmpty()) {
				packWordsToFit(wordsList, oneLine);
				
				int oneLineLength = 0;
				
				for(final short[] word : oneLine)
					oneLineLength += word.length;
				
				final short[] oneLineFont = new short[oneLineLength];
				
				int p = 0;
				
				while(!oneLine.isEmpty()) {
					final short[] fontLine = oneLine.removeFirst();
					for(int j = fontLine.length-1; j != -1; j--)
						oneLineFont[p+j] = fontLine[j];
					p += fontLine.length;
				}
				
				lines.add(oneLineFont);
			}
			
			// Упаковка
			final short[][] fontLine = new short[lines.size()][];
			fontNewLinesHeightAndWidth[i][0] = fontLine.length;
			for(int j = fontLine.length - 1; j != -1; j--)
				fontLine[j] = lines.removeLast();
			
			fontLines.addLast(fontLine);
			totalLength += fontLine.length;
		}

		fontRender = new short[totalLength][];

		for(int i = 0, j = 0; !fontLines.isEmpty();) {
			final short[][] line = fontLines.removeFirst();
			fontNewLines[j++] = line;
			for(int k = 0; k != line.length;)
				fontRender[i++] = line[k++];
		}
		getHeight();
	}
	public final void positionCursor(int cursor, final int[] out) {
		int height = 0;
		int lineNumber = 0;
		while(lineNumber != fontNewLinesHeightAndWidth.length-1 && cursor >= fontNewLinesHeightAndWidth[lineNumber][1]+1) {
			height += fontNewLinesHeightAndWidth[lineNumber][0];
			cursor -= fontNewLinesHeightAndWidth[lineNumber][1]+1;
			lineNumber++;
		}
		
		short[][] itsLine = fontNewLines[lineNumber];
		
		for(lineNumber = 0; lineNumber != itsLine.length - 1 && cursor >= itsLine[lineNumber].length; lineNumber++) {
			height++;
			cursor -= itsLine[lineNumber].length;
		}
		
		out[0] = cursor;
		out[1] = height;
	}
	private final int limitWith(final short[] line, int avilableSpace) {
		int currentLength = 0;
		while(currentLength != line.length) {
			avilableSpace -= Font.FONTS_WIDTH[line[currentLength]]+1;
			if(avilableSpace < 0)
				return currentLength;
			currentLength++;
		}
		return currentLength;
	}
	private final short[] cut(final short[] line, final int offset, final int length) {
		short[] ret = new short[length];
		for(int i = 0; i != length; i++)
			ret[i] = line[offset+i];
		return ret;
	}
	private final void packWordsToFit(final LinkedList<short[]> wordsList, final LinkedList<short[]> out) {
		int size = 0;
		short[] word;
		while(!wordsList.isEmpty()) {
			if(size + Font.sizeWithSpace(wordsList.getFirst()) > maxWidth) {
				if(size != 0)
					return;
				
				word = wordsList.removeFirst();
				final int fitLength = limitWith(word, maxWidth-size);
				out.addLast(cut(word, 0, fitLength));
				size += Font.sizeIntervalWithSpace(word, 0, fitLength);
				wordsList.addFirst(cut(word, fitLength, word.length-fitLength));
				return;
			}
			word = wordsList.removeFirst();
			out.addLast(word);
			size += Font.sizeWithSpace(word);
		}
	}
	public final void render(final int x, int y, final byte color, final Graphics graphics) {
		Renderer.drawLines(fontRender, x, y, 1, color, graphics);
	}
	public final void render(final Component barier, final byte color, final Graphics graphics) {
		int yBeg = Assist.limit(0, barier.getY(), barier.frame().height());
		int yEnd = Assist.limit(0, barier.getY()+barier.getHeight(), barier.frame().height());
		render(barier.getX(), barier.getY(), yBeg, yEnd-yBeg, color, graphics);
	}
	public final void render(final int x, int y, final int visiblePartY, final int visiblePartHeight, final byte color, final Graphics graphics) {
		// TODO: рендерить только видимую часть
		int yBeg = visiblePartY - y;
		int yEnd = yBeg + visiblePartHeight;
		yBeg = Assist.limit(0, yBeg/Font.FONT_HEIGHT, fontRender.length);
		yEnd = Assist.limit(0, yEnd/Font.FONT_HEIGHT + 1, fontRender.length);
		y += yBeg*Font.FONT_HEIGHT;
		for(int i = yBeg; i != yEnd; i++) {
			Renderer.drawLine(fontRender[i], x, y, 1, color, graphics);
			y += Font.FONT_HEIGHT;
		}
	}

	public final int getCursorAt(final int position, int lineNumber) {
		lineNumber = Assist.limit(0, lineNumber, fontRender.length-1);

		int addition = 0;
		int newLineNumber = 0;

		while(newLineNumber != fontNewLinesHeightAndWidth.length-1 && lineNumber >= fontNewLinesHeightAndWidth[newLineNumber][0]) {
			lineNumber -= fontNewLinesHeightAndWidth[newLineNumber][0];
			addition += fontNewLinesHeightAndWidth[newLineNumber][1]+1;
			newLineNumber++;
		}
		
		for(int i = 0; i != lineNumber; i++)
			addition += fontNewLines[newLineNumber][i].length;
		
		return addition+Font.curorAtPositionWithSpace(fontNewLines[newLineNumber][lineNumber], position);
	}

	public int leftCornerCursor(final int cursor) {
		int newCursor = cursor;
		int lineNumber = 0;
		while(lineNumber != fontNewLinesHeightAndWidth.length-1 && newCursor >= fontNewLinesHeightAndWidth[lineNumber][1]+1) {
			newCursor -= fontNewLinesHeightAndWidth[lineNumber][1]+1;
			lineNumber++;
		}
		
		final short[][] itsLine = fontNewLines[lineNumber];
		
		lineNumber = 0;
		while(lineNumber != itsLine.length-1 && newCursor >= itsLine[lineNumber].length) {
			newCursor -= itsLine[lineNumber].length;
			lineNumber++;
		}

		return cursor - newCursor;
	}

	public int rightCornerCursor(final int cursor) {
		int newCursor = cursor;
		int lineNumber = 0;
		while(lineNumber != fontNewLinesHeightAndWidth.length-1 && newCursor >= fontNewLinesHeightAndWidth[lineNumber][1]+1) {
			newCursor -= fontNewLinesHeightAndWidth[lineNumber][1]+1;
			lineNumber++;
		}
		
		final short[][] itsLine = fontNewLines[lineNumber];
		
		lineNumber = 0;
		while(lineNumber != itsLine.length-1 && newCursor >= itsLine[lineNumber].length) {
			newCursor -= itsLine[lineNumber].length;
			lineNumber++;
		}
		
		if(itsLine.length - 1 == lineNumber)
			return cursor - newCursor + itsLine[lineNumber].length;
		
		return cursor - newCursor + itsLine[lineNumber].length-1;
	}
}