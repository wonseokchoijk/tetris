import java.awt.*;
import javax.swing.*;
import java.net.URL;
import classes.*;
import java.awt.event.*;

public class Tetris00 implements KeyListener, ActionListener{

	int blockValue[][] = new int[12][22];
		//안보이는 윗쪽 영역 두줄을 포함.
						
	JFrame frm;
	GameBoard gBoard = new GameBoard();
	Pieces piece = new Pieces();
	Pieces prePiece = new Pieces();
	GameThread gThread;
	Thread gThreadRun;
	private int pieceType;	// 현재 조각 종류
	private int prePieceType;	// 이 다음에 나올 조각 종류
	Point[] blocks = new Point[4];
	Point[] preBlocks = new Point[4]; // 다음 블럭 창에 나올 블럭.
	int centerX, centerY;
	int preX,preY;	//다음에 나올 블럭을 표시할 기준좌표
    JPanel previewPanel;
    JLabel stageLabel,linesLabel;
    //JMenuItem restart;
    JMenuItem restart;
    JMenuItem about;
    //boolean newGame;
    boolean gameStart = false;
    boolean stopFallingBlock = false; //블록2배속 떨어짐을 고정시키는 변수
    //boolean stopFallingBlock2 = false; //스페이스 계속 누르고 있을 때 떨어짐을 고정시키는 변수
    boolean dropBlock = false; //스페이스 눌렀을 때는 블럭이 피융 한번에 떨어지게 하는 불린 변수
    boolean threadStop = false;
	boolean firstBlock = true;	//게임의 첫블록 초기화
	boolean fix = true;


    int stage = 1;	//화면에 표시되는 스테이지 
    int erasedLines;	//화면에 표시되는 없앤 라인 수
    int sLines=0;
    int speed; //게임스피드 - 많으면 느리고 적으면 빠르다.
    int tempSpeed;
    int tempCenterX;
    int undoMovingX;
	
	URL[] bgURL = new URL[11];
	ImageIcon[] bgImage = new ImageIcon[11];
	URL panelBGURL, previewURL, stageURL, linesURL;
	ImageIcon panelBGImage, previewImage, stageImage, linesImage;
	JPanel bg01Panel;

	public Tetris00() {
		frm = new JFrame("Tetris 0.8 by CWS");

		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setLocation(100, 100);
		frm.setBackground(new Color(250,0,0));
	    frm.setResizable(false);
		frm.setVisible(true);
		frm.addKeyListener(this);

		display();
		startingGame();
		
	}

