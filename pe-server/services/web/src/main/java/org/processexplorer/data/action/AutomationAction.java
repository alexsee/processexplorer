package org.processexplorer.data.action;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
public class AutomationAction {

    private long id;

    private String actionName;

    private String actionType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
