package de.unituebingen.sfs.npuzzle;
import java.util.*;

/**
 * Hello NN Puzzle world!
 *
 */
public class NNPuzzle
{
    private int[] tiles;
    private int boardSize;
    private int goalState;
    private int inccuredCost;
    //private NNPuzzle parent;
    // constructor taking N
    public NNPuzzle( int N ) throws Exception{
        if ((N < 3) || (N > 6)){
            throw new Exception("Incorrect board size!");
        }
        int length  = N * N;
        tiles = new int[length];
        for (int i = 0; i < length-1; i++){//length-1 is the end index.
            tiles[i] = i+1;
        }
        tiles[length-1] = 0;//already 0
        //no misplaced tile
        this.goalState = 0;
        //no cost
        this.inccuredCost = 0;
    }

    // constructor, using array of ints
    public NNPuzzle( int[] tiles ){
        //unique with blank tile
        //check size
        //clone and sort
        this.tiles = tiles;
        this.goalState = this.manhattan();
        //this.goalState = this.hamming();
        //set boardSize instance variable
    }

    @Override
    public boolean equals(Object obj) {

	// given
        if (this == obj) { return true;  }
	if (obj == null) { return false; }
	
        if (getClass() != obj.getClass()) {
            return false;
        }

        NNPuzzle pobj = (NNPuzzle) obj;
			
        // two puzzles are equal when they have their tiles positioned equally.

        for(int i = 0; i < this.tiles.length; i++){
            if(this.tiles[i] != pobj.tiles[i]){
                return false;
            }
        }

        if(this.goalState != pobj.goalState){
            return false;
        }
        return true;
        //Arrays.equals
    }

    // given, do not change or delete.
    @Override
    public int hashCode() {
        return Arrays.hashCode(tiles);
    }
    
    public List<NNPuzzle> successors() throws Exception{
	// The following is not binding (but the code compiles ;-)
    Random rand = new Random();

	List<NNPuzzle> successorList = new ArrayList<NNPuzzle>();
	int blankIndex;
	int N = (int)Math.sqrt(this.tiles.length);
	// first, locate zero
    blankIndex = findBlank(this.tiles);

    if (blankIndex == -1){
        throw new Exception("Can not find blank Tile!");
    }
        int col = (blankIndex % N) +1;
        int row = (blankIndex / N) + 1;
        //clone and include partent to make a new nnpuzzle
        if (row == 1) {//top row
            NNPuzzle justDown = moveDown(this.tiles, blankIndex, N);
            justDown.inccuredCost = this.inccuredCost +1;
            //justDown.parent = this;
            successorList.add(justDown);
        } else if (row == N) {//bottom row
            NNPuzzle justUp = moveUp(this.tiles, blankIndex, N);
            justUp.inccuredCost = this.inccuredCost +1;
            //justUp.parent = this;
            successorList.add(justUp);
        } else {
            NNPuzzle down = moveDown(this.tiles, blankIndex, N);
            down.inccuredCost = this.inccuredCost +1;
            //down.parent = this;
            successorList.add(down);
            NNPuzzle up = moveUp(this.tiles, blankIndex, N);
            up.inccuredCost = this.inccuredCost + 1;
            //up.parent = this;
            successorList.add(up);
        }

        if (col == 1) {//leftmost column
            NNPuzzle justRight = moveRight(this.tiles, blankIndex);
            justRight.inccuredCost = this.inccuredCost + 1;
            //justRight.parent = this;
            successorList.add(justRight);
        } else if (col == N) {//rightmost column
            NNPuzzle justLeft = moveLeft(this.tiles, blankIndex);
            justLeft.inccuredCost = this.inccuredCost +1;
            //justLeft.parent = this;
            successorList.add(justLeft);
        } else {
            NNPuzzle left = moveLeft(this.tiles, blankIndex);
            left.inccuredCost = this.inccuredCost +1;
            //left.parent = this;
            successorList.add(left);
            NNPuzzle right = moveRight(this.tiles, blankIndex);
            right.inccuredCost = this.inccuredCost +1;
            //right.parent = this;
            successorList.add(right);
        }
        //if the successor list is randomised, then the search performance will be unstable. Some times much faster
        //Collections.shuffle(successorList);
        return successorList;
    }
    private int findBlank(int[] tiles){
        int blank = -1;
        for (int i = 0; i < tiles.length; i++){
            if (tiles[i]== 0){
                blank = i;
                return blank;
            }
        }
        return blank;
    }

