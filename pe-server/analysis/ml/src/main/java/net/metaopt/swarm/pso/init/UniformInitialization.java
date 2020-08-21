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
 * Default (uniform initialization) 
 */
public class UniformInitialization implements GenericInitialization {
    private Swarm swarm;

    public UniformInitialization(Swarm swarm) {
        this.swarm = swarm;
    }
    
    @Override
    public double initPosition(int index) {
        double maxPosition[] = swarm.getMaxPosition();
        double minPosition[] = swarm.getMinPosition();
        
        if (Double.isNaN(maxPosition[index])) throw new RuntimeException("maxPosition[" + index + "] is NaN!");
        if (Double.isInfinite(maxPosition[index])) throw new RuntimeException("maxPosition[" + index + "] is Infinite!");

        if (Double.isNaN(minPosition[index])) throw new RuntimeException("minPosition[" + index + "] is NaN!");
        if (Double.isInfinite(minPosition[index])) throw new RuntimeException("minPosition[" + index + "] is Infinite!");
        
        return (maxPosition[index] - minPosition[index]) * swarm.getRandomGenerator().nextDouble() + minPosition[index];
    }

    @Override
    public double initVelocity(int index) {
        double minVelocity [] = swarm.getMinVelocity();
        double maxVelocity [] = swarm.getMaxVelocity();
        
        if (Double.isNaN(maxVelocity[index])) throw new RuntimeException("maxVelocity[" + index + "] is NaN!");
        if (Double.isInfinite(maxVelocity[index])) throw new RuntimeException("maxVelocity[" + index + "] is Infinite!");

        if (Double.isNaN(minVelocity[index])) throw new RuntimeException("minVelocity[" + index + "] is NaN!");
        if (Double.isInfinite(minVelocity[index])) throw new RuntimeException("minVelocity[" + index + "] is Infinite!");
            
        return (maxVelocity[index] - minVelocity[index]) * swarm.getRandomGenerator().nextDouble() + minVelocity[index];
    }
    
}
