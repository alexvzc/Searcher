/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher.app;

import static java.text.MessageFormat.format;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import mx.avc.searcher.AStarSearcher;
import mx.avc.searcher.BlindSearcher;
import static mx.avc.searcher.BlindSearcher.SearchType.BREATH_FIRST;
import static mx.avc.searcher.BlindSearcher.SearchType.DEPTH_FIRST;
import mx.avc.searcher.IDAStarSearcher;
import mx.avc.searcher.PathSearcher;
import mx.avc.searcher.StateController;
import mx.avc.searcher.UnreachableStateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author alexv
 */
public class NPuzzleSearcher {

    private static final Log LOGGER = LogFactory.getLog(NPuzzleSearcher.class);

    private enum SearchType {

        DEPTH, BREATH, ASTAR, IDASTAR

    };

    private enum Move {

        UP(1, 0), DOWN(-1, 0), LEFT(0, 1), RIGHT(0, -1);

        private final int deltaCols;

        private final int deltaRows;

        private Move(int delta_cols, int delta_rows) {
            deltaCols = delta_cols;
            deltaRows = delta_rows;
        }

    }

    private static final int EMPTY_TILE = 0;

    private static final int BRUIJN_CODE = 0x077CB531;

    private static final int[] BRUIJN_TABLE = {
        0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8,
        31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9 };

    private final int maxRows;

    private final int maxCols;

    private final int tileSpaces;

    private final Map<Integer, Set<Move>> possibleMoves;

    private final long result;

    private static int findBit(int n) {
        return BRUIJN_TABLE[((n & -n) * BRUIJN_CODE) >>> 27];
    }

    private NPuzzleSearcher(int square_size) {
        maxRows = square_size;
        maxCols = square_size;
        tileSpaces = maxRows * maxCols;
        Map<Integer, Set<Move>> m = new HashMap<Integer, Set<Move>>();

        for(int i = 0; i < tileSpaces; i++) {
            int column = i % maxCols;
            int row = i / maxCols;

            Set<Move> s = EnumSet.noneOf(Move.class);

            if(row > 0) {
                s.add(Move.DOWN);
            }
            if(column > 0) {
                s.add(Move.RIGHT);
            }
            if(row < (maxRows - 1)) {
                s.add(Move.UP);
            }
            if(column < (maxCols - 1)) {
                s.add(Move.LEFT);
            }

            m.put(i, Collections.unmodifiableSet(s));
        }

        possibleMoves = Collections.unmodifiableMap(m);

        long state = 0;
        for(int i = tileSpaces - 2; i >= 0; i--) {
            state = (state << 4) | i;
        }

        result = Long.valueOf(state);
    }

    private int getIndexDelta(Move move) {
        return move.deltaCols * maxCols + move.deltaRows;
    }

    private long arrayToState(int... array) {
        long state = 0;
        for(int p = 0; p < tileSpaces; p++) {
            int i = array[p];
            if(i == EMPTY_TILE) {
                continue;
            }
            state |= ((long)p) << ((i - 1) * 4);
        }
        return state;
    }

    private int[] stateToArray(long state) {
        int[] array = new int[tileSpaces];
        for(int i = 1; i < tileSpaces; i++) {
            int p = (int)(state & 0xfl);
            array[p] = (byte)i;
            state >>>= 4;
        }
        return array;
    }

    private int computeEmptyTile(long state) {
        int empty_bitmap = 0;
        for(int i = 1; i < tileSpaces; i++) {
            empty_bitmap |= 1 << (int)(state & 0xfl);
            state >>>= 4;
        }
        return findBit(~empty_bitmap);
    }

    private boolean isSolvable(long state) {
        int inversions = 0;
        int empty_bitmap = 0;

        for(int i = 1; i < tileSpaces; i++) {
            int ip = (int)(state & 0xfl);
            empty_bitmap |= 1 << ip;
            state >>>= 4;
            long state_j = state;
            for(int j = i + 1; j < tileSpaces; j++) {
                int jp = (int)(state_j & 0xfl);
                state_j >>>= 4;
                if(ip > jp) {
                    inversions++;
                }
            }
        }

        if((tileSpaces & 1) == 0) {
            int empty_tile_pos = findBit(~empty_bitmap);
            inversions += empty_tile_pos / maxCols;
        }

        return ((inversions & 1) == 0);
    }

    public String stateToString(long state) {
        int[] state_elements = stateToArray(state);
        StringBuilder sb = new StringBuilder("[");
        for(int i = 0; i < tileSpaces; i++) {
            if(state_elements[i] != 0) {
                sb.append(state_elements[i]);
            } else {
                sb.append('_');
            }
            if((i % maxCols) == (maxCols - 1)) {
                sb.append('|');
            } else {
                sb.append(' ');
            }
        }
        int l = sb.length();
        sb.delete(l - 1, l).append("]");
        return sb.toString();
    }

