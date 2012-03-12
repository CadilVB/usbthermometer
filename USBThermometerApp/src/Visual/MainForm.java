/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainForm.java
 *
 * Created on Jun 2, 2011, 10:17:06 AM
 */

package Visual;

import Visual.Help.HelpCtx;
import Visual.Tools.InternalFrameMenuItem;
import Visual.Tools.InternalFrameObservable;
import Visual.Tools.InternalFrameAllwaysOnTop;
import Visual.Tools.ExtensionFileFilter;
import USBThermometerLib.Device;
import Engine.Host;
import Engine.HostObserver;
import Engine.NullDevice;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import USBThermometerLib.Temperature;
import Main.Main;
import Tools.Configuration;
import Tools.ConfigurationManager;
import Tools.CharsetControl;
import Tools.TrayIconMouseAdapter;
import Tools.WorkspaceManager;
import Visual.DevicesTree.DevicesTree;
import Visual.Graph.GraphPanel;
import Visual.Tools.TileToolBox;
import Visual.Triggers.TriggersConfigurationDialog;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Paweł
 */
public class MainForm extends javax.swing.JFrame implements HostObserver {

    private DevicesTree devicesTree;
    private SearchProgressBar searchProgressBar;
    private List<Component> visualElements = new ArrayList<Component>();
    private TrayIcon trayIcon = null;

    private static MainForm instance;
    private final ResourceBundle bundle;

    private ConfigurationDialog configurationDialog;
    private TriggersConfigurationDialog tcp;
    private AboutBox aboutBox;

    public static synchronized MainForm getInstance() {
        if( instance == null ) {
            instance = new MainForm();
        }
        return instance;
    }

    public synchronized void restart() {
        if( instance != null ) {
            Main.getLocalhost().removeHostObserver(instance);

            if(configurationDialog != null) {
                configurationDialog.dispose();
            }

            if( tcp != null ) {
                tcp.dispose();
            }

            instance.removeFromSystemTray();

            WorkspaceManager.save();

            instance.setVisible(false);
            instance.dispose();
        }
        instance = new MainForm();
    }
		
