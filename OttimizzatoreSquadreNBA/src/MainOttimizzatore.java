import java.util.ArrayList;

import com.gurobi.gurobi.GRBEnv;
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
			
//			for(Giocatore g: listaGiocatori) {
//				g.printInfo();
//			}
			
		//Inserimento parametri
			
			long B = 140588000000L; 
			int N = listaGiocatori.size();
			int MPG = 48;
			int NumOfPlayers = 12;
			
			ArrayList<Integer> c = new ArrayList<>();
			ArrayList<Integer> h = new ArrayList<Integer>();
			ArrayList<Integer> w = new ArrayList<Integer>();
			ArrayList<Integer> age = new ArrayList<Integer>();
			ArrayList<Integer> vj = new ArrayList<Integer>();
			ArrayList<Integer> ws = new ArrayList<Integer>();
			ArrayList<Integer> gp = new ArrayList<Integer>();
			ArrayList<Integer> s = new ArrayList<Integer>();
			ArrayList<Float> rpg = new ArrayList<Float>();
			ArrayList<Float> bpg = new ArrayList<Float>();
			ArrayList<Float> spg = new ArrayList<Float>();
			ArrayList<Float> topg = new ArrayList<Float>();
			ArrayList<Float> apg = new ArrayList<Float>();
			ArrayList<Float> ppg = new ArrayList<Float>();
			ArrayList<Float> fpg = new ArrayList<Float>();
			ArrayList<Float> mpg = new ArrayList<Float>();
			ArrayList<Float> perc_td = new ArrayList<Float>();
			ArrayList<Float> mFGpg = new ArrayList<Float>();
			ArrayList<Float> aFGpg = new ArrayList<Float>();
			ArrayList<Float> perc_FG = new ArrayList<Float>();
			ArrayList<Float> perc_2PT = new ArrayList<Float>();
			ArrayList<Float> perc_3PT = new ArrayList<Float>();
			ArrayList<Float> perc_FT = new ArrayList<Float>();
			ArrayList<Float> qi = new ArrayList<Float>();
			ArrayList<Float> cv = new ArrayList<Float>();
			ArrayList<Float> def = new ArrayList<Float>();
			ArrayList<Float> p = new ArrayList<Float>();
			ArrayList<Float> bh = new ArrayList<Float>();
			ArrayList<Float> dunk = new ArrayList<Float>();
			ArrayList<Float> alt = new ArrayList<Float>();
			ArrayList<Float> att = new ArrayList<Float>();
			ArrayList<Float> pen = new ArrayList<Float>();
			ArrayList<Float> fb = new ArrayList<Float>();
			ArrayList<Float> spet = new ArrayList<Float>();
			ArrayList<Float> fan = new ArrayList<Float>();
			ArrayList<Float> fun = new ArrayList<Float>();
			ArrayList<Float> grav = new ArrayList<Float>();
			ArrayList<Float> v = new ArrayList<Float>();
			ArrayList<Integer> PG = new ArrayList<Integer>();
			ArrayList<Integer> SG = new ArrayList<Integer>();
			ArrayList<Integer> SF = new ArrayList<Integer>();
			ArrayList<Integer> PF = new ArrayList<Integer>();
			ArrayList<Integer> C = new ArrayList<Integer>();
			
			ArrayList<Float> valore = new ArrayList<Float>();
			

			
			
			for(Giocatore g : listaGiocatori) {
				c.add(g.getWage());
				h.add(g.getHeight());
				w.add(g.getWeight());
				age.add(g.getAge());
				vj.add(g.getVerticalJump());
				ws.add(g.getWingspan());
				gp.add(g.getGamesPlayedLastSeason());
				s.add(g.getWage());
				
				rpg.add(g.getReboundsPerGame());
				bpg.add(g.getBlocksPerGame());
				spg.add(g.getStealsPerGame());
				topg.add(g.getTurnoversPerGame());
				apg.add(g.getAssistsPerGame());
				ppg.add(g.getPointsPerGame());
				fpg.add(g.getFoulsPerGame());
				mpg.add(g.getMinutesPlayedPerGame());
				//perc_td.add(z.getTripleDoubles());
				mFGpg.add(g.getMadeShotsPerGame());
				aFGpg.add(g.getAttemptedShotsPerGame());
				perc_FG.add(g.getShots_Percentage());
				perc_2PT.add(g.getShots2P_Percentage());
				perc_3PT.add(g.getShots3P_Percentage());
				perc_FT.add(g.getFreeThrow_Percentage());
				
				qi.add(g.getIq());
				cv.add(g.getCourtVision());
				def.add(g.getDefense());
				p.add(g.getPassing());
				bh.add(g.getDribbling());
				dunk.add(g.getDunking());
				alt.add(g.getUnselfishness());
				att.add(g.getAttack());
				pen.add(g.getSlashing());
				fb.add(g.getFastbreak());
				spet.add(g.getPuttingOnAShow());
				fan.add(g.getFanLiking());
				fun.add(g.getFunToWatch());
				grav.add(g.getGravity());
				v.add(g.getSpeed());
				
				PG.add(g.getRole() == Ruolo.PG ? 1 : 0);
				SG.add(g.getRole() == Ruolo.SG ? 1 : 0);
				SF.add(g.getRole() == Ruolo.SF ? 1 : 0);
				PF.add(g.getRole() == Ruolo.PF ? 1 : 0);
				C.add(g.getRole() == Ruolo.C ? 1 : 0);
				
				valore.add(g.getAttack() + g.getDefense()) ;
				
				
			}
			
			
			
		// Creazione delle variabili
			
			GRBVar[] x = new GRBVar [listaGiocatori.size()];   // x[i]  = 1 se il giocatore i-esimo   convocato, 0 altrimenti
			
			
			int contaGiocatori = 0;
			for(Giocatore g : listaGiocatori) {
				String name = "x_" + g.getName();
					
				x[contaGiocatori++] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name);
			}
			
			
