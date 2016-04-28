package org.activehome.context.helper;

/*
 * #%L
 * Active Home :: Context
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.active-home
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import org.activehome.context.data.Device;
import org.kevoree.*;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.UUIDModel;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.api.handler.UUIDModel;
import org.kevoree.ContainerRoot;
import org.kevoree.api.ModelService;
import org.kevoree.log.Log;
import org.kevoree.pmodeling.api.ModelCloner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for common Kevoree model manipulation.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public final class ModelHelper {

    /**
     * Utility class.
     */
    private ModelHelper() {
    }

    /**
     * @param type          The type of component (class)
     * @param nodeNameArray List of nodes to look at
     * @param localModel    The Kevoree model to look at
     * @return A list of node name found
     */
    public static LinkedList<String> findAllRunning(
            final String type,
            final String[] nodeNameArray,
            final ContainerRoot localModel) {
        LinkedList<String> list = new LinkedList<>();
        if (localModel != null) {
            for (String nodeName : nodeNameArray) {
                ContainerNode node = localModel.findNodesByID(nodeName);
                if (node != null) {
                    node.getComponents().stream()
                            .filter(ci -> ci.getTypeDefinition() != null
                                    && ci.getTypeDefinition().getName() != null)
                            .forEach(ci -> {
                                if (ci.getTypeDefinition() != null) {
                                    if (ci.getTypeDefinition().getName() != null
                                            && ci.getTypeDefinition().getName().compareTo(type) == 0) {
                                        list.add(nodeName + "." + ci.getName());
                                    } else if (lookForSuperType(ci.getTypeDefinition().getSuperTypes(), type)) {
                                        list.add(nodeName + "." + ci.getName());
                                    }
                                }
                            });
                }
            }
        }
        return list;
    }

    public static LinkedList<String> findAllRunning(
            final String type,
            final String[] nodeNameArray,
            final ModelService modelService) {
        UUIDModel model = modelService.getCurrentModel();
        KevoreeFactory kevFactory = new DefaultKevoreeFactory();
        ModelCloner cloner = kevFactory.createModelCloner();
        ContainerRoot localModel = cloner.clone(model.getModel());
        return findAllRunning(type, nodeNameArray, localModel);
    }

    /**
     * @param type          The type of component (class)
     * @param nodeNameArray List of nodes to look at
     * @param localModel    The Kevoree model to look at
     * @return A map of &lt;name, Device&gt; found
     */
    public static HashMap<String, Device> findAllRunningDevice(
            final String type,
            final String[] nodeNameArray,
            final ContainerRoot localModel) {
        HashMap<String, Device> map = new HashMap<>();
        if (localModel != null) {
            for (String nodeName : nodeNameArray) {
                ContainerNode node = localModel.findNodesByID(nodeName);
                if (node != null) {
                    map.putAll(findDeviceInNode(node, type));
                }
            }
        }
        return map;
    }

    public static HashMap<String, Device> findAllRunningDevice(
            final String type,
            final String[] nodeNameArray,
            final ModelService modelService) {
        UUIDModel model = modelService.getCurrentModel();
        KevoreeFactory kevFactory = new DefaultKevoreeFactory();
        ModelCloner cloner = kevFactory.createModelCloner();
        ContainerRoot localModel = cloner.clone(model.getModel());
        return findAllRunningDevice(type, nodeNameArray, localModel);
    }

    /**
     * @param node The node to look at
     * @param type The type of component (class)
     * @return A map of &lt;name, Device&gt; found
     */
    private static HashMap<String, Device> findDeviceInNode(
            final ContainerNode node,
            final String type) {
        HashMap<String, Device> map = new HashMap<>();
        node.getComponents().stream()
                .filter(ci -> ci.getTypeDefinition() != null && ci.getTypeDefinition().getName() != null)
                .forEach(ci -> {
                    if (ci.getTypeDefinition() != null && ci.getTypeDefinition().getName() != null) {
                        String typeName = ci.getTypeDefinition().getName();
                        if (ci.getTypeDefinition().getName().equals(type)
                                || lookForSuperType(ci.getTypeDefinition().getSuperTypes(), type)) {
                            String id = node.getName() + "." + ci.getName();
                            Device device = new Device(ci.getName(), id, typeName, getComponentAttributes(ci));
                            map.put(id, device);
                        }
                    }
                });
        return map;
    }

    /**
     * Recursively check if a super type of a TypeDefinition
     * is of given type 'superType'.
     *
     * @param tdList    The list of super types
     * @param superType The type we are looking for
     * @return true if superType is found
     */
    private static boolean lookForSuperType(final List<TypeDefinition> tdList,
                                            final String superType) {
        for (TypeDefinition td : tdList) {
            if (td.getName() != null
                    && td.getName().compareTo(superType) == 0) {
                return true;
            } else if (lookForSuperType(td.getSuperTypes(), superType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param ci The component instance we look at
     * @return The map &lt;attribute,value&gt; of the given component instance
     */
    public static HashMap<String, String> getComponentAttributes(
            final ComponentInstance ci) {
        HashMap<String, String> attrMap = new HashMap<>();
        if (ci.getDictionary() != null) {
            for (Value value : ci.getDictionary().getValues()) {
                attrMap.put(value.getName(), value.getValue());
            }
        }
        return attrMap;
    }

    /**
     * Look at the Kevoree model to find InteractiveAppliance
     * which are negotiables and update the negotiableDeviceMap.
     */
    public static HashMap<String, Device> negotiableDeviceMap(final ModelService modelService,
                                                        final String nodeName) {
        KevoreeFactory kevFactory = new DefaultKevoreeFactory();
        ModelCloner cloner = kevFactory.createModelCloner();
        UUIDModel model = modelService.getCurrentModel();
        ContainerRoot localModel = cloner.clone(model.getModel());
        HashMap<String, Device> deviceMap = findAllRunningDevice("InteractiveAppliance",
                new String[]{nodeName}, localModel);
        HashMap<String, Device> negotiableDeviceMap = new HashMap<>();
        deviceMap.keySet().stream()
                .filter(deviceId -> deviceMap.get(deviceId).isNegotiable())
                .forEach(deviceId -> {
                    negotiableDeviceMap.put(deviceId, deviceMap.get(deviceId));
                });
        return negotiableDeviceMap;
    }

}
