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
 * Particle update: each particle selects an rlocal and rother independently from other particles' values
 */
public class RandomByParticleUpdate implements ParticleUpdate {

    @Override
    public void update(Swarm swarm, Particle particle) {
        double position[] = particle.getPosition();
        double velocity[] = particle.getVelocity();
        double globalBestPosition[] = swarm.getBestPosition();
        double particleBestPosition[] = particle.getBestPosition();
        double neighBestPosition[] = swarm.getNeighborhoodBestPosition(particle);
        
        Random rnd = swarm.getRandomGenerator();
        double rlocal = rnd.nextDouble();
        double rneighborhood = rnd.nextDouble();
        double rglobal = rnd.nextDouble();

        for (int i = 0; i < position.length; i++) {
            position[i] = position[i] + velocity[i];
            velocity[i] = swarm.getInertia() * velocity[i] // Inertia
                    + rlocal * swarm.getParticleIncrement() * (particleBestPosition[i] - position[i]) // Local best
                    + rneighborhood * swarm.getNeighborhoodIncrement() * (neighBestPosition[i] - position[i]) // Neighborhood best					
                    + rglobal * swarm.getGlobalIncrement() * (globalBestPosition[i] - position[i]); // Global best
        }
    }

    @Override
    public void begin(Swarm swarm) {
    }

    @Override
    public void end(Swarm swarm) {
    }
}
