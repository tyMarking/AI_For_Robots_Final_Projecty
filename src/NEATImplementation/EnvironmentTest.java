package NEATImplementation;
//package NEATImplementation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
class Agent extends JComponent{
	
	//Agent Parameters
	double proxLength = 200;
	double speed = 1;
	double angle = Math.PI*3/2;
	int size = 100;
	double x = 100, y = 100;
	double x1, x2, x3, x4, y1, y2, y3, y4, xp0, xp1, yp0, yp1;
	double xPos, yPos;
	double p0=0,p1=0;
	long currentTime =  System.currentTimeMillis();
	double shortest = -1;
	//Hamster Parameters
	int leftSpeed = 60;
	int rightSpeed = 60;
	
	//Frame Parameters
	int width = 1600;
	int height = 900;
	
	//Hamster Kinimatics Setup
	boolean updateAngle = true;
	double deltaAngle = 0;
	
	public Agent(int x, int y) {
		this.x = x;
		this.y = -y;
		setSize(width, height);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d=(Graphics2D)g;
		
	     double pi4 = (3.1415 / 4.0);
	     
	     double a1 = angle + pi4;
	     double a2 = angle + 3*pi4;
	     double a3 = angle + 5*pi4;
	     double a4 = angle + 7*pi4;
	
	     x1 = 20*Math.sqrt(2) * Math.cos(a1) + x;
	     x2 = 20*Math.sqrt(2) * Math.cos(a2) + x;
	     x3 = 20*Math.sqrt(2) * Math.cos(a3) + x;       
	     x4 = 20*Math.sqrt(2) * Math.cos(a4) + x;
	     
	     xp0 = proxLength * Math.cos(angle + Math.PI/2.0) + x1;
	     xp1 = proxLength * Math.cos(angle + Math.PI/2.0) + x2;
	     
	     y1 = 20*Math.sqrt(2) * Math.sin(a1) - y;
	     y2 = 20*Math.sqrt(2) * Math.sin(a2) - y;
	     y3 = 20*Math.sqrt(2) * Math.sin(a3) - y;
	     y4 = 20*Math.sqrt(2) * Math.sin(a4) - y;

	     yp0 = proxLength * Math.sin(angle + Math.PI/2.0) + y1;
	     yp1 = proxLength * Math.sin(angle + Math.PI/2.0) + y2;
	     
	     Polygon robot = new Polygon();
	     
	     robot.addPoint((int)x1, (int)y1);
	     robot.addPoint((int)x2, (int)y2);
	     robot.addPoint((int)x3, (int)y3);
	     robot.addPoint((int)x4, (int)y4);
	
	     g.setColor(Color.blue);
	     g2d.fillPolygon(robot);
	     
	     g.setColor(Color.red);
	     g2d.fillOval((int)x1-5, (int)y1-5, 10, 10);
	     g2d.fillOval((int)x2-5, (int)y2-5, 10, 10);
	     
	     g.drawLine((int)x1, (int)y1, (int)xp0, (int)yp0);
	     g.drawLine((int)x2, (int)y2, (int)xp1, (int)yp1);
	     
	     //System.out.println((int)((20*Math.sqrt(2)-2) * Math.sin(angle) + (int)((x1 + x3)/2.0)-5) + " - " + (int)((20*Math.sqrt(2)-2) * Math.cos(angle) - ((y1 + y3)/2.0)-5));
		
	}
	public void update(long time) {
		double deltaT = currentTime/1000.0 - time/1000.0;
		
		if(angle > 2*Math.PI) {
			angle -= 2*Math.PI;
		}
		if(angle < 0) {
			angle += 2*Math.PI;
		}
		double newPos = (leftSpeed*deltaT + rightSpeed*deltaT)/2;
		double newAngle = angle + (leftSpeed-rightSpeed)*deltaT/35;
		double newX = x + newPos * Math.sin(angle) * 1.25;
		double newY = y + newPos * Math.cos(angle) * 1.25;
		double[][] myObs = new double[1][4];
		myObs[0][0] = 0;
		myObs[0][1] = 0;
		myObs[0][2] = 0;
		myObs[0][3] = 0;
		//if isCollided()
		x = newX;
		y = newY;
		angle = newAngle;
		currentTime = time;

		//System.out.println(x + " - " + y);
	}
	public Point getProx(int side, Object obsList []) {
		//ANGLE 0 is DOWN



		//Left
		if (side == 0) {
			xPos = x1;
			yPos = y1;
		} else {
			xPos = x2;
			yPos = y2;
		}
		double newAngle = ((angle+2*Math.PI) % (2*Math.PI));
		//Get equation of line
		//y = m*(x-xPos)+yPos
		//x =(y-yPos)/m + xPos

//		System.out.println("ANGLE: " + String.valueOf(newAngle));

		double m;
		//Four quadrants
		if (newAngle >= 0 && newAngle < Math.PI/2) {
			//3rd Quadrant
//			System.out.println("IN QUADRENT 3");
			m = -1/Math.tan(newAngle);
//			System.out.println("M: " + String.valueOf(m));

			for (Object obs : obsList ) {
				//if in 1st quad flip slope
				double nm;
				if (obs.maxY <= yPos && obs.minX >= xPos){
					nm = -m;
				} else {
					nm = m;
				}

				//Sides: Bottom and Left
				//Right
				double possY = nm * (obs.maxX - xPos) + yPos;
				if (possY >= obs.minY && possY <= obs.maxY) {
					System.out.print("INTERSECTING RIGHT 1 "); //Here
					return new Point((int) obs.maxX, (int) possY);

				}
				//Top side
				double possX = ((obs.minY - yPos) / nm) + xPos;
				if (possX >= obs.minX && possX <= obs.maxX) {
					System.out.print("INTERSECTING TOP 2 "); //Here
					return new Point((int) possX, (int) obs.minY);
				}
			}


		} else if (newAngle >= Math.PI/2 && newAngle < Math.PI) {
			//2nd Quadrant
//			System.out.println("IN QUADRENT 2");
			m = -1/Math.tan(newAngle);
//			System.out.println("M: " + String.valueOf(m));
			for (Object obs : obsList ) {
				//Sides: Bottom and Left
				//if in 4th quad flip slope
				double nm;
				if (obs.minY >= yPos && obs.minX >= xPos){
					nm = -m;
				} else {
					nm = m;
				}

				//Right
				double possY = nm * (obs.maxX - xPos) + yPos;
				if (possY >= obs.minY && possY <= obs.maxY) {
					System.out.print("INTERSECTING RIGHT 3 ");
					return new Point((int) obs.maxX, (int) possY);
				}
				//Bottom side
				double possX = ((obs.maxY - yPos) / nm) + xPos;
				if (possX >= obs.minX && possX <= obs.maxX) {
					System.out.print("INTERSECTING BOTTOM 4 ");
					return new Point((int) possX, (int) obs.maxY);
				}
			}

		} else if (newAngle >= Math.PI && newAngle < 3*Math.PI/2) {
			//1st Quadrant
//			System.out.println("IN QUADRENT 1");
			m = -1/Math.tan(newAngle);
//			System.out.println("M: " + String.valueOf(m));
			for (Object obs : obsList ) {
				//Sides: Bottom and Left
				//if in 3rd quad flip slope
				double nm;
				if (obs.minY >= yPos && obs.maxX <= xPos){
					nm = -m;
				} else {
					nm = m;
				}

				//Left side
				double possY = nm*(obs.minX-xPos) + yPos;
				if (possY >= obs.minY && possY <= obs.maxY) {
					System.out.print("INTERSECTING LEFT 5 "); //HERE
					return new Point((int)obs.minX, (int)possY);
				}
				//Bottom side
				double possX = ((obs.maxY-yPos)/nm) + xPos;
				if (possX >= obs.minX && possX <= obs.maxX) {
					System.out.print("INTERSECTING BOTTOM 6 ");
					return new Point((int)possX, (int)obs.maxY);
				}
			}

		} else if (3*newAngle >= Math.PI/2 && newAngle <= 2*Math.PI) {
			//4th Quadrant
//			System.out.println("IN QUADRENT 4");
			m = -1/Math.tan(newAngle);
//			System.out.println("M: " + String.valueOf(m));
			for (Object obs : obsList ) {
				//Sides: Top and Left
				//if in 2nd quadrent
				double nm;
				if (obs.maxY <= yPos && obs.maxX <= xPos){
					nm = -m;
				} else {
					nm = m;
				}

				//Left side
				double possY = nm*(obs.minX-xPos) + yPos;
				if (possY >= obs.minY && possY <= obs.maxY) {
					System.out.print("INTERSECTING LEFT 7 ");
					return new Point((int)obs.minX, (int)possY);
				}
				//Top side
				double possX = ((obs.minY-yPos)/nm) + xPos;
				if (possX >= obs.minX && possX <= obs.maxX) {
					System.out.print("INTERSECTING TOP 8 ");
					return new Point((int)possX, (int)obs.minY);
				}
			}

		} else {
			System.out.println("ERROR: ANGLE NOT IN RANGE FOR PROX???");
			System.out.println("THE ANGLE IS " + String.valueOf(newAngle));
		}
		xPos = (x1+x3)/2;
		yPos = (y1+y3)/2;
		return new Point();



	}
	public double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
	public boolean isFilled(ArrayList <Double> list) {
		
		for(int k = 0; k < list.size(); k ++) {
     	if(list.get(k) != -10.0) {
     		return true;
     	}
     }
		return false;
	}
	public boolean isCollided(double[][] obs) {
		boolean ret = false;
		
		int NUM_POINTS = 10;
		double[][] fullPoints = new double[NUM_POINTS*4][2];
		
		double dx12 = (x1-x2)/(NUM_POINTS+1);
		double dx23 = (x2-x3)/(NUM_POINTS+1);
		double dx34 = (x3-x4)/(NUM_POINTS+1);
		double dx41 = (x4-x1)/(NUM_POINTS+1);
		double dy12 = (y1-y2)/(NUM_POINTS+1);
		double dy23 = (y2-y3)/(NUM_POINTS+1);
		double dy34 = (y3-y4)/(NUM_POINTS+1);
		double dy41 = (y4-y1)/(NUM_POINTS+1);
		
		for (int i = 1; i < 11; i++) {
			fullPoints[i][0] = dx12*i+x1;
			fullPoints[i+NUM_POINTS][0] = dx23*i+x2;
			fullPoints[i+2*NUM_POINTS][0] = dx34*i+x3;
			fullPoints[i+3*NUM_POINTS][0] = dx41*i+x4;
			
			fullPoints[i][1] = dy12*i+y1;
			fullPoints[i+NUM_POINTS][1] = dy23*i+y2;
			fullPoints[i+2*NUM_POINTS][1] = dy34*i+y3;
			fullPoints[i+3*NUM_POINTS][1] = dy41*i+y4;
		}
		
		System.out.print(fullPoints);
				
				
		collisionLoop:
		for (double[] object : obs) {
			double minX = object[0];
			double minY = object[1];
			double maxX = object[2];
			double maxY = object[3];
			if (object[2] < object[0]) {
				minX = object[2];
				maxX = object[0];
			}
			if (object[3] < object[1]) {
				minY = object[3];
				maxY = object[1];
			}
			
			double dist = Math.pow((this.x - (minX+maxX)/2),2) + Math.pow((this.y - (minY+maxY)/2),2);
			//Max dist to consider squared
			if (dist < 2500) {
				
				double[][] points = new double[4][2];
				points[0][0] = x1;
				points[0][1] = y1;
				points[1][0] = x2;
				points[1][1] = y2;
				points[2][0] = x3;
				points[2][1] = y3;
				points[3][0] = x4;
				points[3][1] = y4;
						
				for (double[] point : points) {
					
					if (point[0] >= minX && point[0] <= maxX && point[1] >= minY && point[1] <= maxY) {
						ret = true;
						break collisionLoop;
					}
				}
				
				for (double[] point : fullPoints) {
					if (point[0] >= minX && point[0] <= maxX && point[1] >= minY && point[1] <= maxY) {
						ret = true;
						break collisionLoop;
					}
				}
			}
			
			
			
			
		}
		
		
		return ret;
	}
	public double calculateAngle(double x1, double y1, double x2, double y2)
	{
	    double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
	    // Keep angle between 0 and 360
	    angle = angle + Math.ceil( -angle / 360 ) * 360;
	    return angle;
	}
}

