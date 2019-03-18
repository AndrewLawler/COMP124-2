import java.util.LinkedList;

/* The Computators of Vrymnos program.
 * Threads are used to represent Apprentices. Each Apprentice will
 * retrieve three tablets from cubicles, sum their values and announce the result.
 * Copyright David Jackson 2019.
 */

/*
Name: Andrew Lawler
Student ID: 201210893
Student E-Mail: A.M.Lawler@student.liverpool.ac.uk
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
   private Box b;
   private int id;

   // Constructor. Apprentice needs access to row of cubicles.
   // Each Apprentice has unique ID.
   public Apprentice(Cubicle cubrow[], int iden, Box box) 
   {  row = cubrow;
      id = iden;
      b = box;
   }

   public void run(){  
      int ncubs = row.length - 1;
      int tot = 0; int value;

         // Get 3 tablets
      for (int i = 0; i < 3; i++){  
         int num = (int)(Math.random()*ncubs) + 1; // Select random cubicle

         // If cubicle is empty, move along until we find a full one

         while ((value = row[num].remove()) == 0){  
            num++;
            if (num > ncubs) num = 1;
         }
         
            // Announce the tablet value and add it to total
         System.out.println("Apprentice " + id + " has retrieved a tablet. The number is " + value); 
         tot += value;
         if(i==2){
            b.insert(tot,id);
         }
         // Yield to allow fairer scheduling of threads
         Thread.yield();
      }
   } 

}

// consumer
class Volumina extends Thread {

   private Box b;
   int finalTotal = 0;

   public Volumina(Box box){
      b = box;
   }

   public void run(){
      while(b.finish==false){
         int n = b.remove();
         finalTotal = finalTotal + n;
      }  
      System.out.println("Volumina summons the final total...");
      System.out.println("Total is "+finalTotal);
      
   } 

}

class Box {

   LinkedList<Integer> box = new LinkedList<>();
   private int v;
   int RunningTotal = 0;
   boolean finish = false;
   private int e;

   public Box(int endResult){
      e = endResult;
   }

   public synchronized void insert(int value, int id) {
      //System.out.println(box);
      while(box.size()==2){
         try {
            wait();
         } 
         catch (InterruptedException e) {}
      }

      if(box.size()<2){
         System.out.println("Apprentice " + id + " has moved their parchment into the box");
         box.add(value);
      }
      finish = false;
      notify();
   }
   
   public synchronized int remove() {
      while(box.size()==0) {
         try{
            wait();
         }
         catch (Exception e) {}
      }

      if(box.size()>=1){
         v = box.removeFirst();
      }

      System.out.println("Volumina removed a total of "+v+" from the box");

      RunningTotal = RunningTotal+v;
      if(RunningTotal==e){
         finish = true;
      }

      notify();
      return v;
      
   }
   
}


public class Compute1 
{
   // We will have 18 cubicles and 6 Apprentices
   private static final int NUMCUBS = 18;
   private static final int NUMAPPS = 6;

   public static void main(String args[]) 
   {  

      // Finding end result
      // eg: All cubicles added

      int endResult = 0;
      for(int i=1; i<=NUMCUBS; i++){
         endResult = endResult+i;
      }
      
      Box box = new Box(endResult);

      Volumina v = new Volumina(box);
      v.start();

      // Set up array of cubicles. Have an extra one so that we can index from 1.
      // Initialise each tablet value to be same as cubicle no.
      Cubicle row[] = new Cubicle[NUMCUBS+1];
      for (int i = 1; i <= NUMCUBS; i++)
         row[i] = new Cubicle(i);

      // Set up array of Apprentices
      Apprentice apprentices[] = new Apprentice[NUMAPPS];
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i] = new Apprentice(row, i+1, box);

      // Start up the Apprentice threads to run concurrently
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i].start();

   }
}