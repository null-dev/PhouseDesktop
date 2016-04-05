package xyz.nulldev.phouse2dd.util;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Project: Phouse2DD
 * Created: 04/04/16
 * Author: nulldev
 */
public class OrJoiner {
    ArrayList<String> toJoin = new ArrayList<>();

    public void add(String string) {
        toJoin.add(string);
    }

    public String join() {
        StringJoiner commaJoiner = new StringJoiner(", ");
        String suffix;
        ArrayList<String> copiedToJoin = (ArrayList<String>) toJoin.clone();
        if(copiedToJoin.size() >= 2) {
            suffix = " or " + copiedToJoin.remove(copiedToJoin.size() - 1);
        } else {
            suffix = "";
        }
        copiedToJoin.forEach(commaJoiner::add);
        return commaJoiner.toString() + suffix;
    }

    @Override
    public String toString() {
        return join();
    }
}
