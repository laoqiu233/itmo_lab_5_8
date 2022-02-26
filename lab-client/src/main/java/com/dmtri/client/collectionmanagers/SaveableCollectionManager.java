package com.dmtri.client.collectionmanagers;

import java.io.FileNotFoundException;

public interface SaveableCollectionManager extends CollectionManager {
    void save() throws FileNotFoundException;
}
