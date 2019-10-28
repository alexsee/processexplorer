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
package net.metaopt.swarm.pso.neighbour;

import net.metaopt.swarm.pso.Particle;
import net.metaopt.swarm.pso.Swarm;

import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract neighborhood for particles
 */
public abstract class Neighborhood {

    // All neighborhoods are stored here, so that we do not need to calculate them each time
    HashMap<Particle, Collection<Particle>> neighborhoods;
    // The best particle in the neighborhood is stored here
    HashMap<Particle, Particle> bestInNeighborhood;

    public Neighborhood() {
        neighborhoods = new HashMap<Particle, Collection<Particle>>();
        bestInNeighborhood = new HashMap<Particle, Particle>();
    }

    /**
     * Calculate all neighbors of particle 'p'
     * 
     * Note: The p's neighbors DO NOT include 'p'
     * 
     * @param p : a particle
     * @return A collection with all neighbors
     */
    public abstract Collection<Particle> calcNeighbours(Particle p);

    /**
     * Get the best particle in the neighborhood
     * @param p
     * @return The best particle in the neighborhood of 'p'
     */
    public Particle getBestParticle(Particle p) {
        return bestInNeighborhood.get(p);
    }

    /**
     * Get the best position ever found by all the particles in the neighborhood of 'p'
     * @param p
     * @return The best position in the neighborhood of 'p'
     */
    public double[] getBestPosition(Particle p) {
        Particle bestp = getBestParticle(p);
        if (bestp == null)
            return null;
        return bestp.getBestPosition();
    }

    /**
     * Get all neighbors of particle 'p'
     * @param p : a particle
     * @return A collection with all neighbors
     */
    public Collection<Particle> getNeighbours(Particle p) {
        Collection<Particle> neighs = neighborhoods.get(p);
        if (neighs == null)
            neighs = calcNeighbours(p);
        return neighs;
    }

    /**
     * Initialize neighborhood
     * @param swarm
     */
    public void init(Swarm swarm) {
        // Create neighborhoods for each particle
        for (Particle p : swarm) {
            Collection<Particle> neigh = getNeighbours(p);
            neighborhoods.put(p, neigh);
        }
    }

    /**
     * Update neighborhood: This is called after each iteration
     * @param swarm
     */
    public void update(Swarm swarm, Particle p) {
        // Find best fitness in this neighborhood
        Particle pbest = getBestParticle(p);
        if ((pbest == null) || swarm.getFitnessFunction().isBetterThan(pbest.getBestFitness(), p.getBestFitness())) {
            // Particle 'p' is the new 'best in neighborhood' => we need to update all neighbors
            Collection<Particle> neigh = getNeighbours(p);
            for (Particle pp : neigh)
                bestInNeighborhood.put(pp, p);
        }
    }
}
