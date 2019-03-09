import java.util.Scanner;
class StdioProcess {
    public static Scanner stdIn = new Scanner(System.in);
    // for debug
    public static void ASSERT(boolean flag, String message) {
        if (!flag) {
            System.err.println(message);
            System.exit(-1);
        }
    }

    // for debug
    public static void IMPOSSIBLE_HERE() {
        ASSERT(false, "IMPOSSIBLE_HERE");
    }

    // for debug
    public static void SIMPLE_ASSERT(boolean flag) {
        ASSERT(flag, "SIMPLE_ASSERT");
    }
};