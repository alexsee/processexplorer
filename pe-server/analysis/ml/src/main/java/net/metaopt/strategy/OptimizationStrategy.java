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
import net.metaopt.swarm.FitnessFunction;
import net.metaopt.swarm.Population;
import net.metaopt.swarm.PopulationFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * Abstract class for optimization strategy implementation
 */
public abstract class OptimizationStrategy extends Observable implements Observer {

    protected Population population;
    /** The total number of iterations, which were needed to obtain optimum solution */
    protected int totalIterations = -1;
    protected double bestFitness = Double.NaN;
    protected double[] bestSolution = null;
    protected FitnessFunction fitness = null;
    protected PopulationFactory factory;

    protected boolean stopFlag;

    public double getBestFitness() {
        return bestFitness;
    }

    public double[] getBestSolution() {
        return bestSolution;
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population swarm) {
        this.population = swarm;
        this.fitness = swarm.getFitnessFunction();
    }

    public int getTotalIterations() {
        return totalIterations;
    }

    public void setTotalIterations(int totalIterations) {
        this.totalIterations = totalIterations;
    }

    public void abortOptimization() {
        stopFlag = true;
    }

    public abstract void optimize() throws ConfigurationException;

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof Boolean))
            return;
        stopFlag = (Boolean) arg;
    }

    protected void notifyFinished(int totalIterations) {
        bestSolution = population.getBestPosition();
        StateResult state = new StateResult(population, bestFitness, bestSolution, ProcessState.FINISHED);
        state.totalIterations = totalIterations;
        setChanged();
        notifyObservers(state);
    }

    protected void notifyStarted() {
        StateResult state = new StateResult(population, bestFitness, bestSolution, ProcessState.STARTED);
        setChanged();
        notifyObservers(state);
    }

    public PopulationFactory getPopulationFactory() {
        return factory;
    }

    public void setPopulationFactory(PopulationFactory factory) {
        this.factory = factory;
    }
    
    

}
