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

        List<String> lines = Files.readAllLines(testFile);
        List<String> meaningfulLines = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            meaningfulLines.add(line);
        }

        if (meaningfulLines.size() % 2 != 0) {
            throw new AssertionError("Test file must contain alternating input/output lines (even count): " + testFile);
        }

        for (int i = 0; i < meaningfulLines.size(); i += 2) {
            String input = meaningfulLines.get(i);
            String expectedOutput = meaningfulLines.get(i + 1);

            controller.processUserInput(input);

            String actualOutput = outputBuffer.toString().replace("\r\n", "\n");
            actualOutput = actualOutput.endsWith("\n")
                    ? actualOutput.substring(0, actualOutput.length() - 1)
                    : actualOutput;

            if (!expectedOutput.equals(actualOutput)) {
                throw new AssertionError("Mismatch in " + testFile + " at pair " + ((i / 2) + 1)
                        + "\nInput: " + input
                        + "\nExpected: " + expectedOutput
                        + "\nActual:   " + actualOutput);
            }

            outputBuffer.reset();
        }
    }

    private void setup() {
        outputBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputBuffer);
        GoosePresenter presenter = new GoosePresenter(out);
        GooseGame game = new GooseGame(presenter);
        controller = new GooseController(game, presenter);
    }
}

