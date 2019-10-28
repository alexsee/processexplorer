/*
 * Copyright (C) 2017 Paulius Danenas, <danpaulius@gmail.com>
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
package net.metaopt.strategy;

import net.metaopt.strategy.StateResult.ProcessState;
import net.metaopt.swarm.ConfigurationException;

public class SimpleIterationStrategy extends OptimizationStrategy {

    @Override
    public void optimize() throws ConfigurationException {
        if (population == null)
            throw new ConfigurationException("Swarm was not initialized");
        int numIter = 0;
        totalIterations = 0;
        population.evolve();
        double currBest = population.getBestFitness();
        bestFitness = currBest;
        notifyStarted();
        for (int i = 0; i < totalIterations; i++) {
            if (stopFlag)
                break;
            population.evolve();
            StateResult state = new StateResult(population, bestFitness, bestSolution, ProcessState.EXECUTING);
            state.currentIteration = numIter;
            state.totalIterations = totalIterations;
            setChanged();
            notifyObservers(state);
        }
        notifyFinished(totalIterations);
    }

}