    //I kept these helper methods separate because it makes the code easy to read
    private NNPuzzle moveLeft(int[] tiles, int blankIndex) throws Exception {
        int[] copy = tiles.clone();
        move(copy, blankIndex, blankIndex-1);
        return new NNPuzzle(copy);
    }
    private NNPuzzle moveRight(int[] tiles, int blankIndex) throws Exception {
        int[] copy = tiles.clone();
        move(copy, blankIndex, blankIndex+1);
        return new NNPuzzle(copy);
    }
    private NNPuzzle moveUp(int[] tiles, int blankIndex, int N) throws Exception {
        int[] copy = tiles.clone();
        move(copy, blankIndex, blankIndex - N);
        return new NNPuzzle(copy);
    }
    private NNPuzzle moveDown(int[] tiles, int blankIndex, int N) throws Exception {
        int[] copy = tiles.clone();
        move(copy, blankIndex, blankIndex + N);
        return new NNPuzzle(copy);
    }

    //a swap or exchange method.
    private void move(int[]tiles, int index, int anotherIndex){
        int temp = tiles[index];
        tiles[index] = tiles[anotherIndex];
        tiles[anotherIndex] = temp;
    }

    public void easyShuffle( int numberOfMoves ) throws Exception {
        HashSet<NNPuzzle> closedList = new HashSet<>();//prevent from cycle
        Random random = new Random();
        for (int i = 0; i < numberOfMoves; i++){
            List<NNPuzzle> nextMoves = this.successors();
            int index = random.nextInt(nextMoves.size());
            NNPuzzle cur = nextMoves.get(index);
            while (closedList.contains(cur)){
                index = random.nextInt(nextMoves.size());
                cur = nextMoves.get(index);
            }
            this.tiles = cur.tiles;
            closedList.add(cur);//for cycle prevention
        }
        NNPuzzle newPuzzle = new NNPuzzle(this.tiles);
        this.goalState = newPuzzle.goalState;//reset goalState after shuffle completed
    }

    public void knuthShuffle() {//shuffle should be divisible the number of permutations. If not some states are going to be represented more.
        int shuffleLength = this.tiles.length - 1;
        Random random = new Random();
        for (int i = 0; i < shuffleLength; i++){
            int j = random.nextInt(i+1);//bound it here so that the end result is just the number of permutations?
            move(this.tiles, i, j);
        }
    }

    public boolean isSolvable() throws Exception {
        boolean isEven = false;
        int count = countInversion(this.tiles);
        if (count%2 == 0){
            isEven = true;
        }
	    return isEven;// count not zero and even
    }
    private int countInversion(int[] tiles){
        int count = 0;

        for (int i  = 0; i < tiles.length-1; i++){
            int key = tiles[i];
            int[] pre = Arrays.copyOfRange(tiles, 0, i);
            if (pre.length > 0) {
                Arrays.sort(pre);
                int insPoint = Arrays.binarySearch(pre, key);
                int modIP = insPoint + 1;
                int length = pre.length;
                int inversion = modIP + length;
                count = count + inversion;
            }
        }
        return count;
    }

    public boolean isSolved() throws Exception {
        //old method before goalState introduced
        /*boolean isSolved = false;
        int N = (int)Math.sqrt(this.tiles.length);
        NNPuzzle solved = new NNPuzzle(N);
        if(this.equals(solved)){
            isSolved = true;
        }
	    return isSolved;*/
        return this.goalState == 0;
    }

    public void createStartState() throws Exception {
        //initialize
        int N = (int)Math.sqrt(this.tiles.length);
        NNPuzzle solved = new NNPuzzle(N);
        this.tiles = solved.tiles;
        //shuffle
        boolean isSolvable = false;
        while (!isSolvable){
            this.knuthShuffle();//shuffle the tiles
            isSolvable = this.isSolvable();
        }
        NNPuzzle newPuzzle = new NNPuzzle(this.tiles);
        this.goalState = newPuzzle.goalState;//reset goalState after shuffle completed
    }

    public int hamming(){//ignore zero place
        int count = 0;
        int size = this.tiles.length-1;
        for(int i = 0; i < size; i++){
            if(this.tiles[i] != i+1){//if it is not the correct tile, increase count by one
                count++;
            }
        }
	    return count;
    }

    public int manhattan() {
        int sum = 0;
        int N = (int)Math.sqrt(this.tiles.length);
        for(int i = 0; i < this.tiles.length; i++){
            if((this.tiles[i] != 0) && (this.tiles[i] != i+1)){//ignore zero and tiles that are in place
                int destIndex = this.tiles[i] -1;
                //compute manhattan distance |column difference| + |row difference|
                int curCol = (i % N) +1;
                int destCol = (destIndex % N) +1;
                int curRow = (i / N) +1;
                int destRow = (destIndex / N) +1;
                int m = Math.abs(curCol-destCol)+ Math.abs(curRow-destRow);

                sum = sum + m;
            }
        }
	    return sum;
    }

