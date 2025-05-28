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
			double[][] PDK = new double[3][];
			double[][] CR = new double[3][];
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
			int[][] M2P = new int[3][];
			int[][] A2P = new int[3][];
			int[][] PERC_2P = new int[3][];
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
			double[][] value = new double[3][];
			
			
			
			
			Giocatore[][] arrayGiocatori = new Giocatore[3][];
			
		
			setArraySizes(contaG, contaF, contaC, PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
					AFG, PERC_FG, M2P, A2P, PERC_2P, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS, value,
					arrayGiocatori);
			
			
			contaG =0;
			contaF =0;
			contaC =0;
			
			
			for(Giocatore g : listaGiocatori) {
				
				
				switch(g.getPos()) {
					case G:
						contaG = counterAfterFillingArrays(PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
							AFG, PERC_FG, M2P, A2P, PERC_2P, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS, value,
							arrayGiocatori, contaG, g, 0);						
							break;
							
					case F:
						contaF = counterAfterFillingArrays(PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
							AFG, PERC_FG, M2P, A2P, PERC_2P, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS, value,
							arrayGiocatori, contaF, g, 1);
							break;
					case C:
						contaC = counterAfterFillingArrays(PLAYER, Pos, Team, PDK, CR, GP, MIN, ST, PTS, REB, AST, STL, BLK, BA, MFG,
							AFG, PERC_FG, M2P, A2P, PERC_2P, M3P, A3P, PERC_3P, MFT, AFT, PERC_FT, OREB, DREB, TOV, PF, FD, PLUS_MINUS, value,
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
					String name  = "x_" + listaGiocatori.indexOf(arrayGiocatori[j][i]);
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
			
			
		//Aggiunta vincolo sul numero di convocati : sum x[j][i] = NumOfPlayers
			
			expr = new GRBLinExpr();
			
			for(int j = 0; j< 3; j++) {
				for(int i = 0; i< x[j].length; i++) {
					expr.addTerm(1.0, x[j][i]);
				}
			}
			
			model.addConstr(expr, GRB.EQUAL, NumOfPlayers, "const_NumOfplayers");
			
		//Calcolo alcuni valori medi di statistiche che possono servire per alcuni vincoli o funzioni obiettivo
			
			double mediaPTS= calcolaMedia(PTS, GP);
			double mediaAST=calcolaMedia(AST, GP);
			double mediaREB=calcolaMedia(REB, GP);
			double mediaTOV=calcolaMedia(TOV, GP);
			double mediaBLK=calcolaMedia(BLK, GP);
			double mediaSTL=calcolaMedia(STL, GP);
			double mediaBA =calcolaMedia(BA, GP);
			double mediaFG_Perc = calcolaPercentualiMedie(PERC_FG);
			double media3PT_Perc = calcolaPercentualiMedie(PERC_3P);
		
		//Aggiunta vincolo sulla media dei punti: sum (avgPPG[j][i] * x[j][i])>= avgPPGMEdio *NumOfPlayers

			
			expr = new GRBLinExpr();
			
			for(int j = 0; j< 3; j++) {
				for(int i = 0; i< x[j].length; i++) {
					expr.addTerm((double)PTS[j][i] / GP[j][i], x[j][i]);
				}
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, mediaPTS*NumOfPlayers, "const_AvgPointsPerGame");
			
		//Aggiunta dei vincoli specifici alle Guardie
			//Aggiunta vincolo sugli assist : sum(avgAst[i] * x[i]) >= mediaAST *4
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[0].length; i++) {
				expr.addTerm((double)AST[0][i] / GP[0][i], x[0][i]);
			}
			
			
			model.addConstr(expr, GRB.GREATER_EQUAL,mediaAST*4, "const_AvgAssists");
			
			//Aggiunta vincolo sui Turnovers: sum(avgTov[i] * x[i]) <= mediaTOV *4
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[0].length; i++) {
				expr.addTerm((double)TOV[0][i] / GP[0][i], x[0][i]);
			}
			
			
			model.addConstr(expr, GRB.LESS_EQUAL,mediaTOV*4, "const_AvgTurnovers");
			
			//Aggiunta vincolo su rapporto ast/tov: sum((ast[i] / Tov[i] ) * x[i]) >= mediaAST / mediaTOV *4
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[0].length; i++) {
				expr.addTerm((double)AST[0][i] / (TOV[0][i] +1), x[0][i]);
				
			}
			
			
			model.addConstr(expr, GRB.GREATER_EQUAL, ((double)mediaAST / mediaTOV) * 4, "const_AstToTovRatio");

			//Aggiunta vincolo su tiro da 3PT: sum(
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< x[0].length; i++) {
				expr.addTerm(PERC_3P[0][i], x[0][i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, media3PT_Perc * 4, "const_3P_Perc");
			
		//Aggiunta dei vincoli specifici ai Forward
			//Aggiunta vincolo sulle palle rubate: sum(avgStl[i] * x[i]) >= mediaSTL
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[1].length; i++) {
				expr.addTerm((double)STL[1][i] / GP[1][i], x[1][i]);
			}
			
			
			model.addConstr(expr, GRB.GREATER_EQUAL,mediaSTL*4, "const_AvgSteals");
			
			//Aggiunta vincolo sui rimbalzi : sum( avgReb[i] * x[i]) >= mediaReb
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[1].length; i++) {
				expr.addTerm((double)REB[1][i] / GP[1][i], x[1][i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, mediaREB*4, "const_AvgRboundsF");
			
			
			
		//Aggiunta dei vincoli specifici ai Centri
			//Aggiunta vincolo sui rimbalzi : sum( avgReb[i] * x[i]) >= mediaReb
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[2].length; i++) {
				expr.addTerm((double)REB[2][i] / GP[2][i], x[2][i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, mediaREB*2, "const_AvgRboundsC");
			
			//Aggiunta vincolo sulle stoppate fatte: sum(avgBlk[i] * x[i]) >= mediaBlk
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[2].length; i++) {
				expr.addTerm((double)BLK[2][i] / GP[2][i], x[2][i]);
			}
			
			
			
			model.addConstr(expr, GRB.GREATER_EQUAL, mediaBLK*2, "const_AvgBlocks");
			
			//Aggiunta vincolo sulle stoppate subite: sum(avgBkS[i] *  x[i]) >= mediaBlkSubiti
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[2].length; i++) {
				expr.addTerm((double)BA[2][i] / GP[2][i], x[2][i]);
			}
			

			model.addConstr(expr, GRB.LESS_EQUAL, mediaBA*2, "const_AvgBlocksSubiti");
			
			//Aggiunta vincolo sul tiro FG%
			
			expr = new GRBLinExpr();
			
			
			for(int i = 0; i< x[2].length; i++) {
				expr.addTerm( PERC_FG[2][i], x[2][i]);
			}
			

			model.addConstr(expr, GRB.GREATER_EQUAL, mediaFG_Perc * 2, "const_AvgFG_Perc");
			
			
			
			
		// Aggiunta della funzione obiettivo: max sum ( pdk[j][i] * x[j][i] )
			
			GRBLinExpr f0 = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					f0.addTerm(PDK[j][i], x[j][i]);
				}
			}
			
			
		// Aggiunta della funzione obiettivo: max sum ( value[j][i] * x[j][i] )
			
			GRBLinExpr f1 = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					f1.addTerm(value[j][i], x[j][i]);
				}
			}
			
			
		// Aggiunta della funzione obiettivo: max sum ( (ppg[j][i] + ast[j][i] + fg_%[j][i]) * x[j][i] )
			
			GRBLinExpr f2 = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					f2.addTerm((PTS[j][i] + AST[j][i] ) / GP[j][i] + PERC_FG[j][i], x[j][i]);
				}
			}
			
			
		// Aggiunta della funzione obiettivo: max sum (( blk[j][i]  + stl[j][i]) * x[j][i] )
			
			GRBLinExpr f3 = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					f3.addTerm((BLK[j][i] + STL[j][i]) / GP[j][i], x[j][i]);
				}
			}
			
			
		// Aggiunta della funzione obiettivo: max sum (( 2p%[j][i] + 3p%[j][i] + ft%[j][i]) * x[j][i] )
			
			GRBLinExpr f4 = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					f4.addTerm(PERC_2P[j][i] + PERC_3P[j][i] + PERC_FT[j][i], x[j][i]);
				}
			}
			
			
		// Aggiunta della funzione obiettivo: max sum (( 2 * 2p%[j][i] + 3 * 3p%[j][i] + ft%[j][i]) * x[j][i] )
			
			GRBLinExpr f5 = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					f5.addTerm( 2 * PERC_2P[j][i] + 3 * PERC_3P[j][i] + PERC_FT[j][i], x[j][i]);
				}
			}
			
			
		// Aggiunta della funzione obiettivo: max sum (( -GP[j][i] * x[j][i] )  = min sum(GP[j][i]  * x[j][i])
			
			GRBLinExpr f6 = new GRBLinExpr();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					f6.addTerm( -1 * GP[j][i], x[j][i]);
				}
			}
			
		
			
		
			model.setObjective(f0, GRB.MAXIMIZE);
						
		//altri possibili vincoli
			
			
			