	public void display(){
		
   	 	JMenuBar menubar = new JMenuBar();	//메뉴바 만듦
   	 	frm.setJMenuBar(menubar);
   	 	//JMenu menuA = new JMenu("Game");
   	 	//restart = new JMenuItem("Restart this Game");
   	 		//menuA.add(restart);
   	 		//restart.addActionListener(this);
   	 	JMenu menuA = new JMenu("Menu");
   	 	JMenu menuB = new JMenu("About");
   	 	restart = new JMenuItem("Restart");
   	 	about = new JMenuItem("About This Program..");
   	 		menuA.add(restart);
   	 		menuB.add(about);
   	 		restart.addActionListener(this);
   	 		about.addActionListener(this);
   	 	//menubar.add(menuA);
   	 	menubar.add(menuA);
   	 	menubar.add(menuB);
		/*
		 * 배경이 되는 bg01SP이 있고 왼쪽에는 stage,lines가, 오른쪽에는 다음 블럭이 나타날 공간(preview)가 있다.
		 * 가운데에는 PanelBGSP(테두리 이미지가 들어가있는)가 들어간다.
		 * PanelBGSP 안에는 gBoard가 들어있다.
		 */
   	 	
   		
   	 	/*JPanel gBoardOut = new JPanel(){
   			public void paintComponent(Graphics g){
   				g.setColor(new Color(60,60,60));
   				g.fillRect(0,0,this.getWidth(),this.getHeight());
   			}	
   		};*/
   		  	 	
   	 	
	    for(int ii=0; ii<bgURL.length; ii++){
   	 		bgURL[ii] = getClass().getClassLoader().getResource("images/bg"+Integer.toString(ii+1)+".jpg"); //배경화면 이미지 가져오기
   	 		bgImage[ii] = new ImageIcon(bgURL[ii]);
	    }
	    bg01Panel = new JPanel(){
            public void paintComponent(Graphics g) {
                Dimension d = frm.getSize();
                g.drawImage(bgImage[changeBG()-1].getImage(), 0, 0, d.width, d.height, null);
            	//g.drawImage(bg01Image.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        bg01Panel.setLayout(new FlowLayout());
	    JScrollPane bg01SP = new JScrollPane(bg01Panel);
	    
	    
	    panelBGURL = getClass().getClassLoader().getResource("images/panelBG.jpg");
	    panelBGImage = new ImageIcon(panelBGURL);  //보드 옆 테두리
	    JPanel panelBGPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                Dimension p = new Dimension(324,524);
                g.drawImage(panelBGImage.getImage(), 0, 0, p.width, p.height, null);
            	//g.drawImage(panelBGImage.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        panelBGPanel.setLayout(new FlowLayout());
	    JScrollPane panelBGSP = new JScrollPane(panelBGPanel);

	    previewURL = getClass().getClassLoader().getResource("images/preview.gif");
	    previewImage = new ImageIcon(previewURL);  //다음 조각이 표시될 사각 스페이스
	    previewPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                Dimension q = new Dimension(122,116);
                g.drawImage(previewImage.getImage(), 30, 0, q.width, q.height, null);

                	prePiece.setBlock(prePieceType);
    				preBlocks = prePiece.getBlock();
	                gBoard.colorCheck(prePieceType);
	                int xplus = 80;
	                int yplus = 60;
	                switch(prePieceType){
	                case(Pieces.typeI) : xplus = 89; yplus= 45;
	                					break;
	                case(Pieces.typeT) : xplus = 76; yplus= 60;
    									break;
	                case(Pieces.typeO) : xplus = 88; yplus= 58;
    									break;
	                case(Pieces.typeL) : xplus = 76; yplus= 60;
	                					break;
	                case(Pieces.typeJ) : xplus = 76; yplus= 60;
    									break;
	                case(Pieces.typeS) : xplus = 75; yplus= 60;
	                					break;
	                case(Pieces.typeZ) : xplus = 78; yplus= 60;
    									break;
	                }
	                
	                
	                for(int aa=0; aa<4; aa++){
		                preX = preBlocks[aa].x * 25 + xplus;
		    			preY = preBlocks[aa].y * 25 + yplus;
		    			
		    			g.setColor(gBoard.pTeduriColor);
		    			g.fillRect(preX, preY, 25,25);
		    			g.setColor(gBoard.pColor);
		    			g.fillRect(preX+3,preY+3,19,19);
		    			g.setColor(new Color(0,0,0));
		    			g.drawRect(preX, preY, 24,24);	//빨간 블럭1
            		}

            	//g.drawImage(previewImage.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        previewPanel.setLayout(new FlowLayout());
        
        stageURL = getClass().getClassLoader().getResource("images/stage.gif");
	    stageImage = new ImageIcon(stageURL);  //stage 표시박스
	    JPanel stagePanel = new JPanel(){
            public void paintComponent(Graphics g) {
                Dimension q = new Dimension(87,80);
                g.drawImage(stageImage.getImage(), 0, 0, q.width, q.height, null);
            	//g.drawImage(previewImage.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        stagePanel.setLayout(new FlowLayout());
        stageLabel = new JLabel("<html><br><font size=6>"+stage);
        stagePanel.add(stageLabel);

        linesURL = getClass().getClassLoader().getResource("images/lines.gif");
	    linesImage = new ImageIcon(linesURL);  //lines 표시박스
	    JPanel linesPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                Dimension q = new Dimension(87,80);
                g.drawImage(linesImage.getImage(), 0, 0, q.width, q.height, null);
            	//g.drawImage(previewImage.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        linesPanel.setLayout(new FlowLayout());
        linesLabel = new JLabel("<html><br><font size=6>"+erasedLines);
        linesPanel.add(linesLabel);
        
        /*스테이지와 라인 판넬 구조
         *stageLines(BorderLayout)에는 stageLinesSouth(South)가 들어간다 
         *stageLinesSouth(FlowLayout)안에는 stageLinesInner(FlowLayout)가 들어간다.
         *stageLinesInner안에는 stagePanel, linesPanel이 들어간다.
         */
        
        JPanel stageLines = new JPanel();
        JPanel stageLinesSouth = new JPanel();
        JPanel stageLinesCenter = new JPanel();
        JPanel stageLinesInner = new JPanel();

        stageLinesSouth.setLayout(new FlowLayout());
        stageLines.setLayout(new BorderLayout());
        stageLines.add(BorderLayout.SOUTH, stageLinesSouth);
        stageLinesSouth.add(stageLinesInner);
        stageLinesInner.add(stagePanel);
        stageLinesInner.add(linesPanel);
        
        panelBGPanel.add(gBoard);

        //panelBGPanel.add(gBoardOut);
	    //gBoardOut.add(gBoard);
	    //gBoard.setOpaque(false);

	    bg01Panel.add(stageLines);
	    bg01Panel.add(panelBGSP);
	    bg01Panel.add(previewPanel);

	    stageLines.setOpaque(false);
	    stageLinesInner.setOpaque(false);
	    stageLinesSouth.setOpaque(false);
	    
	    previewPanel.setPreferredSize(new Dimension(200, 480));
	    stagePanel.setPreferredSize(new Dimension(87, 80));
	    linesPanel.setPreferredSize(new Dimension(87, 80));
	    stageLines.setPreferredSize(new Dimension(200, 480));
	    stageLinesInner.setPreferredSize(new Dimension(100, 180));	    
	    stageLinesSouth.setPreferredSize(new Dimension(200, 180));
	    gBoard.setPreferredSize(new Dimension(301, 500)); //가로세로25, 가로12줄 세로 20줄
	    //gBoardOut.setPreferredSize(new Dimension(301, 508)); //가로세로25, 가로12줄 세로 20줄
	    
	    frm.getContentPane().add(BorderLayout.CENTER, bg01SP);

		frm.setSize(800, 600);
		//bg01Panel.repaint();
		//gBoard.setBorder(BorderFactory.createRaisedBevelBorder());

		gThread = new GameThread();
		gThreadRun = new Thread(gThread);
		gThreadRun.start();
	}
	

	public synchronized void startingGame(){
		for(int i = 0; i < 12; i++){
			for(int j = 0; j < 22; j++){
				blockValue[i][j] = 0; 		//블럭에 아무것도 들어있지 않을 땐 0
			}
		}
		stopFallingBlock = false; //블록2배속 떨어짐을 고정시키는 변수
	    dropBlock = false; //스페이스 눌렀을 때는 블럭이 피융 한번에 떨어지게 하는 불린 변수
	    threadStop = false;
		firstBlock = true;	//게임의 첫블록 초기화
		fix = true;
		
	    stage = 1;	//화면에 표시되는 스테이지
		stageLabel.setText("<html><br><font size=6>"+stage);
	    erasedLines=0;	//화면에 표시되는 없앤 라인 수
		linesLabel.setText("<html><br><font size=6>"+erasedLines);
	    sLines=0;
		bg01Panel.repaint();
	    speed = 1000; //게임스피드 - 많으면 느리고 적으면 빠르다.
		firstBlock = true;	//게임의 첫블록 초기화


		
	}
 	public void actionPerformed(ActionEvent e) // 메뉴 액션 리스너 구현
	{
		if(e.getActionCommand().equals(restart.getActionCommand())){
			//newGame = true;
			speed=1;

			startingGame();
			//프로그램 정보
		}else if(e.getActionCommand().equals(about.getActionCommand())){
			//int tempSp = speed;
			//speed=1000000;
			threadStop = true;
			JOptionPane.showMessageDialog(frm, "Tetris ver 0.8\nThis Program is made by CWS in Sep.,2009\nusing JAVA 1.6.0 Mustang","About this Program..",1);
			threadStop = false;
			gThreadRun.interrupt();
			//notifyA();
			//speed = tempSp;
			//speed=0;
				//about정보표시
		}
	}
 		/*public synchronized void notifyA(){
			gThreadRun.notify();	
 		}*/
 	
	public static void main(String[] args) {
		Tetris00 tetris = new Tetris00();
	}

	public synchronized void makeBlock(){
		if(firstBlock){
			pieceType = (int)((Math.random() * 7)+1);
			firstBlock = false;
		}else{
			pieceType = prePieceType;
		}
		prePieceType = (int)((Math.random() * 7)+1);
		centerX = 6;
		centerY = 0;
		piece.setBlock(pieceType);
		blocks = piece.getBlock();

	}
	public synchronized void moveBlock(){	//Point형 blocks에 들어있는 값을
										//실제 blockValue배열에 적용시켜서 실제 이동을 적용하는 메소드
		delMovedBlock();	//이동전 위치에 있던 값은 0으로 되돌려 지워준다.
		
		for(int i=0; i < 4; i++){
			if(getBlockCol(i)<0 || getBlockRow(i)<0){}	//0보다 작은 배열값이 안나오도록 방지
			else{
				blockValue[getBlockCol(i)][getBlockRow(i)] = pieceType; 
			}
		}
	}

	public synchronized void delMovedBlock(){	//이동 후에 이동 전에 있던 위치의 값을 지워주는 메소드
		
		for(int i=0; i<12; i++){
			for(int j=0; j<22; j++){
				if(blockValue[i][j]>0){
					blockValue[i][j]=0; //움직이는 블럭이 이동하고나면 원래자리는 0으로 없애줌
				}
			}
		}
	}
	public synchronized boolean checkMovableBlock(){	//0이면 아무것도 없는 블럭,
										//음수면 안움직이는 블럭, 양수면 움직이는 블럭
		for(int i=0; i < 12; i++){
			for(int j=0; j < 22; j++){
				if(blockValue[i][j] > 0){
					//System.out.print("true");
					
					return true;

				}		//움직이는 블럭이 있으면 true,
			}			//더이상 움직이는 블럭이 없으면 false를 내뱉는다.
		}
		//System.out.print("false");
		return false;

	}
	public boolean checkGameOver(){	
		//게임오버가 됐는지 체크하는 메소드
		//게임오버가 되면 true를 리턴.
		for(int i=0; i<12; i++){
			if(blockValue[i][1] < 0){
				//gThreadRun.interrupt();
				return true;
				
			}
		}
		/*
		
		for(int i=0; i<4; i++){
			if(row[i] > 21){
				return false;
			}else{}
		}
		*/
		return false;
	}
	public synchronized void gameOver(){
		//threadStop = true;
		JOptionPane.showMessageDialog(frm, "Game Over","Game Over",1);
		//newGame = true;	//헌 스레드를 while문 밖으로 끄집어내서 죽이기 위한 두줄
		speed=3000;
		//gThread = new GameThread();
		//gThreadRun = new Thread(gThread);
		startingGame();
	}
	public synchronized int getValue(int col, int row){
		return blockValue[col][row];
	}
	public synchronized boolean stopCheck(){	//세로 이동이 합법적인지 검사하는 메소드
		for(int i=0; i<4; i++){	//네가지 블럭 중에 하나가 세로값 22(화면끝)이 되면 false보냄
			if(getBlockRow(i) > 20){ //미리 이동시켜보고 안되면 false, 되면 true
				return false;
			}else if(blockValue[getBlockCol(i)][getBlockRow(i)+1]<0){
				return false;	//밑에 이미 블럭이 있으면 false
			}
		}
		return true;
		
	}

	public synchronized void toMinus(){	//블럭들의 값을 다 음수로 만들어주는 메소드
		for(int z=0; z<4; z++){
			blockValue[getBlockCol(z)][getBlockRow(z)] = plusToMinus(blockValue[getBlockCol(z)][getBlockRow(z)]);
		}
		
		//movable = false;
		
	}
	public synchronized int plusToMinus(int value){	//blockValue값의 양수를 음수로 만들어주는 메소드 
		value = value - (value*2);
		return value;
	}
	public synchronized int getBlockCol(int c){	//블럭의 c번째가 buttonValue의
									//몇번째 가로값을 가지고 있는지 알려주는 메소드
		c = centerX + (int)blocks[c].getX();
		return c;
		
	}
	public synchronized int getBlockRow(int c){ //블럭의 c번째가 buttonValue의
								   //몇번째 세로값을 가지고 있는지 알려주는 메소드
		c = centerY + (int)blocks[c].getY();
		return c;
	}
	public synchronized boolean horizonMovable(int a){ //수평이동 가능한가를 알려주는 메소드
		try{
			for(int b=0; b<4; b++){
				if(getBlockCol(b)+a<=-1 || getBlockCol(b)+a>=12){
					return false;	//왼쪽이나 오른쪽 끝이면 더이상 이동 불가능
				}else{
					if(blockValue[getBlockCol(b)+a][getBlockRow(b)]<0){
					return false;  //왼쪽이나 오른쪽에 이미 블럭이 있으면 이동 불가능
					}
				}
			}
		}catch(Exception e){}
			return true;
		
		
	}
	public synchronized void turnBlock(){	//블럭의 회전을 Point형 변수 blocks에 적용시키는 메소드
		if(pieceType!=3){
			for(int i=1; i<4; i++){
				int temp = blocks[i].x;	//x에 -y를 넣고 y에 x를 넣으면 회전이 된다.
				blocks[i].x = -blocks[i].y;
				blocks[i].y = temp;
				
			}
			delMovedBlock();	//이동전 위치에 있던 값은 0으로 되돌려 지워준다.
			for(int i=0; i < 4; i++){
				if(getBlockCol(i)<0 || getBlockRow(i)<0){}	//0보다 작은 배열값이 안나오도록 방지
				else{
					blockValue[getBlockCol(i)][getBlockRow(i)] = pieceType; 
				}
			}
		//Point형 blocks에 들어있는 값들을 조절해서 돌려준다.
		//단, 돌려들어가는 곳에 이미 블럭이 있으면 세로값을 하나씩 올려준다. <-이걸 반복한다.
		}
	}
	public synchronized int checkSeroMove(){	//블럭 회전시 세로에 이동할 공간이 있는지
		try{
			for(int i=0; i<4; i++){						//판별하는 메소드
				int temp = blocks[i].x;	//x에 -y를 넣고 y에 x를 넣으면 회전이 된다.
				int x = -blocks[i].y;
				int y = temp;
				if(centerY+y > 21){
						return 1; //세로 한계값을 넘어버렸을 경우엔 1을 리턴 
				}else if(centerX+x <0){			// 왜?????????????????????????????????
					return 2;	//0보다 작은 값일 경우엔 2를 리턴
				}else if(centerX+x >11){
					return 3; //11보다 큰 값일 경우엔 3을 리턴
	
				}else if(blockValue[centerX+x][centerY+y]<0){	//그 자리에 블럭이 있으면
					return 4; //그 자리에 블럭이 있으면 4를 리턴
				}
	
			}
		}catch(Exception e){}
		
		return 0; //문제 없으면 0을 리턴 
	}
	public synchronized void horizonalTurn(){	//회전시 각각 가로 끝일 때 모자라면 한칸 옆으로 
												//옮겨와 회전이 되게 해주는 메소드
		int numA=0;
		int numB=0;
		tempCenterX = centerX +1;
		int[] x = new int[4];
		int[] y = new int[4];
		for(int i=0; i<4; i++){
			int temp = blocks[i].x;
			x[i] = -blocks[i].y;
			y[i] = temp;

			if(tempCenterX+x[i]<0){ //블럭중 0보다 작은게 있으면 numA++에다 체크
				numA++;
			}
		}
		while(numA!=0){
			tempCenterX++;
			numA = 0;
			for(int a=0; a<4; a++){
				if(tempCenterX+x[a]<0){ //블럭중 0보다 작은게 있으면 numA++에다 체크
					numA++;
				}
			}
		}
		for(int j=0; j<4; j++){
			if(blockValue[tempCenterX+x[j]][centerY+y[j]]<0){ //옆으로 한칸 이동해보고 다른 블럭이 있으면 이동안함. 없으면 한칸 이동.
				numB = numB++;
			}
		}
		if(numB==0){
			undoMovingX = centerX;
			centerX = tempCenterX;
		}
	}
	public synchronized void horizonalTurn2(){	//회전시 각각 가로 끝일 때 모자라면 한칸 옆으로 
		//옮겨와 회전이 되게 해주는 메소드
		int numA=0;
		int numB=0;
		tempCenterX = centerX -1; 
		int[] x = new int[4];
		int[] y = new int[4];
		for(int i=0; i<4; i++){
			int temp = blocks[i].x;
			x[i] = -blocks[i].y;
			y[i] = temp;
		
			if(tempCenterX+x[i]>11){ //블럭중 11보다 큰게 있으면 numA++에다 체크
				numA++;
			}
		}
		while(numA!=0){
			tempCenterX--;
			numA = 0;
			for(int a=0; a<4; a++){
				if(tempCenterX+x[a]>11){ //블럭중 11보다 큰게 있으면 numA++에다 체크
					numA++;
				}
			}
		}
		for(int j=0; j<4; j++){
			if(blockValue[tempCenterX+x[j]][centerY+y[j]]<0){ //옆으로 한칸 이동해보고 다른 블럭이 있으면 이동안함. 없으면 한칸 이동.
				numB = numB++;
			}
		}
		if(numB==0){
			undoMovingX = centerX;
			centerX = tempCenterX;
		}
	}


// 일단 회전시켜보고 블럭별로 오른쪽으로 한칸씩 이동해본 후 다 0이상이면
// 그 블럭들이 가지는 값을 가지고 다른 블럭이 있는지 없는지 체크.
// 0보다 작으면 1을 더 더한후 다른 블럭이 있는지 없는지 체크.
// 없으면 무사히 가운데 블럭을 1오른쪽으로 실제로 이동시킨다.
//
	
	/*public synchronized int checkGaroMove(){
		for(int i=0; i<4; i++){
			int temp = blocks[i].x;	//x에 -y를 넣고 y에 x를 넣으면 회전이 된다.
			int x = -blocks[i].y;
			int y = temp;
			if(centerX+x <0){			//왼쪽 끝. 오른쪽으로 1이동
				return 
			}else if(centerX+x > 11){	//오른쪽 끝. 왼쪽으로 1이동
				
			}else if(blockValue[centerX+x][centerY+y]<0){	//그 자리에 블럭이 있음.
				return true;
			}
		}				
		return 0; //-> 문제없음
	}*/
	//세로가 문제면 세로로 하나 이동. 두개까지 허용하고 그래도 안되면 회전 불가.
	//가로는 왼쪽 오른쪽 하나씩 이동해보고 하나로 안되면 회전 불가.
	
	public synchronized void checkEraseLine(){	//라인 지워도 되는지를 검색해서 int형으로 그 라인을 보내줌
		int lines = 0;
		for(int i=0; i<22; i++){
			lines=0;
			for(int j=0; j<12; j++){
				if(blockValue[j][i]<0){
					lines++;
				}
			}
			if(lines==12){
				eraseLine(i); //라인이 다 차면 지울 행 번호를 eraseLine메소드로 보내준다.
			}
		}
	}
	public synchronized void eraseLine(int y){	//실제로 라인을 지워주는 메소드
		for(int a=0; a<12; a++){
			blockValue[a][y] = 0;	//일단 블럭들을 다 지운다.
		}

				for(int j=(y-1); j>0; j--){	//y보다 위에 있는 열들을 다 밑으로 한칸씩 이동 
			for(int i=0; i<12; i++){
				if(blockValue[i][j]<=0){	//고정블럭들을 한칸씩 밑으로 내려줌
					blockValue[i][j+1] = blockValue[i][j];
				}
			}
		}
		erasedLines++;
		sLines++;
		linesLabel.setText("<html><br><font size=6>"+erasedLines);
		//먼저 해당라인을 지워준다.
	//지운 공간으로 위에 있는 전체 블럭들을 한칸씩 내려준다.
	}
	public synchronized void stageUp(){
		if(sLines>=15){	//지워지는 라인이 15개일때마다 스테이지업
			if(stage<12){
				stage++;

				stageLabel.setText("<html><br><font size=6>"+stage);
				sLines=0;
			//bg01Image.setImage(bgURL[changeBG()]);  //배경화면1
				bg01Panel.repaint();
			}
			switch(stage){
			case 1 : speed=1000;
					 break;
			case 2 : speed=800;
			 		 break;
			case 3 : speed=700;
	 		 		 break;
			case 4 : speed=600;
	 		 		 break;
			case 5 : speed=500;
	 		 		 break;
			case 6 : speed=400;
	 		         break;
			case 7 : speed=300;
	         		 break;
			case 8 : speed=200;
    		 		 break;
			case 9 : speed=150;
	 		 		 break;
			case 10 : speed=100;
	 		 		  break;
			case 11 : speed=80;
					  break;
			default : speed=60;
	  		  		  break;
				
			}
		}
		//라인이 일정 갯수 이상 지워지면 다음 스테이지로 넘어간다.
	}
	public synchronized int changeBG(){	//백그라운드를 바꿔주는 메소드
		if(stage<12){
			return stage;
		}else{
			return 11;
		}
	}
	
	public synchronized void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
			case KeyEvent.VK_LEFT  : 
									 if(gameStart == true){
									 //if(movable){
											if(horizonMovable(-1)){
												 centerX--;
												 moveBlock();
												 gBoard.setBlockValue(blockValue);
												 gBoard.repaint();
	/*											 if(!stopCheck()){
													 try{
														 Thread.sleep(500);
													 }catch(Exception ex){}
												 }*/
											}
									 }
									//}else{}
											 break;

			case KeyEvent.VK_RIGHT :
									if(gameStart == true){
				 					 //if(movable){
				 						 if(horizonMovable(+1)){
					 						 centerX++;
					 						 moveBlock();
											 gBoard.setBlockValue(blockValue);
					 						 gBoard.repaint();
/*											 if(!stopCheck()){
												 try{
													 Thread.sleep(500);
												 }catch(Exception ex){}
											 }*/
				 						 }
				 					 //}else{}
									}
									 break;
			case KeyEvent.VK_DOWN  : 
									if(gameStart == true){
									 if(!stopFallingBlock){
									 	 gThreadRun.interrupt();
										 tempSpeed = speed;
										 speed = 50;
										 stopFallingBlock = true;
									 }
									}
									 //게임시작하자마자 스레드 생성시부터 누르고 있으면
									 //널포인트 예외 발생 -> 예외처리 필요
									 break;
			case KeyEvent.VK_SPACE : 
									if(gameStart == true){
									 if(!dropBlock){
										 gThreadRun.interrupt();
										 fix = false;
										 dropBlock = true;
									 }
				/*
									 while(stopCheck()){
										centerY++;
										moveBlock();
									 }
									 toMinus();	//블럭이 멈춰야 하면 그 자리수를 음수로 변경
									 gBoard.setBlockValue(blockValue);
									 gBoard.repaint();
									 */
									}
									 break;
									 //연속으로 떨어졌을 때 안느려지는 문제 해결 위해선
									 //다른 스레드를 생성하고 있는 내부클래스내에서
									 //해결해야 하는 문제 아닐까?
			case KeyEvent.VK_UP    :
									if(gameStart == true){
					 					 if(checkSeroMove()==0){
					 						 turnBlock();
					 						 gBoard.setBlockValue(blockValue);
					 						 gBoard.repaint();
										 }else if(checkSeroMove()==2){
											 horizonalTurn();
											 if(checkSeroMove()==0){
												 turnBlock();
						 						 gBoard.setBlockValue(blockValue);
						 						 gBoard.repaint();
											 }else{
												 centerX = undoMovingX;
											 }
										 }else if(checkSeroMove()==3){
											 horizonalTurn2();	//왼쪽이 막혀있는 경우 회전시켰을 때 여기서 이미 가로값을 이동시켰는데
											 if(checkSeroMove()==0){	//여기서 회전불가로 시켜버리니 결과적으로 가로값만 이동하는 결과가 된다.
												 turnBlock();
						 						 gBoard.setBlockValue(blockValue);
						 						 gBoard.repaint();
											 }else{
												 centerX = undoMovingX;
											 }
	
										 }
									}
									 break;
									 //회전시 왼쪽끝이나 오른쪽 끝으로 나가면 회전이 안되어야 하는데 예외 발생 문제!!!
									 //왼쪽이나 오른쪽에 블럭이 있을 때도 회전이 되는 문제 !!!
		}

	}
    public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
			case KeyEvent.VK_DOWN  : speed = tempSpeed;
									 stopFallingBlock = false;
									 break;
			case KeyEvent.VK_SPACE : 
									 dropBlock = false;
									 break;

		}
    }
    
