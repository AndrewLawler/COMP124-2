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

         // if we are on the final loop (i==2) send total and id to insert before we yield
         if(i==2){
            b.insert(tot,id);
         }

         // Yield to allow fairer scheduling of threads
         Thread.yield();
      }
   } 

}

class Volumina extends Thread {

   // setting finalTotal to 0 so we can use it to calculate our running total, taking box as a parameter and also NUMAPPS
   private Box b;
   int finalTotal = 0;
   int n;

   // show box being taken as parameter in the constructor as well as NUMAPPS, we then assign box to b and NUMAPPS to n
   public Volumina(Box box, int NUMAPPS){
      n = NUMAPPS;
      b = box;
   }

   public void run(){
      // while we dont have all the pages, run this
      while(b.pagetotal<n){  
         // add to our finalTotal
         finalTotal = finalTotal + b.remove();
      }
      // print final outcome
      System.out.println("\nVolumina has all the pages");
      System.out.println("Final total is: "+finalTotal);
   } 

}

class Box {

   // initializing box and pagetotal counter
   LinkedList<Integer> box = new LinkedList<>();
   int pagetotal = 0;
   // To scale up the project simply change the while box.size == 2 to something else and the program would still work.
   public synchronized void insert(int value, int id){
      // while box = full, wait
      while(box.size()==2){
         try {
            System.out.println("Apprentice " + id + " is now waiting");
            wait();
         } 
         // catch produces nothing so we can leave it empty
         catch (InterruptedException e) {}
      }
      // add to box, print which value has been added and then print the box itself
      box.add(value);
      System.out.println("Apprentice " + id + " has moved their parchment with total "+ value + " into the box");
      System.out.println("Box: "+box);
      // notify waiting threads so they can try and enter their parchment again
      notify();
   }
   
   public synchronized int remove() {
      // while box = empty, wait
      while(box.size()==0) {
         try{
            // print that the box is empty
            System.out.println("Box is empty");
            wait();
         }
         catch (Exception e) {}
      }
      // remove first value in box and print out its value, also print out the box itself
      int v = box.removeFirst();
      System.out.println("Volumina removed a total of "+v+" from the box");
      System.out.println("Box: "+box);
      // add to page total and then print out the current amount of pages we have
      pagetotal++;
      System.out.println("Volumina Page Count: " + pagetotal);
      // notify and return v(value) to Volumina
      notify();
      return v;
   }
   
}


public class Compute1
{
   // We will have 18 cubicles and 6 Apprentices
   // My program is scalable as i could simply change this NUMCUBS value and it would still work perfectly.
   private static final int NUMCUBS = 18;
   // same for NUMAPPS, i passed NUMAPPS into my volumina thread so if you scale up it would still work perfectly. I would just have to make sure that NUMCUBS/NUMAPPS = 3
   private static final int NUMAPPS = 6;

   public static void main(String args[]) 
   {  
      // creating new box object
      Box box = new Box();
      
      // creating volumina and then running her (key: passing box in as parameter and also NUMAPPS so the project is scalable)
      Volumina v = new Volumina(box, NUMAPPS);
      v.start();

      // Set up array of cubicles. Have an extra one so that we can index from 1.
      // Initialise each tablet value to be same as cubicle no.
      Cubicle row[] = new Cubicle[NUMCUBS+1];
      for (int i = 1; i <= NUMCUBS; i++)
         row[i] = new Cubicle(i);

      // Set up array of Apprentices (key: passing box in as parameter)
      Apprentice apprentices[] = new Apprentice[NUMAPPS];
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i] = new Apprentice(row, i+1, box);

      // Start up the Apprentice threads to run concurrently
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i].start();

   }
}