package com.sd.goose;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sd.goose.game.GooseGame;
import com.sd.goose.io.GooseController;
import com.sd.goose.io.GoosePresenter;

public class Test {

    private static final Path TEST_DIR = Path.of("test");

    private GooseController controller;
    private ByteArrayOutputStream outputBuffer;

    public void test() throws IOException {
        if (!Files.isDirectory(TEST_DIR)) {
            throw new IllegalStateException("Missing test directory: " + TEST_DIR.toAbsolutePath());
        }

        List<Path> testFiles;
        try (Stream<Path> stream = Files.list(TEST_DIR)) {
            testFiles = stream
                    .filter(Files::isRegularFile)
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (testFiles.isEmpty()) {
            throw new IllegalStateException("No test files found in: " + TEST_DIR.toAbsolutePath());
        }

        for (Path testFile : testFiles) {
            runTestFile(testFile);
        }

        System.out.println("All tests passed (" + testFiles.size() + " files).");
    }

    private void runTestFile(Path testFile) throws IOException {
        setup();

        GameLines gameLines = getInputAndOutputLines(testFile);
        List<String> input = gameLines.inputLines();
        List<String> expectedOutput = gameLines.outputLines();

        for (int i = 0; i < input.size(); i++) {
            checkTurn(testFile, input.get(i), expectedOutput.get(i));
        }
    }

    private GameLines getInputAndOutputLines(Path testFile) throws IOException {
        List<String> meaningfulLines = cleanLines(testFile);

        List<String> inputLines = new ArrayList<>();
        List<String> outputLines = new ArrayList<>();

        for (int i = 0; i < meaningfulLines.size(); i++) {
            if (i % 2 == 0) {
                inputLines.add(meaningfulLines.get(i));
            } else {
                outputLines.add(meaningfulLines.get(i));
            }
        }

        if (inputLines.size() != outputLines.size()) {
            throw new AssertionError("Test file must contain alternating input/output lines (even count): " + testFile);
        }

        return new GameLines(inputLines, outputLines);
    }

    private List<String> cleanLines(Path testFile) throws IOException {
        List<String> lines = Files.readAllLines(testFile);
        List<String> meaningfulLines = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            meaningfulLines.add(line);
        }
        return meaningfulLines;
    }

    private void checkTurn(Path testFile, String input, String expectedOutput) throws AssertionError {
        controller.processUserInput(input);

        String actualOutput = outputBuffer.toString().replace("\r\n", "\n");
        actualOutput = actualOutput.endsWith("\n")
                ? actualOutput.substring(0, actualOutput.length() - 1)
                : actualOutput;

        if (!expectedOutput.equals(actualOutput)) {
            throw new AssertionError("Mismatch in " + testFile
                    + "\nInput: " + input
                    + "\nExpected: " + expectedOutput
                    + "\nActual:   " + actualOutput);
        }

        outputBuffer.reset();
    }

    private void setup() {
        outputBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputBuffer);
        GoosePresenter presenter = new GoosePresenter(out);
        GooseGame game = new GooseGame(presenter);
        controller = new GooseController(game, presenter);
    }

    private record GameLines(
            List<String> inputLines,
            List<String> outputLines) {
    }
}
