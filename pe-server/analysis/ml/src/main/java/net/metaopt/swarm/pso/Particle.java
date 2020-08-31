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
package net.metaopt.swarm.pso;

import net.metaopt.swarm.Individual;
import net.metaopt.swarm.pso.constraints.ConstraintsHandler;
import net.metaopt.swarm.pso.init.GenericInitialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract swarm particle
 */
public class Particle implements Individual {

    /** Best fitness function so far */
    double bestFitness;
    /** Best particle's position so far */
    double bestPosition[];
    /** current fitness */
    double fitness;
    /** Position */
    double position[];
    /** Velocity */
    double velocity[];
    /** Particle dimension */
    int dimension;

    /**
     * Constructor
     * @param dimension : Particle's dimension
     */
    public Particle(int dimension) {
        allocate(dimension);
        this.dimension = dimension;
    }

    /**
     * Constructor
     * @param sampleParticle : A sample particle to copy
     */
    public Particle(Particle sampleParticle) {
        this.dimension = sampleParticle.getDimension();
        allocate(dimension);
    }

    private void allocate(int dimension) {
        position = new double[dimension];
        bestPosition = new double[dimension];
        velocity = new double[dimension];
        bestFitness = Double.NaN;
        fitness = Double.NaN;
        for (int i = 0; i < position.length; i++)
            bestPosition[i] = Double.NaN;
    }

    /**
     * Apply position and velocity constraints (clamp)
     * @param minPosition : Minimum position
     * @param maxPosition : Maximum position
     * @param minVelocity : Minimum velocity
     * @param maxVelocity : Maximum velocity
     * @param constraintHandler: Constraints handler
     */
    public void applyConstraints(double[] minPosition, double[] maxPosition, double[] minVelocity,
            double[] maxVelocity, ConstraintsHandler constraintHandler) {
        for (int i = 0; i < position.length; i++) {
            if (minPosition != null && !Double.isNaN(minPosition[i]))
                position[i] = (minPosition[i] > position[i] ? constraintHandler.getPosition(this, i, minPosition) : position[i]);
            if (maxPosition != null && !Double.isNaN(maxPosition[i]))
                position[i] = (maxPosition[i] < position[i] ? constraintHandler.getPosition(this, i, maxPosition) : position[i]);
            if (minVelocity != null && !Double.isNaN(minVelocity[i]))
                velocity[i] = (minVelocity[i] > velocity[i] ? constraintHandler.getVelocity(this, i, minVelocity) : velocity[i]);
            if (maxVelocity != null && !Double.isNaN(maxVelocity[i]))
                velocity[i] = (maxVelocity[i] < velocity[i] ? constraintHandler.getVelocity(this, i, maxVelocity) : velocity[i]);
        }
    }

    /**
     * Copy position[] to positionCopy[]
     */
    public void copyPosition(double positionCopy[]) {
        for (int i = 0; i < position.length; i++)
            positionCopy[i] = position[i];
    }

    /**
     * Copy position[] to bestPosition[]
     */
    public void copyPosition2Best() {
        for (int i = 0; i < position.length; i++)
            bestPosition[i] = position[i];
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public double[] getBestPosition() {
        return bestPosition;
    }

    public int getDimension() {
        return position.length;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    public double[] getPosition() {
        return position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void init(GenericInitialization init) {
        for (int i = 0; i < position.length; i++) {
            position[i] = init.initPosition(i);
            velocity[i] = init.initVelocity(i);
            bestPosition[i] = Double.NaN;
        }
    }

    /**
     * Create a new instance of this particle
     * @return A new particle, just like this one
     */
    public Object selfFactory() {
        try {
            Constructor ctor = this.getClass().getDeclaredConstructor(int.class);
	    ctor.setAccessible(true);
            return ctor.newInstance(this.dimension);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    public void setBestPosition(double[] bestPosition) {
        this.bestPosition = bestPosition;
    }

    /**
     * Set fitness and best fitness accordingly. If it's the best fitness so far, copy data to bestFitness[]
     * @param fitness : New fitness value
     * @param maximize : Are we maximizing or minimizing fitness function?
     */
    @Override
    public void setFitness(double fitness, boolean maximize) {
        this.fitness = fitness;
        if ((maximize && (fitness > bestFitness)) // Maximize and bigger? => store data
                || (!maximize && (fitness < bestFitness)) // Minimize and smaller? => store data too
                || Double.isNaN(bestFitness)) {
            copyPosition2Best();
            bestFitness = fitness;
        }
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    @Override
    public String toString() {
        String str = "fitness: " + fitness + "\tbest fitness: " + bestFitness;

        if (position != null) {
            str += "\n\tPosition:\t";
            for (int i = 0; i < position.length; i++)
                str += position[i] + "\t";
        }
        if (velocity != null) {
            str += "\n\tVelocity:\t";
            for (int i = 0; i < velocity.length; i++)
                str += velocity[i] + "\t";
        }
        if (bestPosition != null) {
            str += "\n\tBest:\t";
            for (int i = 0; i < bestPosition.length; i++)
                str += bestPosition[i] + "\t";
        }
        str += "\n";
        return str;
    }
}
