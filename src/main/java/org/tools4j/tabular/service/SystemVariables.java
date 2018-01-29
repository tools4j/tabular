package org.tools4j.tabular.service;

import org.tools4j.tabular.util.PropertiesRepo;
import org.tools4j.tabular.util.PropertiesRepoLoader;

/**
 * User: ben
 * Date: 26/10/17
 * Time: 5:31 PM
 */
public class SystemVariables implements PropertiesRepoLoader{
    @Override
    public PropertiesRepo load(){
        return new PropertiesRepo(System.getProperties());
    }
}
