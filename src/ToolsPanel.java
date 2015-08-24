import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolsPanel extends JPanel{
	
	private ToolBox toolBox = Globals.toolBox;
	private JButton insertionToolJB;
	
	public ToolsPanel(){
		setPreferredSize(new Dimension(35,680));
		setSize(getPreferredSize());
		setBorder(BorderFactory.createEtchedBorder());
		
		insertionToolJB = new JButton();
		insertionToolJB.setPreferredSize(new Dimension(25,25));
		setITButtonActive(false);
		
		add(insertionToolJB);
	}
	
	public void setITButtonActive(boolean state){
		if(state){
			insertionToolJB.setBackground(new Color(225,225,225));
			insertionToolJB.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		}
		else{
			insertionToolJB.setBackground(new Color(230,230,230));;
			insertionToolJB.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		}
	}
}
