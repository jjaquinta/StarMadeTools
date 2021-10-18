package jo.sm.ui.act.file;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jo.sm.logic.StarMadeLogic;
import jo.sm.ui.RenderFrame;
import jo.sm.ui.act.GenericAction;
import jo.sm.ui.logic.ShipSpec;
import jo.sm.ui.logic.ShipTreeLogic;

@SuppressWarnings("serial")
public class OpenFileAction extends GenericAction
{
    private RenderFrame mFrame;
    
    public OpenFileAction(RenderFrame frame)
    {
        mFrame = frame;
        setName("Open File...");
        setToolTipText("Open a file on disk");
    }

    @Override
    public void actionPerformed(ActionEvent ev)
    {
        File dir;
        if (StarMadeLogic.getProps().containsKey("open.file.dir"))
            dir = new File(StarMadeLogic.getProps().getProperty("open.file.dir"));
        else
            dir = new File(System.getProperty("user.home"));
        JFileChooser chooser = new JFileChooser(dir);
        FileNameExtensionFilter filter1 = new FileNameExtensionFilter(
            "Starmade Ship File (*.smd2)", "smd2");
        FileNameExtensionFilter filter2 = new FileNameExtensionFilter(
                "Starmade Exported Ship File (*.sment)", "sment");
        chooser.addChoosableFileFilter(filter1);
        chooser.addChoosableFileFilter(filter2);
        chooser.setFileFilter(filter1);
        int returnVal = chooser.showOpenDialog(mFrame);
        if(returnVal != JFileChooser.APPROVE_OPTION)
            return;
       File smb2 = chooser.getSelectedFile();
       StarMadeLogic.getProps().setProperty("open.file.dir", smb2.getParent());
       StarMadeLogic.saveProps();
       ShipSpec spec = new ShipSpec();
       spec.setType(ShipSpec.FILE);
       spec.setFile(smb2);
       ShipTreeLogic.loadShip(spec, null);
       mFrame.getClient().getUndoer().clear();
    }

}
