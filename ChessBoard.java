import java.io.File;
import java.io.FileInputStream;
import java.util.*;

class ChessBoard {
    public static class ChessPiece {
        public int r;
        public int c;
        // if locked_value>=0, its state is decided
        public int locked_value = -1;
        public int siblings_count = 0;
        public int type_index = -1;
        public int[] value_limit = null;
        public int only_one_eligible_value = -1;
    };

    private ChessPiece[][] chese_matrix = null;
    private boolean[][] chese_constraint_matrix = null;
    private ArrayList<ArrayList<ChessPiece>> type_set_pieces = null;

    int rows = 0;
    int columns = 0;

    public ChessBoard(int rs, int cs) {
        StdioProcess.SIMPLE_ASSERT(rs >= 1);
        StdioProcess.SIMPLE_ASSERT(cs >= 1);
        chese_matrix = new ChessPiece[rs][cs];
        {
            for (int r = 0; r < rs; r++) {
                for (int c = 0; c < cs; c++) {
                    chese_matrix[r][c] = new ChessPiece();
                    chese_matrix[r][c].r = r;
                    chese_matrix[r][c].c = c;
                }
            }
        }
        chese_constraint_matrix = new boolean[2 * rs - 1][2 * cs - 1];
        {
            for (int r = 0; r < 2 * rs - 1; r++) {
                for (int c = 0; c < 2 * cs - 1; c++) {
                    chese_constraint_matrix[r][c] = true;
                }
            }
        }

        rows = rs;
        columns = cs;
    }

    boolean getContraint(int r, int c) {
        return chese_constraint_matrix[r][c];
    }

    void setContraint(int r, int c, boolean v) {
        chese_constraint_matrix[r][c] = v;
    }

