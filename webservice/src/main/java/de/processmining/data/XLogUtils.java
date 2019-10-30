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

package de.processmining.data;

import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;
import org.processmining.xeslite.external.XFactoryExternalStore;

import java.io.File;
import java.util.List;

/**
 * @author Alexander Seeliger on 20.09.2019.
 */
public class XLogUtils {

    public static Object getAttributeValue(XAttribute attribute) {
        if (attribute instanceof XAttributeList) {
            return ((XAttributeList) attribute).getCollection();
        } else if (attribute instanceof XAttributeContainer) {
            return ((XAttributeContainer) attribute).getCollection();
        } else if (attribute instanceof XAttributeLiteral) {
            return ((XAttributeLiteral) attribute).getValue();
        } else if (attribute instanceof XAttributeBoolean) {
            return ((XAttributeBoolean) attribute).getValue();
        } else if (attribute instanceof XAttributeContinuous) {
            return ((XAttributeContinuous) attribute).getValue();
        } else if (attribute instanceof XAttributeDiscrete) {
            return ((XAttributeDiscrete) attribute).getValue();
        } else if (attribute instanceof XAttributeTimestamp) {
            return ((XAttributeTimestamp) attribute).getValue();
        } else if (attribute instanceof XAttributeID) {
            return ((XAttributeID) attribute).getValue();
        } else {
            if (attribute == null) return null;
            throw new IllegalArgumentException("Unexpected attribute type!");
        }
    }

    /**
     * Reads a log from the file system. (xes and xes.gz files are supported)
     *
     * @param file
     * @return
     */
    public static XLog readLog(String file) {
        XesXmlParser parser = null;

        if (file.endsWith(".xes.gz")) {
            parser = new XesXmlGZIPParser(new XFactoryExternalStore.MapDBDiskImpl());
        } else {
            parser = new XesXmlParser(new XFactoryExternalStore.MapDBDiskImpl());
        }

        try {
            List<XLog> logs = parser.parse(new File(file));
            return logs.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
