package com.b.t;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class TestCubicCurve extends Applet {
	
	protected JLabel messageLabel;
	protected JPanel messagePanel;
	
	@Override
	  public void init()
	{
		initMessage();
		this.setLayout(new GridBagLayout());
		messagePanel.setBorder(new LineBorder(Color.BLACK));
		
		this.add(messagePanel, new GridBagConstraints());		
	}
	
	private void initMessage()
	{
		String str = "���Ժ�...";
		
		messageLabel = new JLabel(str);
		messageLabel.setAlignmentX(CENTER_ALIGNMENT);
		messageLabel.setAlignmentY(CENTER_ALIGNMENT);
		messageLabel.setFont(new Font("Helvetica", 1, 14));
		messagePanel = new JPanel(new BorderLayout());
		
		messagePanel.add("North",messageLabel);
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
//		CubicCurve2D cubicCurve = new CubicCurve2D.Double();
//		cubicCurve.setCurve(10, 100, 30, 80, 80, 80, 100, 100);
//		
//		
//		Graphics2D g2d = (Graphics2D) g;
//		
//		g2d.draw(cubicCurve);
		
		GeneralPath gp = new GeneralPath();
		
		gp.moveTo(0, 0);
		gp.lineTo(50, 50);
		gp.lineTo(0, 0);
		
		gp.moveTo(20, 10);
		gp.lineTo(10, 10);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.draw(gp);
		
	}
	
	public static void main(String[] args) {
	    JFrame frame = new JFrame();
	    frame.setLayout(new GridBagLayout());
	    JPanel panel = new JPanel();
	    panel.add(new JLabel("This is a label"));
	    panel.setBorder(new LineBorder(Color.BLACK)); // make it easy to see
	    frame.add(panel, new GridBagConstraints());
	    frame.setSize(400, 400);
	    frame.setLocationRelativeTo(null);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	}

	
	public static void main1(String[] args)
	{
		System.out.println(Math.atan2(1, 1)/Math.PI);
		
		System.out.println(Math.atan2(-1, 1)/Math.PI);
	}

}
