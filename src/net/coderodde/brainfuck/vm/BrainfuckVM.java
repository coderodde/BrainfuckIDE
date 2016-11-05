package net.coderodde.brainfuck.vm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import net.coderodde.brainfuck.ide.CharListener;
import net.coderodde.brainfuck.ide.CharacterInputRequestListener;

public class BrainfuckVM {

    private static final int TAPE_LENGTH = 30_000;

    private final String code;
    private final byte[] tape = new byte[TAPE_LENGTH];
    private int instructionPointer; 
    private int dataPointer;
    private final CharListener charListener;
    private final CharacterInputRequestListener requestListener;

    public BrainfuckVM(String code, 
                       CharListener charListener,
                       CharacterInputRequestListener requestListener) {
        this.code = Objects.requireNonNull(code, "The code is null.");
        this.charListener =
                Objects.requireNonNull(charListener,
                                       "The char listener is null.");
        this.requestListener = 
                Objects.requireNonNull(requestListener, 
                                       "The request listener is null.");
        checkParenthesisStructure(code);
    }

    public void execute() {
        while (instructionPointer < code.length()) {
            char command = code.charAt(instructionPointer);

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
                    requestListener.requestCharacterInput();
                    return;

                default:
                    // Unrecognized character, omit it:
                    ++instructionPointer;
            }
        }
    }

    private void outputCurrentDatum() {
        charListener.acceptChar((char) tape[dataPointer]);
        instructionPointer++;
    }

    public void onByteInput(byte inputByte) {
        tape[dataPointer] = inputByte;
        // 'instructionPointer' points to the ',' command. Increment it in 
        // order to start fetching commands after the ',':
        instructionPointer++; 
        requestListener.stopWaitingForCharacterInput();
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
