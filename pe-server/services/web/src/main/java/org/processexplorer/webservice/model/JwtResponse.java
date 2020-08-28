package org.processexplorer.webservice.model;

import java.io.Serializable;

/**
 * @author Alexander Seeliger on 28.08.2020.
 */
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}