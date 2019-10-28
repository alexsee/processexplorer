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

/**
 * Particle update strategy
 * Every Swarm.evolve() iteration the following methods are called
 * 		- begin(Swarm) : Once at the beginning of each iteration
 * 		- update(Swarm,Particle) : Once for each particle
 * 		- end(Swarm) : Once at the end of each iteration
 */
public interface ParticleUpdate {

    public void begin(Swarm swarm);

    public void end(Swarm swarm);

    public void update(Swarm swarm, Particle particle);
}
