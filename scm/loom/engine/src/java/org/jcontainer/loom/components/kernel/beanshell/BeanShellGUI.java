/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 * This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/).
 */
package org.jcontainer.loom.components.kernel.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.jcontainer.loom.interfaces.Kernel;

/**
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class BeanShellGUI
    extends JPanel
    implements ActionListener
{
    private final JConsole m_jConsole;

    private final Interpreter m_interpreter;

    private Thread m_thread;

    private JFrame m_frame;

    /**
     * Construct a BeanShellGUI with a handle on the Kernel.
     */
    public BeanShellGUI( final Kernel kernel )
    {
        setPreferredSize( new Dimension( 600, 480 ) );

        m_jConsole = new JConsole();

        this.setLayout( new BorderLayout() );
        this.add( m_jConsole, BorderLayout.CENTER );

        m_interpreter = new Interpreter( m_jConsole );
        try
        {
            m_interpreter.set( "phoenix-kernel", kernel );
        }
        catch( EvalError ee )
        {
            ee.printStackTrace();
        }
    }

    /**
     * Initialize after construction.
     *
     */
    public void init()
    {
        m_frame = new JFrame( "BeanShell - Phoenix management" );
        m_frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        m_frame.getContentPane().add( this, BorderLayout.CENTER );

        final JMenuBar menubar = new JMenuBar();
        final JMenu menu = new JMenu( "File" );
        final JMenuItem mi = new JMenuItem( "Close" );

        mi.addActionListener( this );
        menu.add( mi );
        menubar.add( menu );

        m_frame.setJMenuBar( menubar );

        m_thread = new Thread( m_interpreter );

        m_thread.start();
        m_frame.setVisible( true );
        m_frame.pack();
    }

    /**
     * Method actionPerformed by the menu options.
     *
     * @param event the action event.
     *
     */
    public void actionPerformed( final ActionEvent event )
    {
        final String command = event.getActionCommand();

        if( command.equals( "Close" ) )
        {
            m_thread.interrupt();
            m_frame.dispose();
        }
    }
}
