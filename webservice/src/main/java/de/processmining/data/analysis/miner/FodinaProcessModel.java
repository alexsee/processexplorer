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

package de.processmining.data.analysis.miner;

import de.processmining.data.query.QueryService;
import de.processmining.data.query.condition.Condition;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.fitness.AbstractFitness;
import org.processmining.plugins.bpmnminer.fitness.ICSFitness;
import org.processmining.plugins.bpmnminer.miner.FodinaMiner;
import org.processmining.plugins.bpmnminer.types.MinerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author Alexander Seeliger on 28.10.2019.
 */
public class FodinaProcessModel implements IProcessModel {

    private static Logger logger = LoggerFactory.getLogger(FodinaProcessModel.class);

    private double fitness = 0;

    private double numTraces = 0;

    public static FodinaProcessModel createInstance(QueryService queryService, String logName, List<Condition> conditions) {
        var cases = queryService.getAllPaths(logName, conditions);
        var logStat = queryService.getLogStatistics(logName);

        var factory = new XFactoryNaiveImpl();
        var log = factory.createLog();

        double num = 0;

        for (var variant : cases) {
            num += variant.getOccurrence();

            for (int i = 0; i < variant.getOccurrence(); i++) {
                var trace = factory.createTrace();

                for (var evt : variant.getPathIndex()) {
                    var event = factory.createEvent();
                    setName(factory, event, logStat.getActivities().get(evt).getName());

                    trace.add(event);
                }

                log.add(trace);
            }
        }

        MinerSettings settings = new MinerSettings();
        settings.suppressFitnessReport = false;

        FodinaMiner fodinaMiner = FodinaMiner.create(log, settings);
        CausalNet causalNet = fodinaMiner.mine();

        AbstractFitness fitness1 = new ICSFitness(fodinaMiner.getMapper(), causalNet);
        fitness1.replayLog(fodinaMiner.getMapper().getGroupedLog());

        FodinaProcessModel model = new FodinaProcessModel();
        model.fitness = fitness1.getFitness();
        model.numTraces = num;

        log.clear();

        return model;
    }

    private static void setName(XFactory factory, XAttributable obj, String value) {
        XAttribute nameAttrib = factory.createAttributeLiteral(XConceptExtension.KEY_NAME, value, XConceptExtension.instance());
        obj.getAttributes().put(nameAttrib.getKey(), nameAttrib);
    }

    private static void setTimestamp(XFactory factory, XAttributable obj, Date value) {
        XAttribute timeAttrib = factory.createAttributeTimestamp(XTimeExtension.KEY_TIMESTAMP, value, XTimeExtension.instance());
        obj.getAttributes().put(timeAttrib.getKey(), timeAttrib);
    }

    @Override
    public double getFitness() {
        return this.fitness;
    }

    @Override
    public double getSize() {
        return numTraces;
    }


}
