// -*- coding: utf-8 -*-

import java.awt.Color;

public class Mandelbrot extends Thread {
    final static int taille = 500 ;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille) ;
    // Il y a donc taille*taille pixels blancs ou gris à déterminer
    final static int max = 80_000 ;
    // C'est le nombre maximum d'itérations pour déterminer la couleur d'un pixel
    static int nbThreads = 4;
    private int thread;



    public static void main(String[] args)  {
        final long début = System.nanoTime() ;


        Mandelbrot[] T = new Mandelbrot[nbThreads];
        image.show();

        for(int i =0; i< nbThreads; i++){
            T[i]= new Mandelbrot(i);
            T[i].start();
        }

        for(int i =0; i< nbThreads; i++){
            try{
                T[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final long fin = System.nanoTime() ;
        final long durée = (fin - début) / 1_000_000 ;
        System.out.println("Durée = " + (double) durée / 1000 + " s.") ;
    }    

    // La fonction colorierPixel(i,j) colorie le pixel (i,j) de l'image en gris ou blanc
    public static void colorierPixel(int i, int j) {
        final Color gris = new Color(90, 90, 90) ;
        final Color blanc = new Color(255, 255, 255) ;
        final double xc = -.5 ;
        final double yc = 0 ; // Le point (xc,yc) est le centre de l'image
        final double region = 2 ;
        /*
          La région du plan considérée est un carré de côté égal à 2.
          Elle s'étend donc du point (xc - 1, yc - 1) au point (xc + 1, yc + 1)
          c'est-à-dire du point (-1.5, -1) en bas à gauche au point (0.5, 1) en haut
          à droite
        */
        double a = xc - region/2 + region*i/taille ;
        double b = yc - region/2 + region*j/taille ;
        // Le pixel (i,j) correspond au point (a,b)

        if (mandelbrot(a, b, max)) image.set(i, j, gris) ;
        else image.set(i, j, blanc) ; 
    }

    // La fonction mandelbrot(a, b, max) détermine si le point (a,b) est gris
    public static boolean mandelbrot(double a, double b, int max) {
        double x = 0 ;
        double y = 0 ;
        for (int t = 0; t < max; t++) {
            if (x*x + y*y > 4.0) return false ; // Le point (a,b) est blanc
            double nx = x*x - y*y + a ;
            double ny = 2*x*y + b ;
            x = nx ;
            y = ny ;
        }
        return true ; // Le point (a,b) est gris
    }

    Mandelbrot(int thread){
        this.thread =thread;

    }

    public void run(){
        for (int i = 0; i < taille; i++) {
                for (int j = (taille/nbThreads)*(thread); j < (taille/nbThreads)* (1+thread); j++) {
                    colorierPixel(i, j);
                }
            synchronized (image){image.show();}
            }
        }

}


/* 
   $ make
   javac *.java 
   jar cvmf MANIFEST.MF Mandelbrot_Old.jar *.class
   manifeste ajouté
   ajout : Mandelbrot_Old.class(entrée = 1697) (sortie = 1066)(compression : 37 %)
   ajout : Picture.class(entrée = 5689) (sortie = 3039)(compression : 46 %)
   rm *.class 
   $ java -version
   java version "11.0.3" 2019-04-16 LTS
   Java(TM) SE Runtime Environment 18.9 (build 11.0.3+12-LTS)
   Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.3+12-LTS, mixed mode)
   $ java -jar Mandelbrot_Old.jar
   Durée = 35.851 s.
   ^C
*/
