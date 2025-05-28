import java.io.*;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBVar;

public class Writer {
	
	public static void write(GRBVar x[][]) {
		
		try {
			File f = new File("Squadra_Ottima.txt");
			PrintWriter out = new PrintWriter(new FileWriter(f, true));
			
			String listOfChosenPlayers = null;
			
			for(int j=0; j<3; j++) {
				for(int i =0; i<x[j].length; i++) {
					if(x[j][i].get(GRB.DoubleAttr.X) == 1) listOfChosenPlayers += " " + x[j][i].get(GRB.StringAttr.VarName);
				}
			}
			
			
			out.append(listOfChosenPlayers);
			out.close();
		} catch (IOException | GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