    public static void blindSearch( NNPuzzle startState) throws Exception {
        Stack<NNPuzzle> openList = new Stack<>();
        HashSet<NNPuzzle> closedList = new HashSet<>();
        //HashSet<NNPuzzle> openCopy = new HashSet<>();
        List<NNPuzzle> nextStates;
        NNPuzzle current = startState;
        current.printState();
        System.out.println(Arrays.toString(current.tiles));
        openList.add(current);
        //openCopy.add(current);
        int count = 0;

        while (!openList.isEmpty()) {
            current = openList.pop();
            //openCopy.remove(current);
            count++;

            if (current.isSolved()){
                current.printState();
                System.out.println("total states: " + count);
                break;
            }else{
                closedList.add(current);
                nextStates = current.successors();
                ListIterator<NNPuzzle> iterator = nextStates.listIterator();
                while (iterator.hasNext()){
                    NNPuzzle state = iterator.next();

                    //another version use a hash set to maintain a copy of open list. checking is faster.
                    //if ((!openCopy.contains(state))&&(!closedList.contains(state))){
                    if (!closedList.contains(state)){//if state is not in closed list
                        openList.push(state);
                        //openCopy.add(state);
                    }

                }
            }
        }
    }
    public static void heuristicSearch( NNPuzzle startState ) throws Exception {
        PriorityQueue<NNPuzzle> openList = new PriorityQueue<>( new Comparator<NNPuzzle>(){
            public int compare (NNPuzzle p1, NNPuzzle p2){
                int c1 = p1.goalState;
                int c2 = p2.goalState;
                return c1 - c2;
            }
        });
        //HashSet<NNPuzzle> openCopy = new HashSet<>();
        HashSet<NNPuzzle> closedList = new HashSet<>();
        List<NNPuzzle> nextStates;

        NNPuzzle current = startState;
        current.printState();
        System.out.println(Arrays.toString(current.tiles));
        openList.add(startState);//offer()
        //openCopy.add(current);
        int count = 0;//or return size of the closed list + 1

        while (!openList.isEmpty()) {
            current = openList.remove();//poll()
            //openCopy.remove(current);
            count++;

            if (current.isSolved()){
                current.printState();
                System.out.println("total states: " + count);
                break;
            }else{
                closedList.add(current);
                nextStates = current.successors();
                ListIterator<NNPuzzle> iterator = nextStates.listIterator();
                while (iterator.hasNext()){
                    NNPuzzle state = iterator.next();

                    //another version use a hash set to maintain a copy of open list. checking is faster.
                    //if ((!openCopy.contains(state))&&(!closedList.contains(state))){
                    if (!closedList.contains(state)){
                        openList.add(state);
                        //openCopy.add(state);
                    }
                }
            }
        }

    }

