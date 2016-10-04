import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {
		
		Equipement tv = new Equipement("samsung", 80);
		System.out.println("\t\t\tEquipement " + tv.monNom() );
		System.out.println("Entrer : ");
		System.out.println("i --> Info sur l'equipement ");
		System.out.println("q --> Quitter ");
		
		Scanner scanner = new Scanner(System.in);
		String readString = scanner.nextLine();
		switch (readString) {
		case "i":
			tv.affichage();
		case "q":
			System.exit(0);
		
		}
		
		
		
		

	}

}
