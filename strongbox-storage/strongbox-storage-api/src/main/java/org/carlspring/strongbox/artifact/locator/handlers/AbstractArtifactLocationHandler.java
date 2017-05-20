package org.carlspring.strongbox.artifact.locator.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.carlspring.strongbox.providers.io.RepositoryFileAttributes;
import org.carlspring.strongbox.providers.io.RepositoryPath;
import org.carlspring.strongbox.storage.Storage;
import org.carlspring.strongbox.storage.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mtodorov
 * @author stodorov
 */
public abstract class AbstractArtifactLocationHandler
        implements ArtifactDirectoryOperation
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractArtifactLocationHandler.class);
    
    private Storage storage;
    
    private LinkedHashMap<RepositoryPath, List<RepositoryPath>> visitedRootPaths = new LinkedHashMap<>();

    /**
     * The base path within the repository from where to start scanning for artifacts.
     */
    private RepositoryPath basePath;


    public LinkedHashMap<RepositoryPath, List<RepositoryPath>> getVisitedRootPaths()
    {
        return visitedRootPaths;
    }

    public List<RepositoryPath> getVersionDirectories(RepositoryPath basePath)
        throws IOException
    {
        Set<RepositoryPath> versionDirectorySet = new TreeSet<>();
        Files.walk(basePath)
             .forEach(p -> {
                 if (isMetadata(p))
                 {
                     versionDirectorySet.add((RepositoryPath) p.getParent());
                 }
             });
        
        return new ArrayList<>(versionDirectorySet);
    }

    protected boolean isMetadata(Path p)
    {
        try
        {
            return Boolean.TRUE.equals(Files.getAttribute(p, RepositoryFileAttributes.METADATA));
        }
        catch (IOException e)
        {
            logger.error(String.format("Failed to read Path attributes for [%s]", p), e);
            return false;
        }
    }

    @Override
    public Storage getStorage()
    {
        return storage;
    }

    public void setStorage(Storage storage)
    {
        this.storage = storage;
    }

    @Override
    public Repository getRepository()
    {
        return basePath.getFileSystem().getRepository();
    }

    @Override
    public RepositoryPath getBasePath()
    {
        return basePath;
    }

    public void setBasePath(RepositoryPath basePath)
    {
        this.basePath = basePath;
    }
    
}
