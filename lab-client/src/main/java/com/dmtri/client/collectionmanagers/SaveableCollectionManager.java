package com.dmtri.client.collectionmanagers;

import java.io.FileNotFoundException;

public interface SaveableCollectionManager extends CollectionManager {
    public void save() throws FileNotFoundException;
}
