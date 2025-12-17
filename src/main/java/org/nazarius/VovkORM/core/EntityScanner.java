package org.nazarius.VovkORM.core;

import org.nazarius.VovkORM.metadata.TableMetadata;

public interface EntityScanner {
    <T> TableMetadata<T> scan(Class<T> clazz);
}
