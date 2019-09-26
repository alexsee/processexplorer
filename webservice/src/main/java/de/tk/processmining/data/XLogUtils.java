package de.tk.processmining.data;

import org.deckfour.xes.factory.XFactoryRegistry;
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
