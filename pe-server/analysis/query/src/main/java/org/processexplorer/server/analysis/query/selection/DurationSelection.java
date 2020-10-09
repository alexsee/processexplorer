package org.processexplorer.server.analysis.query.selection;

import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.analysis.query.codes.EventAttributeCodes;
import org.processexplorer.server.common.persistence.repository.EventLogAnnotationRepository;

import java.util.List;

/**
 * @author Alexander Seeliger on 09.10.2020.
 */
public class DurationSelection extends Selection {

    @Override
    public Object getSelection(DatabaseModel db) {
        return FunctionCall.avg().addCustomParams(new CustomSql("EXTRACT(EPOCH FROM " + db.caseDurationCol.getColumnNameSQL() + ")"));
    }

    @Override
    public String getName() {
        return "\"Case duration\"";
    }

    @Override
    public List<EventAttributeCodes> getCodes(EventLogAnnotationRepository repository, String logName) {
        return null;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public String getType() {
        return "time";
    }
}
