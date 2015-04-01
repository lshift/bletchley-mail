package net.lshift.bletchley.mail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.lshift.spki.suiteb.ActionType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TestLog implements Log {

    private Map<String,List<ActionType>> log = Maps.newHashMap();

    @Override
    public void log(Task task, ActionType action) throws IOException {
        loggedInternal(task).add(action);
    }

    @Override
    public Iterable<ActionType> logged(Task task) throws IOException {
        return Collections.unmodifiableList(loggedInternal(task));
    }

    private List<ActionType> loggedInternal(Task task) {
        return log.getOrDefault(ObjectFiles.id(task), Lists.newArrayList());
    }

}
