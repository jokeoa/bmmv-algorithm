package cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

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

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (verbose || shouldPrintLine(line)) {
                        System.out.println(line);
                    }
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("\n✓ Tests passed");
            } else {
                System.out.println("\n✗ Tests failed");
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
}
