package com.dmtri.server.collectionmanagers;

import java.io.FileNotFoundException;

import com.dmtri.common.collectionmanagers.CollectionManager;

public interface SaveableCollectionManager extends CollectionManager {
    void save() throws FileNotFoundException;
}
