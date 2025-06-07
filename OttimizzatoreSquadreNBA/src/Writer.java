import java.io.*;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBVar;

public class Writer {
	
	public static void writeTeam(GRBVar x[][], Position[][] pos, String[][] team, Giocatore[][] arrayGiocatori) {
		
		try {
			File f = new File("Squadra_Ottima.txt");
			PrintWriter out = new PrintWriter(new FileWriter(f, true));
			
			String listOfChosenPlayers = "";
			
			out.write("\n----------------------\n");
			
			for(int j=0; j<3; j++) {
				for(int i =0; i<x[j].length; i++) {
					if(x[j][i].get(GRB.DoubleAttr.X) == 1) listOfChosenPlayers += x[j][i].get(GRB.StringAttr.VarName) + " :\t" + arrayGiocatori[j][i].getPlayer() + " " +
							x[j][i].get(GRB.DoubleAttr.X) + "(" + pos[j][i] + " - " + team[j][i] +")\n";
				}
			}
			
			
			out.append(listOfChosenPlayers);
			out.close();
		} catch (IOException | GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeObj(double f0, double f1, double f2, double f3, double f4, double f5, double f6, ArrayList<Giocatore> giocatoriScelti) {
		
		
		 try {
			 File f =new File( "obiettivi.csv");
			 FileWriter fw = new FileWriter(f, true);
			 CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT) ;
			 
			 f0 = Math.round(f0*100) / 100;
			 f1 = Math.round(f1*100) / 100;
			 f2 = Math.round(f2*100) / 100;
			 f3 = Math.round(f3*100) / 100;
			 f4 = Math.round(f4*100) / 100;
			 f5 = Math.round(f5*100) / 100;
			 f6 = Math.round(f6*100) / 100;

			 int creditiSpesi = 0;
			 for(Giocatore g: giocatoriScelti) {
				 creditiSpesi+= g.getCR();
			 }
			 
		     printer.printRecord("f0:" + f0, "f1:" + f1, "f2:" + f2, "f3:" + f3, "f4:" + f4, "f5:" + f5, "f6:" + f6, "Crediti spesi: " + creditiSpesi);
		
		     printer.close();
			 
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	

}
