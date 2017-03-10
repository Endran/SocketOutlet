package org.craftsmenlabs.socketoutlet.server;

import mockit.Verifications;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class Captors {

    public static Runnable captureThreadRunnable() {
        List<Runnable> runnableList = new ArrayList<>();
        new Verifications() {{
            new Thread(withCapture(runnableList));
        }};
        assertThat(runnableList).hasSize(1);
        return runnableList.get(0);
    }
}
