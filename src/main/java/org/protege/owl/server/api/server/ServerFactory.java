package org.protege.owl.server.api.server;

import edu.stanford.protege.metaproject.api.ServerConfiguration;

/**
 * An interface to prepare and build a complete server.
 *
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ServerFactory {

    Server build(ServerConfiguration configuration);
}
