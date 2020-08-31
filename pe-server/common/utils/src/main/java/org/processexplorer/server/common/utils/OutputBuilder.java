/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.processexplorer.server.common.utils;

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
