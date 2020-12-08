package org.processexplorer.webservice.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
@Service
public class AsyncService {

    @Async
    public void run(final Runnable runnable) {
        runnable.run();;
    }

}