    public void addToSystemTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("graphics/kni - ikona.png"));
            trayIcon.setToolTip(bundle.getString("NOT_READY_YET..."));
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    Configuration c = ConfigurationManager.load();

                    if (evt.getButton() == MouseEvent.BUTTON1) {
                        if (isVisible() && (getState() != JFrame.ICONIFIED) ) {
                            if( c.isCloseToTray() ) {
                                setVisible(false);
                            } else {
                                setState(JFrame.ICONIFIED);
                            }
                        } else {
                            setVisible(true);
                            setState(JFrame.NORMAL);
                            setAlwaysOnTop(true);
                            repaint();
                            setAlwaysOnTop(false);
                        }
                    }
                }
            });

            JMenuItem exitItem = new JMenuItem(bundle.getString("EXIT"));
            exitItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Main.exit();
                }
            });

            JMenuItem optionsItem = new JMenuItem(bundle.getString("OPTIONS..."));
            optionsItem.setIcon(new ImageIcon("graphics/wrench-screwdriver.png"));
            optionsItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(configurationDialog == null) {
                        configurationDialog = new ConfigurationDialog(MainForm.getInstance(),true);
                    }
                    configurationDialog.loadConfiguration();
                    configurationDialog.setVisible(true);
                }
            });

            JMenuItem triggersItem = new JMenuItem(bundle.getString("TRIGGERS..."));
            triggersItem.setIcon(new ImageIcon("graphics/gear.png"));
            triggersItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(tcp == null) {
                        tcp = new TriggersConfigurationDialog(MainForm.getInstance(),true);
                    }
                    tcp.setVisible(true);
                }
            });

            JMenuItem aboutItem = new JMenuItem(bundle.getString("ABOUT..."));
            aboutItem.setIcon(new ImageIcon("graphics/question-white.png"));
            aboutItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if( aboutBox == null ) {
                        aboutBox = new AboutBox(instance, true);
                    }
                    aboutBox.setVisible(true);
                }
            });

            JMenuItem showItem = new JMenuItem(bundle.getString("OPEN_USB_THERMOMETER"));
            showItem.setIcon(new ImageIcon("graphics/kni - ikona16.png"));
            showItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    setVisible(true);
                    setState(JFrame.NORMAL);
                }
            });

            //Add components to pop-up menu
            final JPopupMenu pop_up = new JPopupMenu();
            pop_up.add(showItem);
            pop_up.addSeparator();
            pop_up.add(optionsItem);
            pop_up.add(triggersItem);
            pop_up.add(aboutItem);
            pop_up.add(exitItem);

            trayIcon.addMouseListener(new TrayIconMouseAdapter(pop_up));

            updateTrayToolTip();

            try {
                tray.add(trayIcon);
            } catch (AWTException ex) {
                Logger.getLogger("USB Thermometer").log(Level.SEVERE, null, ex);
            }
        }
    }

    public void removeFromSystemTray() {
        SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
        trayIcon = null;
    }

    public boolean isInTray() {
        return ( trayIcon == null ? false : true );
    }

    /** Creates new form MainForm */
    private MainForm() {
        super("USB Thermometer");

        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        System.out.println(locale);
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        Configuration c = ConfigurationManager.load();

        setUILanguage();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        initComponents();

        devicesTree = new DevicesTree();
        searchProgressBar = new SearchProgressBar();

        jSplitPane1.setLeftComponent(devicesTree);

        jToolBar2.add( Box.createHorizontalGlue() );
        jToolBar2.add( SensorStatusLabel.getInstance() );
        jToolBar2.add( new JToolBar.Separator() );
        jToolBar2.add( new JLabel(" USBThermometer "));
        jToolBar2.add( searchProgressBar);
        jToolBar2.add( new JToolBar.Separator() );

        Main.getLocalhost().addHostObserver(this);

        if( c.isShowInTray() ) {
            addToSystemTray();
        }      

        WorkspaceManager.load(this);
        
        if (!c.isStartMinimized()) {
            setVisible(true);
        } else {
            if (c.isShowInTray()) {
                setVisible(false);
            } else {
                setState(JFrame.ICONIFIED);
                setVisible(true);
            }
        }        
    }

    public JDesktopPane getDesktopPanel() {
        return jDesktopPane1;
    }

    public List<Component> getVisualElemets() {
        return visualElements;
    }

    public void removeVisualElement(Component obj) {
        visualElements.remove(obj);
    }
    
    public void addVisualElement(Component obj) {
        visualElements.add(obj);

        if(obj instanceof InternalFrameObservable) {
            InternalFrameMenuItem menuItem = new InternalFrameMenuItem((InternalFrameObservable)obj);
            jMenu3.add(menuItem);
        }
    }

    public void setUILanguage() {
        UIManager.put("OptionPane.yesButtonText", bundle.getString("YES"));
        UIManager.put("OptionPane.noButtonText", bundle.getString("NO"));
        UIManager.put("OptionPane.cancelButtonText", bundle.getString("CANCEL"));
        UIManager.put("FileChooser.lookInLabelText",bundle.getString("LOOK_IN"));
        UIManager.put("FileChooser.filesOfTypeLabelText",bundle.getString("FILES_OF_TYPE"));
        UIManager.put("FileChooser.fileNameLabelText",bundle.getString("FILE_NAME"));
        UIManager.put("FileChooser.cancelButtonText",bundle.getString("CANCEL"));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage("graphics/kni - ikona16.png"));
        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jSplitPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jSplitPane1.setDividerLocation(200);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jDesktopPane1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.add(jDesktopPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel1);

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setPreferredSize(new java.awt.Dimension(100, 55));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new ImageIcon("graphics/btn_gear.png"));
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.setBorderPainted(false);
        jButton1.setFocusCycleRoot(true);
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMinimumSize(new java.awt.Dimension(48, 48));
        jButton1.setPreferredSize(new java.awt.Dimension(55, 55));
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new ImageIcon("graphics/btn_char.png"));
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton2.setBorderPainted(false);
        jButton2.setFocusCycleRoot(true);
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMinimumSize(new java.awt.Dimension(48, 48));
        jButton2.setPreferredSize(new java.awt.Dimension(55, 55));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new ImageIcon("graphics/btn_bar48.png"));
        jButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton3.setBorderPainted(false);
        jButton3.setFocusCycleRoot(true);
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setMinimumSize(new java.awt.Dimension(48, 48));
        jButton3.setPreferredSize(new java.awt.Dimension(55, 55));
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new ImageIcon("graphics/btn_table.png"));
        jButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton4.setBorderPainted(false);
        jButton4.setFocusCycleRoot(true);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new ImageIcon("graphics/btn_report.png"));
        jButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton5.setBorderPainted(false);
        jButton5.setFocusCycleRoot(true);
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new ImageIcon("graphics/btn_screw.png"));
        jButton6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton6.setBorderPainted(false);
        jButton6.setFocusCycleRoot(true);
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        jToolBar2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar2.setFloatable(false);

        jMenu1.setText(bundle.getString("FILE")); // NOI18N

        jMenuItem4.setIcon(new ImageIcon("graphics/document.png"));
        jMenuItem4.setLabel(bundle.getString("NEW_WORKSPACE")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);
        jMenu1.add(jSeparator2);

        jMenuItem6.setIcon(new ImageIcon("graphics/disk-black.png"));
        jMenuItem6.setLabel(bundle.getString("SAVE_WORKSPACE")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem7.setIcon(new ImageIcon("graphics/folder-horizontal-open.png"));
        jMenuItem7.setLabel(bundle.getString("LOAD_WORKSPACE")); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);
        jMenu1.add(jSeparator3);

        jMenuItem8.setText(bundle.getString("EXIT")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuBar1.add(jMenu1);

        jMenu5.setText(bundle.getString("TOOLS")); // NOI18N

        jMenuItem12.setIcon(new ImageIcon("graphics/system-monitor--plus.png"));
        jMenuItem12.setText(bundle.getString("GRAPH")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem12);

        jMenuItem13.setIcon(new ImageIcon("graphics/battery--plus.png"));
        jMenuItem13.setText(bundle.getString("BAR_GRAPH")); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        jMenuItem14.setIcon(new ImageIcon("graphics/table--plus.png"));
        jMenuItem14.setText(bundle.getString("TABLE")); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenuItem15.setIcon(new ImageIcon("graphics/report--plus.png"));
        jMenuItem15.setText(bundle.getString("REPORT")); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);
        jMenu5.add(jSeparator5);

        jMenuItem16.setIcon(new ImageIcon("graphics/gear.png"));
        jMenuItem16.setText(bundle.getString("ALERTS_AND_TRIGGERS")); // NOI18N
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem16);
        jMenu5.add(jSeparator6);

        jMenuItem11.setIcon(new ImageIcon("graphics/wrench-screwdriver.png"));
        jMenuItem11.setText(bundle.getString("SETTINGS")); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem11);

        jMenuBar1.add(jMenu5);

        jMenu3.setText(bundle.getString("WINDOW")); // NOI18N

        jMenuItem1.setIcon(new ImageIcon("graphics/application-tile.png"));
        jMenuItem1.setText(bundle.getString("TILE")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuItem5.setIcon(new ImageIcon("graphics/application-tile-horizontal.png"));
        jMenuItem5.setText(bundle.getString("TILE_HORIZONTALLY")); // NOI18N
        jMenuItem5.setToolTipText("");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem9.setIcon(new ImageIcon("graphics/application-tile-vertical.png"));
        jMenuItem9.setText(bundle.getString("TILE_VERTICALLY")); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);

        jMenuItem10.setIcon(new ImageIcon("graphics/applications-stack.png"));
        jMenuItem10.setText(bundle.getString("CASCADE")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);
        jMenu3.add(jSeparator4);

        jMenuBar1.add(jMenu3);

        jMenu4.setText(bundle.getString("HELP")); // NOI18N

        jMenuItem2.setIcon(new ImageIcon("graphics/book-open-text-image.png"));
        jMenuItem2.setText(bundle.getString("HELP_CONTENTS")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);
        jMenu4.add(jSeparator1);

        jMenuItem3.setIcon(new ImageIcon("graphics/question-white.png"));
        jMenuItem3.setText(bundle.getString("ABOUT")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem3);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        final InternalFrameObservable iFrame = new InternalFrameObservable(bundle.getString("GRAPH"),true,true);
        final GraphPanel graphPanel = new GraphPanel(iFrame);

        int h = jDesktopPane1.getHeight();

        iFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));
        iFrame.setSize(640,( h < 320 ? h : 320 ));
        iFrame.setMinimumSize(new Dimension(360,210));
        iFrame.add(graphPanel);
        iFrame.setVisible(true);
        iFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent arg0) {
               graphPanel.close();
            }
        });

        addVisualElement(iFrame);
        jDesktopPane1.add(iFrame);

        try {
            iFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(configurationDialog == null) {
            configurationDialog = new ConfigurationDialog(this,true);
        }
        configurationDialog.loadConfiguration();
        configurationDialog.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        final InternalFrameAllwaysOnTop topFrame = new InternalFrameAllwaysOnTop(bundle.getString("BAR_GRAPH"),true,true);
        topFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));
        topFrame.setSize(140,120);
        topFrame.setMaximumSize( new Dimension(BarPanel.MAX_WIDTH,Integer.MAX_VALUE));
        final BarPanel barPanel = new BarPanel(topFrame);
        topFrame.add(barPanel);
        topFrame.setVisible(true);
        jDesktopPane1.add(topFrame, 0);
        addVisualElement(topFrame);
        topFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent arg0) {
               barPanel.close();
            }
        });

        try {
            topFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        Configuration c = ConfigurationManager.load();
     
        if( c.isShowInTray() && c.isMinimizeToTray() ) {
            setVisible(false);
        } else {
            setState(JFrame.ICONIFIED);
        }
    }//GEN-LAST:event_formWindowIconified

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        String wd = System.getProperty("user.dir");
        JFileChooser fc = new JFileChooser(wd);
        FileFilter filter1 = new ExtensionFileFilter(bundle.getString("USB_THERMOMETER_WORKSPACE_FILES_(*.UTW)"), new String[] { "UTW" });
        fc.setFileFilter(filter1);
        int rc = fc.showDialog(null, bundle.getString("SAVE"));
        if (rc == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String filePath = f.getPath();
            if(!filePath.toLowerCase().endsWith(".utw"))
            {
                f = new File(filePath + ".utw");
            }
            String filename = f.getAbsolutePath();
            WorkspaceManager.save(filename);
        }
        return;
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        new Thread(new Runnable() {
            @Override
            public void run() {
                WorkspaceManager.clear();
            }
        }).start();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
        Main.exit();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        String wd = System.getProperty("user.dir");
        JFileChooser fc = new JFileChooser(wd);
        FileFilter filter1 = new ExtensionFileFilter(bundle.getString("USB_THERMOMETER_WORKSPACE_FILES_(*.UTW)"), new String[] { "UTW" });
        fc.setFileFilter(filter1);
        int rc = fc.showDialog(null, bundle.getString("OPEN"));
        if (rc == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            final String filename = file.getAbsolutePath();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WorkspaceManager.clear();
                    WorkspaceManager.load(filename);
                }
            }).start();
        }
        return;
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        TileToolBox.tile(jDesktopPane1, 0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        TileToolBox.tileVertical(jDesktopPane1, 0);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        TileToolBox.tileHorizontal(jDesktopPane1, 0);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        TileToolBox.cascade(jDesktopPane1, 0);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        final InternalFrameObservable iFrame = new InternalFrameObservable(bundle.getString("TABLE"),true,true);
        final TablePanel tablePanel = new TablePanel(iFrame);

        int h = jDesktopPane1.getHeight();

        iFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));
        iFrame.setSize(320,( h < 640 ? h : 640 ));
        iFrame.setMinimumSize(new Dimension(255,120));
        iFrame.add(tablePanel);
        iFrame.setVisible(true);
        iFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent arg0) {
               tablePanel.close();
            }
        });

        addVisualElement(iFrame);
        jDesktopPane1.add(iFrame);

        try {
            iFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        final InternalFrameObservable iFrame = new InternalFrameObservable(bundle.getString("REPORT"),true,true);
        final ReportPanel reportPanel = new ReportPanel(iFrame);

        int h = jDesktopPane1.getHeight();
        
        iFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));
        iFrame.setSize(800,( h < 800 ? h : 800 ));
        iFrame.setMinimumSize(new Dimension(360,210));
        iFrame.add(reportPanel);
        iFrame.setVisible(true);
        iFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent arg0) {
               reportPanel.close();
            }
        });

        addVisualElement(iFrame);
        jDesktopPane1.add(iFrame);

        try {
            iFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if( tcp == null ) {
            tcp = new TriggersConfigurationDialog(this, true);
        }
        tcp.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Configuration c = ConfigurationManager.load();

        if( c.isShowInTray() && c.isCloseToTray() ) {
            setVisible(false);
        } else {
            Main.exit();
        }
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        HelpCtx help = new HelpCtx(this, true);
        help.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        if (aboutBox == null) {
            aboutBox = new AboutBox(instance, true);
        }
        aboutBox.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables

    private void updateTrayToolTip() {
        if (trayIcon != null) {
            StringBuilder sb = new StringBuilder();
            for (Device device : Main.getLocalhost().getDevices()) {
                for (Sensor sensor : device.getSensors()) {
                    sb.append(sensor.getName());
                    sb.append(": ");
                    if ( (device instanceof NullDevice) || (sensor.getLastSample() == null)) {
                        sb.append("-\n");
                    } else {
                        sb.append(sensor.getLastSample().getValue());
                        sb.append(Temperature.getUnitString());
                        sb.append("\n");
                    }
                }
            }
            // delete last "new line" char
            if( sb.length() != 0 ) {
                sb.delete(sb.length() - 1, sb.length());
            }
            trayIcon.setToolTip(sb.toString());
        }
    }

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
        updateTrayToolTip();
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
        updateTrayToolTip();
    }

    @Override
    public void notiffyDeviceUpdated(Host host, Device device) {
        updateTrayToolTip();
    }

    @Override
    public void notiffyDeviceAdd(Host host, Device device) {
        updateTrayToolTip();
    }

    @Override
    public void notiffyDeviceRemoved(Host host, Device device) {
        updateTrayToolTip();
    }
}
