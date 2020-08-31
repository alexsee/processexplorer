/*
 * Copyright (C) 2017 Paulius Danenas <danpaulius@gmail.com>
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
import net.metaopt.swarm.pso.Swarm;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple "multiple swarms" training strategy. A total of n swarms are trained
 * using a fixed number of iterations, and best global result is returned
 */
public class MultipleRestartStrategy extends OptimizationStrategy {

    protected int numIterations = 20;
    protected int numRestarts = 10;

    public MultipleRestartStrategy(int n) {
        numRestarts = n;
    }

    public MultipleRestartStrategy(int n, Swarm swarm) {
        setPopulation(swarm);
        numRestarts = n;
    }

    @Override
    public void optimize() throws ConfigurationException {
        if (population == null)
            throw new ConfigurationException("Population was not initialized");
        double[] best = new double[numRestarts];
        double[][] bestSol = new double[numRestarts][];
        for (int i = 0; i < numRestarts; i++) {
            try {
                population = population.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(MultipleRestartStrategy.class.getName()).log(Level.SEVERE, null, ex);
                population = factory.createDefaultPopulation(population);
            }
            notifyStarted();
            for (int k = 0; k < numIterations; k++) {
                population.evolve();
                totalIterations++;
            }
            best[i] = population.getBestFitness();
            bestSol[i] = population.getBestPosition();
            StateResult state = new StateResult(population, best[i], bestSol[i], ProcessState.EXECUTING);
            state.numRuns = i;
            state.totalIterations = numIterations;
            setChanged();
            notifyObservers(state);
            //System.out.println("Iteration " + i + ": " + best[i]);
        }
        bestFitness = best[0];
        bestSolution = bestSol[0];
        if (numRestarts == 1)
            return;
        for (int i = 1; i < numRestarts; i++)
            if (fitness.isMaximize()) {
                double nextBest = Math.max(bestFitness, best[i]);
                if (nextBest > bestFitness) {
                    bestFitness = nextBest;
                    bestSolution = bestSol[i];
                }
            } else {
                double nextBest = Math.min(bestFitness, best[i]);
                if (nextBest < bestFitness) {
                    bestFitness = nextBest;
                    bestSolution = bestSol[i];
                }
            }
        StateResult state = new StateResult(population, bestFitness, bestSolution, ProcessState.FINISHED);
        state.numRuns = numRestarts;
        setChanged();
        notifyObservers(state);
    }

}
