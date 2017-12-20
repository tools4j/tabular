package org.tools4j.tabular.service;

import org.tools4j.tabular.util.PropertiesRepo;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 5:31 PM
 */
public class SystemVariables {
    public PropertiesRepo load(){
        return new PropertiesRepo(System.getProperties());
    }
}
