/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.avc.searcher.AStarSearcher;
import mx.avc.searcher.BlindSearcher;
import mx.avc.searcher.PathSearcher;
import mx.avc.searcher.StateController;
import mx.avc.searcher.UnreachableStateException;
import static java.text.MessageFormat.format;
import static mx.avc.searcher.BlindSearcher.SearchType.BREATH_FIRST;
import static mx.avc.searcher.BlindSearcher.SearchType.DEPTH_FIRST;

/**
 *
 * @author alexv
 */
public class BlockSearcher {

    private static final Log LOGGER = LogFactory.getLog(BlockSearcher.class);

    public static final char EMPTY = '\ufffe';

    private static enum SearchType {

        DEPTH, BREATH, ASTAR

    };

    private static String[] splitState(String state) {
        return state.split("\\|");
    }

    private static String mergeState(String[] columns) {
        Arrays.sort(columns);

        StringBuilder sb = new StringBuilder();
        for(String column : columns) {
            if(column.length() > 0) {
                sb.append(column);
                sb.append('|');
            }
        }

        if(sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    public static class BlockStateOperator implements
            StateController<String, String> {

        public Set<String> nextMovements(String current_state) {
            Set<String> moves = new HashSet<String>();

            List<String> columns = Arrays.asList(splitState(current_state));

            for(int i = 0; i < columns.size(); i++) {
                String column_origin = columns.get(i);
                char origin_block = column_origin.charAt(0);

                if(column_origin.length() > 1) {
                    moves.add(new String(new char[] { origin_block, EMPTY }));
                }

                for(int j = 0; j < columns.size(); j++) {
                    if(i == j) {
                        continue;
                    }
                    String column_destination = columns.get(j);
                    char destination_block = column_destination.charAt(0);

                    moves.add(new String(new char[] { origin_block,
                                destination_block }));
                }
            }

            return moves;
        }

        public String nextState(String current_state, String movement) {
            List<String> columns = new ArrayList<String>(
                    Arrays.asList(current_state.split("\\|")));

            char origin_block = movement.charAt(0);
            char destination_block = movement.charAt(1);

            for(int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                if(origin_block == column.charAt(0)) {
                    column = column.substring(1);
                    columns.set(i, column);
                    break;
                }
            }

            if(destination_block == EMPTY) {
                columns.add(Character.valueOf(origin_block).toString());

            } else {
                for(int i = 0; i < columns.size(); i++) {
                    String column = columns.get(i);
                    if(column.length() > 0
                            && destination_block == column.charAt(0)) {
                        column = origin_block + column;
                        columns.set(i, column);
                        break;
                    }
                }
            }

            return mergeState(columns.toArray(new String[columns.size()]));
        }

        public float getDistance(String current_state, Set<String> final_states) {
            if(final_states.contains(current_state)) {
                return 0f;
            }

            float ret = Float.POSITIVE_INFINITY;

            for(String final_state : final_states) {
                String[] cs = splitState(current_state);
                String[] fs = splitState(final_state);
                float distance = 0f;

                OUTER:
                for(String f : fs) {
                    INNER:
                    for(String c : cs) {
                        if(f.endsWith(c)) {
                            distance += f.length() - c.length();
                            continue OUTER;
                        }
                    }
                    distance += f.length();
                }

                if(distance < ret) {
                    ret = distance;
                }
            }

            return ret;
        }

        public float getCost(String current_state, String movement) {
            return 1f;
        }

    }

    protected static Map<Character, Integer> countChars(String s) {
        Map<Character, Integer> ch_set = new HashMap<Character, Integer>();

        for(int i = 0; i < s.length(); i++) {
            Character ch = s.charAt(i);
            if(ch_set.containsKey(ch)) {
                ch_set.put(ch, ch_set.get(ch) + 1);
            } else {
                ch_set.put(ch, 1);
            }
        }

        return Collections.unmodifiableMap(ch_set);
    }

    protected static boolean isReachable(String a, String b) {
        if(a.length() != b.length()) {
            return false;
        }

        Map<Character, Integer> a_set = countChars(a);
        Map<Character, Integer> b_set = countChars(b);

        return a_set.equals(b_set);
    }

    public static void main(String[] args) {
        if(args.length != 3) {
            LOGGER.error(
                    "Usage: BlockSearcher [DEPTH|BREATH|ASTAR] [initial-state] [final-state]");
            System.exit(-1);
        }

        String type = args[0];
        String initial_s = args[1];
        String final_s = args[2];

        SearchType stype = SearchType.valueOf(type);

        if(!isReachable(initial_s, final_s)) {
            LOGGER.error(format(
                    "Unreachable states: \\'{0}\\' and \\'{1}\\'", initial_s,
                    final_s));
            System.exit(-1);
        }

        BlockStateOperator state_operator = new BlockStateOperator();
        PathSearcher<String, String> ps;

        switch(stype) {
            case BREATH:
                ps = new BlindSearcher<String, String>(BREATH_FIRST,
                        state_operator);
                break;
            case ASTAR:
                ps = new AStarSearcher<String, String>(state_operator);
                break;
            case DEPTH:
            default:
                ps = new BlindSearcher<String, String>(DEPTH_FIRST,
                        state_operator);
                break;
        }

        LOGGER.info(format(
                "Looking for path from ''{0}'' to ''{1}'' using {2}",
                initial_s, final_s, stype.toString()));

        try {
            List<? extends String> path = ps.search(initial_s,
                    Collections.singleton(final_s));

            for(String move : path) {
                char from = move.charAt(0);
                char to = move.charAt(1);
                if(to == EMPTY) {
                    LOGGER.info(format("From the column whose top-most "
                            + "element is ''{0}'' to an new column", from));
                } else {
                    LOGGER.info(format("From the column whose top-most "
                            + "element is ''{0}'' to the column whose "
                            + "top-most element is ''{1}''", from, to));
                }
            }
        } catch(UnreachableStateException use) {
            LOGGER.info("Solution not found");
        }

    }

}
