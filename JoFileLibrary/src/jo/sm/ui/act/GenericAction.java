package jo.sm.ui.act;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public abstract class GenericAction extends AbstractAction
{
    public void setName(String name)
    {
        putValue(NAME, name);
    }
    public String getName()
    {
        return (String)getValue(NAME);
    }
    public void setToolTipText(String toolTipText)
    {
        putValue(SHORT_DESCRIPTION, toolTipText);
    }
    public String getToolTipText()
    {
        return (String)getValue(SHORT_DESCRIPTION);
    }
    public void setChecked(boolean checked)
    {
        putValue(SELECTED_KEY, checked ? "check" : null);
    }
    public boolean getChecked()
    {
        return (getValue(SELECTED_KEY) != null);
    }
}
