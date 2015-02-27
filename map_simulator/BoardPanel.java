package map_simulator;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import javax.swing.*;

public class BoardPanel extends JPanel implements ActionListener, MouseListener {
	private Map m;
	private ControlPanel cp;
	private boolean mouseClick = true;
	private static final int UNEXPLORED = 0;
	private static final int EXPLORED = 1;
	private static final int OBSTACLE = 2;
	private static final int SHORTESTPATH = 3;
	private boolean reset = false;

	public BoardPanel() {
		setLayout(new GridLayout(0, 1));
		this.addMouseListener(this);
		this.m = new Map();
	}

	/*
	 * public void start(){ repaint(); algo = new AStar(m,r); new
	 * Thread(algo).start(); }
	 */
	public void actionPerformed(ActionEvent e) {
		// repaint();
	}

	public void setMouseClick(boolean enable) {
		this.mouseClick = false;
	}

	public void updateControlPanel(ControlPanel cp) {
		this.cp = cp;
	}

	public Map getMap() {
		return m;
	}

	public void setMap(Map map) {
		m = map;
	}

	public void updateMap() {
		validate();
		System.out.println("UPDATING MAP");
		repaint();
	}

	public void updateRobot() {
		validate();
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int x = 0; x < 15; x++) {
			for (int y = 0; y < 20; y++) {
				if (!reset) {

					if (m.getLocInfo(x, y) == OBSTACLE) {
						g.drawImage(m.getObstacle(), x * 32, y * 32, null);
					}
					if (m.getLocInfo(x, y) == UNEXPLORED) {
						g.drawImage(m.getUnexplored(), x * 32, y * 32, null);

					}
					if (m.getLocInfo(x, y) == EXPLORED) {
						g.drawImage(m.getExplored(), x * 32, y * 32, null);
					}
					if (m.getLocInfo(x, y) == SHORTESTPATH) {
						g.drawImage(m.getShortestpath(), x * 32, y * 32, null);
					}
				} else {
					if (m.getLocInfo(x, y) == OBSTACLE) {
						g.drawImage(m.getObstacle(), x * 32, y * 32, null);
					}
					if (m.getLocInfo(x, y) == UNEXPLORED) {
						g.drawImage(m.getUnexplored(), x * 32, y * 32, null);
					}
					if (m.getLocInfo(x, y) == EXPLORED) {
						g.drawImage(m.getExplored(), x * 32, y * 32, null);
					}
				}
			}
		}
		reset = false;
		// g.drawImage(Simulator.robot.getRobot(),
		// Simulator.robot.getPos().getX() * 32, Simulator.robot.getPos().getY()
		// * 32, null);
		if (Simulator.robot != null) {
			if (Simulator.robot.getDirection() == Direction.NORTH) { // front
				// sensors
				((Graphics2D) g).setPaint(Color.yellow);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				// non-sensors
				((Graphics2D) g).setPaint(Color.gray);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
			} else if (Simulator.robot.getDirection() == Direction.WEST) { // left
				// sensors
				((Graphics2D) g).setPaint(Color.yellow);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				// non-sensors
				((Graphics2D) g).setPaint(Color.gray);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
			} else if (Simulator.robot.getDirection() == Direction.EAST) { // right
				// sensors
				((Graphics2D) g).setPaint(Color.yellow);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				// non-sensors
				((Graphics2D) g).setPaint(Color.gray);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
			} else if (Simulator.robot.getDirection() == Direction.SOUTH) { // back
				// sensors
				((Graphics2D) g).setPaint(Color.yellow);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() + 1) * 32, 30, 30);
				// non-sensors
				((Graphics2D) g).setPaint(Color.gray);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY() - 1) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() - 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX() + 1) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
				g.fillRect((Simulator.robot.getPos().getX()) * 32,
						(Simulator.robot.getPos().getY()) * 32, 30, 30);
			}
		}
	}

	public void reset() {
		System.out.println("reset");
		m = new Map();
		reset = true;
		validate();
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		cp.setMouseLoc(e.getX() / 32, e.getY() / 32);
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// System.out.println("Mouse entered!!!");

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// System.out.println("Mouse exited!!!");
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// System.out.println("Mouse released!!!");
		if (mouseClick) {
			if (m.getLocInfo(arg0.getX() / 32, arg0.getY() / 32) == 3) {
				m.setSimulatedLocInfo(arg0.getX() / 32, arg0.getY() / 32, 1);
				m.setLocInfo(arg0.getX() / 32, arg0.getY() / 32, 0);
				repaint();
			} else if (m.getLocInfo(arg0.getX() / 32, arg0.getY() / 32) == 0) {
				// System.out.println("X: " + arg0.getX()/32 + " Y: " +
				// arg0.getY()/32);
				m.setSimulatedLocInfo(arg0.getX() / 32, arg0.getY() / 32, 2);
				m.setLocInfo(arg0.getX() / 32, arg0.getY() / 32, 3);
				repaint();
			}
		}
	}

}
