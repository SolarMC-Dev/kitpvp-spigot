package gg.solarmc.kitpvp.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class CommandIterator implements Iterator<String> {

    private final String[] content;
    private int position;

    public CommandIterator(String[] content) {
        this.content = content;
    }

    @Override
    public boolean hasNext() {
        return position < content.length;
    }

    @Override
    public String next() {
        String next = peek();
        position++;
        return next;
    }

    public String peek() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return content[position];
    }

    public String concatRemaining() {
        return String.join(" ", Arrays.asList(content).subList(position, content.length));
    }

    @Override
    public void forEachRemaining(Consumer<? super String> action) {
        try {
            for (int n = position; n < content.length; n++) {
                action.accept(content[n]);
            }
        } finally {
            position = content.length;
        }
    }
}
