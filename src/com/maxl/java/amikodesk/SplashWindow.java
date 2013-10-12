/*
Copyright (c) 2013 Max Lungarella <cybrmx@gmail.com>

This file is part of AmiKoDesk for Windows.

AmiKoDesk is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.maxl.java.amikodesk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class SplashWindow extends JWindow {
	
	private int m_duration = 0;
	private String m_app_name = "";
	
	public SplashWindow(String app_name, int duration) {
		m_duration = duration;
		m_app_name = app_name;
		showSplashAndExit();
	}
	
	public void showSplash() {
        JPanel content = (JPanel)getContentPane();
        Color color = new Color(255,200,200,255);
        content.setBackground(color);
        
        // Set the window's bounds, centering the window
        int width = 512;
        int height = 550;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);
        
        // Build the splash screen
        if (m_app_name.equals("AmiKo Desktop Desitin") || m_app_name.equals("CoMed Desktop Desitin")) {
            JLabel image = new JLabel(new ImageIcon("./images/desitin.jpg"));
            JLabel copyrt = new JLabel
            		("<html><center><br>" + m_app_name + "<br>" +
            				"Copyright � 2013 ywesee GmbH<br>" +
            				"-" +
            				"</center></html>", JLabel.CENTER);
            copyrt.setFont(new Font("Dialog", Font.PLAIN, 14));
            content.add(copyrt, BorderLayout.SOUTH);
            content.add(image, BorderLayout.CENTER);
		} else if (m_app_name.equals("med-drugs desktop") || m_app_name.equals("med-drugs-fr desktop")) {
            JLabel image = new JLabel(new ImageIcon("./images/meddrugs.png"));
            content.add(image, BorderLayout.CENTER);
		} else if (m_app_name.equals("AmiKo Desktop ZR") || m_app_name.equals("CoMed Desktop ZR")) {
            JLabel image = new JLabel(new ImageIcon("./images/zurrose.png"));
            content.add(image, BorderLayout.CENTER);			
		}

        content.setBorder(BorderFactory.createLineBorder(color));
        this.toFront();
        
        // Display it
        setVisible(true);
        
        // Wait a little while, maybe while loading resources
        try { 
        	Thread.sleep(m_duration); 
        } catch (Exception e) 
        {
        	// 
        }
        
        setVisible(false);
	}
	
	public void showSplashAndExit() {
		showSplash();
	}
}