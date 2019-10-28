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
package net.metaopt.swarm.pso.constraints;

import java.util.Random;

abstract public class RandomizedConstraintsHandler implements ConstraintsHandler {

    protected Random random;

    public RandomizedConstraintsHandler() {
        this.random = new Random();
    }

    public RandomizedConstraintsHandler(Random random) {
        this.random = random;
    }

    public void setRandomGenerator(Random random) {
        this.random = random;
    }

}
