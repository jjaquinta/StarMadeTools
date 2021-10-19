package jo.sm.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BegPanel extends JPanel
{
    private static final int TICK = 200;
    private static final int CHOP = 120;

    public static final String THE_RAIDERS_LAMENT_AUDIO = "http://podiobooks.com/title/the-raiders-lament";
    public static final String SMASHWORDS_BOOKS = "https://www.smashwords.com/profile/view/jjaquinta";
    public static final String AMAZON_BOOKS = "https://www.amazon.com/s?i=digital-text&rh=p_27%3AJolie+Jaquinta&s=relevancerank";
    public static final String DOCUMENTATION = "http://www.starmadewiki.com/wiki/SMEdit";
    
    private int mMessageOffset;
    private int mRepeats;
    
    private JLabel  mStatus;
    private JButton mAudioBook;
    private JButton mPaidBook;
    private JButton mFreeBook;
    
    public BegPanel()
    {
        mRepeats = 3;
        // instantiate
        mStatus = new JLabel(MESSAGE.substring(0, CHOP));
        setBackground(Color.cyan);
        mAudioBook = new JButton("Audiobook");
        mPaidBook = new JButton("E-book (sponsored)");
        mFreeBook = new JButton("E-book (free)");
        Dimension d1 = mAudioBook.getPreferredSize();
        Dimension d2 = mPaidBook.getPreferredSize();
        mStatus.setPreferredSize(new Dimension(1024 - d1.width - d2.width, Math.max(d1.height, d2.height)));
        // layout
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add("Center", mStatus);
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 3));
        buttons.add(mAudioBook);
        buttons.add(mPaidBook);
        buttons.add(mFreeBook);
        add("East", buttons);
        // link
        Thread t = new Thread("beg_ticker") { public void run() { doTicker(); } };
        t.setDaemon(true);
        t.start();
        mPaidBook.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev)
            {
                doGoto(AMAZON_BOOKS);
            }            
        });
        mAudioBook.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev)
            {
                doGoto(THE_RAIDERS_LAMENT_AUDIO);
            }            
        });
        mFreeBook.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev)
            {
                doGoto(SMASHWORDS_BOOKS);
            }            
        });
    }
    
    private void doTicker()
    {
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
        }
        for (;;)
        {
            try
            {
                Thread.sleep(TICK);
            }
            catch (InterruptedException e)
            {
            }
            mMessageOffset++;
            if (mMessageOffset == MESSAGE.length())
            {
                mMessageOffset = 0;
                mRepeats--;
                if (mRepeats < 0)
                    return;
            }
            String msg = MESSAGE.substring(mMessageOffset) + MESSAGE.substring(0, mMessageOffset);
            msg = msg.substring(0, CHOP);
            mStatus.setText(msg);
        }
    }
    
    private void doGoto(String url)
    {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Action.BROWSE))
                try {
                    desktop.browse(URI.create(url));
                    return;
                } catch (IOException e) {
                    // handled below
                }
        }
    }

    public static final String MESSAGE = "This software is made freely available with no charge or limitation. "
            + "Even the source is included. "
            + "It was originally distributed as \"begware\", promoting my book \"The Raider's Lament\". "
            + "I wanted enough downloads/sales to earn enough to buy a Minecraft Lego kit for my daughter. "
            + "The good people of this community helped me reach the target, and a huge shout out to Kahulbane"
            + " for donating quite a bit of it! "
            + "So I've now officially considered this software 'paid for' and have removed the begware nagger. "
            + "If you are interested you can still download my book. I'd appreciate it, even more if you read it "
            + "and review it. You can still choose to donate by buying the sponsored book. But, like this software, "
            + "there will always be a free version available. "
            + "Further proceeds will go towards buying "
            + "the other Minecraft kits for my daughters! "
            + "The buttons below will take you to the audiobook page (free), the sponsored ebook ($0.99), and the"
            + "free eBook. "
            + "Thank you for using and supporting SMEdit. ";
}