    public void keyTyped(KeyEvent e) {}

    
    
	//움직이는 블럭은 양수로, 안움직이는 블럭은 음수로 나타낼 것.
	class GameThread implements Runnable {	//스레드 발생을 위한 내부클래스

		
		public synchronized void run() {
			//전체 맵에 더이상 움직일 수 있는 블럭이 없으면 makeBlock()실행
				//Thread.currentThread().getContextClassLoader(); 
			gameStart = true;
			while(true){	
				while(!checkGameOver()){
					if(threadStop){
						try{
							wait();
						}catch(Exception e){}
					}
					System.out.print("f");
					if(!checkMovableBlock()){	//움직일 수 있는 블럭이 없으면
						//움직일 수 있는 블럭이 있으면 일단은 지우고 다음꺼가 온다.
						makeBlock();	//새 블럭을 만드세용.
						previewPanel.repaint();
					}
					//delMovedBlock();

					
						if(stopCheck()){	//밑이 다른 블럭이나 최고 밑 같은 블럭이 멈춰야 하는 조건인가 아닌가 판별
							centerY++;	// 멈추지 않아도 된다면 Y좌표 +1
							moveBlock();	//실제로 블럭을 이동시킴
						}else{
							if(fix){ //스페이스(드롭)이 아닌 경우에는 블록이 완전히 고정될때까지 조금 시간차를 둔다.
								try{
									Thread.sleep(700);
								}catch(Exception ex){}
							}
							if(stopCheck()){
								centerY++;
								moveBlock();
							}else{
								toMinus();	//블럭이 멈춰야 하면 그 자리수를 음수로 변경
								dropBlock = false;
								fix = true;
							}
						}
						checkEraseLine();
						gBoard.setBlockValue(blockValue); //blockValue배열을 넘겨줌
						gBoard.repaint();
						stageUp();

					/*
					for(int ddd=0; ddd<4; ddd++){
						System.out.print(ddd+"의 가로값 : "+getBlockCol(ddd)+"  ");
						System.out.println(ddd+"의 세로값 : "+getBlockRow(ddd));
					}
					*/
						/*
					for(int yyy=0; yyy<12; yyy++){
						System.out.println(yyy+",20 : "+blockValue[yyy][20]);
					}
					for(int xxx=0; xxx<12; xxx++){
						System.out.println(xxx+",21 : "+blockValue[xxx][21]);
					}
						 */
					/*if(!frm.isActive()){
						try{
							Thread.sleep(0);
						}catch(Exception e){}
					}*/
					if(!dropBlock){
						try{
							Thread.sleep(speed);	//레벨 스피드
						}catch(Exception e){}
					}
					//System.out.println(Thread.activeCount());
					//System.out.println(speed);
					//movable = true;
					//newGame = false;
					//System.out.println(Thread.currentThread());
				}
				gameOver();

			}
		}
	}

	
}


// 1.작대기일때만 버그 발생
// 2.버튼을 좌우로 연타할 때 발생
// 3.바닥에 닿아있는 상태에서만 발생
// 4.sleep무력화. 세로 이동 진행도 안됨. 그냥 스레드가 멈추는 상태.
//
//-->checkMovableBlock()나 toMinus()에 문제가 있을 가능성
//
//
//