class Object extends JComponent{
	int x = 0;
	int y = 0;
	
	double x1, x2, x3, x4, y1, y2, y3, y4;
	
	int size = 100;
	double facing = 45;
	double speed = 1.5;
	
	int width = 1600;
	int height = 900;

	double minX, maxX, minY, maxY;
	
	Color c = new Color((int)(Math.random()*200),(int)(Math.random()*200),(int)(Math.random()*200));
	public Object(int xPos, int yPos) {//does work
		 System.out.print("test");
		 setSize(width, height);
		 x = xPos + (int)(size/2.0);
		 y = yPos + (int)(size/2.0);

		 
	     x1 = x;   
	     x2 = x + size;
	     
	     y1 = y;
	     y2 = y + size;

	     minX = x1;
	     maxX = x2;
	     minY = y1;
	     maxY = y2;
	     
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(c);
     
		g.fillRect(x, y, size, size);
	}
	public void update() {//does not get called
		System.out.print("update function");
		//x += Math.sin(Math.toRadians(facing))*speed;
		//y += Math.cos(Math.toRadians(facing))*speed;
	}
	public double[] getPos() {
		System.out.println(x1 + " - " + y1 + " --------- " + x2 + " - " + y2);//does print
		double corners [] = new double[4];
		
		corners[0] = (double)x1;
		corners[1] = (double)y1;
		corners[2] = (double)x2;
		corners[3] = (double)y2;
		
		return corners;
	}
	public boolean checkCol(Agent a) {
		
		return false;
		
		
	}
	public double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
}
class Maze extends JComponent {
	double widthCells, heightCells;
	double height = 900, width = 1600;
	double cellHeight;
	double cellWidth;
	public Maze(int widthC, int heightC) {
		this.heightCells = heightC;
		this.widthCells = widthC;

		cellHeight = height/ heightCells;
		cellWidth = width / widthCells;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println("Working");
     for(int i = 0; i < widthCells; i ++) {
     	for(int k =0; k < heightCells; k ++) {
     		g.fillRect((int)i*(int)cellWidth, k*(int)cellWidth, (int)cellWidth, (int)cellHeight);
     	}
     }
	}
	public void update() {
		repaint();
	}
}
class GUI extends JPanel implements ActionListener {
 private final int DELAY = 0;
 private Timer timer;
 public GUI() {
 	timer = new Timer(DELAY, this);
     timer.start();
     setDoubleBuffered(true);
 }
 
