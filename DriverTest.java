class DriverTest
{
private static String getParameterInner(String[] args, String key, String default_value)
{
    for(int i=0; i<args.length-1; i++)
    {
        if(args[i].equals(key))
            return args[i+1];
    }
    return default_value;
}
private static String getParameter(String[] args, String key, String default_value)
{
    String res = getParameterInner(args, key, default_value);
    System.out.printf("%s:%s\n", key, res);
    return res;
}
public static void main(String[] args)
{
    String filename = getParameter(args, "--filename", "7X6.txt");
    ChessBoard board = ChessBoard.readFile(filename);
    {
        board.decideTypeIndex_Intelligent();
        board.ingelligentSolver();
    }
    System.out.printf("%s answer:\n", filename);
    board.showCheseValue();
    System.out.println("\n-------------------\n");
}
};