    public static void aStarSearch(NNPuzzle startState) throws Exception{
        PriorityQueue<NNPuzzle> openList = new PriorityQueue<>( new Comparator<NNPuzzle>(){//Comparator.comparingInt(o -> o.goalDistance())
            public int compare (NNPuzzle p1, NNPuzzle p2){
                int c1 = p1.manhattan() + p1.inccuredCost;
                int c2 = p2.manhattan() + p2.inccuredCost;
                return c1 - c2;
            }
        });
        HashMap<NNPuzzle, Integer> openCopy = new HashMap<>();
        HashMap<NNPuzzle, Integer> closedList = new HashMap<>();
        List<NNPuzzle> nextStates;

        NNPuzzle current = startState;
        current.printState();
        System.out.println(Arrays.toString(current.tiles));
        openList.add(startState);
        openCopy.put(current, current.inccuredCost);
        int count = 0;

        while (!openList.isEmpty()) {
            current = openList.remove();
            openCopy.remove(current);
            count++;

            if (current.isSolved()){
                current.printState();
                System.out.println("total states: " + count);
                break;
            }else{
                closedList.put(current, current.inccuredCost);
                nextStates = current.successors();
                ListIterator<NNPuzzle> iterator = nextStates.listIterator();
                while (iterator.hasNext()){
                    NNPuzzle state = iterator.next();
                    if (openCopy.containsKey(state)){
                        if (openCopy.get(state) <= state.inccuredCost){//if there is a better one
                            continue;
                        }else{
                            openList.remove(state);
                            openCopy.remove(state);
                        }
                    }
                    if (closedList.containsKey(state)){
                        if (closedList.get(state) <= state.inccuredCost){//if there is a better one examined
                            continue;
                        }else{
                            closedList.remove(state);
                        }
                    }
                    if((!openCopy.containsKey(state))&&(!closedList.containsKey(state))){
                    openList.add(state);
                    openCopy.put(state, state.inccuredCost);
                    }
                }//cost and distance > =  old and new
                //path length must be even for knuth shuffle
            }
        }
    }
    private void printState(){
        int n = (int)Math.sqrt(this.tiles.length);
        for(int i = 0; i< n; i++) {
            for (int j = (i * n); j < (i+1)* n; j++)
                System.out.print(this.tiles[j]+" ");
                System.out.println("");
        }
        System.out.println("_______ _______ _______");
    }
    //printTrace(parentNode, ParentMove)
    public static void main( String[] args ) throws Exception
    {
        //NNPuzzle test = new NNPuzzle(3);

        //int[] test1 ={0, 2, 3, 5, 6, 7, 1, 4, 8};
        int[] test1 ={1, 2, 3, 4, 5, 6, 7, 0, 13, 9, 11, 8, 10, 15, 12, 14};
        //int[] test1 ={2, 3, 4, 9, 5, 1, 6, 8, 14, 10, 0, 12, 7, 13, 15, 11, 22, 17, 18, 19, 16, 21, 23, 24, 20};
        //int[] test1 ={1, 4, 21, 5, 0, 15, 32, 7, 16, 11, 3, 2, 14, 9, 13, 19, 17, 6, 33, 28, 29, 10, 26, 18, 20, 23, 34, 35, 12, 24, 8, 27, 25, 22, 31, 30};
        NNPuzzle test = new NNPuzzle(test1);


        //int[] test2 ={6, 2, 4, 5, 0, 1, 7, 8, 3};
        //int[] test2 ={7, 1, 4, 8, 2, 11, 15, 3, 6, 9, 12, 13, 10, 0, 5, 14};
        //int[] test2 ={1, 8, 9, 15, 3, 6, 7, 4, 10, 14, 11, 2, 0, 5, 13, 16, 12, 18, 20, 19, 21, 17, 23, 22, 24};
        //int[] test2 = {21, 2, 8, 4, 5, 6, 14, 1, 3, 16, 11, 12, 20, 13, 10, 9, 17, 18, 7, 15, 26, 22, 23, 24, 0, 31, 32, 28, 29, 30, 19, 25, 27, 33, 34, 35};
        //NNPuzzle test = new NNPuzzle(test2);


        //int[] test3 ={4, 3, 6, 8, 7, 2, 0, 1, 5};
        //int[] test3 = {8, 1, 14, 7, 4, 10, 6, 0, 2, 15, 12, 3, 9, 13, 5, 11};
        //int[] test3 = {16, 1, 10, 9, 23, 4, 7, 3, 11, 12, 22, 2, 15, 20, 5, 21, 0, 19, 8, 14, 6, 17, 18, 13, 24};
        //int[] test3 = {13, 7, 2, 4, 9, 18, 1, 3, 10, 6, 12, 5, 22, 33, 8, 11, 24, 23, 27, 31, 21, 17, 35, 28, 0, 19, 20, 26, 16, 29, 32, 15, 14, 25, 30, 34};
        //NNPuzzle test = new NNPuzzle(test3);


        //int[] test4 = {8, 7, 6, 2, 5, 3, 4, 1, 0};
        //int[] test4 = {10, 6, 14, 1, 5, 7, 11, 9, 3, 2, 15, 8, 13, 4, 12, 0};
        //int[] test4 = {5, 15, 20, 16, 6, 11, 9, 2, 17, 19, 1, 22, 3, 14, 21, 8, 7, 13, 24, 12, 10, 23, 4, 18, 0};
        //int[] test4 = {24, 5, 14, 4, 28, 10, 9, 22, 17, 8, 30, 20, 13, 7, 6, 32, 16, 19, 11, 1, 3, 15, 29, 2, 34, 26, 23, 33, 21, 35, 12, 25, 18, 31, 27, 0};
        //NNPuzzle test = new NNPuzzle(test4);

        //test.createStartState();
        //test.easyShuffle(100);
        StopWatch stopwatch = new StopWatch();
        //blindSearch(test);
        heuristicSearch(test);
        aStarSearch(test);
        double time = stopwatch.elapsedTime();
        System.out.println(time);

	try {

	} catch (Exception e) {
            System.out.printf("Exception!");
        }
    }
}