    private long generateRandomly(Random rnd) {
        long state;
        do {
            int space_bitmap = 0;
            state = 0;
            for(int i = tileSpaces; i > 1; i--) {
                int j_bitmap = space_bitmap;
                for(int j = rnd.nextInt(i); j > 0; j--) {
                    j_bitmap |= j_bitmap + 1;
                }
                int tile_pos = findBit(~j_bitmap);
                space_bitmap |= 1 << tile_pos;
                state = (state << 4) | tile_pos;
            }
        } while(!isSolvable(state));

        return state;
    }

    private class Operator implements StateController<Long, Move> {

        public Set<? extends Move> nextMovements(Long current_state) {
            int index = computeEmptyTile(current_state);
            return possibleMoves.get(index);
        }

        public Long nextState(Long current_state, Move move) {
            long state = current_state;
            int emptyt_pos = computeEmptyTile(state);
            int tile_pos = emptyt_pos + getIndexDelta(move);
            long scratch_state = state;
            for(int i = 1; i < tileSpaces; i++) {
                int p = (int)(scratch_state & 0xfl);
                scratch_state >>>= 4;
                if(p == tile_pos) {
                    int bit_offset = (i - 1) * 4;
                    state = (state & ~(0xfl << bit_offset))
                            | (((long)emptyt_pos) << bit_offset);
                    break;
                }
            }
            return state;
        }

        public float getDistance(Long current, Set<Long> goals) {
            float ret = Float.POSITIVE_INFINITY;

            float distance = 0f;

            for(long goal_state : goals) {
                long current_state = current;
                for(int i = 1; i < tileSpaces; i++) {
                    int current_position = (int)(current_state & 0xfl);
                    int goal_position = (int)(goal_state & 0xfl);
                    current_state >>>= 4;
                    goal_state >>>= 4;

                    if(goal_position == current_position) {
                        continue;
                    }

                    int tile_col = current_position % maxCols;
                    int tile_row = current_position / maxCols;
                    int goal_col = goal_position % maxCols;
                    int goal_row = goal_position / maxCols;

                    distance += Math.abs(tile_col - goal_col)
                            + Math.abs(tile_row - goal_row);
                }

                if(distance < ret) {
                    ret = distance;
                }
            }

            return ret;
        }

        public float getCost(Long current_state, Move movement) {
            return 1f;
        }

    }

    public static void main(String[] args) {
        NPuzzleSearcher self = new NPuzzleSearcher(3);
        Operator state_operator = self.new Operator();
        Random rnd = new Random();

        //Long initialState = self.generateRandomly(rnd);
        Long initialState = self.arrayToState(2, 5, 3, 1, 4, 7, 6, 8, 0);
        Long goal = self.result;
        Set<Long> goals = Collections.singleton(goal);

        PathSearcher<Long, Move> ps;

        LOGGER.info(format(
                "Initial estimate steps for  solving {0}: {1,number,#}",
                self.stateToString(initialState),
                state_operator.getDistance(initialState, goals)));

        for(SearchType stype : EnumSet.of(SearchType.ASTAR, SearchType.IDASTAR)) {
            switch(stype) {
                case BREATH:
                    ps = new BlindSearcher<Long, Move>(BREATH_FIRST,
                            state_operator);
                    break;
                case ASTAR:
                    if(self.tileSpaces > 9) {
                        continue;
                    }
                    ps = new AStarSearcher<Long, Move>(state_operator);
                    break;
                case IDASTAR:
                    ps = new IDAStarSearcher<Long, Move>(state_operator);
                    break;
                case DEPTH:
                default:
                    ps = new BlindSearcher<Long, Move>(DEPTH_FIRST,
                            state_operator);
                    break;
            }

            LOGGER.info(format(
                    "Looking for path from {0} to {1} using {2}",
                    self.stateToString(initialState),
                    self.stateToString(goal), stype.toString()));

            try {
                List<? extends Move> path = ps.search(initialState, goals);
                Long state = initialState;
                LOGGER.info(format("Initial state: {0}",
                        self.stateToString(state)));
                for(Move move : path) {
                    state = state_operator.nextState(state, move);
                    LOGGER.info(format("Next state {0}: {1}", move.name(),
                            self.stateToString(state)));
                }
            } catch(UnreachableStateException use) {
                LOGGER.info("Solution not found");
            } catch(OutOfMemoryError oome) {
                Runtime r = Runtime.getRuntime();
                long free = r.freeMemory();
                long total = r.totalMemory();
                long max = r.maxMemory();
                System.gc();
                LOGGER.error(format(
                        "Insufficient memory {0}/{1}/{2}", free, max, total));
            }
        }
    }

}
