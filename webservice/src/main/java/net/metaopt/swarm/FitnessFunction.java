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
package net.metaopt.swarm;

import net.metaopt.swarm.pso.Particle;

/**
 * Abstract fitness function. Each optimization task must extend it
 */
public abstract class FitnessFunction {

    /**
     * Should this function be maximized or minimized
     */
    boolean maximize;

    /**
     * Default constructor
     */
    public FitnessFunction() {
        maximize = true; // Default: Maximize
    }

    /**
     * Constructor
     *
     * @param maximize : Should we try to maximize or minimize this function?
     */
    public FitnessFunction(boolean maximize) {
        this.maximize = maximize;
    }

    /**
     * Evaluate particle at a given position
     *
     * @param position : Particle's position
     * @return Fitness function for a particle
     */
    public abstract double evaluate(double position[]);

    /**
     * Evaluate a particle
     *
     * @param particle : Particle to evaluate
     * @return Fitness function for a particle
     */
    public double evaluate(Particle particle) {
        double position[] = particle.getPosition();
        double fit = evaluate(position);
        particle.setFitness(fit, maximize);
        return fit;
    }

    /**
     * Is 'otherValue' better than 'fitness'?
     * @param fitness
     * @param otherValue
     * @return true if 'otherValue' is better than 'fitness'
     */
    public boolean isBetterThan(double fitness, double otherValue) {
        if (maximize) {
            if (otherValue > fitness)
                return true;
        } else
            if (otherValue < fitness)
                return true;
        return false;
    }

    /**
     * Are we maximizing this fitness function?
     */
    public boolean isMaximize() {
        return maximize;
    }

    public void setMaximize(boolean maximize) {
        this.maximize = maximize;
    }

}
