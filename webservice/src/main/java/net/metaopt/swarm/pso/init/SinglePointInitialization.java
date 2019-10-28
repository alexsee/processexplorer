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
package net.metaopt.swarm.pso.init;

import net.metaopt.swarm.pso.Swarm;

/**
 * Initialization of all particles at the same point
 */
public class SinglePointInitialization implements GenericInitialization {

    private double initial[];
    private Swarm swarm;
    
    public SinglePointInitialization(double[] initial, Swarm swarm) {
        this.initial = initial;
        this.swarm = swarm;
    }

    @Override
    public double initPosition(int index) {
        return initial[index];
    }

    @Override
    public double initVelocity(int index) {
        double [] maxVelocity = swarm.getMaxVelocity();
        if (Double.isNaN(maxVelocity[index])) throw new RuntimeException("maxVelocity[" + index + "] is NaN!");
        if (Double.isInfinite(maxVelocity[index])) throw new RuntimeException("maxVelocity[" + index + "] is Infinite!");

        double [] minVelocity = swarm.getMinVelocity();
        if (Double.isNaN(minVelocity[index])) throw new RuntimeException("minVelocity[" + index + "] is NaN!");
        if (Double.isInfinite(minVelocity[index])) throw new RuntimeException("minVelocity[" + index + "] is Infinite!");
            
        return (maxVelocity[index] - minVelocity[index]) * swarm.getRandomGenerator().nextDouble() + minVelocity[index];
    }
    
}
