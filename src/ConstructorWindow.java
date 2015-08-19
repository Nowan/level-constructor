import java.awt.BorderLayout;
import java.awt.Color;
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
	
	private ConstructorWindow(){
		super("Mending Rush - level constructor");
		setSize(1200,750);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		addMouseListener(this);
		
		setJMenuBar(new ConstructorMenuBar());
		
		setVisible(true);
	}

	public static void main(String[] args) {
	}

	@Override
	public void mouseClicked(MouseEvent evt) {}

	@Override
	public void mouseEntered(MouseEvent evt) {}

	@Override
	public void mouseExited(MouseEvent evt) {}

	@Override 
	public void mousePressed(MouseEvent evt) {

	}

	@Override 
	public void mouseReleased(MouseEvent evt) {}

}
