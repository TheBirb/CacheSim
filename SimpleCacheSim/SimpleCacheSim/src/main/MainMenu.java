package main;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;


import engine.Engine;

public class MainMenu {
	
	public static void main(String[] args) {
		int pal;
		int bloq;
		int conj;
		boolean pReemplazo=false;
		int pEscr;
		int fichero;
		BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Escoja tamaño de la palabra 4 u 8 bytes");
		try {
			pal=Integer.parseInt(br.readLine());
			System.out.println("Escoja tamaño del bloque 32 o 64 bytes");
			bloq=Integer.parseInt(br.readLine());
			System.out.println("Escoja tamaño del conjunto: 1(directa), 2, 4 U 8(totalmente asociativa)");
			conj=Integer.parseInt(br.readLine());
			if(conj!=1) {
				System.out.println("Escoja la política de reemplazo: 0 Lru 1 fifo");
				int i=Integer.parseInt(br.readLine());
				if(i==0) {
					pReemplazo=true;
				}
			}
			
			System.out.println("Escoja política de escritura: 0(write-back), 1(write-through, actualizando MC), 2 (write-through, actualizando solo MP");
			pEscr=Integer.parseInt(br.readLine());
			System.out.println("Escriba 0 si quiere insertar un fichero o 1 si quiere meter las direcciones manualmente");
			fichero=Integer.parseInt(br.readLine());
			Engine engine= new Engine(pal,bloq, conj, pReemplazo, pEscr, fichero);
		} catch (NumberFormatException | IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		
		
		
	}

	
}
