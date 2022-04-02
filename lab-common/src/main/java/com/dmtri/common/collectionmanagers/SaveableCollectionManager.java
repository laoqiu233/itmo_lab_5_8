package com.dmtri.common.collectionmanagers;

import java.io.FileNotFoundException;

public interface SaveableCollectionManager extends CollectionManager {
    void save() throws FileNotFoundException;
}
