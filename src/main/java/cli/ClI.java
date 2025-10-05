package cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(
    name = "bmmv-test",
    description = "Run tests for Boyer-Moore Majority Vote algorithm",
    mixinStandardHelpOptions = true,
    version = "1.0"
)
public class ClI implements Callable<Integer> {

    @Parameters(
        index = "0",
        description = "Test type to run: basic, edge, property, performance, memory, integration, distribution, context, cross-validation, iterator, verify, or all"
    )
    private String testType;

    @Option(
        names = {"-v", "--verbose"},
        description = "Show detailed test output"
    )
    private boolean verbose = false;

    @Option(
        names = {"-c", "--csv"},
        description = "Export test results to CSV file"
    )
    private String csvFile;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ClI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        String testClass = mapTestType(testType.toLowerCase());

        if (testClass == null) {
            System.err.println("Unknown test type: " + testType);
            System.err.println("Valid types: basic, edge, property, performance, memory, integration, distribution, context, cross-validation, iterator, verify, all");
            return 1;
        }

        try {
            System.out.println("Running " + testType + " tests...\n");

            ProcessBuilder pb;
            if (testClass.equals("all")) {
                pb = new ProcessBuilder("mvn", "test");
            } else {
                pb = new ProcessBuilder("mvn", "test", "-Dtest=" + testClass);
            }

            pb.redirectErrorStream(true);
            Process process = pb.start();

            TestResult result = new TestResult();
            result.testType = testType;
            result.timestamp = LocalDateTime.now();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    parseTestResults(line, result);

                    if (verbose || shouldPrintLine(line)) {
                        System.out.println(line);
                    }
                }
            }

            int exitCode = process.waitFor();
            result.passed = exitCode == 0;

            if (exitCode == 0) {
                System.out.println("\n✓ Tests passed");
            } else {
                System.out.println("\n✗ Tests failed");
            }

            if (csvFile != null) {
                exportToCsv(result);
            }

            return exitCode;

        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            return 1;
        }
    }

    private String mapTestType(String type) {
        return switch (type) {
            case "basic" -> "BoyerMooreMajorityVoteBasicTest";
            case "edge" -> "BoyerMooreMajorityVoteEdgeCasesTest";
            case "property" -> "BoyerMooreMajorityVotePropertyBasedTest";
            case "performance" -> "BoyerMooreMajorityVotePerformanceTest";
            case "memory" -> "BoyerMooreMajorityVoteMemoryTest";
            case "integration" -> "BoyerMooreMajorityVoteIntegrationTest";
            case "distribution" -> "BoyerMooreMajorityVoteDistributionTest";
            case "context" -> "BoyerMooreMajorityVoteContextTest";
            case "cross-validation" -> "BoyerMooreMajorityVoteCrossValidationTest";
            case "iterator" -> "BoyerMooreMajorityVoteIteratorTest";
            case "verify" -> "BoyerMooreMajorityVoteVerifyTest";
            case "all" -> "all";
            default -> null;
        };
    }

    private boolean shouldPrintLine(String line) {
        return line.contains("[INFO] Tests run:") ||
               line.contains("[INFO] Results:") ||
               line.contains("[ERROR]") ||
               line.contains("BUILD SUCCESS") ||
               line.contains("BUILD FAILURE") ||
               line.contains("Running algorithms.");
    }

    private void parseTestResults(String line, TestResult result) {
        // Pattern: [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
        Pattern testRunPattern = Pattern.compile("Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)");
        Matcher matcher = testRunPattern.matcher(line);

        if (matcher.find()) {
            result.testsRun += Integer.parseInt(matcher.group(1));
            result.failures += Integer.parseInt(matcher.group(2));
            result.errors += Integer.parseInt(matcher.group(3));
            result.skipped += Integer.parseInt(matcher.group(4));
        }

        // Pattern: [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.234 s
        Pattern timePattern = Pattern.compile("Time elapsed: ([\\d.]+) s");
        Matcher timeMatcher = timePattern.matcher(line);

        if (timeMatcher.find()) {
            result.timeElapsed += Double.parseDouble(timeMatcher.group(1));
        }
    }

    private void exportToCsv(TestResult result) {
        try {
            Path path = Paths.get(csvFile);
            boolean fileExists = Files.exists(path);

            try (BufferedWriter writer = Files.newBufferedWriter(path,
                    fileExists ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE)) {

                if (!fileExists) {
                    writer.write("Timestamp,Test Type,Passed,Tests Run,Failures,Errors,Skipped,Time (s)");
                    writer.newLine();
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                writer.write(String.format("%s,%s,%s,%d,%d,%d,%d,%.3f",
                        result.timestamp.format(formatter),
                        result.testType,
                        result.passed ? "Yes" : "No",
                        result.testsRun,
                        result.failures,
                        result.errors,
                        result.skipped,
                        result.timeElapsed));
                writer.newLine();

                System.out.println("\nResults exported to: " + csvFile);
            }

        } catch (Exception e) {
            System.err.println("Failed to export CSV: " + e.getMessage());
        }
    }

    private static class TestResult {
        String testType;
        LocalDateTime timestamp;
        boolean passed;
        int testsRun = 0;
        int failures = 0;
        int errors = 0;
        int skipped = 0;
        double timeElapsed = 0.0;
    }
}
