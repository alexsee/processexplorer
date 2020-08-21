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

import net.metaopt.swarm.FitnessFunction;
import net.metaopt.swarm.Individual;
import net.metaopt.swarm.Population;
import net.metaopt.swarm.PopulationFactory;

public class SwarmFactory implements PopulationFactory {

    @Override
    public Population createDefaultPopulation(int size, FitnessFunction function, Individual individual) {
        return new Swarm(size, (Particle) individual, function);
    }

    @Override
    public Population createDefaultPopulation(Population population) {
        return new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES, (Particle) population.getSampleIndividual(), 
                population.getFitnessFunction());
    }
    
}
