package engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Engine {
	//Direcciones(Datos)
	private int tamPalabra;
	private int tamBloque;
	private int tamConjunto;
	private String pReemplazo;
	private int palabra;
	private int bloqueMP;
	private int nConjunto;
	private int tag;
	//Operaciones
	private int op;
	//tiempos
	private int tMc=2;
	private int tMp=21;
	private int tBl=28;
	//tiempo total y de acceso especifico
	private int tAcceso;
	private int tTotal;
	//directa
	private int nBloqueMC;
	//Conjuntos
	private int qConjunto;
	//Acierto o fallo
	private String af="";
	//Representación
	int[][] grid= new int[8][5];
	/* ocup mod tag rem b
	 * 	-	-	-	-	B0
	 * 	-	-	-	-	B1
	 * 	-	-	-	-	B2
	 * 	-	-	-	-	B3
	 * 	-	-	-	-	B4
	 * 	-	-	-	-	B5
	 * 	-	-	-	-	B6
	 * 	-	-	-	-	B7
	 * 
	 */
	
	public Engine(int tamPalabra, int tamBloque, int tamConjunto, String pReemplazo){
		this.tamPalabra=tamPalabra;
		this.tamBloque=tamBloque/tamPalabra;
		this.tamConjunto= tamConjunto;
		this.pReemplazo= pReemplazo;
		start();
	}
	//Secuencia de paso de dirección
	public void start(){
		int dir=0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Introduzca una dirección");
		try {
			
			dir=Integer.parseInt(br.readLine());
			System.out.println("Introduzca 0 si ld o 1 si st");
			op=Integer.parseInt(br.readLine());
		} catch (NumberFormatException | IOException e) {
			
			e.printStackTrace();
		}
		
		calculate(dir);
		

	}
	//Dependiendo de tamPalabra, intBloque etc calcula palabra, bloque , etc
	
	public void calculate(int dir) {
		this.palabra= dir/tamPalabra;
		this.bloqueMP=this.palabra/this.tamBloque;
		if(this.tamConjunto==2) {
			this.nConjunto=1;
			asociativaConjuntos();
		}else if(this.tamConjunto==4){
			this.nConjunto=2;
			asociativaConjuntos();
		}else if(this.tamConjunto==1) {
			this.nConjunto=0;
			directa();
		}else {
			this.nConjunto=-1;
			totalmenteAsociativa();
		}
	}
	
	public void directa() {
		this.nBloqueMC=this.bloqueMP%8;
		this.tag=this.bloqueMP/8;
		
		
			//buscar si el bloqueMP esta en ese bloqueMC 
			if(grid[this.nBloqueMC][4]==this.bloqueMP) {
				this.af="Acierto";
			}else {
				this.af="Fallo";
			}
			
			if(this.af=="Fallo") {
				int t=0;
					grid[this.nBloqueMC][0]=1;
				if(this.op==0) {
					t=0;
					grid[this.nBloqueMC][1]=0;
				}else {
					t=1;
					grid[this.nBloqueMC][1]=1;
				}
				
				grid[this.nBloqueMC][2]=this.tag;
				grid[this.nBloqueMC][4]=this.bloqueMP;
				//tiempo
				
				if(t==0) {
					this.tAcceso=tMc+2*tBl;
				}else {
					this.tAcceso=tMp+tBl;
				}
				
			}else {
				this.tAcceso=tMc;
			}
			this.tTotal+=this.tAcceso;
			System.out.println(this.af);
			System.out.println(tAcceso);
			System.out.println(tag);
	}
	
	
	
	public void totalmenteAsociativa() {
		
	}
	public void asociativaConjuntos() {
		
	}
	//dependiendo de si es conjuntos, fifo o lru devolverá algo diferente, hacer mejor una para cada una
	public void printCacheStyle() {
		
	}
	
}
