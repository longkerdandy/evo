package com.github.longkerdandy.evo.orientdb.dal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.longkerdandy.evo.api.entity.Device;
import com.github.longkerdandy.evo.api.entity.User;
import com.github.longkerdandy.evo.api.util.JsonUtils;
import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import static com.github.longkerdandy.evo.orientdb.converter.Converter.toDevice;
import static com.github.longkerdandy.evo.orientdb.converter.Converter.toUser;
import static com.github.longkerdandy.evo.orientdb.scheme.Scheme.VERTEX_DEVICE;
import static com.github.longkerdandy.evo.orientdb.scheme.Scheme.VERTEX_USER;

/**
 * Orient Database Access Layer
 */
@SuppressWarnings("unused")
public class OrientStorage {

    protected OrientGraphFactory factory; // factory and pool

    public OrientStorage(OrientGraphFactory factory) {
        this.factory = factory;
    }

    /**
     * Add/Create new user entity
     * User entity must been validated before invoking this method.
     *
     * @param user User Entity
     * @return User Id
     */
    public String addUser(User user) {
        OrientGraph graph = this.factory.getTx();
        try {
            OCommandSQL sql = new OCommandSQL("create vertex " + VERTEX_USER + " content " + JsonUtils.OBJECT_MAPPER.writeValueAsString(user));
            OrientVertex uv = graph.command(sql).execute();
            return String.valueOf(uv.getId());
        } catch (JsonProcessingException e) {
            throw new OException(e);
        } finally {
            graph.shutdown();
        }
    }

    /**
     * Get user entity based on user id
     *
     * @param uid User Id
     * @return User Entity
     */
    public User getUseById(String uid) {
        OrientGraph graph = this.factory.getTx();
        try {
            return toUser(graph.getVertex(uid));
        } finally {
            graph.shutdown();
        }
    }

    /**
     * Add/Create new device entity
     * User entity must been validated before invoking this method.
     *
     * @param device Device Entity
     * @return Device Id
     */
    public String addDevice(Device device) {
        OrientGraph graph = this.factory.getTx();
        try {
            OCommandSQL sql = new OCommandSQL("create vertex " + VERTEX_DEVICE + " content " + JsonUtils.OBJECT_MAPPER.writeValueAsString(device));
            OrientVertex uv = graph.command(sql).execute();
            return String.valueOf(uv.getId());
        } catch (JsonProcessingException e) {
            throw new OException(e);
        } finally {
            graph.shutdown();
        }
    }

    /**
     * Get device entity based on device id
     *
     * @param did Device Id
     * @return Device Entity
     */
    public Device getDeviceById(String did) {
        OrientGraph graph = this.factory.getTx();
        try {
            return toDevice(graph.getVertex(did));
        } finally {
            graph.shutdown();
        }
    }
}
