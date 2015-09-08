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
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConstructorMenuBar extends JMenuBar{
	
	private static final long serialVersionUID = 8899029156806897059L;
	
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
		fileChooser.setCurrentDirectory(new File(Globals.LEVELS_FOLDER));
		
		levelMenu = generateFileMenu();
		workspaceMenu = generateWorkspaceMenu();
		tools=generateToolsMenu();
		
		this.add(levelMenu);
		this.add(workspaceMenu);
		this.add(tools);
	}
	
	private JMenu generateFileMenu(){
		JMenu menu = new JMenu("Level");
		menu.setFont(Globals.DEFAULT_FONT);
		
		//Creating "New level" menu item
		newLevel = new JMenuItem("New");
		newLevel.setFont(Globals.DEFAULT_FONT);
		newLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onNewLevel_Click();
			}
		});
		menu.add(newLevel);
		
		//Creating "Save level" menu item
		saveLevel = new JMenuItem("Save");
		saveLevel.setFont(Globals.DEFAULT_FONT);
		saveLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onSaveLevel_Click();
			}
		});
		menu.add(saveLevel);
		
		//Creating "Load level" menu item
		loadLevel = new JMenuItem("Load");
		loadLevel.setFont(Globals.DEFAULT_FONT);
		loadLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onLoadLevel_Click();
			}
		});
		menu.add(loadLevel);
		
		menu.addSeparator();
		
		//Creating "Test level" menu item
		runLevel = new JMenuItem("Run in emulator");
		runLevel.setFont(Globals.DEFAULT_FONT);
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
		menu.setFont(Globals.DEFAULT_FONT);
		
		showGrid = new JCheckBoxMenuItem("Show grid");
		//showGrid.setEnabled(false);
		showGrid.setFont(Globals.DEFAULT_FONT);
		showGrid.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  ConstructorWindow.instance.workspace.showGrid(showGrid.isSelected());
		      }
		    });
		
		showObjectsBorder = new JCheckBoxMenuItem("Show objects border");
		//showObjectsBorder.setEnabled(false);
		showObjectsBorder.setFont(Globals.DEFAULT_FONT);
		showObjectsBorder.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  ConstructorWindow.instance.workspace.showObjectBorder(showObjectsBorder.isSelected());
		      }
		    });
		
		showTileIndexes = new JCheckBoxMenuItem("Show tile indexes");
		//showTileIndexes.setEnabled(false);
		showTileIndexes.setFont(Globals.DEFAULT_FONT);
		showTileIndexes.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		    	  ConstructorWindow.instance.workspace.showTileIndex(showTileIndexes.isSelected());
		      }
		    });
		
		//Creating "Test level" menu item
		workspaceParameters = new JMenuItem("Parameters");
		workspaceParameters.setFont(Globals.DEFAULT_FONT);
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
		menu.setFont(Globals.DEFAULT_FONT);
		
		JMenuItem goManager = new JMenuItem("Game Object Manager");
		goManager.setFont(Globals.DEFAULT_FONT);
		goManager.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ConstructorWindow.goManager.setVisible(true);
			}
		});
		
		menu.add(goManager);
		
		return menu;
	}
	
	public void setWorkingState(boolean flag){
		showTileIndexes.setEnabled(flag);
		showObjectsBorder.setEnabled(flag);
		showGrid.setEnabled(flag);
		saveLevel.setEnabled(flag);
		if(flag){
			
		}
		else{
			showTileIndexes.setSelected(false);
			showObjectsBorder.setSelected(false);
			showGrid.setSelected(false);
		}
	}
	
	private void onNewLevel_Click(){
		//new NewLevelDialog();
		ConstructorWindow.instance.workspace.setLevel(new Level(30,12));
		ConstructorWindow.instance.setWorkingState(true);
		//showGrid.setSelected(true);
		//showObjectsBorder.setSelected(true);
	}
	
	private void onSaveLevel_Click(){
		String fileAddress = ConstructorWindow.globals.level.getFileAddress();
		if(fileAddress==null){
			int returnValue = fileChooser.showSaveDialog(ConstructorWindow.instance);
			if (returnValue == JFileChooser.APPROVE_OPTION) {	
				File selectedFile = fileChooser.getSelectedFile();
				fileAddress = selectedFile.getAbsolutePath();
	          
				new JOptionPane();
				if (selectedFile.exists()){
					int n = JOptionPane.showConfirmDialog(
	      			  	ConstructorWindow.instance,
	      			    "File already exists. Do you really want to override it?",
	      			    "Message",
	      			    JOptionPane.YES_NO_OPTION);
	        	  	if(n!=0)
	        	  		return;
				}
			}
			else return;
		}
		
		boolean operationSuccess = Globals.xmlConverter.saveLevel(fileAddress);
        if(operationSuccess)
      	  JOptionPane.showMessageDialog(ConstructorWindow.instance, "File successfully saved");
        else
      	  JOptionPane.showMessageDialog(ConstructorWindow.instance, "Couldn't save the file","Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void onLoadLevel_Click(){
		int returnValue = fileChooser.showOpenDialog(ConstructorWindow.instance);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        String fileAddress = selectedFile.getAbsolutePath();
	          
	        Level level = Globals.xmlConverter.loadLevel(fileAddress);
	        if(level==null){
	        	JOptionPane.showMessageDialog(ConstructorWindow.instance, "File couldn't be opened","Error", JOptionPane.ERROR_MESSAGE);  
	        	  return;
	        }
	        ConstructorWindow.instance.workspace.setLevel(level);
	        ConstructorWindow.instance.setWorkingState(true);
	  		showGrid.setSelected(true);
	  		showObjectsBorder.setSelected(true);
		}
	}
	
	private void onRunLevel_Click(){
		//ConstructorWindow.instance.dispose();
	}
}
