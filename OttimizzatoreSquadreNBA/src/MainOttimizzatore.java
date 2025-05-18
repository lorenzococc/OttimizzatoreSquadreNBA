import java.util.ArrayList;

import com.gurobi.gurobi.GRBEnv;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBLinExpr;
import com.gurobi.gurobi.GRBModel;
import com.gurobi.gurobi.GRBVar;
import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRB.IntParam;

public class MainOttimizzatore {

	public static void main(String[] args) {
		
		try
		{
			GRBEnv env = new GRBEnv("GurobiNBA.log");
			
			env.set(IntParam.Presolve, 0);
			env.set(IntParam.Method, 0);
			
			GRBModel model = new GRBModel(env);
			
			
		//Lettura dal file
			ArrayList<Giocatore> listaGiocatori = Reader.readGiocatori();
			
			
			int contaG =0;
			int contaF =0;
			int contaC =0;
			for(Giocatore g: listaGiocatori) {
				switch (g.getPos()) {			
				case G: contaG++;
					break;
				case F: contaF++;
					break;
				case C: contaC++;
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + g.getPos());
				}
			}
			
			
			
		//Inserimento parametri
			
			int B = 95; //Crediti Dunkest
			int N = listaGiocatori.size(); // Specificare il numero di giocatori tra cui scegliere
			int MPG = 48;
			int NumOfPlayers = 10;
			

			String[][] PLAYER = new String [3][];
			Position[][] Pos = new Position[3][];
			String[][] Team = new String [3][];
			Float[][] PDK = new Float[3][];
			Float[][] CR = new Float[3][];
			int[][] GP = new int[3][];
			int[][] MIN = new int[3][];
			int[][] ST = new int[3][];
			int[][] PTS = new int[3][];
			int[][] REB = new int[3][];
			int[][] AST = new int[3][];
			int[][] STL = new int[3][];
			int[][] BLK = new int[3][];
			int[][] BA = new int[3][];
			int[][] MFG = new int[3][];
			int[][] AFG = new int[3][];
			int[][] PERC_FG = new int[3][];
			int[][] M3P = new int[3][];
			int[][] A3P = new int[3][];
			int[][] PERC_3P = new int[3][];
			int[][] MFT = new int[3][];
			int[][] AFT = new int[3][];
			int[][] PERC_FT = new int[3][];
			int[][] OREB = new int[3][];
			int[][] DREB = new int[3][];
			int[][] TOV = new int[3][];
			int[][] PF = new int[3][];
			int[][] FD = new int[3][];
			int[][] PLUS_MINUS = new int[3][];
			
			
			
			
			Giocatore[][] arrayGiocatori = new Giocatore[3][];
			
		
			setArraySizes(contaG, contaF, contaC, PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
					AFG, PERC_FG, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS,
					arrayGiocatori);
			
			
			contaG =0;
			contaF =0;
			contaC =0;
			
			for(Giocatore g : listaGiocatori) {
				
				
				switch(g.getPos()) {
					case G:
						contaG = counterAfterFillingArrays(PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
							AFG, PERC_FG, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS,
							arrayGiocatori, contaG, g, 0);
							break;
							
					case F:
						contaF = counterAfterFillingArrays(PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
							AFG, PERC_FG, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS,
							arrayGiocatori, contaF, g, 1);
							break;
					case C:
						contaC = counterAfterFillingArrays(PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
							AFG, PERC_FG, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS,
							arrayGiocatori, contaC, g, 2);
							break;
					
					default: throw new IllegalArgumentException("Unexpected value: " + g.getPos());
				}
				
				
				
				
				//g.printInfo();
			}
			
			
			
			
			if(N>listaGiocatori.size()) N = listaGiocatori.size(); //Controlla che il valore di N non superi la dimensione massima concessa
			
		// Creazione delle variabili
		
			GRBVar[][] x = new GRBVar [3][]; // x[pos][i]  = 1 se il giocatore i-esimo di posizione pos è convocato, 0 altrimenti
			x[0] = new GRBVar [contaG];
			x[1] = new GRBVar [contaF];
			x[2] = new GRBVar [contaC];
			
			
			for(int j =0; j<3; j++) {
				for(int i=0; i<x[j].length; i++) {
					String name  = "x_" + arrayGiocatori[j][i].getPlayer();
					x[j][i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name);
				}
			}
			
			
			
		//Aggiunta vincolo sul budget: sum (CR[j][i] * x[j][i]) <= B
			
		
			GRBLinExpr expr = new GRBLinExpr();
			for(int j = 0; j< 3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					expr.addTerm(CR[j][i], x[j][i]);
				}
			}
			
			model.addConstr(expr, GRB.LESS_EQUAL, B, "const_Budget");

			
		//Aggiuta vincoli sui ruoli 
			
