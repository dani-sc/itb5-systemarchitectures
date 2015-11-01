package indsys.filter;

import java.io.StreamCorruptedException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.ListIterator;

import indsys.types.Line;
import pimpmypipe.filter.AbstractFilter;
import pimpmypipe.interfaces.Readable;
import pimpmypipe.interfaces.Writeable;

public class LineSorter extends AbstractFilter<Line, Line> {
	private LinkedList<Line> _lines;

	public LineSorter(Readable<Line> input, Writeable<Line> output) throws InvalidParameterException {
		super(input, output);
	}

	@Override
	public Line read() throws StreamCorruptedException {
		if(_lines == null) {
			_lines = new LinkedList<>();
			Line line;
			while( (line = this.readInput()) != null) {
				insertLine(line);
			}
		}
		
		if(_lines.isEmpty()) {
			return null;
		} else {
			return _lines.pop();
		}
	}

	private void insertLine(Line line) {
		ListIterator<Line> it = _lines.listIterator();
		while(it.hasNext()) {
			Line curLine = it.next();
			if(line.compareTo(curLine) <= 0) {
				it.previous();
				it.add(line);
				return;
			}
		}
		it.add(line);
	}

	@Override
	public void write(Line value) throws StreamCorruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
