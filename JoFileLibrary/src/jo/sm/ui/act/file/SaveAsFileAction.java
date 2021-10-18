package jo.sm.ui.act.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jo.sm.data.SparseMatrix;
import jo.sm.logic.RunnableLogic;
import jo.sm.logic.StarMadeLogic;
import jo.sm.mods.IPluginCallback;
import jo.sm.mods.IRunnableWithProgress;
import jo.sm.ship.data.Block;
import jo.sm.ship.data.Data;
import jo.sm.ship.logic.DataLogic;
import jo.sm.ship.logic.ShipLogic;
import jo.sm.ui.RenderFrame;
import jo.sm.ui.act.GenericAction;
import jo.sm.ui.logic.ShipSpec;
import jo.vecmath.Point3i;

@SuppressWarnings("serial")
public class SaveAsFileAction extends GenericAction
{
    private RenderFrame mFrame;
    
    public SaveAsFileAction(RenderFrame frame)
    {
        mFrame = frame;
        setName("File...");
        setToolTipText("Save as smb2 file");
    }

    @Override
    public void actionPerformed(ActionEvent ev)
    {
        if (StarMadeLogic.getInstance().getCurrentModel() == null)
            return;
        File dir;
        if (StarMadeLogic.getProps().contains("open.file.dir"))
            dir = new File(StarMadeLogic.getProps().getProperty("open.file.dir"));
        else
            dir = StarMadeLogic.getInstance().getBaseDir();
        JFileChooser chooser = new JFileChooser(StarMadeLogic.getInstance().getBaseDir());
        chooser.setSelectedFile(dir);
        FileNameExtensionFilter filter1 = new FileNameExtensionFilter(
            "Starmade Ship File", "smd2");
        //FileNameExtensionFilter filter2 = new FileNameExtensionFilter(
        //        "Starmade Exported Ship File", "sment");
        chooser.addChoosableFileFilter(filter1);
        //chooser.addChoosableFileFilter(filter2);
        int returnVal = chooser.showOpenDialog(mFrame);
        if(returnVal != JFileChooser.APPROVE_OPTION)
            return;
        final File smb2 = chooser.getSelectedFile();
        StarMadeLogic.getProps().setProperty("open.file.dir", smb2.getParent());
        StarMadeLogic.saveProps();
        String name = smb2.getName();
        final ShipSpec spec = new ShipSpec();
        spec.setType(ShipSpec.FILE);
        spec.setName(name);
        spec.setFile(smb2);
        SparseMatrix<Block> grid = StarMadeLogic.getModel();
        Map<Point3i, Data> data = ShipLogic.getData(grid);
        final Point3i p = new Point3i();
        final Data d = data.get(p);
        if (d == null)
            throw new IllegalArgumentException("No core element to ship!");
        IRunnableWithProgress t = new IRunnableWithProgress() {
			@Override
			public void run(IPluginCallback cb)
			{
		        try
		        {
		            DataLogic.writeFile(p, d, new FileOutputStream(smb2), true, cb);
		        }
		        catch (IOException e)
		        {
		            throw new IllegalStateException("Cannot save to '"+spec.getFile()+"'", e);
		        }
		        StarMadeLogic.getInstance().setCurrentModel(spec);
			}
        };
        RunnableLogic.run(mFrame, name, t);
    }
}