			//Aggiunta vincolo sul numero di G: sum x[0][i] = 4
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< x[0].length; i++) {
				expr.addTerm(1.0, x[0][i]);
			}
			
			model.addConstr(expr, GRB.EQUAL, 4.0, "const_PG");
			

			//Aggiunta vincolo sul numero di F: sum x[1][i] = 4
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< x[1].length; i++) {
				expr.addTerm(1.0, x[1][i]);
			}
			
			model.addConstr(expr, GRB.EQUAL, 4.0, "const_SF");
			

			//Aggiunta vincolo sul numero di C: sum x[2][i] = 2
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< x[2].length; i++) {
				expr.addTerm(1.0, x[2][i]);
			}
			
			model.addConstr(expr, GRB.EQUAL, 2.0, "const_C");
			
			
		//Aggiunta vincolo sul numero di convocati
			
			expr = new GRBLinExpr();
			
			for(int j = 0; j< 3; j++) {
				for(int i = 0; i< x[j].length; i++) {
					expr.addTerm(1.0, x[j][i]);
				}
			}
			
			model.addConstr(expr, GRB.EQUAL, NumOfPlayers, "const_NumOfplayers");
			
		// Aggiunta della funzione obiettivo: max sum ( valore[i] * x[i] )
			
			GRBLinExpr fo = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					fo.addTerm(PDK[j][i], x[j][i]);
				}
			}
		
			model.setObjective(fo, GRB.MAXIMIZE);
						
		//altri possibili vincoli
			
			
			
