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
package net.metaopt.swarm.pso.particle;

import net.metaopt.swarm.pso.Particle;
import net.metaopt.swarm.pso.Swarm;

import java.util.Random;

/**
 * Particle update strategy
 */
public class SimpleParticleUpdate implements ParticleUpdate {

    /** Random vector for local update */
    double rlocal[];
    /** Random vector for global update */
    double rglobal[];
    /** Random vector for neighborhood update */
    double rneighborhood[];

    /**
     * Constructor 
     * @param particle : Sample of particles that will be updated later
     */
    public SimpleParticleUpdate(Particle particle) {
        rlocal = new double[particle.getDimension()];
        rglobal = new double[particle.getDimension()];
        rneighborhood = new double[particle.getDimension()];
    }

    @Override
    public void begin(Swarm swarm) {
        int i, dim = swarm.getSampleIndividual().getDimension();
        Random rnd = swarm.getRandomGenerator();
        for (i = 0; i < dim; i++) {
            rlocal[i] = rnd.nextDouble();
            rglobal[i] = rnd.nextDouble();
            rneighborhood[i] = rnd.nextDouble();
        }
    }

    @Override
    public void end(Swarm swarm) {
    }

    @Override
    public void update(Swarm swarm, Particle particle) {
        double position[] = particle.getPosition();
        double velocity[] = particle.getVelocity();
        double globalBestPosition[] = swarm.getBestPosition();
        double particleBestPosition[] = particle.getBestPosition();
        double neighBestPosition[] = swarm.getNeighborhoodBestPosition(particle);

        // Update velocity and position
        for (int i = 0; i < position.length; i++) {
            // Update velocity
            velocity[i] = swarm.getInertia() * velocity[i] // Inertia
                + rlocal[i] * swarm.getParticleIncrement() * (particleBestPosition[i] - position[i]) // Local best
                + rneighborhood[i] * swarm.getNeighborhoodIncrement() * (neighBestPosition[i] - position[i]) // Neighborhood best					
                + rglobal[i] * swarm.getGlobalIncrement() * (globalBestPosition[i] - position[i]); // Global best
            // Update position
            position[i] += velocity[i];
        }
    }
}
