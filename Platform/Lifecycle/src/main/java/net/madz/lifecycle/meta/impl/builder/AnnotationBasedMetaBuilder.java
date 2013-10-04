package net.madz.lifecycle.meta.impl.builder;

import net.madz.meta.FlavorMetaData;
import net.madz.meta.KeySet;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataBuilder;

public class AnnotationBasedMetaBuilder<SELF extends MetaData, PARENT extends MetaData> implements
        MetaDataBuilder<SELF, PARENT> {

    @Override
    public void addFlavor(FlavorMetaData<? super SELF> flavor) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeFlavor(Object key) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addKey(Object key) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addKeys(KeySet keySet) {
        // TODO Auto-generated method stub
    }

    @Override
    public SELF getMetaData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void handleError(Throwable e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasKey(Object key) {
        // TODO Auto-generated method stub
        return false;
    }
}