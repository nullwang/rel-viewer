package com.b.g;

import com.b.a.Parameter;
import com.b.a.ViewerEvent;
import com.b.u.ParameterUtil;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by www on 2014/5/25.
 */


public class TestRelViewer {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = frame.getContentPane();
        final RelViewer relViewer = new RelViewer();

        //relViewer.setCodeBase(RelViewer.class.getResource("/"));
        relViewer.setAnimateLayout(ParameterUtil.getBoolean(Parameter.LAYOUT_ANIMATION, true));

        relViewer.init();
        relViewer.start();

        //relViewer.mergeRel("rel.xml");
        content.add(relViewer);

        java.util.Timer ts = new java.util.Timer();
        ts.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        String str = relViewer.getEvent();
                        if (str == null) return;
                        String[] e = str.split(":");
                        if (e.length > 0) {
                            int num = Integer.parseInt(e[0]);
                            if (num == ViewerEvent.EVENT_LAYOUTDONE) {
                                relViewer.showWaiting(false);
                                relViewer.scrollCenter();
                            }
                            if (num == ViewerEvent.EVENT_FILEDONE)
                                relViewer.showWaiting(false);
                        }
                    }
                }
                , 1, 500
        );

        //relViewer.showWaiting(true);
        frame.pack();
        frame.setVisible(true);
    }
}
