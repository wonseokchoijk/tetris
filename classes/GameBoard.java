package classes;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GameBoard extends JPanel {
	public static final int cyanPiece      = 1;
	public static final int darkGreenPiece = 2;
	public static final int greenPiece     = 3;
	public static final int redPiece       = 4;
	public static final int bluePiece      = 5;
	public static final int pinkPiece      = 6;
	public static final int skinPiece 	   = 7;
	
	int[][] blockValue = new int[12][22];
	//GameBoard의 blockValue배열은 Tetris00클래스의 blockValue배열과 다르게
	//음수값을 갖지 않는다. 음수값은 모두 양수값으로 변환시켜 가져온다. 
	
	Color redTeduri = new Color(180,0,0);
	Color redInner = new Color(255,0,0);
	Color blueTeduri = new Color(70,70,220);
	Color blueInner = new Color(130,130,255);
	Color greenTeduri = new Color(0,180,0);
	Color greenInner = new Color(0,255,0);
	Color cyanTeduri = new Color(100,160,250);
	Color cyanInner = new Color(160,220,250);
	Color pinkTeduri = new Color(200,50,50);
	Color pinkInner = new Color(255,120,120);
	Color darkGreenTeduri = new Color(0,80,0);
	Color darkGreenInner = new Color(0,130,0);
	Color skinTeduri = new Color(100,180,40);
	Color skinInner = new Color(180,235,130);

	public Color pTeduriColor;
	public Color pColor;
	
	//private int col;
	//private int row;
	//private int colo;
	private int x;
	private int y;
	//private int color;


	public GameBoard(){
		for(int i = 0; i < 12; i++){
			for(int j = 0; j < 22; j++){
				blockValue[i][j] = 0; 		//블럭에 아무것도 들어있지 않을 땐 0
			}
		}


	//setBackground(new Color(60,60,60));
	//	setSize(240, 400);
	}
	//2차원배열(가로,세로)을 만들어서
	//하나하나에 색상정보를 넣으면?
	//drawPiece의 좌표값은 배열숫자 곱하기 25를 하면 어떨까?

		public synchronized void paintComponent(Graphics g){
				g.setColor(new Color(60,60,60));
				g.fillRect(0, 0, 301, 500);
				
				for(int i=0; i<12; i++){
					for(int j=0; j<22; j++){
	
						if(j>=2 && blockValue[i][j]!=0){
							if(blockValue[i][j]<0){
								blockValue[i][j] = minusToPlus(blockValue[i][j]);
							}
							x = i;
							y = j-2;
	//						setColor(blockValue[i][j]);
							drawPiece(  x, y, blockValue[i][j],g);
	
						}
					}
				}



		}

		/*
		public void update(Graphics g){
			
		}
		*/
		
		public synchronized void drawPiece(int x, int y, int pieceColor, Graphics g){
			colorCheck(pieceColor);
			x = x * 25;
			y = y * 25;
			
			g.setColor(pTeduriColor);
			g.fillRect(x, y, 25,25);
			g.setColor(pColor);
			g.fillRect(x+3,y+3,19,19);
			g.setColor(new Color(0,0,0));
			g.drawRect(x, y, 24,24);
			//g.setColor(new Color(200,200,200));
			//g.drawLine(x, y, 1,24);
	}
		
	public int minusToPlus(int value){	//음수를 양수로 만들어주는 메소드
		value = value - (value*2);
		return value;
	}
		
	public synchronized void colorCheck(int pieceColor){
		switch(pieceColor){
			case redPiece : 	pTeduriColor = redTeduri;
								pColor = redInner;
								break;
			case bluePiece 	:	pTeduriColor = blueTeduri;
								pColor = blueInner;
							 	break;
			case greenPiece :   pTeduriColor = greenTeduri;
								pColor = greenInner;
								break;
			case cyanPiece  :  	pTeduriColor = cyanTeduri;
								pColor = cyanInner;
								break;
			case pinkPiece  :  	pTeduriColor = pinkTeduri;
								pColor = pinkInner;
								break;
			case darkGreenPiece:pTeduriColor = darkGreenTeduri;
								pColor = darkGreenInner;
								break;
			case skinPiece	:	pTeduriColor = skinTeduri;
								pColor = skinInner;
								break;
		}
	}
	public void setBlockValue(int[][] blockValue){

		for(int i=0; i<12; i++){
			for(int j=0; j<22; j++){
				this.blockValue[i][j] = blockValue[i][j];
				//System.out.println(blockValue[i][j]);
			}
		}
	
	}
/*	public int getCol(){
		return this.col;
	}
	public int getRow(){
		return this.row;
	}*/
	/*public int getColor(){
		return this.colo;
	}*/
/*	public void setCol(int col){
		this.col = col;
	}
	public void setRow(int row){
		this.row = row;
	}*/
	/*public void setColor(int colo){
		this.colo = colo;
	}*/
		/*
		public void getBlock(int rowValue, int colValue, int blockType, Graphics g){
			int x = rowValue * 25;
			int y = colValue * 25;
			
			switch(blockType){
				case typeI : 
							 drawPiece(x, y, cyanPiece, g);
				
						  	 break;
							
			}
		}
		*/
		

	

	
}
