package net.coderodde.brainfuckide;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BrainfuckVM {

    private static final int TAPE_LENGTH = 30_000;
    
    private final String code;
    private final byte[] tape = new byte[TAPE_LENGTH];
    private int instructionPointer; 
    private int dataPointer;
    private final PrintStream out;
    
    public BrainfuckVM(String code, PrintStream out) {
        this.code = Objects.requireNonNull(code, "The code is null.");
        this.out = Objects.requireNonNull(out, "Output writer is null.");
        checkParenthesisStructure(code);
    }
    
    public void execute() {
        while (instructionPointer < code.length()) {
            char command = code.charAt(instructionPointer);
//            System.out.println(instructionPointer + ": " + command);
            
            switch (command) {
                case '<':
                    moveDataPointerLeft();
                    break;
                   
                case '>':
                    moveDataPointerRight();
                    break;
                    
                case '+':
                    incrementAtDataPointer();
                    break;
                    
                case '-':
                    decrementAtDataPointer();
                    break;
                    
                case '[':
                    processBlockBegin();
                    break;
                    
                case ']':
                    processBlockEnd();
                    break;
                    
                case '.':
                    outputCurrentDatum();
                    break;
                    
                case ',':
                    return;
            }
        }
    }
    
    private void outputCurrentDatum() {
        out.print((char) tape[dataPointer]);
        instructionPointer++;
    }
    
    public void onByteInput(byte inputByte) {
        tape[dataPointer] = inputByte;
        // 'instructionPointer' points to the ',' command. Increment it in 
        // order to start fetching commands after the ',':
        instructionPointer++; 
        execute();
    }
    
    private void moveDataPointerLeft() {
        dataPointer--;
        instructionPointer++;
        
        if (dataPointer < 0) {
            throw new IllegalStateException(
                    "Data pointer (" + dataPointer + ") out of tape.");
        }
    }
    
    private void moveDataPointerRight() {
        dataPointer++;
        instructionPointer++;
        
        if (dataPointer >= tape.length) {
            throw new IllegalStateException(
                    "Data pointer (" + dataPointer + ") out of tape. Tape " + 
                    "length = " + tape.length + ".");
        }
    }
    
    private void incrementAtDataPointer() {
        instructionPointer++;
        tape[dataPointer]++;
    }
    
    private void decrementAtDataPointer() {
        instructionPointer++;
        tape[dataPointer]--;
    }
    
    private void processBlockBegin() {
        if (tape[dataPointer] != 0) {
            instructionPointer++;
            return;
        } 
        
        int counter = 1;
        int index;
        
        for (index = instructionPointer + 1; index < code.length(); ++index) {
            char currentCharacter = code.charAt(index);
            
            if (currentCharacter == ']') {
                counter--;
                
                if (counter == 0) {
                    break;
                }
            } else if (currentCharacter == '[') {
                counter++;
            }
        }
        
        // Move past the matching ']':
        instructionPointer = index + 1;
    }
    
    private void processBlockEnd() {
        if (tape[dataPointer] == 0) {
            instructionPointer++;
            return;
        }
        
        int counter = 1;
        int index;
        
        for (index = instructionPointer - 1; index >= 0; --index) {
            char currentCharacter = code.charAt(index);
            
            if (currentCharacter == '[') {
                counter--;
                
                if (counter == 0) {
                    break;
                }
            } else if (currentCharacter == ']') {
                counter++;
            }
        }
        
        instructionPointer = index + 1;
    }
    
    private void checkParenthesisStructure(String code) {
        Deque<Character> stack = new ArrayDeque<>();
        RuntimeException exception =
                new IllegalArgumentException("Bad bracket structure");
        
        for (char c : code.toCharArray()) {
            if (c == '[') {                
                stack.addLast(c);
            } else if (c == ']') {
                if (stack.isEmpty()) {
                    throw exception;
                }
                
                if (stack.getLast().equals('[')) {
                    stack.removeLast();
                } else {
                    throw exception;
                }
            }
        }
        
        if (!stack.isEmpty()) {
            throw exception;
        }
    }
}
