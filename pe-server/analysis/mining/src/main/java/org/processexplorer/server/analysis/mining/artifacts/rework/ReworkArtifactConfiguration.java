/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
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

package org.processexplorer.server.analysis.mining.artifacts.rework;

import org.processexplorer.server.analysis.mining.artifacts.ArtifactConfiguration;
import org.processexplorer.server.analysis.mining.artifacts.ArtifactFieldDescription;
import org.processexplorer.server.analysis.mining.artifacts.ArtifactFieldType;

/**
 * @author Alexander Seeliger on 10.12.2019.
 */
public class ReworkArtifactConfiguration extends ArtifactConfiguration {

    @ArtifactFieldDescription(
            name = "Rework activities",
            description = "",
            type = ArtifactFieldType.MULTI_COLUMN
    )
    private ReworkActivity[] reworkActivities;

    public ReworkActivity[] getReworkActivities() {
        return reworkActivities;
    }

    public void setReworkActivities(ReworkActivity[] reworkActivities) {
        this.reworkActivities = reworkActivities;
    }

}
