import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ConstructorMenuBar extends JMenuBar{
	
	private final Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN,11);
	
	private JMenu levelMenu;
	private JMenuItem newLevel;
	private JMenuItem saveLevel;
	private JMenuItem loadLevel;
	private JMenuItem runLevel;
	
	private JMenu workspaceMenu;
	private JCheckBoxMenuItem showGrid;
	private JCheckBoxMenuItem showObjectsBorder;
	private JCheckBoxMenuItem showTileIndexes;
	private JMenuItem workspaceParameters;
	
	private JMenu tools;
	
	final private JFileChooser fileChooser;

	public ConstructorMenuBar(){
		fileChooser=new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file", new String[] {"xml"});
		fileChooser.setFileFilter(filter);
		
		levelMenu = generateFileMenu();
		workspaceMenu = generateWorkspaceMenu();
		tools=generateToolsMenu();
		
		this.add(levelMenu);
		this.add(workspaceMenu);
		this.add(tools);
	}
	
	private JMenu generateFileMenu(){
		JMenu menu = new JMenu("Level");
		menu.setFont(DEFAULT_FONT);
		
		//Creating "New level" menu item
		newLevel = new JMenuItem("New level");
		newLevel.setFont(DEFAULT_FONT);
		newLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onNewLevel_Click();
			}
		});
		menu.add(newLevel);
		
		//Creating "Save level" menu item
		saveLevel = new JMenuItem("Save level");
		saveLevel.setFont(DEFAULT_FONT);
		saveLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onSaveLevel_Click();
			}
		});
		menu.add(saveLevel);
		
		//Creating "Load level" menu item
		loadLevel = new JMenuItem("Load level");
		loadLevel.setFont(DEFAULT_FONT);
		loadLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onLoadLevel_Click();
			}
		});
		menu.add(loadLevel);
		
		menu.addSeparator();
		
		//Creating "Test level" menu item
		runLevel = new JMenuItem("Run");
		runLevel.setFont(DEFAULT_FONT);
		runLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onRunLevel_Click();
			}
		});
		menu.add(runLevel);
		
		return menu;
	}
	
	private JMenu generateWorkspaceMenu(){
		JMenu menu = new JMenu("Workspace");
		menu.setFont(DEFAULT_FONT);
		
		showGrid = new JCheckBoxMenuItem("Show grid");
		//showGrid.setEnabled(false);
		showGrid.setFont(DEFAULT_FONT);
		showGrid.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  
		      }
		    });
		
		showObjectsBorder = new JCheckBoxMenuItem("Show objects border");
		//showObjectsBorder.setEnabled(false);
		showObjectsBorder.setFont(DEFAULT_FONT);
		showObjectsBorder.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  
		      }
		    });
		
		showTileIndexes = new JCheckBoxMenuItem("Show tile indexes");
		//showTileIndexes.setEnabled(false);
		showTileIndexes.setFont(DEFAULT_FONT);
		showTileIndexes.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  
		      }
		    });
		
		//Creating "Test level" menu item
		workspaceParameters = new JMenuItem("Parameters");
		workspaceParameters.setFont(DEFAULT_FONT);
		workspaceParameters.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//onRunLevel_Click();
			}
		});
		
		menu.add(showTileIndexes);
		menu.add(showGrid);
		menu.add(showObjectsBorder);
		menu.addSeparator();
		menu.add(workspaceParameters);
		return menu;
	}
	
	private JMenu generateToolsMenu(){
		JMenu menu = new JMenu("Tools");
		menu.setFont(DEFAULT_FONT);
		
		JMenuItem goManager = new JMenuItem("Game Object Manager");
		goManager.setFont(DEFAULT_FONT);
		goManager.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ConstructorWindow.instance.goManager.setVisible(true);
			}
		});
		
		menu.add(goManager);
		
		return menu;
	}
	
	private void onNewLevel_Click(){
		ConstructorWindow.instance.revalidate();
	}
	
	private void onSaveLevel_Click(){
		int returnValue = fileChooser.showSaveDialog(ConstructorWindow.instance);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File selectedFile = fileChooser.getSelectedFile();
	          String fileAddress = selectedFile.getAbsolutePath();
	    }
		
		
	}
	
	private void onLoadLevel_Click(){
		//ConstructorWindow.instance.dispose();
	}
	
	private void onRunLevel_Click(){
		//ConstructorWindow.instance.dispose();
	}
}
