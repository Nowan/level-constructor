import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ToolsPanel extends JPanel{
	
	private ToolBox toolBox = Globals.toolBox;
	private JButton addTopRowJB;
	private JButton removeTopRowJB;
	private JButton addBottomRowJB;
	private JButton removeBottomRowJB;
	private JSlider workspaceScaleJS;
	private static final int SCALE_MIN = 50;
	private static final int SCALE_MAX = 150;
	private static final int SCALE_INIT = 100;
	
	public ToolsPanel(){
		setPreferredSize(new Dimension(29,680));
		setSize(getPreferredSize());
		setBorder(BorderFactory.createEtchedBorder());
		SpringLayout slayout = new SpringLayout();
		setLayout(slayout);
		
		addTopRowJB = new JButton("<html><body><div style=\"text-align: center;\">&#8657<br>+</div></body></html>");
		addTopRowJB.setPreferredSize(new Dimension(24,60));
		addTopRowJB.setContentAreaFilled(false);
		addTopRowJB.setMargin(new Insets(0,0,0,0));
		addTopRowJB.setFont(Globals.PARAMETER_FONT);
		addTopRowJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConstructorWindow.instance.workspace.canvas.addLevelRow(true);
			}});
		
		removeTopRowJB = new JButton("<html><body><div style=\"text-align: center;\">-<br>&#8659</div></body></html>");
		removeTopRowJB.setPreferredSize(new Dimension(24,60));
		removeTopRowJB.setContentAreaFilled(false);
		removeTopRowJB.setMargin(new Insets(0,0,0,0));
		removeTopRowJB.setFont(Globals.PARAMETER_FONT);
		removeTopRowJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConstructorWindow.instance.workspace.canvas.removeLevelRow(true);
			}});
		
		addBottomRowJB = new JButton("<html><body><div style=\"text-align: center;\">+<br>&#8659</div></body></html>");
		addBottomRowJB.setPreferredSize(new Dimension(24,60));
		addBottomRowJB.setContentAreaFilled(false);
		addBottomRowJB.setMargin(new Insets(0,0,0,0));
		addBottomRowJB.setFont(Globals.PARAMETER_FONT);
		addBottomRowJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConstructorWindow.instance.workspace.canvas.addLevelRow(false);
			}});
		
		removeBottomRowJB = new JButton("<html><body><div style=\"text-align: center;\">&#8657<br>-</div></body></html>");
		removeBottomRowJB.setPreferredSize(new Dimension(24,60));
		removeBottomRowJB.setContentAreaFilled(false);
		removeBottomRowJB.setMargin(new Insets(0,0,0,0));
		removeBottomRowJB.setFont(Globals.PARAMETER_FONT);
		removeBottomRowJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConstructorWindow.instance.workspace.canvas.removeLevelRow(false);
			}});
		
		workspaceScaleJS = new JSlider(JSlider.VERTICAL,SCALE_MIN, SCALE_MAX, SCALE_INIT);
		workspaceScaleJS.setMajorTickSpacing(50);
		workspaceScaleJS.setMinorTickSpacing(25);
		workspaceScaleJS.setPaintTicks(true);
		workspaceScaleJS.setPaintLabels(false);
		workspaceScaleJS.setPreferredSize(new Dimension(20,400));
		workspaceScaleJS.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				ConstructorWindow.instance.workspace.canvas.setScaleFactor((double)(workspaceScaleJS.getValue())/100);
				ConstructorWindow.instance.workspace.canvas.resize();
			}});
		
		add(addTopRowJB);
		slayout.putConstraint(SpringLayout.NORTH, removeTopRowJB, 
				2, SpringLayout.SOUTH, addTopRowJB);
		add(removeTopRowJB);
		
		slayout.putConstraint(SpringLayout.SOUTH, addBottomRowJB, 
				0, SpringLayout.SOUTH, this);
		add(addBottomRowJB);
		
		slayout.putConstraint(SpringLayout.SOUTH, removeBottomRowJB, 
				4, SpringLayout.NORTH, addBottomRowJB);
		add(removeBottomRowJB);
		
		slayout.putConstraint(SpringLayout.VERTICAL_CENTER, workspaceScaleJS, 
				0, SpringLayout.VERTICAL_CENTER, this);
		add(workspaceScaleJS);
	}
}
