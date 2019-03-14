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
   private BoxOfDigitalDelights box;
   private Volumina Volum;
 
   // Constructor. Apprentice needs access to row of cubicles.
   // Each Apprentice has unique ID.
   public Apprentice(Cubicle cubrow[], int iden, BoxOfDigitalDelights b, Volumina v)
   {  row = cubrow;
      id = iden;
      box = b;
      Volum = v;
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
        
         if(i==2){
            box.AddArr(tot, id);
            Volum.run();
         }
      
         // Yield to allow fairer scheduling of threads
         Thread.yield();
 
      }
      
   }
}
 
class BoxOfDigitalDelights {
   public int input = 0;
   public int[] tempBox = new int[2];
   boolean box1 = false;
   boolean box2 = false;
   int boxHost1 = 0;
   int boxHost2 = 0;
   boolean remove = false;

   // Add to box
   public void AddArr(int input, int id){
      if(box1==false){
         box1 = true;
         tempBox[0] = input;
         boxHost1 = id-1;  
      }
      
      else if(box2==false){
         box2 = true;
         tempBox[1] = input;
         boxHost2 = id-1;
      }
      remove = true;
   }
   
   public int RemoveArr(){
      int Value = tempBox[0] + tempBox[1];
      tempBox[0] = 0;
      tempBox[1] = 0;
      box1 = false;
      box2 = false;
      return Value;
   }

   public boolean removed(){
      if(tempBox[0]==0 && tempBox[1]==0){
         remove = false;
      }
      else{
         remove = true;
      }
      return remove;
   }
   
}
 
class Volumina extends Thread{
   private int total = 0;
   private BoxOfDigitalDelights box;
   int[] PageSubmitted = {0,0,0,0,0,0};
   private boolean emptied = true;

   public Volumina(BoxOfDigitalDelights b){
      box = b;
   }

   public void run(){
      PageSubmitted[box.boxHost1] = box.boxHost1;
      PageSubmitted[box.boxHost2] = box.boxHost2;

      total = total + box.RemoveArr();

      if(box.removed()==false && emptied==true){
         int PageTotal = PageSubmitted[0]+PageSubmitted[1]+PageSubmitted[2]+PageSubmitted[3]+PageSubmitted[4]+PageSubmitted[5];
         if(PageTotal==15 && box.removed()==false){
            emptied=false;
            System.out.println("Grand Total: "+total);
         }
         box.remove = true;
      }
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

      Volumina Volum = new Volumina(box);
      Volum.start();

      Cubicle row[] = new Cubicle[NUMCUBS+1];
      for (int i = 1; i <= NUMCUBS; i++)
         row[i] = new Cubicle(i);
     
      // Set up array of Apprentices
      Apprentice apprentices[] = new Apprentice[NUMAPPS];
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i] = new Apprentice(row, i+1, box, Volum);
 
      // Start up the Apprentice threads to run concurrently
      for (int i = 0; i < NUMAPPS; i++)
         apprentices[i].start();
     
     
 
   }
}