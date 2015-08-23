import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ConstructorWindow extends JFrame implements MouseListener{
	
	//Making public instance of the main window to make an access to it easier
	public static final ConstructorWindow instance = new ConstructorWindow();
	
	public static final Globals globals = new Globals();
		
	public static GOManagerWindow goManager;
	
	public ToolsPanel toolsPanel;
	
	private ConstructorWindow(){
		super("Mending Rush - level constructor");
		setSize(1050,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		addMouseListener(this);
		
		goManager= new GOManagerWindow();
		setJMenuBar(new ConstructorMenuBar());
		
		toolsPanel=new ToolsPanel();
		
		add(toolsPanel,BorderLayout.LINE_END);
		
		setVisible(true);
	}

	public static void main(String[] args) {}

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