//			GRBVar[] xq = new GRBVar [listaGiocatori.size()];   // x[i]  = 1 se il giocatore i-esimo   nel quintetto titolare, 0 altrimenti
//			for(Giocatore g : listaGiocatori) {
//				String name = "xq_" + contaGiocatori;
//					
//				x[contaGiocatori++] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, name);
//			}		
//			
			
			
			
		//Aggiunta vincolo sul budget: sum (c[i] * x[i]) <= B
			
		
			GRBLinExpr expr = new GRBLinExpr();
			
			for(int i = 0; i< listaGiocatori.size(); i++) {
				expr.addTerm(c.get(i), x[i]);
			}
			
			model.addConstr(expr, GRB.LESS_EQUAL, B, "const_Budget");
		
			
		//Aggiuta vincoli sui ruoli 
			
			//Aggiunta vincolo sul numero di PG: sum (PG[i] * x[i]) >= 2
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< listaGiocatori.size(); i++) {
				expr.addTerm(PG.get(i), x[i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, 2.0, "const_PG");
			
			//Aggiunta vincolo sul numero di SG: sum (SG[i] * x[i]) >= 2
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< listaGiocatori.size(); i++) {
				expr.addTerm(SG.get(i), x[i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, 2.0, "const_SG");
			
			//Aggiunta vincolo sul numero di SF: sum (SF[i] * x[i]) >= 2
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< listaGiocatori.size(); i++) {
				expr.addTerm(SF.get(i), x[i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, 2.0, "const_SF");
			
			//Aggiunta vincolo sul numero di PF: sum (PF[i] * x[i]) >= 2
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< listaGiocatori.size(); i++) {
				expr.addTerm(PF.get(i), x[i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, 2.0, "const_PF");
			
			//Aggiunta vincolo sul numero di C: sum (C[i] * x[i]) >= 2
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< listaGiocatori.size(); i++) {
				expr.addTerm(C.get(i), x[i]);
			}
			
			model.addConstr(expr, GRB.GREATER_EQUAL, 2.0, "const_C");
			
			
		//Aggiunta vincolo sul numero di convocati
			
			expr = new GRBLinExpr();
			
			for(int i = 0; i< listaGiocatori.size(); i++) {
				expr.addTerm(1.0, x[i]);
			}
			
			model.addConstr(expr, GRB.EQUAL, NumOfPlayers, "const_NumOfplayers");
			
		// Aggiunta della funzione obiettivo: max sum ( valore[i] * x[i] )
			
			GRBLinExpr fo = new GRBLinExpr();
			for(int i=0; i< listaGiocatori.size(); i++) {
				fo.addTerm(valore.get(i), x[i]);
			}
						
			model.setObjective(fo, GRB.MAXIMIZE);
						
		//altri possibili vincoli
			
						
		// Ottimizza il modello
						
			model.optimize();
			
			for(int i =0; i< listaGiocatori.size(); i++) {
				System.out.println(x[i].get(GRB.StringAttr.VarName) + " " + x[i].get(GRB.DoubleAttr.X));
			}		
			
	}catch(Exception e) {}
	}

}
