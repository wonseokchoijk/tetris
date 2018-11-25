import java.awt.*;
import javax.swing.*;
import java.net.URL;
import classes.*;
import java.awt.event.*;

public class Tetris00 implements KeyListener, ActionListener{

	int blockValue[][] = new int[12][22];
		//�Ⱥ��̴� ���� ���� ������ ����.
						
	JFrame frm;
	GameBoard gBoard = new GameBoard();
	Pieces piece = new Pieces();
	Pieces prePiece = new Pieces();
	GameThread gThread;
	Thread gThreadRun;
	private int pieceType;	// ���� ���� ����
	private int prePieceType;	// �� ������ ���� ���� ����
	Point[] blocks = new Point[4];
	Point[] preBlocks = new Point[4]; // ���� �� â�� ���� ��.
	int centerX, centerY;
	int preX,preY;	//������ ���� ���� ǥ���� ������ǥ
    JPanel previewPanel;
    JLabel stageLabel,linesLabel;
    //JMenuItem restart;
    JMenuItem restart;
    JMenuItem about;
    //boolean newGame;
    boolean gameStart = false;
    boolean stopFallingBlock = false; //���2��� �������� ������Ű�� ����
    //boolean stopFallingBlock2 = false; //�����̽� ��� ������ ���� �� �������� ������Ű�� ����
    boolean dropBlock = false; //�����̽� ������ ���� ���� ���� �ѹ��� �������� �ϴ� �Ҹ� ����
    boolean threadStop = false;
	boolean firstBlock = true;	//������ ù��� �ʱ�ȭ
	boolean fix = true;


    int stage = 1;	//ȭ�鿡 ǥ�õǴ� �������� 
    int erasedLines;	//ȭ�鿡 ǥ�õǴ� ���� ���� ��
    int sLines=0;
    int speed; //���ӽ��ǵ� - ������ ������ ������ ������.
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
		
   	 	JMenuBar menubar = new JMenuBar();	//�޴��� ����
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
		 * ����� �Ǵ� bg01SP�� �ְ� ���ʿ��� stage,lines��, �����ʿ��� ���� ���� ��Ÿ�� ����(preview)�� �ִ�.
		 * ������� PanelBGSP(�׵θ� �̹����� ���ִ�)�� ����.
		 * PanelBGSP �ȿ��� gBoard�� ����ִ�.
		 */
   	 	
   		
   	 	/*JPanel gBoardOut = new JPanel(){
   			public void paintComponent(Graphics g){
   				g.setColor(new Color(60,60,60));
   				g.fillRect(0,0,this.getWidth(),this.getHeight());
   			}	
   		};*/
   		  	 	
   	 	
	    for(int ii=0; ii<bgURL.length; ii++){
   	 		bgURL[ii] = getClass().getClassLoader().getResource("images/bg"+Integer.toString(ii+1)+".jpg"); //���ȭ�� �̹��� ��������
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
	    panelBGImage = new ImageIcon(panelBGURL);  //���� �� �׵θ�
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
	    previewImage = new ImageIcon(previewURL);  //���� ������ ǥ�õ� �簢 �����̽�
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
		    			g.drawRect(preX, preY, 24,24);	//���� ��1
            		}

            	//g.drawImage(previewImage.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        previewPanel.setLayout(new FlowLayout());
        
        stageURL = getClass().getClassLoader().getResource("images/stage.gif");
	    stageImage = new ImageIcon(stageURL);  //stage ǥ�ùڽ�
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
	    linesImage = new ImageIcon(linesURL);  //lines ǥ�ùڽ�
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
        
