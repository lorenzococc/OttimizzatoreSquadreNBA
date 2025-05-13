import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Reader {

	public static ArrayList<Giocatore> readGiocatori() {
		ArrayList<Giocatore> listaGiocatori = new ArrayList<>();
		
		try {
			
		String filePath = "C:/Users/loren/Desktop/Programmazione/GeneratoreDiGiocatoriDiBasket/ListaGiocatoriEStatistiche.xlsx";
		
		FileInputStream file = new FileInputStream(new File(filePath));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
	
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		for(Row row : sheet) {
			if(row.getRowNum() == 0 ) continue; //Salto la prima riga, che contiene solo i nomi dei valori
			Giocatore g = letturaGiocatore(row);
			listaGiocatori.add(g);
		}
		
		}catch (IOException e) {
			System.out.println("IO Exception");
		}
		
		
		return listaGiocatori;
		
	}

	private static Giocatore letturaGiocatore(Row row) {	
		Giocatore g = new Giocatore();
		
		Field[] fields = Giocatore.class.getDeclaredFields();
		
		for(Field f: fields) {
			f.setAccessible(true);
		}
		
		int i = 0; 
		
		for(Cell cell : row) {
			
			Object value = null;
			String cellValueAsString = cell.getStringCellValue();
//				switch (cell.getCellType()) {
//				
//		            case STRING: value = cell.getStringCellValue(); //Una stringa potrebbe essere il nome o il ruolo
//		            	
//		            	break;
//		            case NUMERIC: value = cell.getNumericCellValue();
//		            	if(Double.valueOf(value) % 1 == 0) value = (int) value;
//		            	break;
//		            default: value = Integer.valueOf(cell.getStringCellValue());	
//		        }
			
			if(fields[i].getType() == int.class) {
				value = Integer.valueOf(cellValueAsString);
			}
			
			else if (fields[i].getType() == float.class) {
				value = Float.valueOf(cellValueAsString);
			}
			
			else {
			
				switch(cellValueAsString) {
		    		case "PG": value = Ruolo.PG;
		    			break;
		    		case "SG": value = Ruolo.SG;
						break;
		    		case "SF": value = Ruolo.SF;
		    		break;
		    		case "PF": value = Ruolo.PF;
						break;
		    		case "C": value = Ruolo.C;
						break;
						
		    		default: value = cellValueAsString;	//Se non   una delle 5 posizioni allora   una stringa vera e propria
				}
				
			}
			try {
				
				fields[i].set(g, value);
				i++;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		return g;
	}
	
}
