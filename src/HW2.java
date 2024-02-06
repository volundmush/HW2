/*
 * Author:  Andrew Bastien
 * Email: abastien2021@my.fit.edu
 * Course:  CSE 2010
 * Section: E4
 * Term: Spring 2024
 * Project: HW2, Recursion
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class HW2 {
   private final Scanner data;
   private static class Entry {
      public String course;
      public ArrayList<String> slots;

      public Entry(String course, ArrayList<String> slots) {
         this.course = course;
         this.slots = slots;
      }
   }

   private final ArrayList<Entry> entries = new ArrayList<>();

   private static class Schedule {
      public ArrayList<Entry> courses = new ArrayList<>();
      public ArrayList<Entry> conflict = new ArrayList<>();

      public ArrayList<String> slotsUsed = new ArrayList<>();

      public Schedule() {

      }

      public int score(ArrayList<Entry> entries) {
         // The schedule with the highest score prioritizes classes in order of listing.
         int total = 0;

         for(int i = 0; i < entries.size(); i++) {
            int worth = entries.size() - i;
            Entry e = entries.get(i);
            OptionalInt result = IntStream.range(0, courses.size())
                    .filter(x -> e.course.equals(courses.get(x).course))
                    .findFirst();

            if (result.isPresent())
            {
               total += worth;
            }
         }
         
         
         return total;
      }

      public Schedule copy() {
         Schedule out = new Schedule();
          out.courses.addAll(courses);
          out.conflict.addAll(conflict);
          out.slotsUsed.addAll(slotsUsed);
         return out;
      }
   }

   private Schedule output;

   public HW2(Scanner data) {
      this.data = data;
   }

   private void handleLine () {
      final String line = data.nextLine();

      String[] parts = line.split(" ", 2);
      String course = parts[0];
      ArrayList<String> slots = new ArrayList<>();
      for(String slot : parts[1].split(" ")) slots.addLast(slot);

      entries.addLast(new Entry(course, slots));
   }

   private void compareSchedule(Schedule chain) {
      if(output == null) {
         output = chain.copy();
      } else {
         if(chain.score(entries) > output.score(entries)) {
            output = chain.copy();
         }
      }
   }

   private void recurseEntries(Schedule chain, ArrayList<Entry> remaining) {
      if(chain == null) {
         chain = new Schedule();
      }

      if(remaining.isEmpty())
         return;

      ArrayList<Entry> newRemaining = new ArrayList<>(remaining);
      Entry current = newRemaining.removeFirst();

      for(String slot : current.slots) {
         if(chain.slotsUsed.contains(slot)) {
            ArrayList<String> conf = new ArrayList<>();
            conf.addLast(slot);
            chain.conflict.addLast(new Entry(current.course, conf));
            if(newRemaining.isEmpty()) {
               compareSchedule(chain);
            } else {
               recurseEntries(chain, newRemaining);
            }
            chain.conflict.removeLast();
         } else {
            // We have a working timeslot.
            // Timeslot is not used yet. Let's try it.
            chain.slotsUsed.addLast(slot);
            ArrayList<String> chosenSlot = new ArrayList<>();
            chosenSlot.addLast(slot);
            // Add the course...
            chain.courses.addLast(new Entry(current.course, chosenSlot));

            // Perform recursion...
            if(newRemaining.isEmpty()) {
               compareSchedule(chain);
            } else {
               recurseEntries(chain, newRemaining);
            }

            // Remove current course in order to try the next one...
            chain.courses.removeLast();
            chain.slotsUsed.removeLast();
         }
      }
   }

   public void run() {
      // First gather up the data...
      while (data.hasNextLine()) {
         handleLine();
      }

      recurseEntries(null, entries);

      if(output != null) {
         if(!output.courses.isEmpty()) {
            System.out.println("---Course Schedule---");
            for(Entry e : output.courses) {
               System.out.printf("%s %s\r\n", e.course, String.join(" ", e.slots));
            }
         }
         if(!output.conflict.isEmpty()) {
            System.out.println("---Courses with a time conflict---");
            for(Entry e : output.conflict) {
               System.out.printf("%s %s\r\n", e.course, String.join(" ", e.slots));
            }
         }
      }

   }

   public static void main(final String[] args) {
      // Let's check for the given file?
      if (args.length < 1) {
         System.out.println("No file path provided.");
         return;
      }

      // use java.util.Scanner because dang this is complicated.
      try {
         File file = new File(args[0]);
         Scanner data = new Scanner(file, StandardCharsets.US_ASCII.name());
         HW2 program = new HW2(data);
         program.run();
      } catch (FileNotFoundException e) {
         System.out.println("File not found: " + args[0]);
         return;
      }

   }

}



