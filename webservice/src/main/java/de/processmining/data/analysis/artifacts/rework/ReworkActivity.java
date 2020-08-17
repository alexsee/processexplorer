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

package de.processmining.data.analysis.artifacts.rework;

import de.processmining.data.analysis.artifacts.ArtifactFieldDescription;
import de.processmining.data.analysis.artifacts.ArtifactFieldType;

/**
 * @author Alexander Seeliger on 11.12.2019.
 */
public class ReworkActivity {

    @ArtifactFieldDescription(
            name = "Rework Activity",
            description = "Specify the rework activities.",
            type = ArtifactFieldType.ACTIVITY
    )
    private Integer activity;

    @ArtifactFieldDescription(
            name = "Minimum occurrence",
            description = "Specifies the minimum occurrence of a rework activity within a case.",
            type = ArtifactFieldType.NUMBER
    )
    private int min;

    @ArtifactFieldDescription(
            name = "Maximum occurrence",
            description = "Specifies the maximum occurrence of a rework activity within a case.",
            type = ArtifactFieldType.NUMBER
    )
    private int max;


    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}