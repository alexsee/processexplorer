package de.tk.processmining.utils;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class OutputBuilder {

    private StringBuilder sb;

    public OutputBuilder() {
        this.sb = new StringBuilder();
    }

    public void print(String text) {
        sb.append(text);
        sb.append("\r\n");
    }

    public void print(String text, String... args) {
        if (args.length > 0) {
            sb.append(String.format(text, args));
            sb.append("\r\n");
        }
    }

    public void indentPrint(String text) {
        sb.append("\t");
        print(text);
    }

    public void indentPrint(String text, String... args) {
        sb.append("\t");
        print(text, args);
    }

    public void indent() {
        sb.append("\t");
    }

    public void clear() {
        this.sb = new StringBuilder();
    }

    @Override
    public String toString() {
        return sb.toString();
    }

}
