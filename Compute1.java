import javax.swing.Box;

/* The Computators of Vrymnos program.
 * Threads are used to represent Apprentices. Each Apprentice will
 * retrieve three tablets from cubicles, sum their values and announce the result.
 * Copyright David Jackson 2019.
 */

class Cubicle 
{  private boolean full = true; // initially, cubicle contains a tablet
   private int tablet;
  
   // Constructor stores a value on the tablet
   public Cubicle (int value)
   {  tablet = value;
   }

   // Synchronized remove method to prevent simultaneous access to cubicle.
   // Returns value on tablet, zero if no tablet is present
   public synchronized int remove() 
   {  int val = 0;
      if (full)
         val = tablet;
      full = false;
      return val;
   }
}


class Apprentice extends Thread 
{  private Cubicle row[];
  
   private int id;

   // Constructor. Apprentice needs access to row of cubicles.
   // Each Apprentice has unique ID.
   public Apprentice(Cubicle cubrow[], int iden) 
   {  row = cubrow;
      id = iden;
   }

   public void run()
   {  int ncubs = row.length - 1;
      int tot = 0; int value;

      // Get 3 tablets
      for (int i = 0; i < 3; i++)
      {  
         int num = (int)(Math.random()*ncubs) + 1; // Select random cubicle

         // If cubicle is empty, move along until we find a full one
         while ((value = row[num].remove()) == 0)
         {  num++;
            if (num > ncubs) num = 1;
         }
       
         // Announce the tablet value and add it to total
         System.out.println("Apprentice " + id + " has retrieved a tablet. The number is " + value); 
         tot += value;

         // Yield to allow fairer scheduling of threads
         Thread.yield();

      }

      // somehow try and add to box using AddArr
      // Announce the total
      //System.out.println("Apprentice " + id + " announces a total of " + tot);
       
   }
}

class BoxOfDigitalDelights {
   public int input = 0;
   public int[] name = new int[6];

   public void AddArr(int pos, int input){
      name[pos] = input;
   }
   
   // Print Box
   public void Print(){
      System.out.println(name[0]);
      //System.out.println("Box run...");
   }
}

class Volumina extends Thread{

   public Volumina(){
   }

   public void run(){   
      //System.out.println("Volumina run...");
   }
}

public class Compute1 
{
   // We will have 18 cubicles and 6 Apprentices
   private static final int NUMCUBS = 18;
   private static final int NUMAPPS = 6;

   public static void main(String args[]) 
   {  
      // Set up array of cubicles. Have an extra one so that we can index from 1.
      // Initialise each tablet value to be same as cubicle no.

      BoxOfDigitalDelights box = new BoxOfDigitalDelights();
      box.AddArr(0,5);

      Cubicle row[] = new Cubicle[NUMCUBS+1];
      for (int i = 1; i <= NUMCUBS; i++)
         row[i] = new Cubicle(i);

      // Set up array of Apprentices
      Apprentice apprentices[] = new Apprentice[NUMAPPS];
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i] = new Apprentice(row, i+1);

      // Start up the Apprentice threads to run concurrently
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i].start();
      
      Volumina Volum = new Volumina();
      Volum.start();

   }
}