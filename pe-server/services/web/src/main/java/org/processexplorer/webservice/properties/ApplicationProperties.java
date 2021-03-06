/*
 * ProcessExplorer
 * Copyright (C) 2020  Alexander Seeliger
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

package org.processexplorer.webservice.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Alexander Seeliger on 10.08.2020.
 */
@ConfigurationProperties("application")
public class ApplicationProperties {

    private String aprilBaseUri = "http://localhost:5000";


    public String getAprilBaseUri() {
        return aprilBaseUri;
    }

    public void setAprilBaseUri(String aprilBaseUri) {
        this.aprilBaseUri = aprilBaseUri;
    }
}
