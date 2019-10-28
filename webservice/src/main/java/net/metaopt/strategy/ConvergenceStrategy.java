/*
 * Copyright (C) 2017 Paulius Danenas
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

/**
 * The swarm is iteratively trained until no further improvement can be observed
 * @author Paulius Danėnas <danpaulius@gmail.com>
 */
public class ConvergenceStrategy extends OptimizationStrategy  {

    /* The number of search iterations, after which the search is terminated, if no improvement is observed */
    protected int numIterConverge = 50;

    public ConvergenceStrategy() {
    }

    public ConvergenceStrategy(Swarm swarm) {
        this.setPopulation(swarm);
    }

    @Override
    public void optimize() throws ConfigurationException {
        if (population == null)
            throw new ConfigurationException("Swarm was not initialized");
        fitness = population.getFitnessFunction();
        int numIter = 0;
        totalIterations = 0;
        population.evolve();
        double currBest = population.getBestFitness();
        bestFitness = currBest;
        notifyStarted();
        boolean stop = false;
        while (!stop && !stopFlag) {
            population.evolve();
            currBest = population.getBestFitness();
            boolean cond = fitness.isMaximize() ? currBest > bestFitness : currBest < bestFitness;
            if (cond) {
                bestFitness = currBest;
                numIter = 0;
            } else
                numIter++;
            totalIterations++;
            stop = numIter == numIterConverge;
            StateResult state = new StateResult(population, bestFitness, bestSolution, ProcessState.EXECUTING);
            state.currentIteration = numIter;
            state.totalIterations = totalIterations;
            setChanged();
            notifyObservers(state);
            //System.out.println("Iteration: " + numIter + ", best fitness: " + bestFitness);
        }
        notifyFinished(totalIterations);
    }

    public int getNumIterationsConverge() {
        return numIterConverge;
    }

    /**
     * Set the number of iterations which are run until convergence. Note that this is reset each time when more optimal solution is found
     * @param numIterConverge The number of convergence iterations
     */
    public void setNumIterationsConverge(int numIterConverge) {
        this.numIterConverge = numIterConverge;
    }
    
}
