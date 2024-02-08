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
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;

public class HW2 {
   private final Scanner data;
   private static class Entry {
      public String course;
      public ArrayList<String> slots;

      public Entry(String course, ArrayList<String> slots) {
         this.course = course;
         this.slots = slots;
      }

      @Override
      public String toString() {
         return String.format("%s %s", course, String.join(" ", slots));
      }
   }

   private final ArrayList<Entry> entries = new ArrayList<>();
   private final ArrayList<String> codes = new ArrayList<>();

   private static class Schedule {
      public ArrayList<Entry> courses = new ArrayList<>();
      public ArrayList<Entry> conflict = new ArrayList<>();

      public ArrayList<String> slotsUsed = new ArrayList<>();

      public Schedule() {

      }

      public int score(ArrayList<String> codes) {
         // The schedule with the highest score prioritizes classes in order of listing.
         int total = 0;

         for(Entry e : courses) {
            int found = codes.indexOf(e.course);
            if(found != -1) total += codes.size() - found;
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

      public void render() {
         if (!courses.isEmpty()) {
            System.out.println("---Course Schedule---");
            for (Entry e : courses) {
               System.out.println(e);
            }
         }
         if (!conflict.isEmpty()) {
            System.out.println("---Courses with a time conflict---");
            for (Entry e : conflict) {
               System.out.println(e);
            }
         }
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
      ArrayList<String> slots = new ArrayList<>(Arrays.asList(parts[1].split(" ")));
      codes.add(course);
      entries.add(new Entry(course, slots));
   }

   private void compareSchedule(Schedule chain) {
      if(output == null) {
         output = chain.copy();
      } else {
         if(chain.score(codes) > output.score(codes)) {
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
      Entry current = newRemaining.remove(0);

      for(String slot : current.slots) {
         if(chain.slotsUsed.contains(slot)) {
            ArrayList<String> conf = new ArrayList<>();
            conf.add(slot);
            chain.conflict.add(new Entry(current.course, conf));
            if(newRemaining.isEmpty()) {
               compareSchedule(chain);
            } else {
               recurseEntries(chain, newRemaining);
            }
            chain.conflict.remove(chain.conflict.size()-1);
         } else {
            // We have a working timeslot.
            // Timeslot is not used yet. Let's try it.
            chain.slotsUsed.add(slot);
            ArrayList<String> chosenSlot = new ArrayList<>();
            chosenSlot.add(slot);
            // Add the course...
            chain.courses.add(new Entry(current.course, chosenSlot));

            // Perform recursion...
            if(newRemaining.isEmpty()) {
               compareSchedule(chain);
            } else {
               recurseEntries(chain, newRemaining);
            }

            // Remove current course in order to try the next one...
            chain.courses.remove(chain.courses.size()-1);
            chain.slotsUsed.remove(chain.slotsUsed.size()-1);
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
         output.render();
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



