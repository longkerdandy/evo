package com.github.longkerdandy.evo.arangodb.converter;

import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.EdgeEntity;
import com.github.longkerdandy.evo.api.entity.Document;
import com.github.longkerdandy.evo.api.entity.Relation;

/**
 * Converter
 */
@SuppressWarnings("unused")
public class Converter {

    /**
     * ArangoDB DocumentEntity to Evo Document
     */
    public static <T> Document<T> toDocument(DocumentEntity<T> de) {
        return new Document<>(de.getDocumentKey(), de.getDocumentRevision(), de.getEntity());
    }

    /**
     * ArangoDB EdgeEntity to Evo Relation
     */
    public static <T> Relation<T> toRelation(EdgeEntity<T> ee) {
        return new Relation<>(ee.getDocumentKey(),
                handleToKey(ee.getFromVertexHandle()),
                handleToKey(ee.getToVertexHandle()),
                ee.getDocumentRevision(),
                ee.getEntity());
    }

    /**
     * ArangoDB Handle to Key
     */
    public static String handleToKey(String handle) {
        return handle == null ? null : handle.substring(handle.indexOf('/') + 1);
    }

    /**
     * ArangoDB Key to Handle
     */
    public static String keyToHandle(String collection, String key) {
        return collection + "/" + key;
    }
}
