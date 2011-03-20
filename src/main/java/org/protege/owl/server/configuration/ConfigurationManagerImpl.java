package org.protege.owl.server.configuration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.owl.server.api.ConfigurationManager;
import org.protege.owl.server.api.ServerBuilder;
import org.protege.owl.server.api.ServerFactory;
import org.protege.owl.server.metaproject.MetaProject;
import org.protege.owl.server.metaproject.Vocabulary;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ConfigurationManagerImpl implements ConfigurationManager {
    public final static Logger LOGGER = Logger.getLogger(ConfigurationManagerImpl.class);
    
    private Set<ServerBuilder> builders = new HashSet<ServerBuilder>();
    private Set<ServerFactory> serverFactories = new HashSet<ServerFactory>();
    
    public ConfigurationManagerImpl() {
        String metaproject = System.getProperty("command.line.arg.0");
        if (metaproject != null) {
            loadCommandLineMetaproject(metaproject);
        }
    }
    
    private void loadCommandLineMetaproject(String metaproject) {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            MetaProject.addMetaProjectIRIMapper(manager);
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(metaproject));
            setMetaOntology(ontology);
        }
        catch (Exception e) {
            LOGGER.error("Exception caught trying to load metaproject: " + metaproject, e);
        }
    }
    
    
    public void setMetaOntology(OWLOntology ontology) {
        for (ServerBuilder c : builders) {
            c.stop();
        }
        builders.clear();
        Set<OWLIndividual> configs = Vocabulary.SERVER_CLASS.getIndividuals(ontology);
        for (OWLIndividual config : configs) {
            ServerConfiguration serverConfiguration = new ServerConfiguration(ontology, config);
            builders.add(new ServerBuilderImpl(serverConfiguration));
        }
        for (ServerBuilder builder : builders) {
            for (ServerFactory factory : serverFactories) {
                builder.addServerFactory(factory);
            }
        }
    }
    
    @Override
    public Set<ServerBuilder> getServerBuilders() {
        return builders;
    }

    @Override
    public void addServerFactory(ServerFactory factory) {
        serverFactories.add(factory);
        for (ServerBuilder builder : builders) {
            builder.addServerFactory(factory);
        }
    }

    @Override
    public void removeServerFactory(ServerFactory factory) {
        serverFactories.remove(factory);
        for (ServerBuilder builder : builders) {
            builder.removeServerFactory(factory);
        }
    }

    @Override
    public void start() {
        for (ServerBuilder builder : builders) {
            builder.start();
        }    
    }

    @Override
    public void stop() {
        for (ServerBuilder builder : builders) {
            builder.stop();
        }   
    }

}
