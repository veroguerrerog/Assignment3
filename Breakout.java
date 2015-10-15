/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels.  On some platforms 
  * these may NOT actually be the dimensions of the graphics canvas. */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board.  On some platforms these may NOT actually
  * be the dimensions of the graphics canvas. */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 80;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final double BRICK_WIDTH =
		(WIDTH - (NBRICKS_PER_ROW + 1) * BRICK_SEP) / (double)NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static String NTURNS = "3";
	
/** Counts lives left before Game Over*/	
	private GLabel LIVES_COUNTER = new GLabel (NTURNS);

/** Paddle definition*/
	private GRect paddle = new GRect(getWidth()/2-PADDLE_WIDTH/2, HEIGHT-BRICK_HEIGHT-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	private GLine paddleTop = new GLine(getWidth()/2-PADDLE_WIDTH/2, HEIGHT-BRICK_HEIGHT-PADDLE_Y_OFFSET, getWidth()/2-PADDLE_WIDTH/2+PADDLE_WIDTH, HEIGHT-BRICK_HEIGHT-PADDLE_Y_OFFSET);
	
/** Ball definition*/
	private GOval ball = new GOval(WIDTH/2-BALL_RADIUS, HEIGHT-PADDLE_HEIGHT-PADDLE_Y_OFFSET-BALL_RADIUS*2, BALL_RADIUS*2, BALL_RADIUS*2);
	
/**random number generator*/
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
/** velocity definition*/
	private double vx=rgen.nextDouble(1.0, 3.0);
	private double vy=-rgen.nextDouble(1.0, 3.0);
	
/** Animation refresh rate*/
	private static final int FRAMES_PER_SEC = 60;
	
/** Side intersected; 1: right  2: left  3: up  4: down*/
	private int bounceDirection;

	

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setup();
		animateBall();
	}
	private void setup(){
		addMouseListeners();
		//This sets the size of the window to the correct dimensions, because our window was initializing 22 pixels too narrow.
		this.setSize(WIDTH, HEIGHT);//Correcting for window initializing errors.
		//Creates the grid of colored bricks
		createGrid();
		//Adding the paddle to the canvas.
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
		//Adding the paddle top to the canvas.
		paddleTop.setColor(Color.RED);
		add(paddleTop);
		//Adding the ball to the canvas.
		ball.setColor(Color.BLUE);
		ball.setFilled(true);
		ball.setFillColor(Color.BLUE);
		add(ball);
		//Adding the lives counter to the canvas.
		LIVES_COUNTER.setFont("Cambria-40");
		LIVES_COUNTER.setLocation(WIDTH-40, 40);
		add (LIVES_COUNTER);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	//This method creates a grid of colored blocks.
	private void createGrid(){
		drawTwoRows(BRICK_Y_OFFSET, Color.RED);
		drawTwoRows(BRICK_Y_OFFSET+2*BRICK_HEIGHT+2*BRICK_SEP, Color.ORANGE);
		drawTwoRows(BRICK_Y_OFFSET+4*BRICK_HEIGHT+4*BRICK_SEP, Color.YELLOW);
		drawTwoRows(BRICK_Y_OFFSET+6*BRICK_HEIGHT+6*BRICK_SEP, Color.GREEN);
		drawTwoRows(BRICK_Y_OFFSET+8*BRICK_HEIGHT+8*BRICK_SEP, Color.CYAN);
	}
	//This method draws two rows with the same color, given the y coordinate and a color.
	private void drawTwoRows(int y, Color color){
		for(int i = 0; i<NBRICKS_PER_ROW; i++){
			drawBrick(BRICK_SEP*(i+1)+BRICK_WIDTH*i, y,color);
			drawBrick(BRICK_SEP*(i+1)+BRICK_WIDTH*i, y+BRICK_HEIGHT+BRICK_SEP,color);
		}
	}
	//This method draws a brick given an x coordinate, y coordinate, and a color.
	private void drawBrick(double x, double y, Color color){
		GRect brick = new GRect(x,y,BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setFillColor(color);
		brick.setColor(color);
		add(brick);
	}
	//This method changes the x-coordinate of the paddle each time it is moved, while preventing the movement of the paddle off screen.
	public void mouseMoved(MouseEvent e){
		if(e.getX()>PADDLE_WIDTH/2&&e.getX()<getWidth()-PADDLE_WIDTH/2){
			paddle.setLocation(e.getX()-PADDLE_WIDTH/2,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
			paddleTop.setLocation(e.getX()-PADDLE_WIDTH/2,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}
	}
	//This method makes the ball move(hopefully)
	private void animateBall(){
		while(ball.getX()>0||ball.getY()>0||ball.getX()<WIDTH||ball.getY()<HEIGHT){
			ball.move(vx, vy);
			pause(1000/FRAMES_PER_SEC);
			if(ball.getX()<=0||ball.getX()>=WIDTH-BALL_RADIUS*2){
				vx=-vx;
			}
			if(ball.getY()<=0){
				vy=-vy;
			}
			if(ball.getY()>=HEIGHT) {
				NTURNS = "2";
				add(LIVES_COUNTER);
			}
			GObject collider = getCollidingObject();
			if(collider == paddleTop){
				if (checkCorner(paddleTop.getX(),paddleTop.getY())==ball) {
					vx=-Math.sqrt(vx*vx);
					while (ball.getY()<HEIGHT) {
						ball.move(vx, vy);
						pause(1000/FRAMES_PER_SEC);
					}
				} else if (checkCorner(paddleTop.getX()+PADDLE_WIDTH,paddleTop.getY())==ball) {
					vx=Math.sqrt(vx*vx);
					while (ball.getY()<HEIGHT) {
						ball.move(vx, vy);
						pause(1000/FRAMES_PER_SEC);
					}
				} else {
					vy=-Math.sqrt(vy*vy);
				}
			} else if (collider!=null){
				if(bounceDirection==1){
					vx=-Math.sqrt(vx*vx);
				}
				if (bounceDirection==2){
					vx=Math.sqrt(vx*vx);
				}
				if(bounceDirection==3){
					vy=Math.sqrt(vy*vy);
				}
				if (bounceDirection==4){
					vy=-Math.sqrt(vy*vy);
				}
				if(collider!=paddleTop&&collider!=paddle){
					remove(collider);
				}
			}
		}
	}
	//This method returns the object the ball collides with, if there is a collision.
	private GObject getCollidingObject(){
		if (checkCorner(ball.getX()+BALL_RADIUS,ball.getY()-1)!=null){
			bounceDirection=3;
			return checkCorner(ball.getX()+BALL_RADIUS,ball.getY()-1);
		} else if (checkCorner(ball.getX()+2*BALL_RADIUS+1,ball.getY()+BALL_RADIUS)!=null){
			bounceDirection=1;
			return checkCorner(ball.getX()+2*BALL_RADIUS+1,ball.getY()+BALL_RADIUS);
		} else if (checkCorner(ball.getX()-1,ball.getY()+BALL_RADIUS)!=null){
			bounceDirection=2;
			return checkCorner(ball.getX()-1,ball.getY()+BALL_RADIUS);
		} else if (checkCorner(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS+1)!=null){
			bounceDirection=4;
			return checkCorner(ball.getX()+BALL_RADIUS,ball.getY()+2*BALL_RADIUS+1);
		} else {
			return null;
		}
	}
	private GObject checkCorner(double x, double y){
		return getElementAt(x,y);
	}
}