        /*���������� ���� �ǳ� ����
         *stageLines(BorderLayout)���� stageLinesSouth(South)�� ���� 
         *stageLinesSouth(FlowLayout)�ȿ��� stageLinesInner(FlowLayout)�� ����.
         *stageLinesInner�ȿ��� stagePanel, linesPanel�� ����.
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
	    gBoard.setPreferredSize(new Dimension(301, 500)); //���μ���25, ����12�� ���� 20��
	    //gBoardOut.setPreferredSize(new Dimension(301, 508)); //���μ���25, ����12�� ���� 20��
	    
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
				blockValue[i][j] = 0; 		//���� �ƹ��͵� ������� ���� �� 0
			}
		}
		stopFallingBlock = false; //���2��� �������� ������Ű�� ����
	    dropBlock = false; //�����̽� ������ ���� ���� ���� �ѹ��� �������� �ϴ� �Ҹ� ����
	    threadStop = false;
		firstBlock = true;	//������ ù��� �ʱ�ȭ
		fix = true;
		
	    stage = 1;	//ȭ�鿡 ǥ�õǴ� ��������
		stageLabel.setText("<html><br><font size=6>"+stage);
	    erasedLines=0;	//ȭ�鿡 ǥ�õǴ� ���� ���� ��
		linesLabel.setText("<html><br><font size=6>"+erasedLines);
	    sLines=0;
		bg01Panel.repaint();
	    speed = 1000; //���ӽ��ǵ� - ������ ������ ������ ������.
		firstBlock = true;	//������ ù��� �ʱ�ȭ


		
	}
 	public void actionPerformed(ActionEvent e) // �޴� �׼� ������ ����
	{
		if(e.getActionCommand().equals(restart.getActionCommand())){
			//newGame = true;
			speed=1;

			startingGame();
			//���α׷� ����
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
				//about����ǥ��
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
	public synchronized void moveBlock(){	//Point�� blocks�� ����ִ� ����
										//���� blockValue�迭�� ������Ѽ� ���� �̵��� �����ϴ� �޼ҵ�
		delMovedBlock();	//�̵��� ��ġ�� �ִ� ���� 0���� �ǵ��� �����ش�.
		
		for(int i=0; i < 4; i++){
			if(getBlockCol(i)<0 || getBlockRow(i)<0){}	//0���� ���� �迭���� �ȳ������� ����
			else{
				blockValue[getBlockCol(i)][getBlockRow(i)] = pieceType; 
			}
		}
	}

	public synchronized void delMovedBlock(){	//�̵� �Ŀ� �̵� ���� �ִ� ��ġ�� ���� �����ִ� �޼ҵ�
		
		for(int i=0; i<12; i++){
			for(int j=0; j<22; j++){
				if(blockValue[i][j]>0){
					blockValue[i][j]=0; //�����̴� ���� �̵��ϰ��� �����ڸ��� 0���� ������
				}
			}
		}
	}
	public synchronized boolean checkMovableBlock(){	//0�̸� �ƹ��͵� ���� ��,
										//������ �ȿ����̴� ��, ����� �����̴� ��
		for(int i=0; i < 12; i++){
			for(int j=0; j < 22; j++){
				if(blockValue[i][j] > 0){
					//System.out.print("true");
					
					return true;

				}		//�����̴� ���� ������ true,
			}			//���̻� �����̴� ���� ������ false�� ����´�.
		}
		//System.out.print("false");
		return false;

	}
	public boolean checkGameOver(){	
		//���ӿ����� �ƴ��� üũ�ϴ� �޼ҵ�
		//���ӿ����� �Ǹ� true�� ����.
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
		//newGame = true;	//�� �����带 while�� ������ ������� ���̱� ���� ����
		speed=3000;
		//gThread = new GameThread();
		//gThreadRun = new Thread(gThread);
		startingGame();
	}
	public synchronized int getValue(int col, int row){
		return blockValue[col][row];
	}
	public synchronized boolean stopCheck(){	//���� �̵��� �չ������� �˻��ϴ� �޼ҵ�
		for(int i=0; i<4; i++){	//�װ��� �� �߿� �ϳ��� ���ΰ� 22(ȭ�鳡)�� �Ǹ� false����
			if(getBlockRow(i) > 20){ //�̸� �̵����Ѻ��� �ȵǸ� false, �Ǹ� true
				return false;
			}else if(blockValue[getBlockCol(i)][getBlockRow(i)+1]<0){
				return false;	//�ؿ� �̹� ���� ������ false
			}
		}
		return true;
		
	}

	public synchronized void toMinus(){	//������ ���� �� ������ ������ִ� �޼ҵ�
		for(int z=0; z<4; z++){
			blockValue[getBlockCol(z)][getBlockRow(z)] = plusToMinus(blockValue[getBlockCol(z)][getBlockRow(z)]);
		}
		
		//movable = false;
		
	}
	public synchronized int plusToMinus(int value){	//blockValue���� ����� ������ ������ִ� �޼ҵ� 
		value = value - (value*2);
		return value;
	}
	public synchronized int getBlockCol(int c){	//���� c��°�� buttonValue��
									//���° ���ΰ��� ������ �ִ��� �˷��ִ� �޼ҵ�
		c = centerX + (int)blocks[c].getX();
		return c;
		
	}
	public synchronized int getBlockRow(int c){ //���� c��°�� buttonValue��
								   //���° ���ΰ��� ������ �ִ��� �˷��ִ� �޼ҵ�
		c = centerY + (int)blocks[c].getY();
		return c;
	}
	public synchronized boolean horizonMovable(int a){ //�����̵� �����Ѱ��� �˷��ִ� �޼ҵ�
		try{
			for(int b=0; b<4; b++){
				if(getBlockCol(b)+a<=-1 || getBlockCol(b)+a>=12){
					return false;	//�����̳� ������ ���̸� ���̻� �̵� �Ұ���
				}else{
					if(blockValue[getBlockCol(b)+a][getBlockRow(b)]<0){
					return false;  //�����̳� �����ʿ� �̹� ���� ������ �̵� �Ұ���
					}
				}
			}
		}catch(Exception e){}
			return true;
		
		
	}
	public synchronized void turnBlock(){	//���� ȸ���� Point�� ���� blocks�� �����Ű�� �޼ҵ�
		if(pieceType!=3){
			for(int i=1; i<4; i++){
				int temp = blocks[i].x;	//x�� -y�� �ְ� y�� x�� ������ ȸ���� �ȴ�.
				blocks[i].x = -blocks[i].y;
				blocks[i].y = temp;
				
			}
			delMovedBlock();	//�̵��� ��ġ�� �ִ� ���� 0���� �ǵ��� �����ش�.
			for(int i=0; i < 4; i++){
				if(getBlockCol(i)<0 || getBlockRow(i)<0){}	//0���� ���� �迭���� �ȳ������� ����
				else{
					blockValue[getBlockCol(i)][getBlockRow(i)] = pieceType; 
				}
			}
		//Point�� blocks�� ����ִ� ������ �����ؼ� �����ش�.
		//��, �������� ���� �̹� ���� ������ ���ΰ��� �ϳ��� �÷��ش�. <-�̰� �ݺ��Ѵ�.
		}
	}
	public synchronized int checkSeroMove(){	//�� ȸ���� ���ο� �̵��� ������ �ִ���
		try{
			for(int i=0; i<4; i++){						//�Ǻ��ϴ� �޼ҵ�
				int temp = blocks[i].x;	//x�� -y�� �ְ� y�� x�� ������ ȸ���� �ȴ�.
				int x = -blocks[i].y;
				int y = temp;
				if(centerY+y > 21){
						return 1; //���� �Ѱ谪�� �Ѿ������ ��쿣 1�� ���� 
				}else if(centerX+x <0){			// ��?????????????????????????????????
					return 2;	//0���� ���� ���� ��쿣 2�� ����
				}else if(centerX+x >11){
					return 3; //11���� ū ���� ��쿣 3�� ����
	
				}else if(blockValue[centerX+x][centerY+y]<0){	//�� �ڸ��� ���� ������
					return 4; //�� �ڸ��� ���� ������ 4�� ����
				}
	
			}
		}catch(Exception e){}
		
		return 0; //���� ������ 0�� ���� 
	}
	public synchronized void horizonalTurn(){	//ȸ���� ���� ���� ���� �� ���ڶ�� ��ĭ ������ 
												//�Űܿ� ȸ���� �ǰ� ���ִ� �޼ҵ�
		int numA=0;
		int numB=0;
		tempCenterX = centerX +1;
		int[] x = new int[4];
		int[] y = new int[4];
		for(int i=0; i<4; i++){
			int temp = blocks[i].x;
			x[i] = -blocks[i].y;
			y[i] = temp;

			if(tempCenterX+x[i]<0){ //���� 0���� ������ ������ numA++���� üũ
				numA++;
			}
		}
		while(numA!=0){
			tempCenterX++;
			numA = 0;
			for(int a=0; a<4; a++){
				if(tempCenterX+x[a]<0){ //���� 0���� ������ ������ numA++���� üũ
					numA++;
				}
			}
		}
		for(int j=0; j<4; j++){
			if(blockValue[tempCenterX+x[j]][centerY+y[j]]<0){ //������ ��ĭ �̵��غ��� �ٸ� ���� ������ �̵�����. ������ ��ĭ �̵�.
				numB = numB++;
			}
		}
		if(numB==0){
			undoMovingX = centerX;
			centerX = tempCenterX;
		}
	}
	public synchronized void horizonalTurn2(){	//ȸ���� ���� ���� ���� �� ���ڶ�� ��ĭ ������ 
		//�Űܿ� ȸ���� �ǰ� ���ִ� �޼ҵ�
		int numA=0;
		int numB=0;
		tempCenterX = centerX -1; 
		int[] x = new int[4];
		int[] y = new int[4];
		for(int i=0; i<4; i++){
			int temp = blocks[i].x;
			x[i] = -blocks[i].y;
			y[i] = temp;
		
			if(tempCenterX+x[i]>11){ //���� 11���� ū�� ������ numA++���� üũ
				numA++;
			}
		}
		while(numA!=0){
			tempCenterX--;
			numA = 0;
			for(int a=0; a<4; a++){
				if(tempCenterX+x[a]>11){ //���� 11���� ū�� ������ numA++���� üũ
					numA++;
				}
			}
		}
		for(int j=0; j<4; j++){
			if(blockValue[tempCenterX+x[j]][centerY+y[j]]<0){ //������ ��ĭ �̵��غ��� �ٸ� ���� ������ �̵�����. ������ ��ĭ �̵�.
				numB = numB++;
			}
		}
		if(numB==0){
			undoMovingX = centerX;
			centerX = tempCenterX;
		}
	}


// �ϴ� ȸ�����Ѻ��� ������ ���������� ��ĭ�� �̵��غ� �� �� 0�̻��̸�
// �� ������ ������ ���� ������ �ٸ� ���� �ִ��� ������ üũ.
// 0���� ������ 1�� �� ������ �ٸ� ���� �ִ��� ������ üũ.
// ������ ������ ��� ���� 1���������� ������ �̵���Ų��.
//
	
	/*public synchronized int checkGaroMove(){
		for(int i=0; i<4; i++){
			int temp = blocks[i].x;	//x�� -y�� �ְ� y�� x�� ������ ȸ���� �ȴ�.
			int x = -blocks[i].y;
			int y = temp;
			if(centerX+x <0){			//���� ��. ���������� 1�̵�
				return 
			}else if(centerX+x > 11){	//������ ��. �������� 1�̵�
				
			}else if(blockValue[centerX+x][centerY+y]<0){	//�� �ڸ��� ���� ����.
				return true;
			}
		}				
		return 0; //-> ��������
	}*/
	//���ΰ� ������ ���η� �ϳ� �̵�. �ΰ����� ����ϰ� �׷��� �ȵǸ� ȸ�� �Ұ�.
	//���δ� ���� ������ �ϳ��� �̵��غ��� �ϳ��� �ȵǸ� ȸ�� �Ұ�.
	
