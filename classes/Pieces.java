package classes;
import java.awt.Point;

public class Pieces {
	public static final int typeI = 1;
	public static final int typeT = 2;
	public static final int typeO = 3;
	public static final int typeL = 4;
	public static final int typeJ = 5;
	public static final int typeS = 6;
	public static final int typeZ = 7;
	
	private Point[] pValue = new Point[4]; //각 조각들의 좌표값


	public void setBlock(int type){
		switch(type){
			case typeI : pValue[0] = new Point( 0, 0);	//중앙 블럭
						 pValue[1] = new Point(-1, 0);
						 pValue[2] = new Point(-2, 0);
						 pValue[3] = new Point( 1, 0);
						 break;
			case typeT : pValue[0] = new Point( 0, 0);	//중앙 블럭
			 			 pValue[1] = new Point(-1, 0);
			 			 pValue[2] = new Point( 1, 0);
			 			 pValue[3] = new Point( 0,-1);	//ok
			 			 break;
			case typeO : pValue[0] = new Point( 0, 0);	//중앙 블럭
						 pValue[1] = new Point(-1, 0);
						 pValue[2] = new Point(-1,-1);
						 pValue[3] = new Point( 0,-1);
						 break;
			case typeL : pValue[0] = new Point( 0, 0);	//중앙 블럭
						 pValue[1] = new Point(-1, 0);
						 pValue[2] = new Point( 1, 0);
						 pValue[3] = new Point( 1,-1);	//ok
						 break;
			case typeJ : pValue[0] = new Point( 0, 0);	//중앙 블럭
			 			 pValue[1] = new Point(-1, 0);
			 			 pValue[2] = new Point( 1, 0);
			 			 pValue[3] = new Point(-1,-1);	//ok
			 			 break;
			case typeS : pValue[0] = new Point( 0, 0);	//중앙 블럭
			 			 pValue[1] = new Point(-1, 0);
			 			 pValue[2] = new Point( 0,-1);
			 			 pValue[3] = new Point( 1,-1);	//ok
			 			 break;
			case typeZ : pValue[0] = new Point( 0, 0);	//중앙 블럭
			 			 pValue[1] = new Point( 0,-1);
			 			 pValue[2] = new Point(-1,-1);
			 			 pValue[3] = new Point( 1, 0);
			 			 break;
		}
	}
	public Point[] getBlock(){
		return pValue; 	
	}
 
	
	//모든 블럭은 가로7 세로 1에 블럭이 나타난다.

	//OTJI ZSL
}
