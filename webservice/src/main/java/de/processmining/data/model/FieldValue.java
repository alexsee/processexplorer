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

package de.processmining.data.model;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class FieldValue {

    private String name;

    private Object value;

    public FieldValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public boolean equals(Object object) {
        if (object instanceof FieldValue) {
            FieldValue other = (FieldValue) object;

            if (!(other.getName().equals(getName()))) {
                return false;
            }

            if (other.getValue() == null && getValue() == null) {
                return true;
            }

            if (other.getValue() == null && getValue() != null || other.getValue() != null && getValue() == null) {
                return false;
            }

            return (other.getValue().equals(getValue()));
        }

        return false;
    }

    public int hashCode() {
        return (getValue() != null ? getValue().hashCode() : 0) + (getName() != null ? getName().hashCode() : 0);
    }

    @Override
    public String toString() {
        return getName() + "=" + getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