//			addConstr_height(model, listaGiocatori, h, x, NumOfPlayers, N);
//			addConstr_gamesPlayed(model, listaGiocatori, gp, x, NumOfPlayers, N);
//			addConstr_age(model, listaGiocatori, age, x, NumOfPlayers, N);
//			addConstr_defense(model, listaGiocatori, def, x, NumOfPlayers, N);
		
						
		// Ottimizza il modello
						
			model.optimize();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					if(x[j][i].get(GRB.DoubleAttr.X) == 1) {
						System.out.println(x[j][i].get(GRB.StringAttr.VarName) + " " + x[j][i].get(GRB.DoubleAttr.X) + "(" + Pos[j][i] + ")");						
					}
				}
			}
		
			
	}catch(Exception e) {}
	}

	private static void setArraySizes(int contaG, int contaF, int contaC, String[][] pLAYER, Position[][] pos,
			String[][] team, Float[][] pDK, Float[][] cR, int[][] gP, int[][] mIN, int[][] sT, int[][] pTS, int[][] rEB,
			int[][] aST, int[][] sTL, int[][] bLK, int[][] bA, int[][] mFG, int[][] aFG, int[][] pERC_FG, int[][] m3p,
			int[][] a3p, int[][] pERC_3P, int[][] mFT, int[][] aFT, int[][] pERC_FT, int[][] oREB, int[][] dREB,
			int[][] tOV, int[][] pF, int[][] fD, int[][] pLUS_MINUS, Giocatore[][] arrayGiocatori) {
		
		
		arrayGiocatori[0] = new Giocatore[contaG];
		arrayGiocatori[1] = new Giocatore[contaF];
		arrayGiocatori[2] = new Giocatore[contaC];
		
		pLAYER[0] = new String[contaG];
		pLAYER[1] = new String[contaF];
		pLAYER[2] = new String[contaC];
		
		pos[0] = new Position[contaG];
		pos[1] = new Position[contaF];
		pos[2] = new Position[contaC];
		
		team[0] = new String[contaG];
		team[1] = new String[contaF];
		team[2] = new String[contaC];
		
		pDK[0] = new Float[contaG];
		pDK[1] = new Float[contaF];
		pDK[2] = new Float[contaC];
		
		cR[0] = new Float[contaG];
		cR[1] = new Float[contaF];
		cR[2] = new Float[contaC];
		
		gP[0] = new int[contaG];
		gP[1] = new int[contaF];
		gP[2] = new int[contaC];
		
		mIN[0] = new int[contaG];
		mIN[1] = new int[contaF];
		mIN[2] = new int[contaC];
		
		sT[0] = new int[contaG];
		sT[1] = new int[contaF];
		sT[2] = new int[contaC];
		
		pTS[0] = new int[contaG];
		pTS[1] = new int[contaF];
		pTS[2] = new int[contaC];
		
		rEB[0] = new int[contaG];
		rEB[1] = new int[contaF];
		rEB[2] = new int[contaC];
		
		aST[0] = new int[contaG];
		aST[1] = new int[contaF];
		aST[2] = new int[contaC];
		
		sTL[0] = new int[contaG];
		sTL[1] = new int[contaF];
		sTL[2] = new int[contaC];
		
		bLK[0] = new int[contaG];
		bLK[1] = new int[contaF];
		bLK[2] = new int[contaC];
		
		bA[0] = new int[contaG];
		bA[1] = new int[contaF];
		bA[2] = new int[contaC];		
		
		mFG[0] = new int[contaG];
		mFG[1] = new int[contaF];
		mFG[2] = new int[contaC];
		
		aFG[0] = new int[contaG];
		aFG[1] = new int[contaF];
		aFG[2] = new int[contaC];
		
		pERC_FG[0] = new int[contaG];
		pERC_FG[1] = new int[contaF];
		pERC_FG[2] = new int[contaC];
		
		m3p[0] = new int[contaG];
		m3p[1] = new int[contaF];
		m3p[2] = new int[contaC];
		
		a3p[0] = new int[contaG];
		a3p[1] = new int[contaF];
		a3p[2] = new int[contaC];
		
		pERC_3P[0] = new int[contaG];
		pERC_3P[1] = new int[contaF];
		pERC_3P[2] = new int[contaC];
		
		mFT[0] = new int[contaG];
		mFT[1] = new int[contaF];
		mFT[2] = new int[contaC];
		
		aFT[0] = new int[contaG];
		aFT[1] = new int[contaF];
		aFT[2] = new int[contaC];
		
		pERC_FT[0] = new int[contaG];
		pERC_FT[1] = new int[contaF];
		pERC_FT[2] = new int[contaC];
		
		oREB[0] = new int[contaG];
		oREB[1] = new int[contaF];
		oREB[2] = new int[contaC];
		
		dREB[0] = new int[contaG];
		dREB[1] = new int[contaF];
		dREB[2] = new int[contaC];
		
		tOV[0] = new int[contaG];
		tOV[1] = new int[contaF];
		tOV[2] = new int[contaC];
		
		pF[0] = new int[contaG];
		pF[1] = new int[contaF];
		pF[2] = new int[contaC];
		
		fD[0] = new int[contaG];
		fD[1] = new int[contaF];
		fD[2] = new int[contaC];
		
		pLUS_MINUS[0] = new int[contaG];
		pLUS_MINUS[1] = new int[contaF];
		pLUS_MINUS[2] = new int[contaC];
		
		
	}

	private static int counterAfterFillingArrays(String[][] PLAYER, Position[][] Pos, String[][] Team, Float[][] PDK,
			Float[][] CR, int[][] GP, int[][] MIN, int[][] ST, int[][] PTS, int[][] REB, int[][] AST, int[][] STL,
			int[][] BLK, int[][] BA, int[][] MFG, int[][] AFG, int[][] PERC_FG, int[][] M3P, int[][] A3P,
			int[][] PERC_3P, int[][] MFT, int[][] AFT, int[][] PERC_FT, int[][] OREB, int[][] DREB, int[][] TOV,
			int[][] PF, int[][] FD, int[][] PLUS_MINUS, Giocatore[][] arrayGiocatori, int contaPos, Giocatore g, int posNumber) {
		
				arrayGiocatori [posNumber][contaPos] = g;
		
				PLAYER[posNumber][contaPos]  = g.getPlayer();
				Pos[posNumber][contaPos]  = g.getPos();
				Team[posNumber][contaPos]  = g.getTeam();
				PDK[posNumber][contaPos]  = g.getPDK();
				CR[posNumber][contaPos]  = g.getCR();
				GP[posNumber][contaPos]  = g.getGP();
				MIN[posNumber][contaPos]  = g.getMIN();
				ST[posNumber][contaPos]  = g.getSTL();
				PTS[posNumber][contaPos]  = g.getPTS();
				REB[posNumber][contaPos]  = g.getREB();
				AST[posNumber][contaPos]  = g.getAST();
				STL[posNumber][contaPos]  = g.getSTL();
				BLK[posNumber][contaPos]  = g.getBLK();
				BA[posNumber][contaPos]  = g.getBA();
				MFG[posNumber][contaPos]  = g.getMFG();
				AFG[posNumber][contaPos]  = g.getAFG();
				PERC_FG[posNumber][contaPos]  = g.getPERC_FG();
				M3P[posNumber][contaPos]  = g.getM3P();
				A3P[posNumber][contaPos]  = g.getA3P();
				PERC_3P[posNumber][contaPos]  = g.getPERC_3P();
				MFT[posNumber][contaPos]  = g.getMFT();
				AFT[posNumber][contaPos]  = g.getAFT();
				PERC_FT[posNumber][contaPos]  = g.getPERC_FT();
				OREB[posNumber][contaPos] = g.getOREB();
				DREB[posNumber][contaPos] = g.getDREB();
				TOV[posNumber][contaPos]  = g.getTOV();
				PF[posNumber][contaPos]  = g.getPF();
				FD[posNumber][contaPos] = g.getFD();
				PLUS_MINUS[posNumber][contaPos]  = g.getPLUS_MINUS();
				
				return ++contaPos;
	}
	

}