 public Timer getTimer() {
     return timer;
 }
 public void paintComponent(Graphics g) {//does not get called
     super.paintComponent(g);
     System.out.print("paintComponent");
	}
 public void actionPerformed(ActionEvent e) {
     repaint();
     revalidate();
 } 
}

class EnvironmentTest extends JFrame {
	boolean toggle = false;
	static int key;
	boolean left = false, right = false;

	public static GUI panel;

 public EnvironmentTest() {
 	panel = new GUI();
 	addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
             Timer timer = panel.getTimer();
             timer.stop();
         }
     }
     );
 	
     setTitle("Environment Testing");
     setSize(1600,900);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setVisible(true);    
 }
 public static void main(String[] args) {
 	EnvironmentTest envi = new EnvironmentTest();
 	
 	Agent a0 = new Agent(300,300);

// 	Agent a1 = new Agent(400,400);
//
// 	Agent a2 = new Agent(500,500);
//
 	Maze m = new Maze(5,5);
 	Object o1 = new Object(100,100);
 	Object o2 = new Object(100,500);
 	Object o3 = new Object(500,100);
 	Object o4 = new Object(500,500);

// 	Object o = o1;
 	envi.add(a0);
 	
// 	a0.add(a1);
// 	a0.add(a2);
 	a0.add(m);
 	a0.add(o1);
 	a0.add(o2);
 	a0.add(o3);
 	a0.add(o4);

 	Object[] oList = new Object[4];
 	oList[0] = o1;
 	oList[1] = o2;
 	oList[2] = o3;
 	oList[3] = o4;

 	int counter = 0;
 	double obs[][] = new double[1][4];
 	for(int i = 0; i < obs.length; i ++) {
 		obs[i] = o1.getPos();
 		System.out.println(obs[i][0] + " - " + obs[i][3]);//does print
 	}

 	
 	while(true) {
 		//Repaints GUI, triggering refresh of all jComp's
 		envi.getContentPane().validate();
 		envi.getContentPane().repaint();
 		
 		// Calls agents update function
 		
 		a0.update(System.currentTimeMillis());

 		//a1.update(System.currentTimeMillis());

 		//a2.update(System.currentTimeMillis());
 		
 		Point intersect = new Point();


 		intersect = a0.getProx(0, oList);
 		double prox0 = a0.distance((int)(a0.x1 + a0.x2)/2.0,(int)(a0.y1+a0.y3)/2.0,(int)intersect.getX(), (int)intersect.getY());
 		if(intersect.getX() > 0 && intersect.getY() > 0) {
 			System.out.println(prox0);
 		}
// 		intersect = a1.getProx(0, oList);
 		
// 		intersect = a2.getProx(0, oList);
 		
 		m.update();
 		
     	right(a0);

//     	right(a1);
//
//     	right(a2);
     	//left(a);
     	


 		counter ++;
 		
 		//System.out.println(counter);
 		
 		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
 	}
 }
 public static void right(Agent a) {
 	a.rightSpeed = -10;
		a.leftSpeed = 10;
 }
 public static void left(Agent a) {
 	a.rightSpeed = 10;
		a.leftSpeed = -10;
 }
 public static void forward(Agent a) {
 	a.rightSpeed = 60;
		a.leftSpeed = 60;
 }
 public static void stop(Agent a) {
 	a.rightSpeed = 0;
		a.leftSpeed = 0;
 }
 public static void print(String input) {
 	System.out.println(input);
 }
} 