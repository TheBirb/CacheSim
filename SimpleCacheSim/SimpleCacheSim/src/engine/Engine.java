package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Engine {
	//Politica de escritura
	private boolean lRu=false;
	//Direcciones(Datos)
	private int tamPalabra;
	private int tamBloque;
	private int tamConjunto;
	private int palabra;
	private int bloqueMP;
	private int nConjunto;
	private int tag;
	//Politica de escritura
	private int pEscritura; //si es 0 es WB si es 1 es WT con actualizacion de MC 2 WT sin actualizacion de MC
	//Operaciones
	private int op;
	//tiempos
	private int tMc=2;
	private int tMp=21;
	private int tBl;
	//tiempo total y de acceso especifico
	private int tAcceso;
	private int tTotal=0;
	//numero de referencias y tasa de acierto 
	private int nRef=0;
	private int nAciertos=0;
	private float tAcierto=0;
	private String acierto="Acierto";
	//directa
	private int nBloqueMC;
	//Conjuntos
	private int qConjunto;
	//Acierto o fallo
	private String af="";
	//Representación
	private int[][] grid= new int[8][5];
	//si elige coger el archivo
	private boolean anyFile=false;
	//guarda la eleccion de manual o no
	private int eleccion=0;
	
	;
	/**
	 * Secuencia de iniciación del simulador
	 * @param tamPalabra tamaño de la palabra en bytes
	 * @param tamBloque tamaño del bloque en bytes
	 * @param tamConjunto tamaño del conjunto
	 * @param pReemplazo politica de reemplazo de la cache
	 * @param pEscritura política de escritura de la cache
	 * @param fichero, eleccion del usuario sobre la inserción o no de un fichero
	 */
	public Engine(int tamPalabra, int tamBloque, int tamConjunto, boolean pReemplazo, int pEscritura, int fichero){
		this.tamPalabra=tamPalabra;
		this.tamBloque=tamBloque/tamPalabra;
		this.tamConjunto= tamConjunto;
		this.lRu= pReemplazo;
		this.pEscritura=pEscritura;
		this.tBl=(this.tMp+this.tamBloque-1);
		for(int i=0; i<8;i++) {
			for(int j=0; j<5; j++) {
				if(j==0) {
					grid[i][0]=0;
				}else {
					grid[i][j]=-1;
				}
			}
		}
		start(fichero);
	}
	//Secuencia de paso de dirección
	public void start(int f){
		int dir=0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		this.eleccion=f;
		
		try {
			
			
			if(f==0) {
				this.anyFile=true;
				procesadorDeArchivos();
				
			}else {
			System.out.println("Introduzca una dirección");
			dir=Integer.parseInt(br.readLine());
			System.out.println("Introduzca 0 si ld o 1 si st");
			op=Integer.parseInt(br.readLine());
			calculate(dir);
			}
			
		} catch (NumberFormatException | IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		;
	}
	/**
	 * Metodo con la unica funcion de procesar un archivo.txt
	 */
	public void procesadorDeArchivos() {
		BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Introduzca el nombre del arhivo");
		
		try {
		String file = br.readLine();
		file=file+".txt";
			File f= new File(file);
			Scanner sr= new Scanner(new FileReader(f));
			String splitBy=",";
			String[] s;
			String line;
			while(sr.hasNextLine()) {
				line=sr.nextLine();
				s=line.split(splitBy);
				this.op=Integer.parseInt(s[1]);
				calculate(Integer.parseInt(s[0]));
				
			}
			System.out.println("Resumen:");
			this.tAcierto=((float)this.nAciertos/(float)this.nRef)*100;
			System.out.println("Tasa de aciertos:" + this.tAcierto+"%");
			System.out.println("Numero de referencias:" + this.nRef);
			System.out.println("Tiempo total:" + this.tTotal);
			System.out.println("Finalizado con éxito");
			
		    return;
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Dependiendo de tamPalabra, intBloque etc calcula palabra, bloque , etc
	 * @param dir, DIRECCION DE MEMORIA
	 */
	public void calculate(int dir) {
		this.palabra= dir/tamPalabra;
		this.bloqueMP=this.palabra/this.tamBloque;
		if(this.tamConjunto==2) {
			this.nConjunto=4;
			asociativaConjuntos1();
		}else if(this.tamConjunto==4){
			this.nConjunto=2;
			asociativaConjuntos2();
		}else if(this.tamConjunto==1) {
			
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
			
			if(this.af.equals("Fallo")) {
				
				if(this.pEscritura==0) {//wb
					if(this.op==0) {
						if(grid[this.nBloqueMC][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							this.tAcceso=this.tMc+this.tBl+this.tBl; 
						}
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][1]=0;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
					}else {
						if(grid[this.nBloqueMC][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							
							this.tAcceso=this.tMc+this.tBl+this.tBl; 
						}
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][1]=1;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						
					}
				}else if(this.pEscritura==1) {//wt act mc
					if(this.op==0) {
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc+this.tBl; 
					}else {
						grid[this.nBloqueMC][0]=1;//OCUP
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc+this.tMp+this.tBl; 
					}
				}else {//wt act mp
					if(this.op==0) {
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc+this.tBl; 
					}else {
						
						this.tAcceso=this.tMc+this.tMp; 
					}
				}
			}else {
				if(this.pEscritura==0) {//wb
					if(this.op==0) {
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc; 
					}else {
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][1]=1;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc; 
					}
				}else if(this.pEscritura==1) {//wt act mc
					if(this.op==0) {
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc; 
					}else {
						grid[this.nBloqueMC][0]=1;//OCUP
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc+this.tMp;  
					}
				}else {//wt act mp
					if(this.op==0) {
						grid[this.nBloqueMC][0]=1;
						grid[this.nBloqueMC][2]=this.tag;
						grid[this.nBloqueMC][4]=this.bloqueMP;
						this.tAcceso=this.tMc;      
					}else {
						grid[this.nBloqueMC][0]=0;//OCUP
						grid[this.nBloqueMC][2]=-1;
						grid[this.nBloqueMC][4]=-1;
						this.tAcceso=this.tMc+this.tMp;
					}
				}
			}
			printCacheStyle();
			
	}
	
	
	
	public void totalmenteAsociativa() {
		this.tag=this.bloqueMP;
		boolean stop=false;
		int i=0;
		int total=-1;
		while(i<8) {//checkea el numero que hay
			if(grid[i][0]==1) {
				total++;
			}
			i++;
		}
		i=0;
		while(i<8 && !stop ) {//check if exists
			if(grid[i][4]==this.bloqueMP) {
				stop=true;
				break;
			}
			i++;
		}
		if(i==8) {
			i--;
		}
		if(stop) {//checkea si es acierto o no
			this.af="Acierto";
			if(this.pEscritura==0) {//wb
				if(op==1) {
					grid[i][1]=1;
				}
				if(this.lRu) {
					changeLRU(i,total, true);
				}
				this.tAcceso=this.tMc; 
			}else if(this.pEscritura==1) {//wt mc
				if(this.op==0) {
					this.tAcceso=this.tMc; 
				}else {
					this.tAcceso=this.tMc+this.tMp;
				}
				if(this.lRu) {
					changeLRU(i,total, true);
				}
			}else {//wt mp
				if(this.op==0) {
					this.tAcceso=this.tMc; 
					if(this.lRu) {
						changeLRU(i,total, true);
					}
				}else {
					this.tAcceso=this.tMc+this.tMp;
					grid[i][4]=-1;//bloque
					grid[i][0]=0;//ocup
					grid[i][2]=-1;//tag
					changeLRU(i,-1, true);
				}
				
			}
		}else {
			this.af="Fallo";
			stop=false;
			i=0;
			
			while(i<8 && !stop) {//checkea si hay alguno libre
				if(grid[i][0]==0) {
					stop=true;
					break;
				}
				i++;
			}
			
			if(i==8) {
				i--;
			}
			if(stop==true) {
				
				if(this.pEscritura==0) {//wb
					grid[i][4]=this.bloqueMP;
					grid[i][0]=1;//ocup
					grid[i][2]=this.bloqueMP;//tag
					if(op==1) {
						
						grid[i][1]=1;
					}
					this.tAcceso=this.tMc+this.tBl; 
					changeLRU(i,total+1,false);
				}else if(this.pEscritura==1) {//wt mc
					grid[i][4]=this.bloqueMP;
					grid[i][0]=1;//ocup
					grid[i][2]=this.bloqueMP;//tag
					if(op==0) {
						this.tAcceso=this.tMc+this.tBl; 
					}else{
						this.tAcceso=this.tMc+this.tMp+this.tBl;  
					}
					
					changeLRU(i,total+1,false);
				}else {//wt mp
					if(this.op==0) {
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						this.tAcceso=this.tMc+this.tBl; 
						changeLRU(i,total+1,false);
					}else {
						
						this.tAcceso=this.tMc+this.tMp;  
						
					}
					
				}
			}else {
				
				stop=false;
				i=0;
				
				while(i<8 && !stop) {//checkea cual es el LRU o fifo mas bajo
					if(grid[i][3]<1) {
						stop=true;
						break;
					}
					i++;
				}
				
				if(i==8) {
					i--;
				}
				grid[i][4]=this.bloqueMP;
				if(this.pEscritura==0) {//wb
					grid[i][4]=this.bloqueMP;
					grid[i][0]=1;//ocup
					grid[i][2]=this.bloqueMP;//tag
					if(grid[i][1]==0) {//checkea si esta activado o no el dirty
						this.tAcceso=this.tMc+this.tBl;
					}else {
						this.tAcceso=this.tMc+this.tBl+this.tBl;
					}
					if(this.op==0) {
						grid[i][1]=0;
						
					}else {
						grid[i][1]=1;
					}
					
					
					
					changeLRU(i,total,true);
				}else if(this.pEscritura==1) {//wt mc
					grid[i][4]=this.bloqueMP;
					grid[i][0]=1;//ocup
					grid[i][2]=this.bloqueMP;//tag
					if(this.op==0) {
						
						this.tAcceso=this.tMc+this.tBl; 
					}else {
						this.tAcceso=this.tMc+this.tMp+this.tBl;
					}
					changeLRU(i,total,true);
				}else {//wt mp
					if(this.op==0) {
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						this.tAcceso=this.tMc+this.tBl;
						changeLRU(i,total,true);
					}else {
						
						this.tAcceso=this.tMc+this.tMp;
						
						
					}
					
				}
			}
			
		}
		printCacheStyle();
		
		
	}
	public void asociativaConjuntos1() {
		this.qConjunto=this.bloqueMP%this.nConjunto;
		this.tag=this.bloqueMP/this.nConjunto;
		if(this.qConjunto==0) {
			boolean stop=false;
			int i=0;
			int total=-1;
			while(i<2) {//checkea el numero que hay
				if(grid[i][0]==1) {
					total++;
				}
				i++;
			}
			i=0;
			while(i<2 && !stop ) {//check if exists
				if(grid[i][4]==this.bloqueMP) {
					stop=true;
					break;
				}
				i++;
			}
			if(i==2) {
				i--;
			}
			if(stop) {//checkea si es acierto o no
				this.af="Acierto";
				if(this.pEscritura==0) {//wb
					if(op==1) {
						grid[i][1]=1;
					}
					if(this.lRu) {
						changeLRU2(i,0,2,total, true);
					}
					this.tAcceso=this.tMc; 
				}else if(this.pEscritura==1) {//wt mc
					if(this.op==0) {
						this.tAcceso=this.tMc; 
					}else {
						this.tAcceso=this.tMc+this.tMp;
					}
					if(this.lRu) {
						changeLRU2(i,0,2,total, true);
					}
				}else {//wt mp
					if(this.op==0) {
						this.tAcceso=this.tMc; 
						if(this.lRu) {
							changeLRU2(i,0,2,total, true);
						}
					}else {
						this.tAcceso=this.tMc+this.tMp;
						grid[i][4]=-1;//bloque
						grid[i][0]=0;//ocup
						grid[i][2]=-1;//tag
						changeLRU2(i,0,2,-1, true);
					}
					
				}
			}else {
				this.af="Fallo";
				stop=false;
				i=0;
				
				while(i<2 && !stop) {//checkea si hay alguno libre
					if(grid[i][0]==0) {
						stop=true;
						break;
					}
					i++;
				}
				
				if(i==2) {//si llega al final
					i--;
				}
				if(stop==true) {
					
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						
						if(op==1) {
							
							
							grid[i][1]=1;
						}else {
							
							
							grid[i][1]=0;
						}
						
							this.tAcceso=this.tMc+this.tBl;
						
						
						changeLRU2(i,0,2,total+1,false);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==0) {
							this.tAcceso=this.tMc+this.tBl; 
						}else{
							this.tAcceso=this.tMc+this.tMp+this.tBl;  
						}
						
						changeLRU2(i,0,2,total+1,false);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl; 
							changeLRU2(i,0,2,total+1,false);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;  
							
						}
						
					}
				}else {
					
					stop=false;
					i=0;
					
					while(i<2 && !stop) {//checkea cual es el LRU o fifo mas bajo
						if(grid[i][3]<1) {
							stop=true;
							break;
						}
						i++;
					}
					
					if(i==2) {
						i--;
					}
					grid[i][4]=this.bloqueMP;
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(grid[i][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							this.tAcceso=this.tMc+this.tBl+this.tBl;
						}
						if(op==1) {
							
							grid[i][1]=1;
						}else {
							grid[i][1]=0;
						}
						
						changeLRU2(i,0,2,total,true);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(this.op==0) {
							
							this.tAcceso=this.tMc+this.tBl; 
						}else {
							this.tAcceso=this.tMc+this.tMp+this.tBl;
						}
						changeLRU2(i,0,2,total,true);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl;
							changeLRU2(i,0,2,total,true);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;
							
							
						}
						
					}
				}
				
			}
		}else if(this.qConjunto==1) {//-----------------------------------------Conjunto 2
			boolean stop=false;
			int i=2;
			int total=-1;
			while(i<4) {//checkea el numero que hay
				if(grid[i][0]==1) {
					total++;
				}
				i++;
			}
			i=2;
			while(i<4 && !stop ) {//check if exists
				if(grid[i][4]==this.bloqueMP) {
					stop=true;
					break;
				}
				i++;
			}
			if(i==4) {
				i--;
			}
			if(stop) {//checkea si es acierto o no
				this.af="Acierto";
				if(this.pEscritura==0) {//wb
					if(op==1) {
						grid[i][1]=1;
					}
					if(this.lRu) {
						changeLRU2(i,2,4,total, true);
					}
					this.tAcceso=this.tMc; 
				}else if(this.pEscritura==1) {//wt mc
					if(this.op==0) {
						this.tAcceso=this.tMc; 
					}else {
						this.tAcceso=this.tMc+this.tMp;
					}
					if(this.lRu) {
						changeLRU2(i,2,4,total, true);
					}
				}else {//wt mp
					if(this.op==0) {
						this.tAcceso=this.tMc; 
						if(this.lRu) {
							changeLRU2(i,2,4,total, true);
						}
					}else {
						this.tAcceso=this.tMc+this.tMp;
						grid[i][4]=-1;//bloque
						grid[i][0]=0;//ocup
						grid[i][2]=-1;//tag
						changeLRU2(i,2,4,-1, true);
					}
					
				}
			}else {
				this.af="Fallo";
				stop=false;
				i=2;
				
				while(i<4 && !stop) {//checkea si hay alguno libre
					if(grid[i][0]==0) {
						stop=true;
						break;
					}
					i++;
				}
				
				if(i==4) {//si llega al final
					i--;
				}
				if(stop==true) {
					
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==1) {
							
							grid[i][1]=1;
						}
						this.tAcceso=this.tMc+this.tBl; 
						changeLRU2(i,2,4,total+1,false);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==0) {
							this.tAcceso=this.tMc+this.tBl; 
						}else{
							this.tAcceso=this.tMc+this.tMp+this.tBl;  
						}
						
						changeLRU2(i,2,4,total+1,false);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl; 
							changeLRU2(i,2,4,total+1,false);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;  
							
						}
						
					}
				}else {
					
					stop=false;
					i=2;
					
					while(i<4 && !stop) {//checkea cual es el LRU o fifo mas bajo
						if(grid[i][3]<1) {
							stop=true;
							break;
						}
						i++;
					}
					
					if(i==4) {
						i--;
					}
					grid[i][4]=this.bloqueMP;
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(grid[i][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							this.tAcceso=this.tMc+this.tBl+this.tBl;
						}
						if(op==1) {
							
							grid[i][1]=1;
						}else {
							grid[i][1]=0;
						}
					
						
						
						changeLRU2(i,2,4,total,true);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(this.op==0) {
							
							this.tAcceso=this.tMc+this.tBl; 
						}else {
							this.tAcceso=this.tMc+this.tMp+this.tBl;
						}
						changeLRU2(i,2,4,total,true);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl;
							changeLRU2(i,2,4,total,true);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;
							
							
						}
						
					}
				}
				
			}
		}else if(this.qConjunto==2) {//-------------------------------Conjunto 3
			boolean stop=false;
			int i=4;
			int total=-1;
			while(i<6) {//checkea el numero que hay
				if(grid[i][0]==1) {
					total++;
				}
				i++;
			}
			i=4;
			while(i<6 && !stop ) {//check if exists
				if(grid[i][4]==this.bloqueMP) {
					stop=true;
					break;
				}
				i++;
			}
			if(i==6) {
				i--;
			}
			if(stop) {//checkea si es acierto o no
				this.af="Acierto";
				if(this.pEscritura==0) {//wb
					if(op==1) {
						grid[i][1]=1;
					}
					if(this.lRu) {
						changeLRU2(i,4,6,total, true);
					}
					this.tAcceso=this.tMc; 
				}else if(this.pEscritura==1) {//wt mc
					if(this.op==0) {
						this.tAcceso=this.tMc; 
					}else {
						this.tAcceso=this.tMc+this.tMp;
					}
					if(this.lRu) {
						changeLRU2(i,4,6,total, true);
					}
				}else {//wt mp
					if(this.op==0) {
						this.tAcceso=this.tMc; 
						if(this.lRu) {
							changeLRU2(i,4,6,total, true);
						}
					}else {
						this.tAcceso=this.tMc+this.tMp;
						grid[i][4]=-1;//bloque
						grid[i][0]=0;//ocup
						grid[i][2]=-1;//tag
						changeLRU2(i,4,6,-1, true);
					}
					
				}
			}else {
				this.af="Fallo";
				stop=false;
				i=4;
				
				while(i<6 && !stop) {//checkea si hay alguno libre
					if(grid[i][0]==0) {
						stop=true;
						break;
					}
					i++;
				}
				
				if(i==6) {//si llega al final
					i--;
				}
				if(stop==true) {
					
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==1) {
							
							grid[i][1]=1;
						}
						this.tAcceso=this.tMc+this.tBl; 
						changeLRU2(i,4,6,total+1,false);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==0) {
							this.tAcceso=this.tMc+this.tBl; 
						}else{
							this.tAcceso=this.tMc+this.tMp+this.tBl;  
						}
						
						changeLRU2(i,4,6,total+1,false);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl; 
							changeLRU2(i,4,6,total+1,false);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;  
							
						}
						
					}
				}else {
					
					stop=false;
					i=4;
					
					while(i<6 && !stop) {//checkea cual es el LRU o fifo mas bajo
						if(grid[i][3]<1) {
							stop=true;
							break;
						}
						i++;
					}
					
					if(i==6) {
						i--;
					}
					grid[i][4]=this.bloqueMP;
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(grid[i][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							this.tAcceso=this.tMc+this.tBl+this.tBl;
						}
						if(op==1) {
							
							grid[i][1]=1;
						}else {
							grid[i][1]=0;
						}
						
						changeLRU2(i,4,6,total,true);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(this.op==0) {
							
							this.tAcceso=this.tMc+this.tBl; 
						}else {
							this.tAcceso=this.tMc+this.tMp+this.tBl;
						}
						changeLRU2(i,4,6,total,true);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl;
							changeLRU2(i,4,6,total,true);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;
							
							
						}
						
					}
				}
				
			}
		}else{//----------------------------------------------Conjunto 4
			boolean stop=false;
			int i=6;
			int total=-1;
			while(i<8) {//checkea el numero que hay
				if(grid[i][0]==1) {
					total++;
				}
				i++;
			}
			i=6;
			while(i<8 && !stop ) {//check if exists
				if(grid[i][4]==this.bloqueMP) {
					stop=true;
					break;
				}
				i++;
			}
			if(i==8) {
				i--;
			}
			if(stop) {//checkea si es acierto o no
				this.af="Acierto";
				if(this.pEscritura==0) {//wb
					if(op==1) {
						grid[i][1]=1;
					}
					if(this.lRu) {
						changeLRU2(i,6,8,total, true);
					}
					this.tAcceso=this.tMc; 
				}else if(this.pEscritura==1) {//wt mc
					if(this.op==0) {
						this.tAcceso=this.tMc; 
					}else {
						this.tAcceso=this.tMc+this.tMp;
					}
					if(this.lRu) {
						changeLRU2(i,6,8,total, true);
					}
				}else {//wt mp
					if(this.op==0) {
						this.tAcceso=this.tMc; 
						if(this.lRu) {
							changeLRU2(i,6,8,total, true);
						}
					}else {
						this.tAcceso=this.tMc+this.tMp;
						grid[i][4]=-1;//bloque
						grid[i][0]=0;//ocup
						grid[i][2]=-1;//tag
						changeLRU2(i,6,8,-1, true);
					}
					
				}
			}else {
				this.af="Fallo";
				stop=false;
				i=6;
				
				while(i<8 && !stop) {//checkea si hay alguno libre
					if(grid[i][0]==0) {
						stop=true;
						break;
					}
					i++;
				}
				
				if(i==8) {//si llega al final
					i--;
				}
				if(stop==true) {
					
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==1) {
							
							grid[i][1]=1;
						}
						this.tAcceso=this.tMc+this.tBl; 
						changeLRU2(i,6,8,total+1,false);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==0) {
							this.tAcceso=this.tMc+this.tBl; 
						}else{
							this.tAcceso=this.tMc+this.tMp+this.tBl;  
						}
						
						changeLRU2(i,6,8,total+1,false);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl; 
							changeLRU2(i,6,8,total+1,false);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;  
							
						}
						
					}
				}else {
					
					stop=false;
					i=6;
					
					while(i<8 && !stop) {//checkea cual es el LRU o fifo mas bajo
						if(grid[i][3]<1) {
							stop=true;
							break;
						}
						i++;
					}
					
					if(i==8) {
						i--;
					}
					grid[i][4]=this.bloqueMP;
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(grid[i][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							this.tAcceso=this.tMc+this.tBl+this.tBl;
						}
						if(op==1) {
							
							grid[i][1]=1;
						}else {
							grid[i][1]=0;
						}
						
						changeLRU2(i,6,8,total,true);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(this.op==0) {
							
							this.tAcceso=this.tMc+this.tBl; 
						}else {
							this.tAcceso=this.tMc+this.tMp+this.tBl;
						}
						changeLRU2(i,6,8,total,true);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl;
							changeLRU2(i,6,8,total,true);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;
							
							
						}
						
					}
				}
				
			}
		}
		printCacheStyle();
		
	}
	
	public void asociativaConjuntos2() {
		this.qConjunto=this.bloqueMP%this.nConjunto;
		this.tag=this.bloqueMP/this.nConjunto;
		if (this.qConjunto==0) {
			boolean stop=false;
			int i=0;
			int total=-1;
			while(i<4) {//checkea el numero que hay
				if(grid[i][0]==1) {
					total++;
				}
				i++;
			}
			i=0;
			while(i<4 && !stop ) {//check if exists
				if(grid[i][4]==this.bloqueMP) {
					stop=true;
					break;
				}
				i++;
			}
			if(i==4) {
				i--;
			}
			if(stop) {//checkea si es acierto o no
				this.af="Acierto";
				if(this.pEscritura==0) {//wb
					if(op==1) {
						grid[i][1]=1;
					}
					if(this.lRu) {
						changeLRU2(i,0,4,total, true);
					}
					this.tAcceso=this.tMc; 
				}else if(this.pEscritura==1) {//wt mc
					if(this.op==0) {
						this.tAcceso=this.tMc; 
					}else {
						this.tAcceso=this.tMc+this.tMp;
					}
					if(this.lRu) {
						changeLRU2(i,0,4,total, true);
					}
				}else {//wt mp
					if(this.op==0) {
						this.tAcceso=this.tMc; 
						if(this.lRu) {
							changeLRU2(i,0,4,total, true);
						}
					}else {
						this.tAcceso=this.tMc+this.tMp;
						grid[i][4]=-1;//bloque
						grid[i][0]=0;//ocup
						grid[i][2]=-1;//tag
						changeLRU2(i,0,4,-1, true);
					}
					
				}
			}else {
				this.af="Fallo";
				stop=false;
				i=0;
				
				while(i<4 && !stop) {//checkea si hay alguno libre
					if(grid[i][0]==0) {
						stop=true;
						break;
					}
					i++;
				}
				
				if(i==4) {//si llega al final
					i--;
				}
				if(stop==true) {
					
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==1) {
							
							grid[i][1]=1;
						}
						this.tAcceso=this.tMc+this.tBl; 
						changeLRU2(i,0,4,total+1,false);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==0) {
							this.tAcceso=this.tMc+this.tBl; 
						}else{
							this.tAcceso=this.tMc+this.tMp+this.tBl;  
						}
						
						changeLRU2(i,0,4,total+1,false);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl; 
							changeLRU2(i,0,4,total+1,false);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;  
							
						}
						
					}
				}else {
					
					stop=false;
					i=0;
					
					while(i<4 && !stop) {//checkea cual es el LRU o fifo mas bajo
						if(grid[i][3]<1) {
							stop=true;
							break;
						}
						i++;
					}
					
					if(i==4) {
						i--;
					}
					grid[i][4]=this.bloqueMP;
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(grid[i][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							this.tAcceso=this.tMc+this.tBl+this.tBl;
						}
						if(op==1) {
							
							grid[i][1]=1;
						}else {
							grid[i][1]=0;
						}
						
						changeLRU2(i,0,4,total,true);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(this.op==0) {
							
							this.tAcceso=this.tMc+this.tBl; 
						}else {
							this.tAcceso=this.tMc+this.tMp+this.tBl;
						}
						changeLRU2(i,0,4,total,true);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl;
							changeLRU2(i,0,4,total,true);
						}else {
							
							this.tAcceso=this.tMc+this.tMp;
							
							
						}
						
					}
				}
				
			}
		}else {//-------------------------------------------------------------------Conjunto 2
			boolean stop=false;
			int i=4;
			int total=-1;
			while(i<8) {//checkea el numero que hay
				if(grid[i][0]==1) {
					total++;
				}
				i++;
			}
			i=0;
			while(i<8 && !stop ) {//check if exists
				if(grid[i][4]==this.bloqueMP) {
					stop=true;
					break;
				}
				i++;
			}
			if(i==8) {
				i--;
			}
			if(stop) {//checkea si es acierto o no
				this.af="Acierto";
				if(this.pEscritura==0) {//wb
					if(op==1) {
						grid[i][1]=1;
					}
					if(this.lRu) {
						changeLRU2(i,4,8,total, true);
					}
					this.tAcceso=this.tMc; 
				}else if(this.pEscritura==1) {//wt mc
					if(this.op==0) {
						this.tAcceso=this.tMc; 
					}else {
						this.tAcceso=this.tMc+this.tMp;
					}
					if(this.lRu) {
						changeLRU2(i,4,8,total, true);
					}
				}else {//wt mp
					if(this.op==0) {
						this.tAcceso=this.tMc; 
						if(this.lRu) {
							changeLRU2(i,4,8,total, true);
						}
					}else {
						this.tAcceso=this.tMc+this.tMp;
						grid[i][4]=-1;//bloque
						grid[i][0]=0;//ocup
						grid[i][2]=-1;//tag
						changeLRU2(i,4,8,-1, true);
					}
					
				}
			}else {
				this.af="Fallo";
				stop=false;
				i=4;
				
				while(i<8 && !stop) {//checkea si hay alguno libre
					if(grid[i][0]==0) {
						stop=true;
						break;
					}
					i++;
				}
				
				if(i==8) {//si llega al final
					i--;
				}
				if(stop==true) {
					
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==1) {
							
							grid[i][1]=1;
						}
						this.tAcceso=this.tMc+this.tBl; 
						changeLRU2(i,4,8,total+1,false);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(op==0) {
							this.tAcceso=this.tMc+this.tBl; 
						}else{
							this.tAcceso=this.tMc+this.tMp+this.tBl;  
						}
						
						changeLRU2(i,4,8,total+1,false);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl; 
							changeLRU2(i,4,8,total+1,false);
						}else {
							grid[i][4]=-1;
							grid[i][0]=0;//ocup
							grid[i][2]=-1;//tag
							this.tAcceso=this.tMc+this.tMp;  
							changeLRU2(i,4,8,-1,true);
						}
						
					}
				}else {
					
					stop=false;
					i=4;
					
					while(i<8 && !stop) {//checkea cual es el LRU o fifo mas bajo
						if(grid[i][3]<1) {
							stop=true;
							break;
						}
						i++;
					}
					
					if(i==8) {
						i--;
					}
					grid[i][4]=this.bloqueMP;
					if(this.pEscritura==0) {//wb
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(grid[i][1]==0) {
							this.tAcceso=this.tMc+this.tBl;
						}else {
							this.tAcceso=this.tMc+this.tBl+this.tBl;
						}
						if(op==1) {
							
							grid[i][1]=1;
						}else {
							grid[i][1]=0;
						}
						
						changeLRU2(i,4,8,total,true);
					}else if(this.pEscritura==1) {//wt mc
						grid[i][4]=this.bloqueMP;
						grid[i][0]=1;//ocup
						grid[i][2]=this.bloqueMP;//tag
						if(this.op==0) {
							
							this.tAcceso=this.tMc+this.tBl; 
						}else {
							this.tAcceso=this.tMc+this.tMp+this.tBl;
						}
						changeLRU2(i,4,8,total,true);
					}else {//wt mp
						if(this.op==0) {
							grid[i][4]=this.bloqueMP;
							grid[i][0]=1;//ocup
							grid[i][2]=this.bloqueMP;//tag
							this.tAcceso=this.tMc+this.tBl;
							changeLRU2(i,4,8,total,true);
						}else {
							grid[i][4]=-1;
							grid[i][0]=0;//ocup
							grid[i][2]=-1;//tag
							this.tAcceso=this.tMc+this.tMp;
							changeLRU2(i,4,8,-1,true);
							
						}
						
					}
				}
				
			}
		}
		
		printCacheStyle();
		
		
	}
	
	//dependiendo de si es conjuntos, fifo o lru devolverá algo diferente, hacer mejor una para cada una
	public void printCacheStyle() {
		
		System.out.println("---ocup--mod--tag--rem--bloque");
		System.out.println("    "+grid[0][0]+"    "+grid[0][1] +"    "+grid[0][2] +"    "+grid[0][3] +"    "+grid[0][4]);
		System.out.println("    "+grid[1][0]+"    "+grid[1][1] +"    "+grid[1][2] +"    "+grid[1][3] +"    "+grid[1][4]);
		System.out.println("    "+grid[2][0]+"    "+grid[2][1] +"    "+grid[2][2] +"    "+grid[2][3] +"    "+grid[2][4]);
		System.out.println("    "+grid[3][0]+"    "+grid[3][1] +"    "+grid[3][2] +"    "+grid[3][3] +"    "+grid[3][4]);
		System.out.println("    "+grid[4][0]+"    "+grid[4][1] +"    "+grid[4][2] +"    "+grid[4][3] +"    "+grid[4][4]);
		System.out.println("    "+grid[5][0]+"    "+grid[5][1] +"    "+grid[5][2] +"    "+grid[5][3] +"    "+grid[5][4]);
		System.out.println("    "+grid[6][0]+"    "+grid[6][1] +"    "+grid[6][2] +"    "+grid[6][3] +"    "+grid[6][4]);
		System.out.println("    "+grid[7][0]+"    "+grid[7][1] +"    "+grid[7][2] +"    "+grid[7][3] +"    "+grid[7][4]);
		System.out.println("-----------------------------------------------------");
		System.out.println("Palabra: "+this.palabra);
		System.out.println("Bloque MP: "+ this.bloqueMP);
		System.out.println("Tag: "+this.tag);
		if(this.tamConjunto==2 || this.tamConjunto==4) {
			System.out.println("Conjunto: "+this.qConjunto);
		}else if(this.tamConjunto==1) {
			System.out.println("Bloque de MC: "+ this.nBloqueMC);
		}
		System.out.println(this.af);
		System.out.println("Tiempo: "+this.tAcceso);
		System.out.println("-----------------------------------------------------");
		this.nRef++;
		this.tTotal=this.tTotal+this.tAcceso;
		if(this.af.equals(acierto)){
			this.nAciertos++;
			
		}
		if(!this.anyFile) {
			
		
			BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Si quiere continuar ponga 0 si no ponga 1");
			try {
				
				int eleccion=Integer.parseInt(br.readLine());
				
				if(eleccion==0) {
					start(this.eleccion);
				}else {
					this.tAcierto=((float)this.nAciertos/(float)this.nRef)*100;
					System.out.println("Tasa de aciertos:" + this.tAcierto+"%");
					System.out.println("Numero de referencias:" + this.nRef);
					System.out.println("Tiempo total:" + this.tTotal);
					
				    return;
				}
				
			} catch (NumberFormatException | IOException e) {
				
				e.printStackTrace();
			}
			
		}
		
		
	}
	public void changeLRU(int j, int now,boolean funcion) {//solo funciona con asociativa con LRU
		
		
				
		if(funcion) {
			int min=grid[j][3];
			if(this.lRu=true) {
				for(int i=0;i<8; i++) {
					
					if(grid[i][3]!=-1&&min<grid[i][3]) {
						grid[i][3]=grid[i][3]-1;
					}
				}
			}else {
				for(int i=0; i<8; i++) {
					if(grid[i][3]!=-1&&min<grid[i][3]) {
						grid[i][3]=grid[i][3]-1;
					}
				}
			}
			
		}else {
			;//no hacer nada
		}
		grid[j][3]=now;
		
	}
	/**
	 * 
	 * @param j actual situación del LRU o FIFO
	 * @param now, como va a estar ahora(depende del numero de bloques que haya en cache)
	 * @param first(primer bloque del conjunto)
	 * @param max(ultimo bloque del conjunto) esta y la anterior sirven para controlar el tamaño
	 * @param funcion, si tiene que actuar el cambio o no. Se podria considerar como un mecanismo de activación
	 */
	public void changeLRU2(int j,int first, int max,int now, boolean funcion){//function lo que hace es si tiene que actuar o no el programa //solo funciona con conjuntos
		
		
		
		if(funcion) {
			int min=grid[j][3];
			if(this.lRu=true) {
				for(int i=first;i<max; i++) {
					
					if(grid[i][3]!=-1&&min<grid[i][3]) {
						grid[i][3]=grid[i][3]-1;
					}
				}
			}else {
				for(int i=0; i<8; i++) {
					if(grid[i][3]!=-1&&min<grid[i][3]) {
						grid[i][3]=grid[i][3]-1;
					}
				}
			}
			
		}else {
			;//no hacer nada
		}
		grid[j][3]=now;
		
	}
	
	
	
}
