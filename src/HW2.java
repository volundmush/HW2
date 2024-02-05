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
import java.util.Scanner;

public class HW2 {
   private final Scanner data;
   private static class Entry {
      public String course;
      public SinglyLinkedList<String> slots;

      public Entry(String course, SinglyLinkedList<String> slots) {
         this.course = course;
         this.slots = slots;
      }
   }

   private static class Schedule {
      public SinglyLinkedList<Entry> courses = new SinglyLinkedList<>();
      public SinglyLinkedList<Entry> conflict = new SinglyLinkedList<>();

      public SinglyLinkedList<String> slotsUsed = new SinglyLinkedList<>();

      public Schedule() {

      }
   }

   private final SinglyLinkedList<Entry> entries = new SinglyLinkedList<>();
   private Schedule output;

   public HW2(Scanner data) {
      this.data = data;
   }

   private void handleLine () {
      final String line = data.nextLine();

      String[] parts = line.split(" ", 2);
      String course = parts[0];
      SinglyLinkedList<String> slots = new SinglyLinkedList<>();
      for(String slot : parts[1].split(" ")) slots.addLast(slot);

      entries.addLast(new Entry(course, slots));
   }

   private Schedule recurseEntries(int depth, Schedule chain, SinglyLinkedList<Entry> remaining) {
      if(chain == null) {
         chain = new Schedule();
      }

      for(int i = 0; i < remaining.size(); i++) {
            Entry current = remaining.removeFirst();

            for(int j = 0; j < current.slots.size(); j++) {
               String slot = current.slots.removeFirst();
               if(chain.slotsUsed.contains(slot)) {
                  // This time slot is already used.
                  SinglyLinkedList<String> conf = new SinglyLinkedList<>();
                  conf.addLast(slot);
                  chain.conflict.addLast(new Entry(current.course, conf));
               } else {
                  // Timeslot is not used yet. Let's try it.
                  chain.slotsUsed.addLast(slot);
                  SinglyLinkedList<String> conf = new SinglyLinkedList<>();
                  conf.addLast(slot);
                  chain.courses.addLast(new Entry(current.course, conf));
               }

               current.slots.addLast(slot);
            }

            remaining.addLast(current);
      }

   }

   public void run() {
      // First gather up the data...
      while (data.hasNextLine()) {
         handleLine();
      }

      Schedule results = recurseEntries(0, null, entries);

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