	public synchronized void checkEraseLine(){	//���� ������ �Ǵ����� �˻��ؼ� int������ �� ������ ������
		int lines = 0;
		for(int i=0; i<22; i++){
			lines=0;
			for(int j=0; j<12; j++){
				if(blockValue[j][i]<0){
					lines++;
				}
			}
			if(lines==12){
				eraseLine(i); //������ �� ���� ���� �� ��ȣ�� eraseLine�޼ҵ�� �����ش�.
			}
		}
	}
	public synchronized void eraseLine(int y){	//������ ������ �����ִ� �޼ҵ�
		for(int a=0; a<12; a++){
			blockValue[a][y] = 0;	//�ϴ� ������ �� �����.
		}

				for(int j=(y-1); j>0; j--){	//y���� ���� �ִ� ������ �� ������ ��ĭ�� �̵� 
			for(int i=0; i<12; i++){
				if(blockValue[i][j]<=0){	//���������� ��ĭ�� ������ ������
					blockValue[i][j+1] = blockValue[i][j];
				}
			}
		}
		erasedLines++;
		sLines++;
		linesLabel.setText("<html><br><font size=6>"+erasedLines);
		//���� �ش������ �����ش�.
	//���� �������� ���� �ִ� ��ü ������ ��ĭ�� �����ش�.
	}
	public synchronized void stageUp(){
		if(sLines>=15){	//�������� ������ 15���϶����� ����������
			if(stage<12){
				stage++;

				stageLabel.setText("<html><br><font size=6>"+stage);
				sLines=0;
			//bg01Image.setImage(bgURL[changeBG()]);  //���ȭ��1
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
		//������ ���� ���� �̻� �������� ���� ���������� �Ѿ��.
	}
	public synchronized int changeBG(){	//��׶��带 �ٲ��ִ� �޼ҵ�
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
									 //���ӽ������ڸ��� ������ �����ú��� ������ ������
									 //������Ʈ ���� �߻� -> ����ó�� �ʿ�
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
									 toMinus();	//���� ����� �ϸ� �� �ڸ����� ������ ����
									 gBoard.setBlockValue(blockValue);
									 gBoard.repaint();
									 */
									}
									 break;
									 //�������� �������� �� �ȴ������� ���� �ذ� ���ؼ�
									 //�ٸ� �����带 �����ϰ� �ִ� ����Ŭ����������
									 //�ذ��ؾ� �ϴ� ���� �ƴұ�?
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
											 horizonalTurn2();	//������ �����ִ� ��� ȸ�������� �� ���⼭ �̹� ���ΰ��� �̵����״µ�
											 if(checkSeroMove()==0){	//���⼭ ȸ���Ұ��� ���ѹ����� ��������� ���ΰ��� �̵��ϴ� ����� �ȴ�.
												 turnBlock();
						 						 gBoard.setBlockValue(blockValue);
						 						 gBoard.repaint();
											 }else{
												 centerX = undoMovingX;
											 }
	
										 }
									}
									 break;
									 //ȸ���� ���ʳ��̳� ������ ������ ������ ȸ���� �ȵǾ�� �ϴµ� ���� �߻� ����!!!
									 //�����̳� �����ʿ� ���� ���� ���� ȸ���� �Ǵ� ���� !!!
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

    
    
