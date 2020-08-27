package org.processexplorer.server.analysis.query.db;

import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.FunctionCall;

/**
 * @author Alexander Seeliger on 27.08.2020.
 */
public class PostgresFunctionCall {

    public static FunctionCall age() {
        return new FunctionCall(new CustomSql("AGE"));
    }

}
