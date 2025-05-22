package hep.crest.server.tzero.repositories;

import java.util.List;

public interface ITzeroDb {

    /**
     * Get the list of calibration runs.
     * @return List of runs
     */
    List<Long> calibRunList();
}

