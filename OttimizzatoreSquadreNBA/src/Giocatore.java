import java.lang.reflect.Field;

public class Giocatore {
	
	private String Player;
	private Position Pos;
	private String Team;
	private double PDK;
	private double CR;
	private int GP;
	private int MIN;
	private int ST;
	private int PTS;
	private int REB;
	private int AST;
	private int STL;
	private int BLK;
	private int BA;
	private int MFG;
	private int AFG;
	private int PERC_FG;
	private int M3P;
	private int A3P;
	private int PERC_3P;
	private int MFT;
	private int AFT;
	private int PERC_FT;
	private int OREB;
	private int DREB;
	private int TOV;
	private int PF;
	private int FD;
	private int PLUS_MINUS;
	
	
	public String getPlayer() {
		return Player;
	}
	public void setPlayer(String player) {
		Player = player;
	}
	public Position getPos() {
		return Pos;
	}
	public void setPos(Position pos) {
		Pos = pos;
	}
	public String getTeam() {
		return Team;
	}
	public void setTeam(String team) {
		Team = team;
	}
	public double getPDK() {
		return PDK;
	}
	public void setPDK(double pDK) {
		PDK = pDK;
	}
	public double getCR() {
		return CR;
	}
	public void setCR(double cR) {
		CR = cR;
	}
	public int getGP() {
		return GP;
	}
	public void setGP(int gP) {
		GP = gP;
	}
	public int getMIN() {
		return MIN;
	}
	public void setMIN(int mIN) {
		MIN = mIN;
	}
	public int getST() {
		return ST;
	}
	public void setST(int sT) {
		ST = sT;
	}
	public int getPTS() {
		return PTS;
	}
	public void setPTS(int pTS) {
		PTS = pTS;
	}
	public int getREB() {
		return REB;
	}
	public void setREB(int rEB) {
		REB = rEB;
	}
	public int getAST() {
		return AST;
	}
	public void setAST(int aST) {
		AST = aST;
	}
	public int getSTL() {
		return STL;
	}
	public void setSTL(int sTL) {
		STL = sTL;
	}
	public int getBLK() {
		return BLK;
	}
	public void setBLK(int bLK) {
		BLK = bLK;
	}
	public int getBA() {
		return BA;
	}
	public void setBA(int bA) {
		BA = bA;
	}
	public int getMFG() {
		return MFG;
	}
	public void setMFG(int mFG) {
		MFG = mFG;
	}
	public int getAFG() {
		return AFG;
	}
	public void setAFG(int aFG) {
		AFG = aFG;
	}
	public int getPERC_FG() {
		return PERC_FG;
	}
	public void setPERC_FG(int pERC_FG) {
		PERC_FG = pERC_FG;
	}
	public int getM3P() {
		return M3P;
	}
	public void setM3P(int m3p) {
		M3P = m3p;
	}
	public int getA3P() {
		return A3P;
	}
	public void setA3P(int a3p) {
		A3P = a3p;
	}
	public int getPERC_3P() {
		return PERC_3P;
	}
	public void setPERC_3P(int pERC_3P) {
		PERC_3P = pERC_3P;
	}
	public int getMFT() {
		return MFT;
	}
	public void setMFT(int mFT) {
		MFT = mFT;
	}
	public int getAFT() {
		return AFT;
	}
	public void setAFT(int aFT) {
		AFT = aFT;
	}
	public int getPERC_FT() {
		return PERC_FT;
	}
	public void setPERC_FT(int pERC_FT) {
		PERC_FT = pERC_FT;
	}
	public int getOREB() {
		return OREB;
	}
	public void setOREB(int oREB) {
		OREB = oREB;
	}
	public int getDREB() {
		return DREB;
	}
	public void setDREB(int dREB) {
		DREB = dREB;
	}
	public int getTOV() {
		return TOV;
	}
	public void setTOV(int tOV) {
		TOV = tOV;
	}
	public int getPF() {
		return PF;
	}
	public void setPF(int pF) {
		PF = pF;
	}
	public int getFD() {
		return FD;
	}
	public void setFD(int fD) {
		FD = fD;
	}
	public int getPLUS_MINUS() {
		return PLUS_MINUS;
	}
	public void setPLUS_MINUS(int pLUS_MINUS) {
		PLUS_MINUS = pLUS_MINUS;
	}
	
	public void printInfo() {
		for(Field f: Giocatore.class.getDeclaredFields()) {
			f.setAccessible(true);
			try {
				
				System.out.print(f.get(this).toString() + "\t");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
	}
}
