import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class ConstructorWindow extends JFrame implements MouseListener{
	
	//Making public instance of the main window to make an access to it easier
	public static final ConstructorWindow instance = new ConstructorWindow();
	
	public static final Globals globals = new Globals();
		
	public static GOManagerWindow goManager;
	
	public CollectionsPanel collectionsPanel;
	public Workspace workspace;
	public JScrollPane workspaceContainer;
	public ToolsPanel toolsPanel;
	public ConstructorMenuBar menuBar;
	
	private ConstructorWindow(){
		super("Mending Rush - level constructor");
		setMinimumSize(new Dimension(1050,700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		addMouseListener(this);
		
		goManager= new GOManagerWindow();
		
		menuBar=new ConstructorMenuBar();
		setJMenuBar(menuBar);
		
		toolsPanel = new ToolsPanel();
		collectionsPanel=new CollectionsPanel();
		workspace = new Workspace();
		workspaceContainer=new JScrollPane(workspace);
		workspaceContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		workspaceContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(toolsPanel,BorderLayout.LINE_START);
		add(workspaceContainer,BorderLayout.CENTER);
		add(collectionsPanel,BorderLayout.LINE_END);
		
		setWorkingState(false);
		
		setVisible(true);
	}

	public static void main(String[] args) {}

	public void setWorkingState(boolean flag){
		menuBar.setWorkingState(flag);
		toolsPanel.setWorkingState(flag);
	}
	
	@Override
	public void mouseClicked(MouseEvent evt) {}

	@Override
	public void mouseEntered(MouseEvent evt) {}

	@Override
	public void mouseExited(MouseEvent evt) {}

	@Override 
	public void mousePressed(MouseEvent evt) {}

	@Override 
	public void mouseReleased(MouseEvent evt) {}

}