	//�����̴� ���� �����, �ȿ����̴� ���� ������ ��Ÿ�� ��.
	class GameThread implements Runnable {	//������ �߻��� ���� ����Ŭ����

		
		public synchronized void run() {
			//��ü �ʿ� ���̻� ������ �� �ִ� ���� ������ makeBlock()����
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
					if(!checkMovableBlock()){	//������ �� �ִ� ���� ������
						//������ �� �ִ� ���� ������ �ϴ��� ����� �������� �´�.
						makeBlock();	//�� ���� ���弼��.
						previewPanel.repaint();
					}
					//delMovedBlock();

					
						if(stopCheck()){	//���� �ٸ� ���̳� �ְ� �� ���� ���� ����� �ϴ� �����ΰ� �ƴѰ� �Ǻ�
							centerY++;	// ������ �ʾƵ� �ȴٸ� Y��ǥ +1
							moveBlock();	//������ ���� �̵���Ŵ
						}else{
							if(fix){ //�����̽�(���)�� �ƴ� ��쿡�� ����� ������ �����ɶ����� ���� �ð����� �д�.
								try{
									Thread.sleep(700);
								}catch(Exception ex){}
							}
							if(stopCheck()){
								centerY++;
								moveBlock();
							}else{
								toMinus();	//���� ����� �ϸ� �� �ڸ����� ������ ����
								dropBlock = false;
								fix = true;
							}
						}
						checkEraseLine();
						gBoard.setBlockValue(blockValue); //blockValue�迭�� �Ѱ���
						gBoard.repaint();
						stageUp();

					/*
					for(int ddd=0; ddd<4; ddd++){
						System.out.print(ddd+"�� ���ΰ� : "+getBlockCol(ddd)+"  ");
						System.out.println(ddd+"�� ���ΰ� : "+getBlockRow(ddd));
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
							Thread.sleep(speed);	//���� ���ǵ�
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


// 1.�۴���϶��� ���� �߻�
// 2.��ư�� �¿�� ��Ÿ�� �� �߻�
// 3.�ٴڿ� ����ִ� ���¿����� �߻�
// 4.sleep����ȭ. ���� �̵� ���൵ �ȵ�. �׳� �����尡 ���ߴ� ����.
//
//-->checkMovableBlock()�� toMinus()�� ������ ���� ���ɼ�
//
//
//