    public static ChessBoard readFile(String filename) {
        try {
            File f = new File(filename);
            Scanner fin = new Scanner(new FileInputStream(f));
            String tmp = fin.nextLine();
            tmp = tmp.replace(":", "");
            String[] strs = tmp.split("X");
            int rows = new Integer(strs[0]);
            int cols = new Integer(strs[1]);
            ChessBoard res = new ChessBoard(rows, cols);
            fin.nextLine();
            for (int i = 0; i < 2 * rows - 1; i++) {
                String current = fin.nextLine();
                current = current.substring(1);
                for (int j = 0; j < 2 * cols - 1; j++) {
                    if (current.charAt(j) == '|' || current.charAt(j) == '-') {
                        res.setContraint(i, j, true);
                    } else {
                        res.setContraint(i, j, false);
                    }
                    if (i % 2 == 0 && j % 2 == 0) {
                        char c = current.charAt(j);
                        if (c >= '1' && c <= '9') {
                            res.chese_matrix[i / 2][j / 2].locked_value = (int) c - '1';
                        }
                    }
                }
            }
            return res;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void showCheseValue() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int locked_value = this.chese_matrix[r][c].locked_value;
                if (locked_value >= 0)
                    System.out.printf("%3d ", locked_value + 1);
                else
                    System.out.printf("  . ");
            }
            if (r != rows - 1)
                System.out.println("");
        }
    }

    public void showCheseValueLimitCount() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                System.out.printf("%3d ", sumZeroValue(this.chese_matrix[r][c]));
            }
            if (r != rows - 1)
                System.out.println("");
        }
    }

    public void showSiblings() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int siblings_count = this.chese_matrix[r][c].siblings_count;
                System.out.printf("%3d ", siblings_count);
            }
            if (r != rows - 1)
                System.out.println("");
        }
    }

    public void showCheseTypeIndex() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int type_index = this.chese_matrix[r][c].type_index;
                if (type_index >= 0)
                    System.out.printf("%3d ", type_index + 1);
                else
                    System.out.printf("  . ");
            }
            if (r != rows - 1)
                System.out.println("");
        }
    }

    private int base_type_index = 0;

    int getTypeIndex() {
        return base_type_index++;
    }
    private void decideTypeIndexInner(int r, int c, int value)
    {
    	if(value>=0 && this.chese_matrix[r][c].type_index>=0) {
    		if(value != this.chese_matrix[r][c].type_index)
    		{
    			System.err.println("error");
    		}
    	}
    	if(this.chese_matrix[r][c].type_index < 0)
    	{
    		if(value >= 0)
    			this.chese_matrix[r][c].type_index = value;
    		else
    		{
    			this.chese_matrix[r][c].type_index = getTypeIndex();
    			value = this.chese_matrix[r][c].type_index;
    		}
    		
    		int[] rarr = new int[] { r - 1, r + 1, r, r };
            int[] carr = new int[] { c, c, c - 1, c + 1 };
            for (int k = 0; k < 4; k++) {
                // neighbor
                int ri = rarr[k];
                int ci = carr[k];
                // constraint
                int cri = (2 * ri + 2 * r) / 2;
                int cci = (2 * ci + 2 * c) / 2;
                if (ri < 0 || ci < 0)
                    continue;
                if (ri >= rows || ci >= columns)
                    continue;
                if (this.getContraint(cri, cci))
                    continue;
                if(this.chese_matrix[ri][ci].type_index >= 0) continue;
                decideTypeIndexInner(ri, ci, value);
            }
    	}
    }
    void decideTypeIndex() {
        
    	for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
            	decideTypeIndexInner(r,c,-1);
            }
        }
        
        
        this.type_set_pieces = new ArrayList<ArrayList<ChessPiece>>();
        for (int i = 0; i < base_type_index; i++) {
            this.type_set_pieces.add(new ArrayList<ChessPiece>());
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int type_index = this.chese_matrix[r][c].type_index;
                StdioProcess.SIMPLE_ASSERT(type_index >= 0);
                this.type_set_pieces.get(type_index).add(this.chese_matrix[r][c]);
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int type_index = this.chese_matrix[r][c].type_index;
                this.chese_matrix[r][c].siblings_count = this.type_set_pieces.get(type_index).size();
                this.chese_matrix[r][c].value_limit = new int[this.chese_matrix[r][c].siblings_count];
                for (int k = 0; k < this.chese_matrix[r][c].siblings_count; k++) {
                    this.chese_matrix[r][c].value_limit[k] = 0;
                }
            }
        }
    }

    void decideTypeIndex_Intelligent()
    {
        decideTypeIndex();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++)
            {
                ChessPiece cp = this.chese_matrix[r][c];
                if(cp.locked_value >= 0)
                {
                    int old_value = cp.locked_value;
                    cp.locked_value = -1;
                    this.setLockedValue(r,c,old_value);
                }
            }
        }
    }

    private boolean isSolved()
    {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if(this.chese_matrix[r][c].locked_value < 0)
                    return false;    
            }
        }
        return true;
    }
    //0:  solve is ok.
    //1:  good
    //-1: bad error, solving is bad
    private int getNextR_Intelligent()
    {
        if(isSolved()) return 0;
        int nr = -1;
        int nc = -1;
        int min_value = 10000;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                ChessPiece cp = this.chese_matrix[r][c];
                if(cp.locked_value >= 0 )
                    continue;
                int count = sumZeroValue(cp);
                if(count == 0)
                    return -1;
                if(min_value > count)
                {
                    min_value = count;
                    nr = r;
                    nc = c;
                }
            }
        }
        nextr = nr;
        nextc = nc;
        if(nextr == -1 || nextc == -1)
        {
            System.out.println("error 274");
        }
        return 1;
    }
    private int sumZeroValue(ChessPiece cp)
    {
        int sum = 0;
        for(int i=0; i<cp.siblings_count; i++)
        {
            if(cp.value_limit[i] == 0)
                sum++;
        }
        return sum;
    }

    int[] setLockedValue(int r, int c, int lock_value) {
        ChessPiece cp = this.chese_matrix[r][c];
        cp.locked_value = lock_value;
        int type_index = cp.type_index;
        ArrayList<ChessPiece> sets = type_set_pieces.get(type_index);
        for(int i=0; i<sets.size(); i++)
        {
            ChessPiece other = sets.get(i);
            if(other.locked_value >= 0) continue;
            if(other.r==r && other.c == c) continue;
            other.value_limit[lock_value]++;
        }


        for(int i=0; i<columns; i++)
        {
            if (0 <= i && i < columns && i!=c) {
                ChessPiece other = this.chese_matrix[r][i];
               
                if(other.locked_value >= 0) continue;
                if(other.type_index == cp.type_index) continue;
                if(lock_value >= other.value_limit.length) continue;
                other.value_limit[lock_value]++;
            }
        }
        for(int i=0; i<rows; i++)
        {
            if (0 <= i && i < rows && i!=r) {
                ChessPiece other = this.chese_matrix[i][c];
                if(other.locked_value >= 0) continue;
                if(other.type_index == cp.type_index) continue;
                if(lock_value >= other.value_limit.length) continue;
                other.value_limit[lock_value]++;
            }
        }
        int[] old_state = Arrays.copyOf(cp.value_limit, cp.value_limit.length);
        for(int i=0; i<cp.value_limit.length; i++)
            cp.value_limit[i] = 1;
        return old_state;
    }

    void releaseLockedValue(int r, int c, int[] old_state)
    {
        ChessPiece cp = this.chese_matrix[r][c];
        if(cp.locked_value < 0)
        {
            System.err.println("error: releaseLockedValue1");
        }
        int lock_value = cp.locked_value;
        if(old_state.length != cp.value_limit.length)
        {
            System.err.println("error: releaseLockedValue2");
        }
        for(int i=0; i<old_state.length; i++)
        {
            cp.value_limit[i] = old_state[i];
        }

        int type_index = cp.type_index;
        ArrayList<ChessPiece> sets = type_set_pieces.get(type_index);
        for(int i=0; i<sets.size(); i++)
        {
            ChessPiece other = sets.get(i);
            if(other.locked_value >= 0) continue;
            if(other.r==r && other.c == c) continue;
            other.value_limit[lock_value]--;
        }

        for(int i=0; i<columns; i++)
        {
            if (0 <= i && i < columns &&i!=c) {
                ChessPiece other = this.chese_matrix[r][i];
                if(other.locked_value >= 0) continue;
                if(other.type_index == cp.type_index) continue;
                if(lock_value >= other.value_limit.length) continue;
                other.value_limit[lock_value]--;
            }
        }
        for(int i=0; i<rows; i++)
        {
            if (0 <= i && i < rows&&i!=r) {
                ChessPiece other = this.chese_matrix[i][c];
                if(other.locked_value >= 0) continue;
                if(other.type_index == cp.type_index) continue;
                if(lock_value >= other.value_limit.length) continue;
                other.value_limit[lock_value]--;
            }
        }

        cp.locked_value = -1;

    }

    class RefInt{
       public int count = 0;
    };

    boolean ingelligentSolverInner(int r, int c, RefInt ri)
    {
        ri.count++;
        ChessPiece current = this.chese_matrix[r][c];
        for (int ii = 0; ii < current.siblings_count; ii++) 
        {
            if(current.value_limit[ii] > 0) continue;
            int lock_value = ii;
            int[] old_state = this.setLockedValue(r, c,lock_value);
            int res = getNextR_Intelligent();
            if(res == -1)
            {
                this.releaseLockedValue(r,c,old_state);
                continue;
            }
            else if(res == 0)
            {
                return true;
            }
            else
            {
                if(this.ingelligentSolverInner(nextr, nextc, ri))
                {
                    return true;
                }
                else
                {
                    this.releaseLockedValue(r,c,old_state);
                    continue;
                }
            }
        }
        return false;
    }

   

    public void ingelligentSolver()
    {
        RefInt ri = new RefInt();
        long begin = System.currentTimeMillis();
        int res = getNextR_Intelligent();
        if(res == 1)
            ingelligentSolverInner(nextr, nextc, ri);
        System.out.printf("used milliseconds: %d\n", System.currentTimeMillis()-begin);
        System.out.printf("function calls count: %d\n", ri.count);
    }

    

    
    private int nextr;
    private int nextc;
    
};