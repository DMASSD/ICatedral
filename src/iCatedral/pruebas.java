package iCatedral;

public class pruebas {

	public static void main(String[] args) {  
        
	    String carpetaDefinida = "C:\\Users\\Daniel\\Desktop\\isaark";

	    ICatedral guadalupe = new ICatedral("guadalupe",carpetaDefinida,carpetaDefinida+
	    		"\\La_Guadalupana.abc");

	    ICatedral himno = new ICatedral("himno",carpetaDefinida,carpetaDefinida+
	    		"\\HimnoGuadalupano2.abc");

	    ICatedral chiapaneca = new ICatedral("chiapaneca",carpetaDefinida,carpetaDefinida+
	    		"\\LasChiapanecas.abc");
//
	    ICatedral navidad = new ICatedral("navidad",carpetaDefinida,carpetaDefinida+
	    		"\\blanca_navidad.abc");

//	    ICatedral pruebas = new ICatedral("pruebas",carpetaDefinida);
	    
//	    ICatedral.sendSong(pruebas);
	       
	    ICatedralPlayList.currentPlayList.add(guadalupe);
	    
	    ICatedralPlayList.currentPlayList.add(himno);
	    
	    ICatedralPlayList.currentPlayList.add(chiapaneca);
	    
	    ICatedralPlayList.currentPlayList.add(navidad);
	       
//	    ICatedralPlayList.currentPlayList.forEach(song-> System.out.println(song.getName()));
	    
	    ICatedralPlayList pruebaList = new ICatedralPlayList("Pruebas",carpetaDefinida);
	    
	    
	    
	}
}