//			addConstr_height(model, listaGiocatori, h, x, NumOfPlayers, N);
//			addConstr_gamesPlayed(model, listaGiocatori, gp, x, NumOfPlayers, N);
//			addConstr_age(model, listaGiocatori, age, x, NumOfPlayers, N);
//			addConstr_defense(model, listaGiocatori, def, x, NumOfPlayers, N);
			
			 // Scrittura del modello in formato .lp
            model.write("model.lp");
		
						
		// Ottimizza il modello
						
			model.optimize();
			
			
			for(int j = 0; j<3; j++) {
				for(int i = 0; i<x[j].length; i++) {
					if(x[j][i].get(GRB.DoubleAttr.X) == 1) {
						System.out.println(x[j][i].get(GRB.StringAttr.VarName) + " :\t" + arrayGiocatori[j][i].getPlayer() + " " +
								x[j][i].get(GRB.DoubleAttr.X) + "(" + Pos[j][i] + " - " + Team[j][i] +")");						
					}
				}
			}
			
		// Scrittura della soluzione in formato .sol
            model.write("solution.sol");
            
        //Salvataggio della squadra
            Writer.write(x, Pos, Team, arrayGiocatori);
            
        // Pulizia
            model.dispose();
            env.dispose();

		
			
	}catch(Exception e) {}
	}

	private static double calcolaPercentualiMedie(int[][] percentage) {
		int numOfplayers = 0;
		double sumOfPercentages = 0; 
		
		for(int j=0; j<3; j++) {
			for(int i =0; i<percentage[j].length; i++) {
				sumOfPercentages += percentage[j][i];
				numOfplayers++;
			}
		}
		return sumOfPercentages /  numOfplayers;
	}

	private static double calcolaMedia(int[][] statsArray, int[][] gamesPlayed) {
		double sumOfAvgStats = 0;
		int numOfPlayers =0;
		
		for(int j=0; j<3; j++) {
			for(int i =0; i< statsArray[j].length; i++) {
				sumOfAvgStats += (double) statsArray[j][i] / gamesPlayed[j][i];
				numOfPlayers++;
			}
	
		}
		return sumOfAvgStats / numOfPlayers;
	}



	private static void setArraySizes(int contaG, int contaF, int contaC, String[][] pLAYER, Position[][] pos,
			String[][] team, double[][] pDK, double[][] cR, int[][] gP, int[][] mIN, int[][] sT, int[][] pTS, int[][] rEB,
			int[][] aST, int[][] sTL, int[][] bLK, int[][] bA, int[][] mFG, int[][] aFG, int[][] pERC_FG, int[][] m2p, int[][] a2p, int[][]pERC_2P,
			int[][] m3p, int[][] a3p, int[][] pERC_3P, int[][] mFT, int[][] aFT, int[][] pERC_FT, int[][] oREB, int[][] dREB,
			int[][] tOV, int[][] pF, int[][] fD, int[][] pLUS_MINUS, double[][] value, Giocatore[][] arrayGiocatori) {
		
		
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
		
		pDK[0] = new double[contaG];
		pDK[1] = new double[contaF];
		pDK[2] = new double[contaC];
		
		cR[0] = new double[contaG];
		cR[1] = new double[contaF];
		cR[2] = new double[contaC];
		
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
		
		m2p[0] = new int[contaG];
		m2p[1] = new int[contaF];
		m2p[2] = new int[contaC];
		
		a2p[0] = new int[contaG];
		a2p[1] = new int[contaF];
		a2p[2] = new int[contaC];
		
		pERC_2P[0] = new int[contaG];
		pERC_2P[1] = new int[contaF];
		pERC_2P[2] = new int[contaC];
		
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
		
		value[0] = new double[contaG];
		value[1] = new double[contaF];
		value[2] = new double[contaC];
		
	}

	private static int counterAfterFillingArrays(String[][] PLAYER, Position[][] Pos, String[][] Team, double[][] PDK,
			double[][] CR, int[][] GP, int[][] MIN, int[][] ST, int[][] PTS, int[][] REB, int[][] AST, int[][] STL,
			int[][] BLK, int[][] BA, int[][] MFG, int[][] AFG, int[][] PERC_FG,int[][] M2P, int[][] A2P,
			int[][] PERC_2P, int[][] M3P, int[][] A3P,int[][] PERC_3P, int[][] MFT, int[][] AFT, int[][] PERC_FT,
			int[][] OREB, int[][] DREB, int[][] TOV,int[][] PF, int[][] FD, int[][] PLUS_MINUS, double[][] value, Giocatore[][] arrayGiocatori, int contaPos, Giocatore g, int posNumber) {
		
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
				M2P[posNumber][contaPos]  = g.getMFG() - g.getM3P();
				A2P[posNumber][contaPos]  = g.getAFG() - g.getA3P();
				PERC_2P[posNumber][contaPos]  = calculate2P_perc(M2P[posNumber][contaPos], PERC_2P[posNumber][contaPos]);
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
				
		
				value[posNumber][contaPos] = decideValue(g);
				
				return ++contaPos;
	}

	private static int calculate2P_perc(int m, int a) { //Serve nel caso si abbia un giocatore con 0 attempted
		int result = (int) ((m  * 100 ) / (a + Double.MIN_VALUE));
		return result;
	}

	private static double decideValue(Giocatore g) {
		
		int tiriSbagliati = g.getAFG() - g.getMFG();
		int tiriLiberiSbagliati = g.getAFT() - g.getMFT();
		
		double punteggio = g.getPTS() + 1.2 * g.getREB() + 1.5* g.getAST() + 1.5* g.getSTL() - 1.5*g.getTOV() + 1.5*g.getBLK() - 0.5* g.getBA() - tiriSbagliati - tiriLiberiSbagliati; 

		return (double) punteggio;
		
		
		
	}
	

}
