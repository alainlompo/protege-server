package org.protege.editor.owl.server.builder;

import org.protege.editor.owl.server.api.BuilderService;
import org.protege.editor.owl.server.api.ServerFactory;
import org.protege.editor.owl.server.api.ServerLayer;
import org.protege.editor.owl.server.api.TransportFactory;
import org.protege.editor.owl.server.api.TransportHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.protege.metaproject.api.ServerConfiguration;

/**
 * Represents a service that is responsible for building the server in OSGi environment.
 *
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ServerBuilderService implements BuilderService {

    private final static Logger logger = LoggerFactory.getLogger(ServerBuilderService.class);

    private ServerFactory serverFactory;

    private TransportFactory transportFactory;

    private ServerConfiguration configuration;

    @Override
    public void initialize(ServerConfiguration configuration) {
        this.configuration = configuration;
        logger.info("Server configuration loaded successfully");
    }

    @Override
    public void setServerFactory(ServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    @Override
    public void setTransportFactory(TransportFactory transportFactory) {
//        if (factory.getFactoryName().equals(configuration.getTransportId())) {
            this.transportFactory = transportFactory;
//        }
    }

    /*
     * OSGi will call this when all of the SCR components required dependencies
     * have been satisfied.
     */
    @Override
    public void activate() {
        try {
            ServerLayer server = (ServerLayer) serverFactory.build(configuration);
            TransportHandler transport = transportFactory.build(configuration);
            server.setTransport(transport);
            transport.bind(server);
        }
        catch (Exception e) {
            logger.error("Failed to build the server instance", e);
        }
    }
}